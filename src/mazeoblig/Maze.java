package mazeoblig;

import simulator.PositionInMaze;
import simulator.VirtualUser;

import java.awt.*;
import java.applet.*;

/**
 *
 * <p>Title: Maze</p>
 *
 * <p>Description: En enkel applet som viser den randomiserte labyrinten</p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Tegner opp maze i en applet, basert p� definisjon som man finner p� RMIServer
 * RMIServer p� sin side  henter st�rrelsen fra definisjonen i Maze
 * @author asd
 *
 */
@SuppressWarnings("serial")
public class Maze extends Applet {

	private BoxMazeInterface bm;
	private Box[][] maze;
	public static int DIM = 10;
	private int dim = DIM;

	static int xp;
	static int yp;
	static boolean found = false;

	private String server_hostname;
	private int server_portnumber;

	private TalkToServerInterface talkToServerInterface;
	private HashMap clientPositions = null;

	/**
	 * Setup server and registry connection and retrieves all remote objects from RMI server
	 */
	public void init() {
		int size = dim;
		/*
		 ** Kobler opp mot RMIServer, under forutsetning av at disse
		 ** kj�rer p� samme maskin. Hvis ikke m� oppkoblingen
		 ** skrives om slik at dette passer med virkeligheten.
		 */
		if (server_hostname == null)
			server_hostname = RMIServer.getHostName();
		if (server_portnumber == 0)
			server_portnumber = RMIServer.getRMIPort();
		try {
			java.rmi.registry.Registry r = java.rmi.registry.LocateRegistry.
					getRegistry(server_hostname,
							server_portnumber);

			/*
			 ** Henter inn referansen til Labyrinten (ROR)
			 */
			bm = (BoxMazeInterface) r.lookup(RMIServer.MazeName);
			maze = bm.getMaze();

			//Henter referansen til TalkToServerInterface metoder
			talkToServerInterface = (TalkToServerInterface) r.lookup(RMIServer.talkToServerIdString);

		} catch (RemoteException e) {
			System.err.println("Remote Exception: " + e.getMessage());
			System.exit(0);
		} catch (NotBoundException f) {
			/*
			 ** En exception her er en indikasjon p� at man ved oppslag (lookup())
			 ** ikke finner det objektet som man s�ker.
			 ** �rsaken til at dette skjer kan v�re mange, men v�r oppmerksom p�
			 ** at hvis hostname ikke er OK (RMIServer gir da feilmelding under
			 ** oppstart) kan v�re en �rsak.
			 */
			System.err.println("Not Bound Exception: " + f.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Create clients and populate maze
	 */
	public void start() {

//		CreateClients clients = new CreateClients(10);
	}

	//Get a parameter value
	public String getParameter(String key, String def) {
		return getParameter(key) != null ? getParameter(key) : def;
	}

	//Get Applet information
	public String getAppletInfo() {
		return "Applet Information";
	}

	//Get parameter info
	public String[][] getParameterInfo() {
		java.lang.String[][] pinfo = {{"Size", "int", ""},
		};
		return pinfo;
	}

	/**
	 * Render the maze
	 * @param g
	 */
	public void paint(Graphics g) {
		int x, y;

		// Tegner baser p� box-definisjonene ....

		for (x = 1; x < (dim - 1); ++x)
			for (y = 1; y < (dim - 1); ++y) {
				if (maze[x][y].getUp() == null)
					g.drawLine(x * 50, y * 50, x * 50 + 50, y * 50);
				if (maze[x][y].getDown() == null)
					g.drawLine(x * 50, y * 50 + 50, x * 50 + 50, y * 50 + 50);
				if (maze[x][y].getLeft() == null)
					g.drawLine(x * 50, y * 50, x * 50, y * 50 + 50);
				if (maze[x][y].getRight() == null)
					g.drawLine(x * 50 + 50, y * 50, x * 50 + 50, y * 50 + 50);
			}

		System.out.println("Paint was called");
		findMazeExit();
	}

	/**
	 * Finds the way out of the maze using a backtracking-algorithm. Each position is sent to server,
	 * then a list of all client locations are returned. All these positions are
	 * then painted on the applet
	 */
	private void findMazeExit() {

		Client client = new Client(maze);
		client.moveOutOfMaze(client.posFirstIteration);
		client.moveOutOfMaze(client.posSecondIteration);
	}

	/**
	 * Called whenever the client moves to a new position
	 *
	 * @param g graphics object
	 */
	@Override
	public void update(Graphics g) {

		clientPositions.forEach((key, value) -> {

			PositionInMaze pos = (PositionInMaze) value;

			g.drawOval(pos.getXpos() * 50, pos.getYpos() * 50, 50, 50);

		});

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Class used for creating a new client
	 */
	class Client {

		private int clientId;
		private VirtualUser virtualUser;
		private PositionInMaze[] posFirstIteration;
		private PositionInMaze[] posSecondIteration;

		Client(Box[][] maze) {
			try {
				virtualUser = new VirtualUser(maze);
				clientId = talkToServerInterface.getClientId();
				posFirstIteration = virtualUser.getFirstIterationLoop();
				posSecondIteration = virtualUser.getIterationLoop();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		void moveOutOfMaze(PositionInMaze[] pos) {

			for (PositionInMaze po : pos) {
				try {
					talkToServerInterface.sendPosition(clientId, po);
					clientPositions = talkToServerInterface.getAllClientPositions();
					update(getGraphics());
					System.out.println("From server: " + clientPositions.get(clientId));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * class used for creating n clients using threads
	 */
	class CreateClients extends Thread {

		ExecutorService threadPool;
		int numberOfClients;

		CreateClients(int numberOfClients) {
			this.numberOfClients = numberOfClients;
		}

		public void run() {

			threadPool = Executors.newFixedThreadPool(numberOfClients);
			threadPool.execute(() -> new Client(maze));
			threadPool.shutdown();
		}
	}

}
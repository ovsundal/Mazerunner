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
import java.util.HashMap;

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
	private final int CLIENTS_TO_CREATE = 3;

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
	 * Creates clients, mapupdater and populate maze
	 */
	public void start() {

		for(int i = 0; i < CLIENTS_TO_CREATE; i++) {
			new CreateClient().start();
		}
		new RequestMapUpdate().start();

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

		g.clearRect(0, 0, getWidth(), getHeight() );

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

			if(clientPositions != null) {

				clientPositions.forEach((key, value) -> {

					PositionInMaze pos = (PositionInMaze) value;

					g.drawOval(pos.getXpos() * 50, pos.getYpos() * 50, 50, 50);

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				});
			}
	}

	/**
	 * class used for creating n clients using threads
	 */
	private class CreateClient extends Thread {

		CreateClient() {}

		public void run() {

			try {
				VirtualUser user = new VirtualUser(maze,talkToServerInterface);

				while (true) {
					user.nextPosition();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private class RequestMapUpdate extends Thread {
		public void run() {
			try {
				while(true) {
					sleep(100);
					talkToServerInterface.sendAllClientPositions();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
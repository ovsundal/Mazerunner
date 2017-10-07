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

import static java.lang.Thread.sleep;

/**
 * Class responsible for drawing the maze and populating it with clients
 */
@SuppressWarnings("serial")
public class Maze extends Applet {

    private BoxMazeInterface bm;
    private Box[][] maze;
    public static int DIM = 30;
    private int dim = DIM;
    private String server_hostname;
    private int server_portnumber;
    private ServerInterface serverInterface;
    private HashMap<Integer, InformationObject> clientPositions = null;
    private final int CLIENTS_TO_CREATE = 50;
    private Integer mapDrawingClientId;
    long timeStart = System.currentTimeMillis();

    //used for double buffering inside paint method (From http://www.realapplets.com/tutorial/doublebuffering.html)
    private Graphics bufferGraphics;
    private Image offscreen;
    private Dimension dimension;

    /**
     * Establish server and registry connection (will only work if server and client is run from the same computer)
     * Retrieve all remote objects from RMI server. Method supplied at project start, modified by author
     */
    public void init() {

        //used for double buffering
        dimension = getSize();
        offscreen = createImage(dimension.width, dimension.height);
        bufferGraphics = offscreen.getGraphics();

        //establish connection
        if (server_hostname == null)
            server_hostname = RMIServer.getHostName();
        if (server_portnumber == 0)
            server_portnumber = RMIServer.getRMIPort();
        try {
            java.rmi.registry.Registry r = java.rmi.registry.LocateRegistry.
                    getRegistry(server_hostname,
                            server_portnumber);

			//get maze reference
            bm = (BoxMazeInterface) r.lookup(RMIServer.MazeName);
            maze = bm.getMaze();

            //get reference to server methods
            serverInterface = (ServerInterface) r.lookup(RMIServer.serverIdString);

        } catch (RemoteException e) {
            System.err.println("Remote Exception: " + e.getMessage());
            System.exit(0);
        } catch (NotBoundException f) {
            System.err.println("Not Bound Exception: " + f.getMessage());
            System.exit(0);
        }
    }

    /**
     * Creates clients, mapupdater and populates maze
     */
    public void start() {

        for (int i = 0; i < CLIENTS_TO_CREATE; i++) {

            try {
                new CreateClient().start();
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        new RequestMapUpdate().start();
    }

    /**
     * Render the maze, statistics and registered clients. Use doublebuffer to reduce applet render flimmering
     */
    public void paint(Graphics g) {
        int x, y;

        //clear the previous map
        bufferGraphics.clearRect(0, 0, dimension.width, dimension.height);
        bufferGraphics.setColor(Color.BLACK);
        // Draw the map
        for (x = 1; x < (dim - 1); ++x)
            for (y = 1; y < (dim - 1); ++y) {
                if (maze[x][y].getUp() == null)
                    bufferGraphics.drawLine(x * 10, y * 10, x * 10 + 10, y * 10);
                if (maze[x][y].getDown() == null)
                    bufferGraphics.drawLine(x * 10, y * 10 + 10, x * 10 + 10, y * 10 + 10);
                if (maze[x][y].getLeft() == null)
                    bufferGraphics.drawLine(x * 10, y * 10, x * 10, y * 10 + 10);
                if (maze[x][y].getRight() == null)
                    bufferGraphics.drawLine(x * 10 + 10, y * 10, x * 10 + 10, y * 10 + 10);
            }

            //if client positions does exist, render the clients into the buffered map
        if (clientPositions != null) {

            int totalReceivedMessagesByServer = clientPositions.get(mapDrawingClientId).getTotalServerMessagesReceived();
            int totalSentMessagesByServer = clientPositions.get(mapDrawingClientId).getTotalClientMessagesSent();
            long timeDeltaInSeconds;

            //calculate messages/second.
                timeDeltaInSeconds =  (System.currentTimeMillis() - timeStart) / 1000;

            //add server statistics
            bufferGraphics.drawString("Active clients: " + clientPositions.size(), 0, 315);

            bufferGraphics.drawString("Total messages server has received: " + totalReceivedMessagesByServer, 0, 330);

            bufferGraphics.drawString("Total messages sent from server: " + totalSentMessagesByServer, 0, 345);

            bufferGraphics.drawString("---------------------", 0, 360);

            //protect against division by zero
            try {
                bufferGraphics.drawString("AVG RECEIVED / SECOND: " +totalReceivedMessagesByServer / timeDeltaInSeconds,
                        0, 375);
            } catch (ArithmeticException e) {
                bufferGraphics.drawString("AVG RECEIVED / SECOND: 0",0, 375);
            }

            try {
                bufferGraphics.drawString("AVG SENT / SECOND: " + totalSentMessagesByServer / timeDeltaInSeconds,
                        0, 390);
            } catch (ArithmeticException e) {
                bufferGraphics.drawString("AVG SENT / SECOND: 0" ,0, 390);
            }

            bufferGraphics.drawString("---------------------", 0, 405);

            try {
                bufferGraphics.drawString("AVG RECEIVED PER CLIENT / SECOND: " +
                        (totalReceivedMessagesByServer / timeDeltaInSeconds) / CLIENTS_TO_CREATE,0, 420);
            } catch (ArithmeticException e) {
                bufferGraphics.drawString("AVG RECEIVED PER CLIENT / SECOND: 0",0, 420);
            }

            try {
                bufferGraphics.drawString("AVG SENT PER CLIENT / SECOND: " +
                        (totalSentMessagesByServer / timeDeltaInSeconds) / CLIENTS_TO_CREATE,0, 435);
            } catch (ArithmeticException e) {
                bufferGraphics.drawString("AVG SENT PER CLIENT / SECOND: 0",0, 435);
            }

            //draw client positions
            clientPositions.forEach((key, value) -> {

                PositionInMaze pos = value.getPosition();
                Color color = value.getColor();

                bufferGraphics.setColor(color);
                bufferGraphics.fillOval(pos.getXpos() * 10, pos.getYpos() * 10, 10, 10);
            });

            //paint the buffered image onto applet graphics
            g.setColor(Color.BLACK);
            g.drawImage(offscreen,0,0,this);
        }
    }

    /**
     * class used for creating n clients using threads. One of these clients (which one doesn't matter)
     * will be the mapdrawer, responsible for periodically rendering the map
     */
    private class CreateClient extends Thread {

        CreateClient() {}

        public void run() {

            try {
                VirtualUser user = new VirtualUser(maze, serverInterface);

                //assign a random mapdrawerclient
                if (mapDrawingClientId == null) {
                    mapDrawingClientId = user.getClientId();
                }

                //keep sending updated positions to server
                while (true) {

                    user.sendInfoToServer();

                    //if user is the mapdrawerclient, copy all data the client newly received from server to the Maze
                    // class, and repaint the map using this data
                    if (user.getClientId().equals(mapDrawingClientId)) {

                        clientPositions = user.getInfoFromAllClients();
                        repaint();
                    }

                    //timeout so clients do not move too quickly
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * class periodically signals the server to send stored client positions back to all clients using callbacks
     */
    private class RequestMapUpdate extends Thread {
        public void run() {
            try {

                while (true) {
                    sleep(100);
                    serverInterface.sendUpdatedInformationObjectFromServerToClient();
                }
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
package mazeoblig;

import simulator.ClientCallbackInterface;
import simulator.PositionInMaze;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

/**
 * Class responsible for client-server-client communication
 */
public class ServerMethods extends UnicastRemoteObject implements ServerInterface {

    private int clientId = 0;
    private HashMap<Integer, PositionInMaze> clientPositions = new HashMap<>();
    private HashMap<Integer, ClientCallbackInterface> clientList = new HashMap<>();
    private HashMap<Integer, Color> clientColors = new HashMap<Integer, Color>();


    protected ServerMethods() throws RemoteException {
    }




    /**
     * Method collects new client positions and appends them to list
     * @param id - client id
     * @param pos - client new position
     * @throws RemoteException
     */
    @Override
    public void sendPosition(int id, PositionInMaze pos) throws RemoteException {
        synchronized (clientPositions) {
            clientPositions.put(id, pos);
        }
    }

    @Override
    public Integer setClientId(ClientCallbackInterface cb) throws RemoteException {
        synchronized (clientList) {
            int id = clientId++;
            clientList.put(id, cb);
            System.out.println("A new client was created: " + clientId);
            return id;
        }
    }

    /**
     * Iterate through the list of clients and send all positions to clients registered in cb-interface.
     * @throws RemoteException
     */
    @Override
    public void sendAllClientPositions() throws RemoteException {
        synchronized (clientList) {
            synchronized (clientPositions) {
                clientList.forEach((key, value) -> {
                    try {
                        value.updateMap(clientPositions);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    @Override
    public void sendClientColors(int id, Color color) throws RemoteException {
        synchronized (clientColors) {
            clientColors.put(id, color);
        }
    }

    @Override
    public HashMap requestClientColors() throws RemoteException {
        return clientColors;
    }
}

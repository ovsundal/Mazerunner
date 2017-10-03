package mazeoblig;

import simulator.PositionInMaze;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

/**
 * Class responsible for client-server-client communication
 */
public class TalkToServer extends UnicastRemoteObject implements TalkToServerInterface {

    private int clientId = 0;
    private HashMap<Integer, PositionInMaze> clientPositions = new HashMap<>();

    protected TalkToServer() throws RemoteException {
    }

    @Override
    public void sendPosition(int id, PositionInMaze pos) throws RemoteException {
        synchronized (clientPositions) {
            clientPositions.put(id, pos);
        }

    }

    @Override
    public synchronized int getClientId() throws RemoteException {
        System.out.println(clientId);
        return clientId++;
    }

    @Override
    public HashMap getAllClientPositions() throws RemoteException {
        return clientPositions;
    }
}

package mazeoblig;

import simulator.CallbackInterface;
import simulator.PositionInMaze;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Class responsible for client-server-client communication
 */
public class TalkToServer extends UnicastRemoteObject implements TalkToServerInterface {

    private int clientId = 0;
    private HashMap<Integer, PositionInMaze> clientPositions = new HashMap<>();
    private HashMap<Integer, CallbackInterface> clientList = new HashMap<>();


    protected TalkToServer() throws RemoteException {
    }

    /**
     * Method collects new client positions and appends them to list
     * @param id - client id
     * @param pos - client new position
     * @throws RemoteException
     */
    @Override
    public void sendPosition(int id, PositionInMaze pos) throws RemoteException {
            clientPositions.put(id, pos);
        System.out.println("Client id " + id + " added position");
    }

    @Override
    public Integer setClientId(CallbackInterface cb) throws RemoteException {
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

        for(HashMap.Entry<Integer, CallbackInterface> entry : clientList.entrySet()) {
            entry.getValue().updateMap(clientPositions);

        }

        System.out.println("positions called");
    }



}

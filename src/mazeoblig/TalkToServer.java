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

    @Override
    public void sendAllClientPositions() throws RemoteException {

        for(Map.Entry<Integer, CallbackInterface> entry : clientList.entrySet())
            //fortsett her, bruk cb interface for å levere listen til alle klienter !!!!!!!!!!!!!!!!!!!!
            System.out.println("positions called");
    }



}

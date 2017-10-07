package mazeoblig;

import simulator.ClientCallbackInterface;
import simulator.PositionInMaze;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

/**
 * Class with methods responsible for client-server-client communication
 */
public class ServerMethods extends UnicastRemoteObject implements ServerInterface {

    private int clientId = 0;
    private HashMap<Integer, ClientCallbackInterface> clientList = new HashMap<>();
    private HashMap<Integer, InformationObject> informationObjectHashMap = new HashMap<>();
    private int amountClientMessagesReceived = 0;
    private int amountClientMessagesSent = 0;


    protected ServerMethods() throws RemoteException {}

    /**
     * Assign each newly connected client a unique id
     * @param cb
     * @return
     * @throws RemoteException
     */
    @Override
    public Integer setClientId(ClientCallbackInterface cb) throws RemoteException {
        synchronized (clientList) {
            int id = clientId++;
            clientList.put(id, cb);
            return id;
        }
    }

    /**
     * All information server RECEIVES FROM client is encapsulated within the information object. Server recieves
     * the object from each client in this method, updates the object with its own server-specific data,
     * and adds the object to a hashmap
     * @param object
     * @throws RemoteException
     */
    @Override
    public void sendInformationObjectFromClientToServer(InformationObject object) throws RemoteException {
        synchronized (clientList) {
            synchronized (informationObjectHashMap) {

                amountClientMessagesReceived++;

                //register client and callbackinterface if it doesn't exist
                if(!clientList.containsKey(object.getClientId())) {
                    clientList.put(object.getClientId(), object.getClientCallbackInterface());
                }

                //update the informationObject with data produced by the server
                object.setTotalClientMessagesSent(amountClientMessagesSent);
                object.setTotalServerMessagesReceived(amountClientMessagesReceived);

                //store it on the server
                informationObjectHashMap.put(object.getClientId(), object);
            }
        }
    }

    /**
     * Send the hashmap containing collected information from all clients (and server specific data)
     * back to all registered clients
     * @return
     * @throws RemoteException
     */
    @Override
    public InformationObject sendUpdatedInformationObjectFromServerToClient() throws RemoteException {
        synchronized (clientList) {
            synchronized (informationObjectHashMap) {

                clientList.forEach((id, client) -> {
                    try {

                        //send all stored information objects to all registered clients
                        client.receiveInformationObjectFromServer(informationObjectHashMap);
                        amountClientMessagesSent++;

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        return null;
    }
}

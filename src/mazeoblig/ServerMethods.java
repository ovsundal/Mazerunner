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
     * All information server RECEIVES FROM client is provided by information object in this method.
     * This method stores it in server
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
     * All information server SENDS TO client is provided by information object in this method.
     * The server adds all information it has and sends it back to every registered client
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

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
    private HashMap<Integer, InformationObject> informationObjectHashMap = new HashMap<>();


    protected ServerMethods() throws RemoteException {
    }




    /**
     * Method collects new client positions and appends them to list

     * @throws RemoteException
     */
//    @Override
//    public void sendPosition(int id, PositionInMaze pos) throws RemoteException {
//        synchronized (clientPositions) {
//            clientPositions.put(id, pos);
//        }
//    }

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

//    @Override
//    public void sendClientColors(int id, Color color) throws RemoteException {
//        synchronized (clientColors) {
//            clientColors.put(id, color);
//        }
//    }

    @Override
    public HashMap requestClientColors() throws RemoteException {
        return clientColors;
    }

    /**
     * All information server RECEIVES FROM client is provided by information object in this method.
     * This method stores it in server
     * @param object
     * @throws RemoteException
     */
    @Override
    public void sendInformationObjectToServer(InformationObject object) throws RemoteException {
        synchronized (clientList) {
            synchronized (informationObjectHashMap) {

                //register client and callbackinterface if it doesn't exist
                if(!clientList.containsKey(object.getClientId())) {
                    clientList.put(object.getClientId(), object.getClientCallbackInterface());
                }

                //update the object with data from the server
                //SERVERINFO HERE

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
    public InformationObject receiveInformationObject() throws RemoteException {
        synchronized (clientList) {
            synchronized (informationObjectHashMap) {

                //send all stored information objects to all registered clients
                clientList.forEach((id, client) -> {
                    try {
                        client.receiveInformationObjectFromServer(informationObjectHashMap);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        return null;
    }
}

package simulator;

import mazeoblig.InformationObject;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Server uses this callback interface to communicate back to clients
 */
public interface ClientCallbackInterface extends Remote {

    /**
     * Each client is assigned an id by the server. Server informs the client of assigned id using this method
     */
    Integer getClientId() throws RemoteException;

    /**
     * Sends a hashmap with updated information from all client and server-specific data back to respective client
     * using this method
     * @param objectHashMap
     * @throws RemoteException
     */
    void receiveInformationObjectFromServer(HashMap<Integer, InformationObject> objectHashMap) throws RemoteException;
}

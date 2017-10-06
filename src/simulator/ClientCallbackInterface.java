package simulator;

import mazeoblig.InformationObject;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Messages sent from server back to clients do that using this callbackinterface
 */

public interface ClientCallbackInterface extends Remote {
    Integer getClientId() throws RemoteException;

    /**
     * Sends a hashmap with server updated information (containing information about all clients) back to respective client
     * @param objectHashMap
     * @throws RemoteException
     */
    void receiveInformationObjectFromServer(HashMap<Integer, InformationObject> objectHashMap) throws RemoteException;
}

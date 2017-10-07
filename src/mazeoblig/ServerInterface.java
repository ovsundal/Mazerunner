package mazeoblig;

import simulator.ClientCallbackInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface responsible for client-server-client communication
 */
public interface ServerInterface extends Remote{
    /**
     * Server creates a client id and sends it back to respective client
     * @param cb
     * @return
     * @throws RemoteException
     */
    Integer setClientId(ClientCallbackInterface cb) throws RemoteException;

    /**
     * Used for client to send client-specific information to the server
     * @param obj
     * @throws RemoteException
     */
    void sendInformationObjectFromClientToServer(InformationObject obj) throws RemoteException;

    /**
     * Used by server to send collected information (from all clients) back to all registered clients
     * @return
     * @throws RemoteException
     */
    InformationObject sendUpdatedInformationObjectFromServerToClient() throws RemoteException;
}

package mazeoblig;

import simulator.ClientCallbackInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ServerInterface extends Remote{
    Integer setClientId(ClientCallbackInterface cb) throws RemoteException;
    void sendInformationObjectFromClientToServer(InformationObject obj) throws RemoteException;
    InformationObject sendUpdatedInformationObjectFromServerToClient() throws RemoteException;
}

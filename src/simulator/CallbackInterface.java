package simulator;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface CallbackInterface extends Remote {
    Integer getClientId() throws RemoteException;
    void updateMap(HashMap <Integer, PositionInMaze> listOfAllPosition) throws RemoteException;
}

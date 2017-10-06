package simulator;

import mazeoblig.InformationObject;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface ClientCallbackInterface extends Remote {
    Integer getClientId() throws RemoteException;
    void updateMap(HashMap <Integer, PositionInMaze> listOfAllPosition) throws RemoteException;
    void receiveInformationObjectFromServer(HashMap<Integer, InformationObject> objectHashMap) throws RemoteException;
}

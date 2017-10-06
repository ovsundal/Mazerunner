package mazeoblig;

import simulator.ClientCallbackInterface;
import simulator.PositionInMaze;

import java.awt.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface ServerInterface extends Remote{
    void sendPosition(int id, PositionInMaze pos) throws RemoteException;
    Integer setClientId(ClientCallbackInterface cb) throws RemoteException;
    void sendAllClientPositions() throws RemoteException;
    void sendClientColors(int id, Color color) throws RemoteException;
}

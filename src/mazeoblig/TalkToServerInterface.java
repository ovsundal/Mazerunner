package mazeoblig;

import simulator.PositionInMaze;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface TalkToServerInterface extends Remote{
    void sendPosition(int id, PositionInMaze pos) throws RemoteException;
    int getClientId() throws RemoteException;
    HashMap getAllClientPositions() throws RemoteException;
}

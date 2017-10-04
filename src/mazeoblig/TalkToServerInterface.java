package mazeoblig;

import simulator.CallbackInterface;
import simulator.PositionInMaze;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface TalkToServerInterface extends Remote{
    void sendPosition(int id, PositionInMaze pos) throws RemoteException;
    Integer setClientId(CallbackInterface cb) throws RemoteException;
    void sendAllClientPositions() throws RemoteException;
}

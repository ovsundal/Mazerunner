package mazeoblig;

import simulator.PositionInMaze;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TalkToServerInterface extends Remote{
    void sendPosition(PositionInMaze pos) throws RemoteException;
}

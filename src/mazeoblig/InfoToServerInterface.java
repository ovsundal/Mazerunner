package mazeoblig;

import simulator.PositionInMaze;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InfoToServerInterface extends Remote {
    void sendPosition(PositionInMaze pos) throws RemoteException;
}

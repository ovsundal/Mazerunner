package mazeoblig;

import simulator.PositionInMaze;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class responsible for client-server-client communication
 */
public class TalkToServer extends UnicastRemoteObject implements TalkToServerInterface {


    protected TalkToServer() throws RemoteException {
    }

    @Override
    public void sendPosition(PositionInMaze pos) throws RemoteException {
        System.out.println(pos);
    }
}

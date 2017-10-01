package mazeoblig;

import simulator.PositionInMaze;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Class responsible for client-server-client communication
 */
public class TalkToServer extends UnicastRemoteObject implements TalkToServerInterface {

    private int clientId = 0;

    protected TalkToServer() throws RemoteException {
    }

    @Override
    public void sendPosition(PositionInMaze pos) throws RemoteException {
        System.out.println(pos);
    }

    @Override
    public int getClientId() throws RemoteException {
        return clientId++;
    }
}

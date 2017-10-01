package mazeoblig;

import simulator.PositionInMaze;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class InfoToServer extends UnicastRemoteObject implements InfoToServerInterface {


    protected InfoToServer() throws RemoteException {
    }

    @Override
    public void sendPosition(PositionInMaze pos) throws RemoteException {
        System.out.println(pos);
    }
}

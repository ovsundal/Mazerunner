package mazeoblig;

import simulator.ClientCallbackInterface;
import simulator.PositionInMaze;
import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * All information client needs to pass to server (and server needs to pass to client) is stored within this object
 * It is updated with relevant client info, then sent to server. Server retrieves the data, add its own data, and stores
 * it in a hashmap. This hashmap with information objects from all clients is then sent back to each client
 */
public class InformationObject implements Serializable {

    //info provided by client
    private Integer clientId;
    private PositionInMaze position;
    private Color color;
    private ClientCallbackInterface clientCallbackInterface = null;

    //info provided by server
    private int totalServerMessagesReceived;
    private int totalClientMessagesSent;

    public InformationObject(ClientCallbackInterface cb, Color color) throws RemoteException {
        super();
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public PositionInMaze getPosition() {
        return position;
    }

    public void setPosition(PositionInMaze position) {
        this.position = position;
    }

    public int getTotalServerMessagesReceived() {
        return totalServerMessagesReceived;
    }

    public void setTotalServerMessagesReceived(int totalServerMessagesReceived) {
        this.totalServerMessagesReceived = totalServerMessagesReceived;
    }

    public int getTotalClientMessagesSent() {
        return totalClientMessagesSent;
    }

    public void setTotalClientMessagesSent(int totalClientMessagesSent) {
        this.totalClientMessagesSent = totalClientMessagesSent;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public ClientCallbackInterface getClientCallbackInterface() {
        return clientCallbackInterface;
    }

}

package mazeoblig;

import simulator.ClientCallbackInterface;
import simulator.PositionInMaze;
import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;

public class InformationObject implements Serializable {

    //info provided by client
    private Integer clientId;
    private PositionInMaze position;
    private Color color;
    private ClientCallbackInterface clientCallbackInterface;

    //info provided by server
    private int timeSinceServerStarted;
    private int totalServerMessagesReceived;
    private int totalClientMessagesSent;
    private HashMap<Integer, InformationObject> listOfAllClientPositions;

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

    public int getTimeSinceServerStarted() {
        return timeSinceServerStarted;
    }

    public void setTimeSinceServerStarted(int timeSinceServerStarted) {
        this.timeSinceServerStarted = timeSinceServerStarted;
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

    public void setClientCallbackInterface(ClientCallbackInterface clientCallbackInterface) {
        this.clientCallbackInterface = clientCallbackInterface;
    }
}

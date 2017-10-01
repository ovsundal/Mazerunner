package mazeoblig;

import simulator.PositionInMaze;

public interface TalkToServerInterface {
    void sendPosition(PositionInMaze pos);
}

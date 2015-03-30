package me.kapehh.OnlineStatus.queue;

/**
 * Created by Karen on 30.03.2015.
 */
public class QueueItem {
    public static final int JOIN = 1;
    public static final int LEAVE = 2;
    public static final int NEW_WORLD = 3;

    private String playerName;
    private String worldName;
    private int typeOfRequest;

    public QueueItem(String playerName, String worldName, int typeOfRequest) {
        this.playerName = playerName;
        this.worldName = worldName;
        this.typeOfRequest = typeOfRequest;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public int getTypeOfRequest() {
        return typeOfRequest;
    }

    public void setTypeOfRequest(int typeOfRequest) {
        this.typeOfRequest = typeOfRequest;
    }
}

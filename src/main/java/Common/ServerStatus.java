package Common;

import loadClient.loadController.Server;

import java.io.Serializable;

import static Common.ServerStatus.ServerStateType.UNKNOWN;

/**
 * This data structure hold the serverStateType of a server
 */
public class ServerStatus implements Serializable {
    public enum ServerStateType {
        WAITING, TRANSMITTING, LOADING, ERROR, UNKNOWN
    }

    float progress;
    boolean alive;
    boolean dataReady;
    ServerStateType serverStateType;


    public ServerStatus() {
        progress = 0;
        serverStateType = UNKNOWN;
        alive = false;
        dataReady = false;
    }

    public ServerStatus(ServerStatus status) {
        this.progress = status.getProgress();
        this.alive = status.isAlive();
        this.serverStateType = status.serverStateType;
        this.dataReady = status.dataReady;
    }

    public boolean isDataReady() {
        return dataReady;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public ServerStateType getServerStateType() {
        return serverStateType;
    }

    public void setServerStateType(ServerStateType serverStateType) {
        this.serverStateType = serverStateType;
    }
}



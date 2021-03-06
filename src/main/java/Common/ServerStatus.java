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

    double progress;
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

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
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

    public void setDataReady(boolean dataReady) {
        this.dataReady = dataReady;
    }

    public void setServerStateType(ServerStateType serverStateType) {
        this.serverStateType = serverStateType;
    }

    @Override
    public String toString() {
        return "ServerStatus{" +
                "progress=" + progress +
                ", alive=" + alive +
                ", dataReady=" + dataReady +
                ", serverStateType=" + serverStateType +
                '}';
    }
}



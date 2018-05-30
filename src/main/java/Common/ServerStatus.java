package Common;


import static Common.ServerStatus.Status.UNKNOWN;

/**
 * This data structure hold the status of a server
 */
public class ServerStatus {
    public enum Status {
        WAITING, TRANSMITTING, LOADING, ERROR, UNKNOWN
    }
    float progress;
    boolean alive;
    Status status;

    public ServerStatus() {
        progress = 0;
        status = UNKNOWN;
        alive = false;
    }

    public ServerStatus(float progress, boolean alive, Status status) {
        this.progress = progress;
        this.alive = alive;
        this.status = status;
    }

}



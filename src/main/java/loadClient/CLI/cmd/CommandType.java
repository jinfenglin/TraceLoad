package loadClient.CLI.cmd;

/**
 *
 */
public enum CommandType {
    START("start"),
    RELOAD("reload"),
    CLEAN("clean"),
    STOP("stop");

    private String commandType;

    CommandType(String commandType) {
        this.commandType = commandType;
    }
}

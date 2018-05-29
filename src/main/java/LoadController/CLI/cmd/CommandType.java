package LoadController.CLI.cmd;

/**
 *
 */
public enum CommandType {
    START("start"),
    RELOAD("reload"),
    CLEAN("clean");

    private String commandType;

    CommandType(String commandType) {
        this.commandType = commandType;
    }
}

package LoadController.CLI.cmd;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import sun.tools.jar.CommandLine;

/**
 * Factory which convert a string command line to an command object that can be executed
 */
public class CommandFactory {
    private CommandFactory factory;
    private CommandLineParser cmdLineParser;

    private CommandFactory() {
        cmdLineParser = new DefaultParser();
    }

    public CommandFactory getFactory() {
        if (factory == null) {
            factory = new CommandFactory();
        }
        return factory;
    }

    public Command convert(String commandLineStr) {
        String[] commandArgs = commandLineStr.split(" ");
        String operator = commandArgs[0];
        CommandType cmdType = CommandType.valueOf(operator);
        Command cmd = null;
        switch (cmdType) {
            case CLEAN:
                cmd = new CleanCmd(commandLineStr);
                break;
            case START:
                cmd = new StartCmd(commandLineStr);
                break;
            case RELOAD:
                cmd = new ReloadConfigCmd(commandLineStr);
                break;
        }
        return cmd;
    }

}

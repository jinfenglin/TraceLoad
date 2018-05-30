package loadClient.CLI.cmd;

import org.apache.commons.cli.*;
import loadClient.loadController.LoadController;


public abstract class Command {
    protected String commandStr;
    protected Options options;

    public Command(String commandStr) {
        this.commandStr = commandStr;
        this.options = new Options();
    }

    CommandLine parserCommandLine(String commandLineStr, Options options) throws ParseException {
        String[] commandArgs = commandLineStr.split(" ");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = parser.parse(options, commandArgs);
        return cmdLine;
    }

    public abstract void execute(LoadController loadController);
}

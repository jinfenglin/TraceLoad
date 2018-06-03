package loadClient.CLI.cmd;

import loadClient.loadController.LoadController;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

/**
 *
 */
public class StopCmd extends Command {
    public StopCmd(String commandStr) {
        super(commandStr);
    }

    @Override
    public void execute(LoadController loadController) {
        try {
            CommandLine commandLine = parserCommandLine(this.commandStr, this.options);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}

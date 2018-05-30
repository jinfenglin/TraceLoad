package loadClient.CLI.cmd;

import loadClient.loadController.LoadController;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 *
 */
public class CleanCmd extends Command {

    public CleanCmd(String commandStr) {
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

package loadClient.CLI.cmd;

import loadClient.loadController.LoadController;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

/**
 *
 */
public class StartCmd extends Command {

    public StartCmd(String commandStr) {
        super(commandStr);
    }

    @Override
    public void execute(LoadController loadController) {
        try {
            CommandLine commandLine = parserCommandLine(this.commandStr, this.options);
            loadController.startLoad();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

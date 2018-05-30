package loadClient.CLI;

import loadClient.CLI.cmd.Command;
import loadClient.CLI.cmd.CommandFactory;
import loadClient.loadController.LoadController;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Command line interface for the loadClient.loadController
 */
public class LoaderControllerCLI {
    private CommandFactory cmdFactory;
    private LoadController loadController;

    public LoaderControllerCLI() {
        cmdFactory = CommandFactory.getFactory();
        loadController = new LoadController();

    }

    public void run() throws ParseException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                System.out.print(">");
                String line = br.readLine();
                Command command = cmdFactory.toCommand(line);
                command.execute(loadController);
            }
        } finally {
            br.close();
        }
    }

    public static void main(String[] args) throws ParseException, IOException {
        LoaderControllerCLI cli = new LoaderControllerCLI();
        cli.run();
    }
}

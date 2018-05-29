package LoadController.CLI;

import LoadController.CLI.cmd.Command;
import LoadController.CLI.cmd.CommandType;
import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;


/**
 * Command line interface for the LoadController
 */
public class LoaderControllerCLI {
    private Map<CommandType, Command> commands;
    private CommandLineParser cmdLineParser;

    public LoaderControllerCLI() {
        cmdLineParser = new DefaultParser();
    }

    public void run() throws ParseException, IOException {
        Options options = new Options();
        options.addOption("p", "print", false, "Send print request to printer.")
                .addOption("g", "gui", false, "Show GUI Application")
                .addOption("n", true, "No. of copies to print");

//        HelpFormatter formatter = new HelpFormatter();
//        formatter.printHelp("CLITester", options);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                System.out.print(">");
                String line = br.readLine();
                String[] commandArgs = line.split(" ");
                CommandLine cmdLine = cmdLineParser.parse(options, commandArgs);
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

package loadClient.CLI;

import loadClient.CLI.cmd.Command;
import loadClient.CLI.cmd.CommandFactory;
import loadClient.loadController.ConfigurationFormatException;
import loadClient.loadController.LoadController;
import org.apache.commons.cli.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Command line interface for the loadClient.loadController
 */
public class LoaderControllerCLI {
    private CommandFactory cmdFactory;
    private LoadController loadController;

    public LoaderControllerCLI() throws ClassNotFoundException, ParserConfigurationException, ConfigurationFormatException, SAXException, IOException {
        cmdFactory = CommandFactory.getFactory();
        loadController = new LoadController();
        loadController.loadConfig("src/main/resources/controllerConfig.xml");

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

    public static void main(String[] args) throws ParseException, IOException, ClassNotFoundException,
            ParserConfigurationException, ConfigurationFormatException, SAXException {
        LoaderControllerCLI cli = new LoaderControllerCLI();
        cli.run();
    }
}

package loadClient.loadController;

import Common.ServerStatus;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the control panel of the distributed LoadGenerators. As the LoadGenerators are distributed over the servers,
 * this LoadManger can distribute the configurations to the clusters and manage the progress of all the workers.
 */
public class LoadController {
    List<LoadGenerator> generators;

    public LoadController() {
        generators = new ArrayList<>();
    }

    /**
     * Load configuration to init the controller
     *
     * @param xmlConfigPath The relative path using the classloader's search path as root
     */
    public void loadConfig(String xmlConfigPath) throws ParserConfigurationException, IOException, SAXException, ConfigurationFormatException, ClassNotFoundException {
        for (LoadGenerator generator : generators) {
            generator.close();
        }
        generators = LoadGeneratorFactory.getFactory().getLoadGenerators(xmlConfigPath);
    }

    public void startLoad() throws IOException, InterruptedException {
        for (LoadGenerator generator : generators) {
            generator.startLoad();
        }
    }

    public List<ServerStatus> showServersStatus() {
        List<ServerStatus> statuses = new ArrayList<>();
        for (LoadGenerator loadGenerator : generators) {
            for (Server server : loadGenerator.servers) {
                statuses.add(server.getStatus());
            }
        }
        return statuses;
    }

}

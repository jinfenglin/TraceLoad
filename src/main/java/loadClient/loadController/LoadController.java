package loadClient.loadController;

import Common.ServerStatus;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 * This is the control panel of the distributed LoadGenerators. As the LoadGenerators are distributed over the servers,
 * this LoadManger can distribute the configurations to the clusters and manage the progress of all the workers.
 */
public class LoadController {
    List<String> clusterIPs;

    private LoadController() {

    }

    /**
     * Load configuration to init the controller
     *
     * @param xmlConfigPath The relative path using the classloader's search path as root
     */
    public void loadConfig(String xmlConfigPath) throws ParserConfigurationException, IOException, SAXException {

    }

    public void startLoad() {

    }

    public ServerStatus showServersStatus() {
        return new ServerStatus();
    }

}

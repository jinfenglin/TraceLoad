package loadClient.loadController;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Create load generators based on the configuration file
 */
public class LoadGeneratorFactory {
    private final String LOAD_GENERATOR_TAG_NAME = "LoadGenerator";
    private final String SERVERS = "servers";
    private final String SERVER = "server";
    private final String DATA_SOURCES = "dataSources";
    private final String DATA_SOURCE = "dataSource";
    private final String TIMER = "timer";
    private final String TEMP_DIR = "temporaryDir";

    private static LoadGeneratorFactory factory;

    private LoadGeneratorFactory() {
    }

    public static LoadGeneratorFactory getFactory() {
        if (factory == null) {
            factory = new LoadGeneratorFactory();
        }
        return factory;
    }

    public List<LoadGenerator> getLoadGenerators(String xmlConfigPath) throws Exception {
        File xmlConfigFile = new File(xmlConfigPath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document configDocument = dBuilder.parse(xmlConfigFile);
        configDocument.getDocumentElement().normalize();
        NodeList loadGenConfigBlocks = configDocument.getElementsByTagName(LOAD_GENERATOR_TAG_NAME);
        List<LoadGenerator> loadGenerators = new ArrayList<>();
        for (int blockIndex = 0; blockIndex < loadGenConfigBlocks.getLength(); blockIndex += 1) {
            LoadGenerator loadGenerator = createLoadGenerator((Element) loadGenConfigBlocks.item(blockIndex));
            loadGenerators.add(loadGenerator);
        }
        return loadGenerators;
    }

    /**
     * Create a LoadGenerator based on the <LoadGeneration></LoadGeneration> marker/block in the xml configuration file.
     *
     * @param loadGeneratorNode
     * @return
     */
    private LoadGenerator createLoadGenerator(Element loadGeneratorNode) throws Exception {
        NodeList serversConfig = loadGeneratorNode.getElementsByTagName(SERVERS);
        NodeList dataSourcesConfig = loadGeneratorNode.getElementsByTagName(DATA_SOURCES);
        NodeList timerConfig = loadGeneratorNode.getElementsByTagName(TIMER);
        NodeList tempDirConfig = loadGeneratorNode.getElementsByTagName(TEMP_DIR);
        String id = loadGeneratorNode.getAttribute("id");
        if (tempDirConfig.getLength() == 0) {
            throw new ConfigurationFormatException("temporary directory is not given");
        }
        if (serversConfig.getLength() == 0) {
            throw new ConfigurationFormatException("servers config is not found");
        }
        if (dataSourcesConfig.getLength() == 0) {
            throw new ConfigurationFormatException("data sources config is not found");
        }
        if (timerConfig.getLength() == 0) {
            throw new ConfigurationFormatException("timer config is not found");
        }
        File tempDir = getTempDir(tempDirConfig.item(0).getTextContent());
        List<Server> servers = getServers((Element) serversConfig.item(0));
        List<DataSource> dataSources = getDataSource((Element) dataSourcesConfig.item(0), tempDir);
        LoadTimer timer = new LoadTimer((Element) timerConfig.item(0));
        return new LoadGenerator(id, servers, dataSources, timer, tempDir);
    }

    private File getTempDir(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    private List<Server> getServers(Element serversConfig) throws Exception {
        NodeList configs = serversConfig.getElementsByTagName(SERVER);
        List<Server> servers = new ArrayList<>();
        for (int serverIndex = 0; serverIndex < configs.getLength(); serverIndex += 1) {
            Element serverElement = (Element) configs.item(serverIndex);
            Server server = new Server(serverElement);
            servers.add(server);
        }
        return servers;
    }

    private List<DataSource> getDataSource(Element dataSourceConfigs, File tempDir) throws IOException {
        NodeList configs = dataSourceConfigs.getElementsByTagName(DATA_SOURCE);
        List<DataSource> dataSources = new ArrayList<>();
        for (int sourceIndex = 0; sourceIndex < configs.getLength(); sourceIndex += 1) {
            Element sourceElement = (Element) configs.item(sourceIndex);
            DataSource dataSource = new DataSource(sourceElement, tempDir);
            dataSources.add(dataSource);
        }
        return dataSources;
    }
}

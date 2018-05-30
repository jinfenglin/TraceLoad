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
import java.util.ArrayList;
import java.util.List;

/**
 * Create load generators based on the configuration file
 */
public class LoadGeneratorFactory {
    private final String LOAD_GENERATOR_TAG_NAME = "LoadGenerator";
    private final String SERVERS = "servers";
    private final String DATA_SOURCES = "dataSources";

    private static LoadGeneratorFactory factory;

    private LoadGeneratorFactory() {
    }

    public static LoadGeneratorFactory getFactory() {
        if (factory == null) {
            factory = new LoadGeneratorFactory();
        }
        return factory;
    }

    public List<LoadGenerator> getLoadGenerators(String xmlConfigPath) throws ParserConfigurationException, IOException, SAXException {
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
    private LoadGenerator createLoadGenerator(Element loadGeneratorNode) {
        NodeList serversConfig = loadGeneratorNode.getElementsByTagName(SERVERS);
        NodeList dataSourcesConfig = loadGeneratorNode.getElementsByTagName(DATA_SOURCES);
        List<Server> servers = getServers(serversConfig);
        List<DataSource> dataSources = getDataSource(dataSourcesConfig);
        return new LoadGenerator(servers, dataSources);
    }

    private List<Server> getServers(NodeList serversConfig) {
        List<Server> servers = new ArrayList<>();
        for (int serverIndex = 0; serverIndex < serversConfig.getLength(); serverIndex += 1) {
            Element serverElement = (Element) serversConfig.item(serverIndex);
            Server server = new Server(serverElement);
            servers.add(server);
        }
        return servers;
    }

    private List<DataSource> getDataSource(NodeList dataSourceConfigs) {
        List<DataSource> dataSources = new ArrayList<>();
        for (int sourceIndex = 0; sourceIndex < dataSourceConfigs.getLength(); sourceIndex += 1) {
            Element sourceElement = (Element) dataSourceConfigs.item(sourceIndex);
            DataSource dataSource = new DataSource(sourceElement);
            dataSources.add(dataSource);
        }
        return dataSources;
    }

}

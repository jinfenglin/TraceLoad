import loadClient.loadController.ConfigurationFormatException;
import loadClient.loadController.LoadGenerator;
import loadClient.loadController.LoadGeneratorFactory;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

/**
 *
 */
public class LoadGeneratorTests {
    @Test
    public void createLoadGenerator() throws IOException, SAXException, ParserConfigurationException, ConfigurationFormatException, ClassNotFoundException {
        String xmlPath = "src/main/resources/controllerConfig.xml";
        List<LoadGenerator> generatorList = LoadGeneratorFactory.getFactory().getLoadGenerators(xmlPath);
        Assert.assertNotEquals(0, generatorList.size());
    }
}

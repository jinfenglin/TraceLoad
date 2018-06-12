import Common.LoadOperation;
import loadClient.loadController.ConfigurationFormatException;
import loadClient.loadController.LoadGenerator;
import loadClient.loadController.LoadGeneratorFactory;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

    @Test
    public void verifyOPfile() throws ClassNotFoundException, ParserConfigurationException, ConfigurationFormatException, SAXException, IOException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("G:\\Download\\test_1\\ds1.op"));
        while (true) {
            Object obj = in.readObject();
            if (obj instanceof String) {
                String sign = (String) obj;
                if(sign.equals("$END$"))
                    break;
            }
            LoadOperation op = (LoadOperation) obj;
            Assert.assertEquals(op.getFileName(), "f1.txt");
        }
    }
}

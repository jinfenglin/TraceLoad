import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import static org.apache.log4j.Level.INFO;

/**
 * This is the control panel of the distributed LoadGenerators. As the LoadGenerators are distributed over the servers,
 * this LoadManger can distribute the configurations to the clusters and manage the progress of all the workers.
 */
public class LoadManager {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger("main");
        logger.log(INFO, "this is a test");
    }
}

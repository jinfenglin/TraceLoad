package loadClient.loadController;

import org.w3c.dom.Element;

import java.util.logging.Logger;

/**
 *
 */
public class Server {
    Logger logger;

    public Server(Element serverElement) {
        logger = Logger.getLogger(getClass().getName());
    }
}

package loadClient.loadController;

import Common.UserInfo;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.logging.Logger;

/**
 * Server object in the controller side
 */
public class Server {
    Logger logger;
    final String IP = "IP";
    final String PORT = "port";
    final String TARGET = "target";

    String ip, port;
    Target target;

    public Server(Element serverElement) {
        logger = Logger.getLogger(getClass().getName());
        ip = serverElement.getElementsByTagName(IP).item(0).getTextContent();
        port = serverElement.getElementsByTagName(PORT).item(0).getTextContent();
        Element targetElement = (Element) serverElement.getElementsByTagName(TARGET).item(0);
        target = new Target(targetElement);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }
}

class Target {
    enum Type {
        DISK, POSTGRES, NEO4J
    }

    final String TYPE = "type";
    final String USER_INFO = "userInfo";
    final String PATH = "path";

    private Type targetType;
    private UserInfo userInfo;
    String path;

    public Target(Element targetElement) {
        String typeStr = targetElement.getElementsByTagName(TYPE).item(0).getTextContent().toUpperCase();
        NodeList userInfoBlock = targetElement.getElementsByTagName(USER_INFO);
        NodeList pathBlock = targetElement.getElementsByTagName(PATH);
        targetType = Type.valueOf(typeStr);
        if (userInfoBlock.getLength() > 0) {
            userInfo = new UserInfo((Element) userInfoBlock.item(0));
        }
        if (pathBlock.getLength() > 0) {
            path = pathBlock.item(0).getTextContent();
        }
    }

    public Type getTargetType() {
        return targetType;
    }

    public void setTargetType(Type targetType) {
        this.targetType = targetType;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

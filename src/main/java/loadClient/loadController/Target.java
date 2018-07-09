package loadClient.loadController;

import Common.UserInfo;
import loadServer.TargetAdaptors.DiskAdaptor;
import loadServer.TargetAdaptors.SQLAdaptor;
import loadServer.TargetAdaptors.TargetAdaptor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.sql.SQLException;

/**
 *
 */
public class Target implements Serializable {
    public enum Type {
        DISK, POSTGRES, NEO4J
    }

    final String TYPE = "type";
    final String USER_INFO = "userInfo";
    final String PATH = "path";

    private Type targetType;
    private UserInfo userInfo;
    private String path;

    public Target(Element targetElement) throws Exception {
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


    public TargetAdaptor getTargetAdaptor() throws Exception {
        switch (targetType) {
            case DISK:
                return new DiskAdaptor(this);
            case NEO4J:
                break;
            case POSTGRES:
                return new SQLAdaptor(this);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Target{" +
                "targetType=" + targetType +
                '}';
    }
}

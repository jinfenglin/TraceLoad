package Common;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.Serializable;

public class UserInfo implements Serializable{
    final String USERNAME = "username";
    final String PASSWD = "passwd";
    private String username, passwd;

    public UserInfo(String username, String passwd) {
        this.username = username;
        this.passwd = passwd;
    }

    public UserInfo(Element userInfoElement) {
        NodeList useranmeBlock = userInfoElement.getElementsByTagName(USERNAME);
        NodeList passwdBlock = userInfoElement.getElementsByTagName(PASSWD);
        if (useranmeBlock.getLength() > 0) {
            this.username = useranmeBlock.item(0).getTextContent();
        }

        if (passwdBlock.getLength() > 0) {
            this.passwd = passwdBlock.item(0).getTextContent();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}

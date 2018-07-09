package loadServer.TargetAdaptors;

import loadClient.loadController.Target;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 */
public class SQLAdaptor implements TargetAdaptor {
    String url, username, password;
    Connection connection;
    String TABLE_NAME = "artifacts";
    String FILE_NAME_COL = "fileName";
    String ARTI_CONTENT_COL = "content";

    public SQLAdaptor(Target target) throws SQLException {
        this.url = target.getPath();
        this.username = target.getUserInfo().getUsername();
        this.password = target.getUserInfo().getPasswd();
    }

    @Override
    public void create(String id, String content) throws Exception {
        Statement st = connection.createStatement();
        st.executeUpdate(String.format("INSERT INTO %s VALUES ('%s', '%s')", TABLE_NAME, id, content));
    }

    @Override
    public void delete(String id) throws Exception {
        Statement st = connection.createStatement();
        st.executeUpdate(String.format("DELETE FROM \"%s\" WHERE \"%s\"='%s'", TABLE_NAME, FILE_NAME_COL, id));
    }

    @Override
    public void update(String id, String content) throws Exception {
        Statement st = connection.createStatement();
        st.executeUpdate(String.format("UPDATE \"%s\" SET \"%s\"='%s' WHERE \"%s\"='%s'", TABLE_NAME, ARTI_CONTENT_COL, content, FILE_NAME_COL, id));
    }

    @Override
    public List<String> read(String id) throws Exception {
        String query = String.format("SELECT \"%s\" FROM \"%s\" WHERE \"%s\" = '%s'", ARTI_CONTENT_COL, TABLE_NAME, FILE_NAME_COL, id);
        List<String> res = new ArrayList<>();

        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(query);
        while (rs.next()) {
            String s = rs.getString(ARTI_CONTENT_COL);
            res.add(s);
        }
        return res;
    }

    @Override
    public void login() throws Exception {
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        //props.setProperty("ssl", "true");
        connection = DriverManager.getConnection(url, props);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }

    public void dropTable() throws Exception {
        String query = String.format("DROP TABLE IF EXISTS \"%s\" ", TABLE_NAME);
        Statement st = connection.createStatement();
        st.executeUpdate(query);
    }

    public void createTable() throws Exception {
        String query = String.format("CREATE TABLE \"%s\" ( \"%s\" text, \"%s\" text, PRIMARY KEY ( \"%s\" ))", TABLE_NAME, FILE_NAME_COL, ARTI_CONTENT_COL, FILE_NAME_COL);
        Statement st = connection.createStatement();
        st.executeUpdate(query);
    }

    public void reset() throws Exception {
        login();
        dropTable();
        createTable();
    }
}

package loadServer.TargetAdaptors.graphDb;

import com.steelbridgelabs.oss.neo4j.structure.Neo4JElementIdProvider;
import com.steelbridgelabs.oss.neo4j.structure.Neo4JGraph;
import com.steelbridgelabs.oss.neo4j.structure.providers.Neo4JNativeElementIdProvider;
import loadClient.loadController.Target;
import loadServer.TargetAdaptors.TargetAdaptor;
import org.apache.commons.configuration.BaseConfiguration;
import org.apache.tinkerpop.gremlin.orientdb.OrientGraph;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class GraphDbAdaptor implements IGraphDbManger, TargetAdaptor {

    private Target loadTarget;

    public GraphDbAdaptor(Target loadTarget) {
        this.loadTarget = loadTarget;
    }

    private Graph createNeo4jGraph(Target loadTarget) {
        String boltUrl = loadTarget.getPath();
        String userName = loadTarget.getUserInfo().getUsername();
        String password = loadTarget.getUserInfo().getPasswd();
        Driver driver = GraphDatabase.driver(boltUrl, AuthTokens.basic(userName, password));
        Neo4JElementIdProvider<?> vertexIdProvider = new Neo4JNativeElementIdProvider();
        Neo4JElementIdProvider<?> edgeIdProvider = new Neo4JNativeElementIdProvider();
        Graph graph = new Neo4JGraph(driver, vertexIdProvider, edgeIdProvider);
        return graph;
    }

    private Graph createOrientDBGraph(Target loadTarget) {
        BaseConfiguration config = new BaseConfiguration();
        config.setProperty("orient-url", loadTarget.getPath());
        config.setProperty("orient-user", loadTarget.getUserInfo().getUsername());
        config.setProperty("orient-pass", loadTarget.getUserInfo().getPasswd());
        config.setProperty("orient-transactional", true);
        return OrientGraph.open(config);
    }

    @Override
    public Graph getGraph() throws Exception {
        Graph graph = null;
        switch (loadTarget.getTargetType()) {
            case DISK:
                break;
            case POSTGRES:
                break;
            case NEO4J:
                graph = createNeo4jGraph(loadTarget);
                break;
            case ORIENTDB:
                graph = createOrientDBGraph(loadTarget);
                break;
        }
        return graph;
    }

    @Override
    public void create(String id, String content, String dataSourceName) throws Exception {
        Map<String, Object> attribs = new HashMap<>();
        attribs.put(FILE_NAME_COL, id);
        attribs.put(ARTI_CONTENT_COL, content);
        createNode(dataSourceName, attribs);
    }

    @Override
    public void delete(String id, String dataSourceName) throws Exception {
        Map<String, Object> attribs = new HashMap<>();
        attribs.put(FILE_NAME_COL, id);
        removeVertexByFilter(dataSourceName, attribs);
    }

    @Override
    public void update(String id, String content, String dataSourceName) throws Exception {
        Map<String, Object> attribs = new HashMap<>();
        attribs.put(FILE_NAME_COL, id);
        List<Vertex> vertices = findVerticesByFilter(dataSourceName, attribs);
        if (vertices.size() != 1)
            throw new Exception("Multiple vertices found for update");
        attribs.put(ARTI_CONTENT_COL, content);
        updateVertexProperties(vertices.get(0).id().toString(), attribs);
    }

    @Override
    public List<String> read(String id, String dataSourceName) throws Exception {
        Map<String, Object> attribs = new HashMap<>();
        attribs.put(FILE_NAME_COL, id);
        List<Vertex> vertices = findVerticesByFilter(dataSourceName, attribs);
        if (vertices.size() != 1)
            throw new Exception("Multiple vertices found for update");
        List<String> res = new ArrayList<>();
        res.add(vertices.get(0).property(FILE_NAME_COL).toString());
        return res;
    }

    @Override
    public void login() throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void reset(List<String> dataSourceNames) throws Exception {
        flushDB();
    }
}

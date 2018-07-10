package loadServer.TargetAdaptors.graphDb;

import org.apache.tinkerpop.gremlin.process.traversal.Path;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.neo4j.driver.v1.StatementResult;

import java.util.*;

import static org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__.*;

/**
 * The interface containging the operations for graph db. Including CRUD operations.. Each method
 * should acquire a new graph instance and execute commands in an transaction. This interface should be thread safe.
 * All properties are added through
 */
public interface IGraphDbManger {
    Graph getGraph() throws Exception;

    /*** Create ***/

    /**
     * Create a node with given label and attributes.
     *
     * @param label
     * @param attribs
     * @return
     * @throws Exception
     */
    default Vertex createNode(String label, Map<String, Object> attribs) throws Exception {
        Objects.requireNonNull(label, "Label should not be null");
        try (Graph graph = getGraph()) {
            try (Transaction transaction = graph.tx()) {
                Vertex vertex = graph.addVertex(label);
                addAttribs(vertex, attribs);
                transaction.commit();
                return vertex;
            }
        }
    }

    default Edge createEdge(String sourceID, String targetID, String label, Map<String, Object> attribs) throws Exception {
        try (Graph graph = getGraph()) {
            try (Transaction transaction = graph.tx()) {
                GraphTraversal traversal = graph.traversal().V(sourceID).addE(label).to(graph.traversal().V(targetID).next());
                for (String attName : attribs.keySet()) {
                    traversal = traversal.property(attName, attribs.get(attName));
                }
                Edge edge = (Edge) traversal.next();
                transaction.commit();
                return edge;
            }
        }
    }

    /*** Read ***/

    default Vertex findVertex(String ID) throws Exception {
        try (Graph graph = getGraph()) {
            Vertex vertex = graph.vertices(ID).next();
            return vertex;
        }
    }

    default List<Vertex> findVerticesByLabel(String label) throws Exception {
        return findVerticesByFilter(label, null);
    }

    default List<Vertex> findVerticesByLabelWithLimit(String label, int limit) throws Exception {
        return findVerticesByFilterWithLimit(label, null, limit);
    }

    default List<Vertex> findVerticesByFilter(String label, Map<String, Object> attribs) throws Exception {
        try (Graph graph = getGraph();) {
            GraphTraversal traversal = graph.traversal().V();
            if (label != null)
                traversal = graph.traversal().V().hasLabel(label);
            traversal = filterAttribs(traversal, attribs);
            List<Vertex> res = traversal.toList();
            return res;
        }
    }

    //TODO clean the duplication
    default List<Vertex> findVerticesByFilterWithLimit(String label, Map<String, Object> attribs, int limit) throws Exception {
        try (Graph graph = getGraph();) {
            GraphTraversal traversal = graph.traversal().V();
            if (label != null)
                traversal = graph.traversal().V().hasLabel(label);
            traversal = filterAttribs(traversal, attribs).limit(limit);
            List<Vertex> res = traversal.toList();
            return res;
        }
    }

    default Edge findEdge(String ID) throws Exception {
        try (Graph graph = getGraph()) {
            Edge res = graph.traversal().E(ID).next();
            return res;
        }
    }

    default List<Edge> findEdgesBetweenVertexTypes(String sourceNodeLabel, String targetNodeType) throws Exception {
        try (Graph graph = getGraph()) {
            List<Edge> res = graph.traversal().V().hasLabel(sourceNodeLabel).bothE().where(otherV().hasLabel(targetNodeType)).toList();
            return res;
        }
    }

    default List<Edge> findEdgeBetweenVertices(String sourceNodeID, String targetNodeID) throws Exception {
        try (Graph graph = getGraph()) {
            List<Edge> res = graph.traversal().V(sourceNodeID).bothE().where(otherV().hasId(targetNodeID)).toList();
            return res;
        }
    }

    default List<Edge> findEdgeForNode(String nodeId) throws Exception {
        try (Graph graph = getGraph()) {
            List<Edge> res = graph.traversal().V(nodeId).bothE().toList();
            return res;
        }
    }

    default List<Path> findPathBetweenNodes(String sourceNodeID, String targetNodeID) throws Exception {
        try (Graph graph = getGraph()) {
            List<Path> res = graph.traversal().V(sourceNodeID).repeat(out().simplePath()).until(hasId(targetNodeID)).path().toList();
            return res;
        }
    }

    default Iterator<Vertex> getAllVertices() throws Exception {
        try (Graph graph = getGraph()) {
            return graph.vertices();
        }
    }

    default Iterator<Edge> getAllEdges() throws Exception {
        try (Graph graph = getGraph()) {
            Iterator<Edge> res = graph.edges();
            return res;
        }
    }

    default List<Vertex> findAdjacentVertex(String nodeID) throws Exception {
        try (Graph graph = getGraph()) {
            List<Vertex> adjacent = new ArrayList<>();
            adjacent.addAll(graph.traversal().V(nodeID).outE().inV().toList());
            adjacent.addAll(graph.traversal().V(nodeID).inE().outV().toList());
            return adjacent;
        }
    }

    default long countVertices() throws Exception {
        return IteratorUtils.count(getAllVertices());
    }

    default long countEdges() throws Exception {
        return IteratorUtils.count(getAllEdges());
    }


    /*** Update ***/
    /**
     * This function not work for neo4j server.
     *
     * @param id
     * @param attribs
     * @throws Exception
     */
    default void updateVertexProperties(String id, Map<String, Object> attribs) throws Exception {
        try (Graph graph = getGraph()) {
            try (Transaction tranx = graph.traversal().tx()) {
                GraphTraversal traversal = graph.traversal().V(id);
                for (String attName : attribs.keySet()) {
                    traversal = traversal.property(attName, attribs.get(attName).toString());
                }
                traversal.iterate();
                tranx.commit();
            }
        }
    }

    default void updateEdgeProperties(String id, Map<String, Object> attribs) throws Exception {
        try (Graph graph = getGraph()) {
            try (Transaction tranx = graph.traversal().tx()) {
                GraphTraversal traversal = graph.traversal().E(id);
                for (String attName : attribs.keySet()) {
                    traversal = traversal.property(attName, attribs.get(attName).toString());
                }
                traversal.iterate();
                tranx.commit();
            }
        }
    }

    /*** Delete ***/
    default void removeVertex(String ID) throws Exception {
        Objects.requireNonNull(ID, "ID should not be null");
        try (Graph graph = getGraph()) {
            try (Transaction tranx = graph.traversal().tx()) {
                graph.traversal().V(ID).drop().iterate();
                tranx.commit();
            }
        }
    }

    default void removeVertexByLabel(String label) throws Exception {
        Objects.requireNonNull(label, "Label should not be null");
        removeVertexByFilter(label, null);
    }

    default void removeVertexByAttrib(Map<String, Object> attribs) throws Exception {
        Objects.requireNonNull(attribs, "Attributes should not be null");
        removeVertexByFilter(null, attribs);
    }

    /**
     * Remove the vertex whose label and attributes matches the given parameters.
     * If label or attributes are null just ignore the parameters.
     *
     * @param label
     * @param attribs
     * @throws Exception
     */
    default void removeVertexByFilter(String label, Map<String, Object> attribs) throws Exception {
        try (Graph graph = getGraph()) {
            try (Transaction transx = graph.traversal().tx()) {

                GraphTraversal traversal = graph.traversal().V();
                if (label != null)
                    traversal = traversal.hasLabel(label);
                if (attribs != null)
                    traversal = filterAttribs(traversal, attribs);
                traversal.drop().iterate();
                transx.commit();
            }
        }
    }

    default void removeEdge(String sourceNodeID, String targetNodeID) throws Exception {
        Objects.requireNonNull(sourceNodeID, "Source node ID should not be null");
        Objects.requireNonNull(targetNodeID, "Target node ID should not be null");
        try (Graph graph = getGraph()) {
            try (Transaction transx = graph.traversal().tx()) {
                graph.traversal().V(sourceNodeID).bothE().where(otherV().hasId(targetNodeID)).drop().iterate();
                transx.commit();
            }
        }
    }

    default void removeEdge(String ID) throws Exception {
        Objects.requireNonNull(ID, "ID should not be null");
        try (Graph graph = getGraph()) {
            try (Transaction transx = graph.traversal().tx()) {
                graph.traversal().E(ID).drop().iterate();
                transx.commit();
            }
        }
    }

    default void removeEdgeByLabel(String label) throws Exception {
        Objects.requireNonNull(label, "Label should not be null");
        removeEdgeByFilter(label, null);
    }

    default void removeEdgeByAttrib(Map<String, Object> attribs) throws Exception {
        Objects.requireNonNull(attribs, "Attributes should not be null");
        removeEdgeByFilter(null, attribs);
    }

    default void detachNode(String nodeID) throws Exception {
        Objects.requireNonNull(nodeID, "ID should not be null");
        try (Graph graph = getGraph()) {
            try (Transaction transx = graph.traversal().tx()) {
                graph.traversal().V(nodeID).bothE().drop().iterate();
                transx.commit();
            }
        }
    }

    /**
     * Remove the Edges whose label and attributes matches the given parameters.
     * If label or attributes are null just ignore the parameters.
     *
     * @param label
     * @param attribs
     * @throws Exception
     */
    default void removeEdgeByFilter(String label, Map<String, Object> attribs) throws Exception {
        try (Graph graph = getGraph()) {
            try (Transaction transx = graph.traversal().tx()) {
                GraphTraversal traversal = graph.traversal().E();
                if (label != null)
                    traversal = traversal.hasLabel(label);
                if (attribs != null)
                    traversal = filterAttribs(traversal, attribs);
                traversal.drop().iterate();
                transx.commit();
            }
        }
    }


    default void flushDB() throws Exception {
        try (Graph graph = getGraph()) {
            try (Transaction transx = graph.traversal().tx()) {
                graph.traversal().V().drop().iterate();
                transx.commit();
            }
        }
    }

    /**
     * Add the properties to a vertex with {@link VertexProperty.Cardinality#single}.
     * This function should be called with in a transaction, otherwise the result will not be updated.
     *
     * @param vertext
     * @param attribs
     * @return
     */
    default void addAttribs(Vertex vertext, Map<String, Object> attribs) {
        if (attribs == null)
            return;
        for (String attribName : attribs.keySet()) {
            vertext.property(attribName, attribs.get(attribName).toString());
        }
    }


    static GraphTraversal filterAttribs(GraphTraversal traversal, Map<String, Object> attribs) {
        if (attribs != null) {
            for (String attName : attribs.keySet()) {
                traversal = traversal.has(attName, attribs.get(attName));
            }
        }
        return traversal;
    }
}

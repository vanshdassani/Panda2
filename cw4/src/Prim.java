import graph.*;

import java.util.*;

public class Prim {

    private Graph<Integer, Integer> graph; // G = (V, E)
    private Graph<Integer, Integer> mst;
    private Set<Node<Integer>> allNodes; // V
    private List<Node<Integer>> visitedNodes; // W ⊆ V
    private Set<Node<Integer>> notVisitedNodes; // V \ W
    private Set<Edge<Integer, Integer>> utilisedEdges; // F ⊆ E
    private Node<Integer> source; // s ∈ V
    private Map<Node<Integer>, Integer> distances;


    /**
     * Initialises all properties ready to run Prims.
     * Then calls findMinimumSpanningTree to get value for mst
     */
    public Prim(Graph<Integer, Integer> graph) {
        this.graph = graph;

        source = this.graph.getNode(1); // NOTE maybe fix, looks for node value = 1

        // Init V = {v | v <- graph}
        allNodes = new HashSet<Node<Integer>>(graph.getNodes()); // NOTE possible need
                                                                 // graph.getNodes()

        // Init W = {s}
        visitedNodes = new LinkedList<Node<Integer>>();
        visitedNodes.add(source);

        // Init V \ W = setNodes.removeAll(visitedNodes)
        Set<Node<Integer>> notVisitedNodes = new HashSet<Node<Integer>>(allNodes);
        notVisitedNodes.removeAll(visitedNodes);

        // Init F = {}
        utilisedEdges = new HashSet<Edge<Integer, Integer>>();

        // Init D(v) = d(s, v) for all v in V
        distances = new HashMap<Node<Integer>, Integer>();
        initDistances();

        // Find minimum spanning tree
        mst = findMinimumSpanningTree();
    }


    /**
     * Set all keys in distances to each node in graph. Initialise all
     * corresponding values to distance to source node
     */
    private void initDistances() {
        // set k = node, v = dist to source
        for (Node node : allNodes) {
            distances.put(node, dist(source, node));
        }
    }


    /**
     * Uses Prim's algorithm to find the mimimum spanning tree for graph
     */
    private Graph<Integer, Integer> findMinimumSpanningTree() {

        // mst to construct from utilisedEdges after Prims, and then return later
        Graph minimumSpanningTree = new DirectedGraph<Integer, Integer>();

        // continue until visitedNodes = allNodes
        while (!visitedNodes.equals(allNodes)) {

            // Select a new current vertex w in V \ W, with minimal D(w)
            Pair<Node<Integer>, Edge<Integer, Integer>> min = findMinDistNode();

            // Add current vertex w to W, add related edge to F
            visitedNodes.add(min.getLeft());
            utilisedEdges.add(min.getRight());

            // Update distances for all v in V \ W
            for (Node<Integer> v : notVisitedNodes) {
                distances.replace(v, Dist(v));
            }
        }

        // construct tree from utilisedEdges
        for (Node<Integer> node : allNodes) {
            minimumSpanningTree.add(node);
        }
        for (Edge<Integer, Integer> edge : utilisedEdges) {
            minimumSpanningTree.add(edge);
        }

        return minimumSpanningTree;
    }


    /** TODO
     * Looks up node with shortest distance to mst (so far) and returns a
     * pair: (cloest vertex, corresponding edge)
     */
     private Pair<Node<Integer>, Edge<Integer, Integer>> findMinDistNode() {


         return null;
     }


    /**
     * Finds the shortest immediate distance from node v1 to v2. If not
     * directly connected, will return infinity (Integer.MAX_VALUE)
     *
     * @param v1 the first node to find distance between (edge source)
     * @param v2 the second node to find distance between (edge target)
     */
    private Integer dist(Node<Integer> v1, Node<Integer> v2) {
        Integer distance = new Integer(Integer.MAX_VALUE);

        // if edge exists, return edge.getData.
        // Else, will return Integer.MAX_VALUE
        for (Edge<Integer, Integer> edge : graph.getEdgesFrom(v1)) {
            if (edge.getTarget().equals(v2)) {
                distance = edge.getData();
                break;
            }
        }

        return distance;
    }


    /** TODO
     * Finds immediate distance between mst so far and given vertex in graph
     * TODO Change so only checks for distance to last node added to the mst so far
     *
     * @param vertex the vertex to find shortest immediate distance to MST for
     */
    private Integer Dist(Node<Integer> vertex) {
        // find min(D(v), d(w, v)) //

        // find d(vertex, node) for last node added
        //Integer distToNewNode = dist(vertex, )



        return -1;
    }


    /**
     * Returns minimum spanning tree for graph
     */
    public Graph<Integer, Integer> getMinimumSpanningTree() {
        //TODO: You should return a new graph that represents the minimum
        // spanning tree of the graph.
        return mst;
    }

}

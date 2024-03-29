import graph.*;

import java.util.*;

public class Prim {

    private Graph<Integer, Integer> graph; // G = (V, E)
    private Graph<Integer, Integer> mst;
    private Set<Node<Integer>> allNodes; // V
    private LinkedList<Node<Integer>> visitedNodes; // W ⊆ V
    private LinkedList<Node<Integer>> notVisitedNodes; // V \ W
    private Set<Edge<Integer, Integer>> utilisedEdges; // F ⊆ E
    private Node<Integer> source; // s ∈ V
    private Map<Node<Integer>, Edge<Integer, Integer>> distances;


    /**
     * Initialises all properties ready to run Prims.
     * Then calls findMinimumSpanningTree to get value for mst
     */
    public Prim(Graph<Integer, Integer> graph) {
        this.graph = graph;

        source = this.graph.getNode(1); // NOTE maybe fix, looks for node value = 1

        // Init V = {v | v <- graph}
        allNodes = new HashSet<Node<Integer>>(graph.getNodes());

        System.err.print("start allNodes:");
        System.err.println(Arrays.toString(allNodes.toArray()));


        // Init W = {s}
        visitedNodes = new LinkedList<Node<Integer>>();
        visitedNodes.add(source);
        System.err.print("start visitedNodes:");
        System.err.println(Arrays.toString(visitedNodes.toArray()));

        // Init V \ W = setNodes.removeAll(visitedNodes)
        notVisitedNodes = new LinkedList<Node<Integer>>(allNodes);
        notVisitedNodes.remove(source);
        System.err.print("start notVisitedNodes:");
        System.err.println(Arrays.toString(notVisitedNodes.toArray()));

        // Init F = {}
        utilisedEdges = new HashSet<Edge<Integer, Integer>>();

        // Init D(v) = d(s, v) for all v in V
        distances = new HashMap<Node<Integer>, Edge<Integer, Integer>>();
        initDistances();

        // Find minimum spanning tree
        mst = findMinimumSpanningTree();
    }


    /**
     * Set all keys in distances to each node in graph. Initialise all
     * corresponding values to edge from node to source with corresponding
     * distance. If Edge doesn't exist, new edge with distance infinity is
     * created. <-NOTE
     */
    private void initDistances() {
        // set k = node, v = new Edge(node, source, distance from node to source)
        for (Node node : allNodes) {
            distances.put(node, new Edge(node, source, dist(node, source)));
        }

        // remove source node (already in mst)
        distances.remove(source);

        System.err.print("Distances: ");
        System.err.println(distances.toString());
    }


    /**
     * Uses Prim's algorithm to find the mimimum spanning tree for graph
     */
    private Graph<Integer, Integer> findMinimumSpanningTree() {

        // mst to construct from utilisedEdges after Prims, and then return later
        Graph minimumSpanningTree = new UndirectedGraph<Integer, Integer>();

        // continue until visitedNodes = allNodes
        while (!notVisitedNodes.isEmpty()) {

            // Select a new current vertex w in V \ W, with minimal D(w)
            Pair<Node<Integer>, Edge<Integer, Integer>> min = findMinDistNode();


            // Add current vertex w to W, add related edge to F
            visitedNodes.add(min.getLeft());
            utilisedEdges.add(min.getRight());

            // Remove added node from distances map
            distances.remove(min.getLeft());

            // Remove from notVisitedNodes
            notVisitedNodes.remove(min.getLeft());

            // Update distances for all v in V \ W
            for (Node<Integer> v : notVisitedNodes) {
                distances.replace(v, Dist(v));
            }

            System.err.println("\n===NEXT ITR===:");
            System.err.print("min: ");
            System.err.println(min.getRight().toString());
            System.err.print("visitedNodes: ");
            System.err.println(visitedNodes.toString());
            System.err.print("notVisitedNodes: ");
            System.err.println(notVisitedNodes.toString());
            System.err.print("utilisedEdges: ");
            System.err.println(utilisedEdges.toString());
            System.err.print("Distances: ");
            System.err.println(distances.toString());
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


    /**
     * Looks up node with shortest distance to mst (so far) and returns a
     * pair: (cloest vertex, corresponding edge)
     */
     private Pair<Node<Integer>, Edge<Integer, Integer>> findMinDistNode() {

         Pair<Node<Integer>, Edge<Integer, Integer>> closest = new Pair(notVisitedNodes.peekFirst(), distances.get(notVisitedNodes.peekFirst()));

         for (Node<Integer> node : notVisitedNodes) {
             // store current node
             Pair<Node<Integer>, Edge<Integer, Integer>> current = new Pair(node, distances.get(node));

             // if current node closer than closest, update closest
             if (current.getRight().getData() < closest.getRight().getData()) {
                 closest = current;
             }
         }
         return closest;
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

        // if v1==v2, return 0
        if (v1 == v2) {
            distance = 0;
        }
        else {
            // if edge exists, return edge.getData()
            // Else, will return Integer.MAX_VALUE
            for (Edge<Integer, Integer> edge : graph.getEdgesFrom(v1)) {
                if (edge.getTarget().equals(v2)) {
                    distance = edge.getData();
                    break;
                }
            }
        }

        return distance;
    }


    /**
     * Returns immediate distance between mst so far and given vertex in graph
     * NOTE Only checks for distance to last node added to the mst so far, and
     *      previous D(v)
     *
     * @param vertex the vertex to find shortest immediate distance to MST for
     */
    private Edge<Integer, Integer> Dist(Node<Integer> vertex) {

        // find d(vertex, node) and corresponding edge for last node added
        Edge<Integer, Integer> edgeToNewNode = new Edge(vertex, visitedNodes.getLast(), dist(vertex, visitedNodes.getLast()));

        // set shortestDist to distance to mst now (before updating)
        Edge<Integer, Integer> shortestDistEdge = distances.get(vertex);

        // if distance to newly added node is less, return that instead
        if (edgeToNewNode.getData() < shortestDistEdge.getData()) {
            shortestDistEdge = edgeToNewNode;
        }

        return shortestDistEdge;
    }


    /**
     * Returns minimum spanning tree for graph
     */
    public Graph<Integer, Integer> getMinimumSpanningTree() {
        return mst;
    }

}

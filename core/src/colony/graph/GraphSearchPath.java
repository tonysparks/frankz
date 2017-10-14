/*
 * see license.txt
 */
package colony.graph;

import java.util.List;

/**
 * Searches for the path from a given start node to an end node.
 * 
 * @author Tony
 *
 */
public interface GraphSearchPath<E> {

    /**
     * Finds the path from the start node to the end goal node.
     * 
     * @param start - starting point
     * @param goal - ending point
     * @return a list of nodes that link the start and end node, null if no path exists.
     */
    public List<GraphNode<E>> search(GraphNode<E> start, GraphNode<E> goal);
}

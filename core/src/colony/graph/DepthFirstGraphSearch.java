/*
 * 
 * see license.txt
 */
package colony.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * Performs a Depth-First search on the given graph.
 * 
 * @author Tony
 *
 */
public class DepthFirstGraphSearch<E> implements GraphSearch<E> {

    /*
     * (non-Javadoc)
     * @see leola.live.game.graph.GraphSearch#search(leola.live.game.graph.GraphNode, leola.live.game.graph.GraphSearch.SearchCondition)
     */
    public GraphNode<E> search(GraphNode<E> graph, SearchCondition<E> condition) {
        return search(graph, condition, new HashSet<GraphNode<E>>());
    }

    /**
     * Does a depth first search for the root node.
     * 
     * @param node
     * @param visited
     * @return
     */
    private GraphNode<E> search(GraphNode<E> node, SearchCondition<E> condition, Set<GraphNode<E>> visited ) {
        if ( !visited.contains(node)) {
            visited.add(node);
            
            /* If we reached the node of interest, return it */
            if ( condition.foundItem(node) ) {
                return node;
            }
            
            /* Search each neighbor for the root */
            Edges<E> edges = node.edges();
            for(int i = 0; i < edges.size(); i++) {                
                Edge<E> edge = edges.get(i);
                if(edge == null) {
                    continue;
                }
                

                /* Search the neighbor node */
                GraphNode<E> rightNode = edge.getRight();
                if ( rightNode !=null && !visited.contains(rightNode) ) {
                    return search(rightNode, condition, visited);
                }
            }    
        }        
        
        return null; /* Not found */
    }
}

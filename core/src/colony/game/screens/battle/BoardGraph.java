/*
 * see license.txt 
 */
package colony.game.screens.battle;

import java.util.List;

import colony.game.screens.battle.Board.Slot;
import colony.graph.Edge;
import colony.graph.Edges.Directions;
import colony.graph.GraphNode;
import colony.graph.GraphSearchPath;

/**
 * Graph of the game board
 * 
 * @author Tony
 *
 */
//
public class BoardGraph {

    private GraphNode<Slot>[][] graph;
        
    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public BoardGraph(Board board) {
        this.graph = new GraphNode[Board.HEIGHT][Board.WIDTH];
        
        board.forEachSlot( slot -> {
            this.graph[slot.y][slot.x] = new GraphNode<Slot>(slot);
        });
        
        // build edges of the graph
        for(int y = 0; y < Board.HEIGHT; y++) {
            for(int x = 0; x < Board.WIDTH; x++ ) {    
                GraphNode<Slot> node = graph[y][x];
                if(node==null) continue;
                
                GraphNode<Slot> nw = null;
                if(y>0 && x>0) nw = graph[y - 1][x - 1];
                
                GraphNode<Slot> n = null;
                if(y>0) n = graph[y - 1][x];
                
                GraphNode<Slot> ne = null;
                if(y>0 && x<Board.WIDTH-1) ne = graph[y - 1][x + 1];
                
                GraphNode<Slot> e = null;
                if(x<Board.WIDTH-1) e = graph[y][x + 1];
                
                GraphNode<Slot> se = null;
                if(y<Board.HEIGHT-1 && x<Board.WIDTH-1) se = graph[y + 1][x + 1];
                
                GraphNode<Slot> s = null;
                if(y<Board.HEIGHT-1) s = graph[y + 1][x];
                
                GraphNode<Slot> sw = null;
                if(y<Board.HEIGHT-1 && x>0) sw = graph[y + 1][x - 1];
                
                GraphNode<Slot> w = null;
                if(x>0) w = graph[y][x - 1];
                
                if (n != null) {
                    node.addEdge(Directions.N, new Edge<Slot>(node, n));
                }
                if (ne != null && (n!=null||e!=null)) {
                    node.addEdge(Directions.NE, new Edge<Slot>(node, ne));
                }
                if (e != null) {
                    node.addEdge(Directions.E, new Edge<Slot>(node, e));
                }
                if (se != null && (s!=null||e!=null)) {
                    node.addEdge(Directions.SE, new Edge<Slot>(node, se));
                }
                if (s != null) {
                    node.addEdge(Directions.S, new Edge<Slot>(node, s));
                }
                if (sw != null && (s!=null||w!=null)) {
                    node.addEdge(Directions.SW, new Edge<Slot>(node, sw));
                }
                if (w != null) {
                    node.addEdge(Directions.W, new Edge<Slot>(node, w));
                }
                if (nw != null && (n!=null||w!=null) ) {
                    node.addEdge(Directions.NW, new Edge<Slot>(node, nw));
                }
            }
        } 
    }
    

    
    public GraphNode<Slot> getNode(Slot slot) {
        return getNode(slot.x, slot.y);
    }
    
    public GraphNode<Slot> getNode(int x, int y) {
        return this.graph[y][x];
    }

    public List<GraphNode<Slot>> findPath(GraphSearchPath<Slot> search, Slot start, Slot end) {
        GraphNode<Slot> a = getNode(start);
        GraphNode<Slot> b = getNode(end);
        
        return search.search(a, b);        
    }
}

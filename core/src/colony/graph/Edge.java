/*
 * see license.txt
 */
package colony.graph;

/**
 * An {@link Edge} denotes the 'link' between two {@link GraphNode}s.  An {@link Edge} can have an associated actionPoints and data.
 * 
 * @author Tony
 *
 */
public class Edge<E> {

    private GraphNode<E> left;
    private GraphNode<E> right;
    private int weight;
      

    /**
     * Constructs a new {@link Edge} which links the left and right {@link GraphNode}s.
     * 
     * <p>
     * Defaults the weight to 0 if weight is not important.
     * 
     * @param left - the left {@link GraphNode}
     * @param right - the right {@link GraphNode}
     */
    public Edge(GraphNode<E> left, GraphNode<E> right) {
        this(left, right, 0);
    }
    
    /**
     * Constructs a new {@link Edge} which links the left and right {@link GraphNode}s.
     * 
     * @param left - the left {@link GraphNode}
     * @param right - the right {@link GraphNode}
     * @param weight - the actionPoints of this edge (used for searching).
     */
    public Edge(GraphNode<E> left, GraphNode<E> right, int weight) {
        this.left = left;
        this.right = right;
        this.weight = weight;
    }
    
    /**
     * @return the left
     */
    public GraphNode<E> getLeft() {
        return this.left;
    }
    /**
     * @param left the left to set
     */
    public void setLeft(GraphNode<E> left) {
        this.left = left;
    }
    /**
     * @return the right
     */
    public GraphNode<E> getRight() {
        return this.right;
    }
    /**
     * @param right the right to set
     */
    public void setRight(GraphNode<E> right) {
        this.right = right;
    }
    /**
     * @return the weight
     */
    public int getWeight() {
        return this.weight;
    }
    /**
     * @param weight the weight to set
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }
}

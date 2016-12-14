package visit;


/**
 * This interface is used by the DFSes, to decide what are the operations to perform.
 */
public interface VisitDFS {
    /**
     * The operation to perform at the beginning of the visit
     * @return the vertex where the visit should start.
     */
    public abstract int[] atStartVisit();
    
    /**
     * The operation to perform when an arc is visited.
     * @param v the tail of the arc
     * @param w the head of the arc
     * @return true if w has not been visited yet, false otherwise
     */
    public abstract boolean atVisitedArc(int v, int w);
    

    /**
     * The operation to perform when a vertex is visited.
     * @param v the vertex
     * @return 0 if the vertex has never been visited, 1 if the vertex has already been visited, but not the whole DFS subtree, 2 otherwise
     */
    public abstract short atVisitedVertex(int v);
    
    /**
     * The operation to perform after a vertex is visited.
     * @param v the vertex
     */
    public abstract void afterVisitedVertex(int v);
    
     /**
     * The operation to perform at the end of the visit.
     */
    public abstract void atEndVisit();
}


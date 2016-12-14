package visit;


/**
 * This interface is used by the BFSes, to decide what are the operations to perform.
 */
public interface VisitBFS {
    /**
     * The operation to perform at the beginning of the visit
     * @return the vertex where the visit should start.
     */
    public abstract int[] atStartVisit();
    
    /**
     * The operation to perform when an arc is visited.
     * @param v the tail of the arc
     * @param w the head of the arc
     * @return true if the visit must continue from w, false otherwise
     */
    public abstract boolean atVisitedArc(int v, int w);
    
     /**
     * The operation to perform at the end of the visit.
     */
    public abstract void atEndVisit();
}


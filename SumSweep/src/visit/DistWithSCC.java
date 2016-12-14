package visit;

/**
 * @author michele
 * Visits a graph and sets the maximum eccentricity in the SCC of the starting vertex.
 */
public class DistWithSCC extends Dist {

	private int[] scc;
	public int eccInSCC;
	
	public DistWithSCC(int nn, int start, int[] scc) {
		super(nn, start);
		this.scc = scc;
	}

	@Override
	public boolean atVisitedArc(int v, int w) {
		if(dist[w] == -1) {
	        dist[w] = dist[v] + 1;
	        far = w;
	        if (scc[w] == scc[start]) {
	        	eccInSCC = dist[w];
	        }
	        return true;
	    } else {
	    	return false;
	    }
	}
	
}

package visit;

import it.unimi.dsi.fastutil.ints.IntArrays;

/**
 * This class is used to compute all distances from a vertex v, together with the predecessors of each vertex in the BFS tree. The
 * two values are stored in the variables dist and pred.
 */
public class DistPred implements VisitBFS {

	public int dist[];
	public int far;
	public int pred[];
	public int start;
	
	
	public DistPred(int nn, int start) {
		dist = new int[nn];
		pred = new int[nn];
		this.start = start;
	}
	
	@Override
	public boolean atVisitedArc(int v, int w) {
		if(dist[w] == -1) {
			pred[w] = v;
	        dist[w] = dist[v] + 1;
	        if (dist[w] > dist[far]) {
	            far = w;
	        }
	        return true;
	    } else {
	    	return false;
	    }
	}

	@Override
	public int[] atStartVisit() {
		IntArrays.fill(dist, -1);
		dist[start] = 0;
		far = start;
		pred[start] = -1;
		int[] toReturn = {start};
		return toReturn;
	}
	
	@Override
	public void atEndVisit() {}
	
	public int getCentralVertex() {
		int central = far;
		for (int i = 0; i < dist[far] / 2; i++) {
			central = pred[central];
		}
		return central;
	}
	

}

package visit;

import it.unimi.dsi.fastutil.ints.IntArrays;

/**
 * This class is used to find all distances from a given vertex v, stored in variable dist. If dist[i] = -1, it means that v and i
 * are not connected.
 */
public class DistMultipleInSCC implements VisitBFS {

	public int dist[];
	public int scc[];
	protected int ecc;
	public int[] eccStart;
	protected int start;
	public int queue[];
	
	public void run(graph.Dir g, int pivot[], int toAvoid, int eccToAvoid, boolean forward) {
		for (int v : pivot) {
			if (scc[v] != scc[toAvoid]) {
				this.start = v;
				if (forward) {
					g.BFSGivenQ(this);
				} else {
					g.BBFSGivenQ(this);
				}
			} else {
				this.eccStart[scc[toAvoid]] = eccToAvoid;
			}
		}
	}
		
	public DistMultipleInSCC(int nn, int scc[]) {
		dist = new int[nn];
		this.scc = scc;
		this.eccStart = new int[nn];
		IntArrays.fill(dist, -1);
		queue = new int[nn + 2];
		queue[0] = 2;
		queue[1] = 2;
	}
	
	
	@Override
	public boolean atVisitedArc(int v, int w) {
		if(dist[w] == -1 && scc[w] == scc[v]) {
	        dist[w] = dist[v] + 1;
	        ecc = dist[w];
	        return true;
	    } else {
	    	return false;
	    }
	}

	@Override
	public int[] atStartVisit() {
		ecc = 0;

		if (dist[start] == -1) {
			dist[start] = 0;
			queue[queue[1]++] = start;
		}
		return queue;
	}
	
	@Override
	public void atEndVisit() {
		this.eccStart[this.scc[start]] = ecc;
	}

}

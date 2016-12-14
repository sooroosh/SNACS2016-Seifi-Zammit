package visit;

import it.unimi.dsi.fastutil.ints.IntArrays;

/**
 * This class is used to find all distances from a given vertex v, stored in variable dist. If dist[i] = -1, it means that v and i
 * are not connected.
 */
public class Dist implements VisitBFS {

	public int dist[];
	public int far;
	protected int start;
	
	public int[] getDistDistr() {
		int[] distDistr = new int[dist[far] + 1];
		for (int i = 0; i < dist.length; i++) {
			if (dist[i] != -1) {
				distDistr[dist[i]]++;
			}
		}
		return distDistr;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	
	public Dist(int nn, int start) {
		dist = new int[nn];
		this.start = start;
	}
	
	@Override
	public boolean atVisitedArc(int v, int w) {
		if(dist[w] == -1) {
	        dist[w] = dist[v] + 1;
	        far = w;
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
		int[] toReturn = {start};
		return toReturn;
	}
	
	@Override
	public void atEndVisit() {}

}

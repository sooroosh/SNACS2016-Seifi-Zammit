package visit;

import graph.Undir;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.webgraph.LazyIntIterator;

/**
 * This class is used to find the values needed for the undirected SumSweep is performed.
 */
public class SumSweepUnd implements VisitBFS {

	public int nn;
	public int dist[];
	public boolean firstBranch[];
	public int far;
	public Undir g;
	public int firstPath;
	public int newStart;
	public int start;
	
	public SumSweepUnd(Undir g, int start) {
		nn = g.getNN();
		dist = new int[g.getNN()];
		firstBranch = new boolean[g.getNN()];
		this.g = g;
		this.start = start;
	}
	
	@Override
	public boolean atVisitedArc(int v, int w) {
		if(dist[w] == -1) {
	        dist[w] = dist[v] + 1;
	        far = w;
	        firstBranch[w] = firstBranch[v] || firstBranch[w];
	        return true;
	    } else {
	    	return false;
	    }
	}

	@Override
	public int[] atStartVisit() {
		IntArrays.fill(dist, -1);
		BooleanArrays.fill(firstBranch, false);
		dist[start] = 0;
		firstPath = 0;
		
		if (g.getOutDeg(start) == 1) {
			int pred = start;
			LazyIntIterator adjS = g.getAdj(start);
			start = adjS.nextInt();
			dist[start] = ++firstPath;
			

			while (g.getOutDeg(start) == 2) {
				adjS = g.getAdj(start);

				if (adjS.nextInt() == pred) {
					pred = start;
					start = adjS.nextInt();
				} else {
					pred = start;
					start = g.getAdj(start).nextInt();
				}
				if (dist[start] >= 0) {
					break;
				}
				dist[start] = ++firstPath;
			}
		}
		if (g.getOutDeg(start) > 1) {
			firstBranch[g.getAdj(start).nextInt()] = true;
		}
		far = start;
		int toReturn[] = {start};
		return toReturn;
	}
	
	@Override
	public void atEndVisit() {}

}

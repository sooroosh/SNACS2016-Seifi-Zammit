package visit;

import it.unimi.dsi.fastutil.booleans.BooleanArrays;

/**
 * This class stores what vertices are visited by a BFS.
 */
public class ConnBFS implements VisitBFS {

	public boolean visited[];
	public int start[];
	
	
	public ConnBFS(int nn, int start) {
		visited = new boolean[nn];
		this.start = new int[1];
		this.start[0] = start;
	}

	
	public ConnBFS(int nn, int start[]) {
		visited = new boolean[nn];
		this.start = start;
	}
	
	
	@Override
	public boolean atVisitedArc(int v, int w) {
	    if (visited[w]) {
	        return false;
	    } else {
	        visited[w] = true;
	        return true;
	    }
	}

	@Override
	public int[] atStartVisit() {
		BooleanArrays.fill(visited, false);
		for (int v : start) {
			visited[v] = true;
		}
		return start;
	}
	
	@Override
	public void atEndVisit() {}
}

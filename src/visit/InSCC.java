package visit;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;

/**
 * @author michele
 * This class is used to perform a BFS inside a SCC.
 */
public class InSCC implements VisitBFS {
	int scc[];
	int pivot[];
	public int distFromMyPivot[];
	public IntArrayList distFromOtherPivot[];
	
	public InSCC(int[] scc, int pivot[]) {
		this.scc = scc;
		distFromOtherPivot = new IntArrayList[scc.length];
		distFromMyPivot = new int[scc.length];
		this.pivot = pivot;
		IntArrays.fill(distFromMyPivot, -1);
		
		for (int v : pivot) {
			distFromMyPivot[v] = 0;
		}
	}

	@Override
	public int[] atStartVisit() {
		
		for (int i = 0; i < distFromOtherPivot.length; i++) {
			distFromOtherPivot[i] = new IntArrayList();
		}
		
		return pivot;
	}

	@Override
	public boolean atVisitedArc(int v, int w) {

		if (distFromMyPivot[w] == -1 && scc[w] == scc[v]) {
			distFromMyPivot[w] = distFromMyPivot[v] + 1;
			return true;
		} else if (scc[w] != scc[v]) {
			distFromOtherPivot[w].add(scc[v]);
			distFromOtherPivot[w].add(distFromMyPivot[v] + 1);
		}
		return false;
		
	}

	@Override
	public void atEndVisit() {}
	
}

package visit;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;


/**
 * This class is used to find the connected components of an undirected graph. They are stored in the variable cc[], and cc[i] is
 * the component of vertex i.
 */
public class CC implements VisitBFS {

	public int cc[];
	public int currentCC = -1;
	public IntArrayList CCSizes;
	public int maxSizeCC = 0;
	private int start = -1;
	private int queue[];
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public CC(int nn) {
		cc = new int[nn];
		IntArrays.fill(cc, -1);
		CCSizes = new IntArrayList();
		queue = new int[nn + 2];
		queue[0] = 2;
		queue[1] = 2;
	}
	
	@Override
	public boolean atVisitedArc(int v, int w) {
	    if (cc[w] == -1) {
	    	cc[w] = cc[v];
	    	CCSizes.set(CCSizes.size() - 1, CCSizes.get(CCSizes.size() - 1) + 1);
	        return true;
	    } else {
	        return false;
	    }
	}

	@Override
	public int[] atStartVisit() {
		cc[start] = ++currentCC;
		CCSizes.add(1);
		queue[queue[1]++] = start;
		return queue;
	}
	
	@Override
	public void atEndVisit() {
		if(currentCC > 0) {
			if (CCSizes.get(maxSizeCC) < CCSizes.get(currentCC)) {
				maxSizeCC = currentCC;
			}
		}
	}	
	
}

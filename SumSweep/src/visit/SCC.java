package visit;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;

import java.util.Stack;



/**
 * This class is used to compute the strongly connected components of a directed graph, through a DFS. The components are stored in the
 * variable cc: cc[i] contains the connected component of vertex i.
 */
public class SCC implements VisitDFS {
	public int cc[];
	public int index[];
	public int father[];
	private int lowlink[];
	private int currentIndex;
	private int currentCC = 0;
	private Stack<Integer> stack;
	public int maxSizeCC;
	public IntArrayList sizes;
	private int start;
	boolean isInS[];
	boolean concluded[];

	public SCC(int nn) {
		index = new int[nn];
		lowlink = new int[nn];
		cc = new int[nn];
		stack = new Stack<Integer>();
		currentIndex = 1;
		sizes = new IntArrayList();
		father = new int[nn];
		IntArrays.fill(father, -1);
		isInS = new boolean[nn];
		concluded = new boolean[nn];
	}


	public int getNCC() {
		return currentCC;
	}

	public void setStart(int start) {
		this.start = start;
	}

	@Override
	public int[] atStartVisit() {
		father[start] = start;
		int[] toReturn = {start};
		return toReturn;
	}


	@Override
	public short atVisitedVertex(int v) {
		if (index[v] == 0) {
			index[v] = currentIndex;
			lowlink[v] = currentIndex++;
			stack.push(v);
			isInS[v] = true;
			return 0;			
		} else if (!concluded[v]) {
			return 1;
		} else {
			return 2;
		}
	}

	@Override
	public void afterVisitedVertex(int v) {
		concluded[v] = true;
	
		lowlink[father[v]] = Math.min(lowlink[father[v]], lowlink[v]);
		if (lowlink[v] == index[v])  {
			int w = -1;
			int currentCCSize = 0;
			while (w != v) {
				if (w == 3 || w == 4) {
					System.out.print("");
				}
				currentCCSize++;
				w = stack.pop();
				isInS[w] = false;
				cc[w] = currentCC;
				if (currentCC != this.maxSizeCC && currentCCSize > sizes.get(maxSizeCC)) {
					this.maxSizeCC = currentCC;
				}
			}
			sizes.add(currentCCSize);
			currentCC++;
		}
	}	

	@Override
	public void atEndVisit() {}

	@Override
	public boolean atVisitedArc(int v, int w) {
		if (index[w] == 0) {
			father[w] = v;
			return true;
		} else if (isInS[w]) {
			lowlink[v] = Math.min(lowlink[v], index[w]);
		}
		return false;
	}

}

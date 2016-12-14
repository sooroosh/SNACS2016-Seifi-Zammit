package adjlist;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.webgraph.LazyIntIterator;
import it.unimi.dsi.webgraph.LazyIntIterators;
/**
 * An implementation of a graph through its adjacency list. Very fast when it is necessary to iterate over all neighbors, but with slow
 * check if an arc exists (O(deg(v))), slow file open, memory consuming. 
 */
public class AdjListArray extends Abstract {

	protected IntArrayList adj[];
	protected int nn = 0;
	protected long ne = 0;
	
	
	/**
	 * Creates a graph of size nn with no edge.
	 * @param nn the requested size.
	 */
	public AdjListArray(int nn) {
		this.ne = 0;
		this.createEmptyGraph(nn);
	}
	
	/**
	 * Creates a graph copying the graph provided.
	 * @param g the graph to copy.
	 */
	protected AdjListArray(AdjListArray g) {
		this.adj = g.adj;
		this.nn = g.nn;
		this.ne = g.ne;
	}
	
	
	/**
	 * Loads a graph from a Webgraph file.
	 * @param filename the file path.
	 */
	public static AdjListArray loadFromWebgraph(String filename) {
		return (AdjListWebGraph.loadFromFile(filename)).toAdjListGraph();
	}
	
	
	
	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getNN()
	 */
	@Override
	public int getNN() {
		return nn;
	}
	
	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getNE()
	 */
	@Override
	public long getNE() {
		return ne;
	}
	
	
	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getAdj(int)
	 */
	@Override
	public LazyIntIterator getAdj(int v) {
		return LazyIntIterators.wrap(adj[v].toIntArray());
	}
	
	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getDeg(int)
	 */
	@Override
	public int getDeg(int v) {
		return adj[v].size();
	}

	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#addArc(int, int)
	 */
	@Override
	public void addArc(int v, int w) {
		ne++;
		adj[v].add(w);
	}

	
	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getCopyForMultithread()
	 */
	@Override
	public Abstract getCopyForMultithread() {
		return this;
	}
	

	/**
	 * Creates an empty graph with nn nodes.
	 * @param nn the number of nodes.
	 */
	protected void createEmptyGraph(int nn) {
		ne = 0;
		this.nn = nn;
		adj = new IntArrayList[nn];

		for (int i = 0; i < nn; i++) {
			adj[i] = new IntArrayList();
		}
	}

	/* (non-Javadoc)
	 * @see adjlist.Abstract#permuteVertices(int[])
	 */
	@Override
	public void permuteVertices(int[] permutation) {
		IntArrayList newAdj[] = new IntArrayList[nn];
		for (int i = 0; i < nn; i++) {
			newAdj[i] = new IntArrayList();
		}
		for (int i = 0; i < nn; i++) {
			for (int v : adj[i]) {
				newAdj[permutation[i]].add(permutation[v]);
			}
		}
		this.adj = newAdj;
	}
}

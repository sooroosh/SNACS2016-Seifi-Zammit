package adjlist;

import java.io.IOException;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.ImmutableSubgraph;
import it.unimi.dsi.webgraph.LazyIntIterator;
import it.unimi.dsi.webgraph.Transform;

/**
 * This class is a wrapper that implements a graph using the WebGraph library (see http://webgraph.di.unimi.it/). 
 * It is slower than an adjacency list implementation, but it has very low memory requirements (since graphs are not loaded into RAM memory).
 */
public class AdjListWebGraph extends Abstract {

	ImmutableGraph g;
	
	/**
	 * Creates an AdjListWebGraph from an ImmutableGraph (see  http://webgraph.di.unimi.it/ for methods to load an ImmutableGraph).
	 * @param g the input ImmutableGraph.
	 */
	private AdjListWebGraph(ImmutableGraph g) {
		this.g = g;
	}
		
	/**
	 * Loads a webgraph from a file.
	 * @param filename the file name (example: folder/input), without extension. It is assumed that the three corresponding files 
	 * (input.graph, input.offset, input.properties) are in the same folder.
	 * @return the webgraph loaded.
	 */
	public static AdjListWebGraph loadFromFile(String filename) {
		AdjListWebGraph g = null;
		
		try {
			g = new AdjListWebGraph(BVGraph.load(filename));
		} catch (IOException e) {
			System.err.println("File " + filename + " does not exist, or it is not in the correct format.");
		}
		return g;
	}
	
	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getInducedSubgraph(boolean[])
	 */
	@Override
	public Abstract getInducedSubgraph(boolean elements[]) {
		IntArrayList newNumbers = new IntArrayList();
		
		for (int i = 0; i < getNN(); i++) {
			if (elements[i]) {
				newNumbers.add(i);
			}
		}
		ImmutableSubgraph s = new ImmutableSubgraph(g, newNumbers.toIntArray());
				
		return new AdjListWebGraph(s);
	}

	
	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getNN()
	 */
	@Override
	public int getNN() {
		return g.numNodes();
	}

	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getNE()
	 */
	@Override
	public long getNE() {
		return g.numArcs();
	}

	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getAdj(int)
	 */
	@Override
	public synchronized LazyIntIterator getAdj(int v) {
		return g.successors(v);
	}

	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getDeg(int)
	 */
	@Override
	public int getDeg(int v) {
		return g.outdegree(v);
	}

	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#addArc(int, int)
	 */
	@Override
	public void addArc(int s, int t) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Webgraphs are immutable!");
	}

	/* (non-Javadoc)
	 * @see adjlist.AbstractAdjList#getCopyForMultithread()
	 */
	@Override
	public Abstract getCopyForMultithread() {
		AdjListWebGraph g = new AdjListWebGraph(this.g.copy());
		return g;
	}
	
	/**
	 * Transforms this directed graph into an undirected one.
	 */
	@Override
	public void symmetrize() {
		g = Transform.symmetrize(g);
	}
	
	/* (non-Javadoc)
	 * @see adjlist.Abstract#clone()
	 */
	@Override
	public AdjListWebGraph clone() {
		return new AdjListWebGraph(g.copy());
	}

	/* (non-Javadoc)
	 * @see adjlist.Abstract#permuteVertices(int[])
	 */
	@Override
	public void permuteVertices(int[] permutation) {
		throw new UnsupportedOperationException("This operation is not supported for WebGraphs!");
	}
}

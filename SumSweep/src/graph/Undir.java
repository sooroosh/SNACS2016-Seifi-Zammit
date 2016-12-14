package graph;

import adjlist.Abstract;
import graph.LoadMethods;
import adjlist.AdjListArray;
import utilities.Utilities;
import visit.CC;

/**
 * This is the main class used to work with undirected graphs.
 */
public class Undir extends GeneralGraph {

	/* (non-Javadoc)
	 * @see graph.GeneralGraph#getElapsedTime()
	 */
	@Override
	public long getElapsedTime() {return elapsedTime;}

	/**
	 * Creates a new undirected graph. WARNING: it is not checked that g is undirected.
	 * @param g an undirected graph.
	 */
	public Undir(Abstract g) {
		this.dir = g;
	}
	
	/**
	 * Creates a new undirected graph from an existing one.
	 * @param g an undirected graph.
	 */
	public Undir(Undir g) {
		this.dir = g.dir;
	}
	
	/**
	 * Creates a new empty graph with nn vertices. WARNING: the operation may fail depending on the implementation.
	 * @param nn the number of vertices
	 * @param type the graph implementation
	 */
	protected Undir(int nn, GraphTypes type) {
		if (type == GraphTypes.ADJLIST) {
			dir = new AdjListArray(nn);
		} else if (type == GraphTypes.WEBGRAPH) {
			throw new UnsupportedOperationException("You cannot create an empty webgraph and populate it! Please, load it from a file."); 
		}
	}

	/**
	 * Creates a new empty graph with nn vertices. WARNING: the operation may fail depending on the implementation.
	 * @param nn the number of vertices.
	 */
	protected Undir(int nn) {
		this(nn, Utilities.graphType);
	}

	/**
	 * Loads an undirected graph.
	 * @param filename the file where the graph is stored.
	 * @param type the implementation required
	 */	
	public static Undir load(String filename, GraphTypes type, LoadMethods method) {
		return (Undir) load(filename, false, type, method);
	}
	/**
	 * Loads an undirected graph.
	 * @param filename the file where the graph is stored.
	 * @param type the implementation required
	 */
	public static Undir load(String filename, GraphTypes type) {
		return load(filename, type, Utilities.loadMethod);
	}

	/**
	 * Loads an undirected graph.
	 * @param filename the file where the graph is stored.
	 */
	public static Undir load(String filename) {
		return load(filename, Utilities.graphType);
	}

	/**
	 * Adds an arc. WARNING: depending on the implementation, this operation may fail.
	 * @param s the source
	 * @param t the target
	 */
	public void addArc(int s, int t) throws UnsupportedOperationException {
		if (s != t && !this.arcExists(s, t)) {
			dir.addArc(s, t);
			dir.addArc(t, s);
		}
	}

	@Override
	public long getNE() {
		return dir.getNE() / 2;
	}
	
	
	/**
	 * Removes from this graph every node which is not in the biggest connected component.
	 */
	public void transformIntoBiggestCC() {
		CC visit = this.findCC();
		boolean elements[] = new boolean[getNN()];

		for (int i = 0; i < getNN(); i++) {
			elements[i] = (visit.cc[i] == visit.maxSizeCC);
		}
		this.transformIntoInducedSubgraph(elements);
	}

	/**
	 * Removes from this graph every node which is not in the biggest connected component.
	 */
	public Undir getBiggestCC() {
		CC visit = this.findCC();
		boolean elements[] = new boolean[getNN()];

		for (int i = 0; i < getNN(); i++) {
			elements[i] = (visit.cc[i] == visit.maxSizeCC);
		}
		return getInducedSubgraph(elements);
	}

	/**
	 * Returns an induced subgraph.
	 * @param elements the set of elements to include in the subgraph
	 * @return the induced subgraph.
	 */
	public Undir getInducedSubgraph(boolean elements[]) {
		return new Undir(dir.getInducedSubgraph(elements));
	}

	/**
	 * Transforms this graph into an induced subgraph specified by the parameter elements.
	 * @param elements
	 */
	public void transformIntoInducedSubgraph(boolean elements[]) {
		dir = dir.getInducedSubgraph(elements);
	}


	/**
	 * Finds the connected components of this graph.
	 * @return a visit.SCC containing all information on the connected components
	 */
	public visit.CC findCC() {
		visit.CC visit = new visit.CC(getNN());

		for (int i = 0; i < getNN(); i++) {
			if (visit.cc[i] == -1) {
				visit.setStart(i);
				this.BFSGivenQ(visit);
			}
		}
		return visit;
	}

	/* (non-Javadoc)
	 * @see graph.GeneralGraph#getCopyForMultiThread()
	 */
	@Override
	public GeneralGraph getCopyForMultiThread() {
		return new Undir(dir.getCopyForMultithread());
	}
	

	/* (non-Javadoc)
	 * @see graph.GeneralGraph#asciiGraphOutput()
	 */
	@Override
	public String asciiGraphOutput() {
		return "# Undirected graph (each edge is stored in both directions).";
	}
}


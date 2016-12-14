package graph;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.webgraph.LazyIntIterator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import adjlist.Abstract;
import adjlist.AdjListArray;
import graph.LoadMethods;

import com.martiansoftware.jsap.JSAPException;

import utilities.Utilities;
import visit.CC;
import visit.VisitBFS;
import visit.VisitDFS;

/**
 * This class is a wrapper that contains the main routines related to directed graphs. It contains two graphs: the forward one and the
 * backward one.
 * When working with directed graphs, this is the class that should be used (rather than low-level classes like BaseGraph).
 */
public class Dir extends GeneralGraph {
	private Abstract rev;
	
	/**
	 * Return a graph obtained from this by reversing all edges in the graph.
	 */
	public Dir getReverse() {
		return new Dir(rev, dir);
	}
	
	/**
	 * Reverses all edges in the graph.
	 */
	public void reverse() {
		Abstract temp = this.dir;
		this.dir = this.rev;
		this.rev = temp;
	}
	
	/**
	 * Creates an empty directed graph with nn nodes. 
	 * @param nn the number of nodes
	 * @param type the graph type
	 */
	public Dir(int nn, GraphTypes type) {
		if (type == GraphTypes.ADJLIST) {
			this.dir = new AdjListArray(nn);
			this.rev = new AdjListArray(nn); 
		} else if (type == GraphTypes.WEBGRAPH) {
			throw new UnsupportedOperationException("You cannot create an empty webgraph and populate it! Please, load it from a file."); 
		}
	}
	
	/**
	 * Copies the directed graph provided. 
	 * @param graph the graph provided.
	 */
	public Dir(Dir graph) {
		this.dir = graph.dir;
		this.rev = graph.rev;
	}
	
	/**
	 * Creates a directed graph from the forward and backward graph. Consistency is not checked. 
	 * @param dir the forward graph
	 * @param rev the backward graph
	 */
	protected Dir(Abstract dir, Abstract rev) {
		this.dir = dir;
		this.rev = rev;
	}
	
	
	/**
	 * Permutes the vertices of the graph
	 * @param permutation the permutation required.
	 */
	@Override
	public void permuteVertices(int[] permutation) {
		dir.permuteVertices(permutation);
		rev.permuteVertices(permutation);
	}


	/**
	 * Loads an undirected graph.
	 * @param filename the file where the graph is stored.
	 * @param type the implementation required
	 * @param method the method needed to load the graph.
	 */
	public static Dir load(String filename, GraphTypes type, LoadMethods method) {
		return (Dir) load(filename, true, type, method);
	}
	/**
	 * Loads an undirected graph.
	 * @param filename the file where the graph is stored.
	 * @param type the implementation required
	 */
	public static Dir load(String filename, GraphTypes type) {
		return load(filename, type, Utilities.loadMethod);
	}

	
	/**
	 * Loads an undirected graph.
	 * @param filename the file where the graph is stored.
	 */
	public static Dir load(String filename) {
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
			rev.addArc(t, s);
		}
	}

	
	/**
	 * Adds an arc. WARNING: depending on the implementation, this operation may fail.
	 * @param s the source
	 * @param t the target
	 */
	public void addArcNoCheck(int s, int t) throws UnsupportedOperationException {
		dir.addArc(s, t);
		rev.addArc(t, s);
	}
	/* (non-Javadoc)
	 * @see graph.HighLevelGraphOK#exportAsWebgraph(java.lang.String)
	 */
	@Override
	public void exportAsWebgraph(String output) {
		try {
			dir.exportAsWebgraph(Utilities.webGraphPath + output);
			rev.exportAsWebgraph(Utilities.webGraphPath + output + "_rev");
		} catch (SecurityException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException
				| ClassNotFoundException | InstantiationException | IOException
				| JSAPException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 
	 * @param v a vertex
	 * @return an iterator on the vertices incident to v.
	 */
	public LazyIntIterator getInc(int v) {
		return rev.getAdj(v);
	}


	/**
	 * 
	 * @param v a vertex
	 * @return the in-degree of v.
	 */
	public int getInDeg(int v) {
		return rev.getDeg(v);
	}


	/**
	 * Performs a backward BFS (see BaseGraph.BFS for more information).
	 * @param visit
	 */
	public void BBFS(VisitBFS visit) {
		long t = System.currentTimeMillis();
		rev.BFS(visit);
		elapsedTime = System.currentTimeMillis() - t;
	}


	

	/**
	 * Performs a backward DFS (see BaseGraph.DFSWithStack for more information).
	 * @param visit
	 */
	public void BDFS(VisitDFS visit) {
		long t = System.currentTimeMillis();
		rev.DFS(visit);
		elapsedTime = System.currentTimeMillis() - t;
	}


	/**
	 * Finds the strongly connected components of this graph.
	 * @return a visit.SCC containing all information on the strongly connected components
	 */
	public visit.SCC findSCC() {
		long t = System.currentTimeMillis();
		visit.SCC visit = new visit.SCC(getNN());
		for (int v = 0; v < getNN(); v++) {
			if (visit.index[v] == 0) {
				visit.setStart(v);
				DFS(visit);
			}
		}
		elapsedTime = System.currentTimeMillis() - t;
		return visit;
	}

	/**
	 * Finds the weakly connected components of this graph.
	 * @return a visit.SCC containing all information on the strongly connected components
	 */
	public Undir toUndirectedGraph() {
		Abstract newDir = dir.clone();
		newDir.symmetrize();
		return new Undir(newDir);
	}


	/* (non-Javadoc)
	 * @see graph.HighLevelGraphOK#getInducedSubgraph(boolean[])
	 */
	@Override
	public GeneralGraph getInducedSubgraph(boolean elements[]) {
		return new Dir(dir.getInducedSubgraph(elements), rev.getInducedSubgraph(elements));
	}

	/**
	 * Removes from this graph every node which is not in the biggest weakly connected component.
	 */
	public void transformIntoBiggestWCC() {
		visit.CC visit = this.findWCC();
		boolean elements[] = new boolean[getNN()];

		for (int i = 0; i < getNN(); i++) {
			elements[i] = (visit.cc[i] == visit.maxSizeCC);
		}
		this.transformIntoInducedSubgraph(elements);
	}

	public CC findWCC() {
		return this.toUndirectedGraph().findCC();
	}

	/**
	 * Removes from this graph every node which is not in the biggest strongly connected component.
	 */
	public void transformIntoBiggestSCC() {
		visit.SCC visit = this.findSCC();
		boolean elements[] = new boolean[getNN()];

		for (int i = 0; i < getNN(); i++) {
			elements[i] = (visit.cc[i] == visit.maxSizeCC);
		}
		this.transformIntoInducedSubgraph(elements);
	}


	/* (non-Javadoc)
	 * @see graph.HighLevelGraphOK#transformIntoInducedSubgraph(boolean[])
	 */
	@Override
	public void transformIntoInducedSubgraph(boolean elements[]) {
		dir = dir.getInducedSubgraph(elements);
		rev = rev.getInducedSubgraph(elements);
	}



	/**
	 * @return the vertex with maximum in-degree.
	 */
	public int maxInDegVert() {
		return rev.maxDegVert();
	}

	
	/* (non-Javadoc)
	 * @see graph.GeneralGraph#getCopyForMultiThread()
	 */
	@Override
	public GeneralGraph getCopyForMultiThread() {
		return new Dir(dir.getCopyForMultithread(), rev.getCopyForMultithread());
	}

	/* (non-Javadoc)
	 * @see graph.GeneralGraph#asciiGraphOutput()
	 */
	@Override
	public String asciiGraphOutput() {
		return "# Directed graph";
	}

	
	protected int[] getNewNumbers(int groups[], int nGroups) {
		int newNumbers[] = new int[groups.length];
		int sizes[] = new int[nGroups];
		int cumulativeSizes[] = new int[nGroups];
		cumulativeSizes[0] = 0;
		
		for (int i = 0; i < groups.length; i++) {
			sizes[groups[i]]++;
		}
		
		for (int i = 1; i < cumulativeSizes.length; i++) {
			cumulativeSizes[i] = cumulativeSizes[i - 1] + sizes[i - 1];
		}
		
		for (int v = 0; v < groups.length; v++) {
			newNumbers[v] = cumulativeSizes[groups[v]]++;
		}
		
		return newNumbers;
	}
	
	/**
	 * Collapses all vertices in the same group, and returns the obtained graph. Furthermore, vertices in this graph are sorted according 
	 * to the groups
	 * @param groups an array containing in position i the group of vertex i
	 * @param nGroups the number of groups
	 * @return a directed graph obtained by collapsing vertices
	 */
	public Dir collapseVertices(int groups[], int nGroups) {
		Dir g = new Dir(nGroups, GraphTypes.ADJLIST);
		int newNumbers[] = this.getNewNumbers(groups, nGroups);
		int newGroups[] = new int[newNumbers.length];
		this.permuteVertices(newNumbers);
		boolean alreadyAdded[] = new boolean[nGroups];
		IntArrayList toReset = new IntArrayList();
		int v = 0, w;
		LazyIntIterator iter;
		for (int i = 0; i < newNumbers.length; i++) {
			newGroups[newNumbers[i]] = groups[i];
		}
		
		for (int i = 0; i < newGroups.length; i++) {
			while (v < newGroups.length && newGroups[v] == i) {
				iter = getAdj(v);
				while ((w = iter.nextInt()) != -1) {
					if (newGroups[v] != newGroups[w] && !alreadyAdded[newGroups[w]]) {
						g.addArcNoCheck(newGroups[v], newGroups[w]);
						alreadyAdded[newGroups[w]] = true;
						toReset.add(newGroups[w]);
					}
				}
				v++;
			}
			while (toReset.size() > 0) {
				alreadyAdded[toReset.pop()] = false;
			}
		}
		return g;
	}
	
	
	/**
	 * Performs a BBFS (see BaseGraph.BFS for more information).
	 * @param visit
	 */
	public void BBFSGivenQ(VisitBFS visit) {
		long t = System.currentTimeMillis();
		rev.BFSGivenQ(visit);
		elapsedTime = System.currentTimeMillis() - t;
	}
}

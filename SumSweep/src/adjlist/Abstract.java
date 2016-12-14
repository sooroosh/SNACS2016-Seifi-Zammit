package adjlist;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.martiansoftware.jsap.JSAPException;

import utilities.GraphConverter;
import visit.VisitBFS;
import visit.VisitDFS;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.webgraph.LazyIntIterator;

/**
 * The adjacency list is the most important part in the implementation of a graph. This abstract class defines what
 * an adjacency list is, i.e. what routines an implementation used by this project must provide: 
 * a number of nodes, a number of edges, and for each node a degree and a set of neighbors. It also requires
 * a function to copy the graph, in case multithreaded access is needed.
 * This class also contains some basic algorithms (mainly visits), common to all kinds of graphs (directed and undirected).
 */


public abstract class Abstract {

	/**
	 * @return The number of nodes in the graph.
	 */
	public abstract int getNN();

	/**
	 * @return The number of edges in the graph.
	 */
	public abstract long getNE();

	/**
	 * @param v
	 * @return The degree of vertex v.
	 */
	public abstract int getDeg(int v);

	/**
	 * @param v
	 * @return An iterator over all neighbors of v.
	 */
	public abstract LazyIntIterator getAdj(int v);

	public void symmetrize() {
		LazyIntIterator iter;
		int w;
		for (int v = 0; v < getNN(); v++) {
			iter = getAdj(v);
			while((w = iter.nextInt()) != -1) {
				//if (!arcExists(w,v)) {
					addArc(w, v);
				//}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public Abstract clone() {
		Abstract copy;
		LazyIntIterator iter;
		int w;
		
		try {
			copy = getClass().getDeclaredConstructor(int.class).newInstance(getNN());
			for (int v = 0; v < getNN(); v++) {
				iter = getAdj(v);
				while((w = iter.nextInt()) != -1) {
					copy.addArc(v, w);
				}
			}
			return copy;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Adds an arc (s,t) to the graph. Depending on the graph implementation, this operation might not be supported.
	 * @param s
	 * @param t
	 * @throws UnsupportedOperationException
	 */
	public abstract void addArc(int s, int t) throws UnsupportedOperationException;

	/**
	 * @return The vertex with maximum degree.
	 */
	public int maxDegVert() {
		int max = 0;
		for (int v = 1; v < getNN(); v++) {
			if (getDeg(v) > getDeg(max)) {
				max = v;
			}
		}
		return max;
	}




	/**
	 * Check if an arc is in the graph. This operation checks all neighbors of v, needing O(|adj(v)|) time: depending on the
	 * implementation, it might be overridden to obtain better performances.
	 * @param v
	 * @param w
	 * @return true if the arc exists.
	 */
	public boolean arcExists(int v, int w) {

		LazyIntIterator iter = getAdj(v);
		int y;
		while ((y = iter.nextInt()) != -1) {
			if (y == w) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Inverts a graph G, obtaining G'. In other words, for each arc (v,w) in G, G' contains an arc (w,v).
	 * @return The inverted graph.
	 */
	public Abstract reverse() {
		Abstract rev = new AdjListArray(getNN());
		LazyIntIterator iter;
		int w;

		for (int v = 0; v < getNN(); v++) {
			iter = getAdj(v);
			while((w = iter.nextInt()) != -1) {
				try {
					rev.addArc(w, v);
				} catch (UnsupportedOperationException e) {
					e.printStackTrace();
				}
			}
		}
		return rev;
	}


	/**
	 * Runs a BFS, performing at each step the routines specified by visit.
	 * @param visit a class containing all routines used by the BFS.
	 */
	public void BFS(VisitBFS visit) {

		LazyIntIterator iter;

		int v;
		int w;

			int q[] = visit.atStartVisit();
			int startQ = 0, endQ = q.length;
			q = IntArrays.ensureCapacity(q, getNN());

			while (endQ > startQ) {
				v = q[startQ++];
				iter = getAdj(v);

				while((w = iter.nextInt() ) != -1) {

					if (visit.atVisitedArc(v, w)) {
						q[endQ++] = w;
					}
				} 
			}

		visit.atEndVisit();
	}
	
	/**
	 * Runs a BFS, performing at each step the routines specified by visit.
	 * @param visit a class containing all routines used by the BFS.
	 */
	public void BFSGivenQ(VisitBFS visit) {

		LazyIntIterator iter;

		int v;
		int w;

			int q[] = visit.atStartVisit();
			q = IntArrays.ensureCapacity(q, getNN() + 2);

			while (q[1] > q[0]) {
				v = q[q[0]++];
				iter = getAdj(v);

				while((w = iter.nextInt() ) != -1) {

					if (visit.atVisitedArc(v, w)) {
						q[q[1]++] = w;
					}
				} 
			}
		
		visit.atEndVisit();
	}


	/**
	 * Performs a DFS using a stack. At each steps, it runs the routines specified by the visit.
	 * @param visit a class containing all routines used by the DFS.
	 */
	public void DFS(VisitDFS visit) {

		LazyIntIterator iter;

		int v, w;
		
		int start[] = visit.atStartVisit();
			IntArrayList stack = new IntArrayList();
			
			for (int k = 0; k < start.length; k++) {
				stack.add(start[k]);
			}

			while (stack.size() > 0) {
				v = stack.top();
				short passages = visit.atVisitedVertex(v);
				if (passages == 0) {
					iter = getAdj(v);
					while((w = iter.nextInt() ) != -1) {
						if (visit.atVisitedArc(v, w)) {
							stack.push(w);
						}
					}
				}
				else if (passages == 1) {
					visit.afterVisitedVertex(v);
					stack.pop();
				} else {
					stack.pop();
				}
			}
		
		visit.atEndVisit();
	}
	

	/**
	 * Returns an induced subgraph containing all vertices specified by the array elements. In other words, the 
	 * returned graph contains all vertices v such that elements[v]=true and all arcs (v,w) such that both
	 * elements[v] and elements[w] are true.
	 * @param elements a boolean array defining which vertices to include
	 * @return The induced subgraph.
	 */
	public Abstract getInducedSubgraph(boolean[] elements) {

		int newNumbers[] = new int[getNN()];
		int newNN = 0, w;
		LazyIntIterator iter;
		for (int i = 0; i < getNN(); i++) {
			if (elements[i]) {
				newNumbers[i] = newNN++;
			}
		}
		AdjListArray g = new AdjListArray(newNN);

		for (int i = 0; i < getNN(); i++) {
			if (elements[i]) {
				iter = this.getAdj(i);
				while((w = iter.nextInt() ) != -1) {
					if (elements[w]) {
						g.addArc(newNumbers[i], newNumbers[w]);
					}
				}
			}
		}

		return g;
	}

	/**
	 * @return A copy of the graph, if necessary for multiple access.
	 */
	public abstract Abstract getCopyForMultithread();



	/**
	 * Converts this adjacency list into an array-implemented one.
	 * @return the new adjacency list.
	 */
	public AdjListArray toAdjListGraph() {
		int w;
		LazyIntIterator iter;
		AdjListArray toReturn = new AdjListArray(getNN());

		for (int v = 0; v < getNN(); v++) {
			iter = getAdj(v);
			while((w = iter.nextInt()) != -1) {
				toReturn.addArc(v, w);
			}
		}
		return toReturn;
	}


	/**
	 * Exports the current graph as an ASCII graph, and stores it using the BufferedWriter specified.
	 * @param bw a buffered writer pointing the file where the graph has to be stored.
	 * @throws IOException 
	 */
	public void exportAsAscii(BufferedWriter bw) throws IOException {
		LazyIntIterator iter;
		int w;

		for (int v = 0; v < getNN(); v++) {
			iter = getAdj(v);
			while((w = iter.nextInt() ) != -1) {
				bw.write(v + "\t" + w + "\n");
			}
		}
		bw.close();
	}

	/**
	 * Exports the current graph as a webgraph, and stores it in the file specified.
	 * @param output the file where the webgraph is stored.
	 * @throws IOException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws JSAPException
	 */
	public void exportAsWebgraph(String output) throws IOException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, JSAPException {
		BufferedWriter bw = new BufferedWriter(new FileWriter("tmp/_temp.txt"));
		LazyIntIterator iter;
		int w;
		for (int v = 0; v < getNN(); v++) {
			iter = getAdj(v);
			while((w = iter.nextInt() ) != -1) {
				bw.write(v + "\t" + w + "\n");
			}
		}
		bw.close();
		GraphConverter.convertAsciiToWebgraphImplicit("tmp/_temp.txt", output);
		File toRemove = new File("tmp/_temp.txt");
		toRemove.delete();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String result = "NN: " + getNN() + " NE: " + getNE() + ".\n";
		LazyIntIterator iter;
		int i,j;
		for (i = 0; i < getNN(); i++) {
			iter = getAdj(i);
			result += "Neighbors of " + i + " (degree " + getDeg(i) + "): ";
			while ((j = iter.nextInt()) != -1) {
				result += j + ", ";
			}
			result = result + "\n";
		}
		result = result.substring(0, result.lastIndexOf(',')) + ".";
		return result;
	}


	/**
	 * Permutes all vertices, so that after the operation vertex i becomes permutation[i].
	 * @param permutation the permutation required
	 */
	public abstract void permuteVertices(int[] permutation);
	
}

package graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.io.BufferedWriter;

import adjlist.Abstract;
import adjlist.AdjListArray;
import adjlist.AdjListWebGraph;
import graph.LoadMethods;


import com.martiansoftware.jsap.JSAPException;

import it.unimi.dsi.webgraph.LazyIntIterator;
import utilities.Utilities;
import visit.VisitBFS;
import visit.VisitDFS;

/**
 * This abstract class contains all routines that are common to directed and undirected graphs.
 */
public abstract class GeneralGraph {
	public Abstract dir;
	protected long startTime;
	protected long elapsedTime;

	/**
	 * @return the elapsed time in the last operation.
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}

	
	/**
	 * Permutes the vertices of the graph
	 * @param permutation the permutation required.
	 */
	public void permuteVertices(int[] permutation) {
		dir.permuteVertices(permutation);
	}

	/**
	 * @return the number of nodes.
	 */
	public int getNN() {
		return dir.getNN();
	}

	/**
	 * @return the number of odes.
	 */
	public String toString() {
		return dir.toString();
	}
	/**
	 * @return the number of edges.
	 */
	public long getNE() {
		return dir.getNE();
	}

	/**
	 * @return the vertex with maximum out-degree.
	 */
	public int maxOutDegVert() {
		return dir.maxDegVert();
	}


	/**
	 * @param v a vertex
	 * @return the degree of v.
	 */
	public int getOutDeg(int v) {
		return dir.getDeg(v);
	}


	/**
	 * Performs a BFS (see BaseGraph.BFS for more information).
	 * @param visit
	 */
	public void BFS(VisitBFS visit) {
		long t = System.currentTimeMillis();
		dir.BFS(visit);
		elapsedTime = System.currentTimeMillis() - t;
	}
	
	/**
	 * Performs a BFS (see BaseGraph.BFS for more information).
	 * @param visit
	 */
	public void BFSGivenQ(VisitBFS visit) {
		long t = System.currentTimeMillis();
		dir.BFSGivenQ(visit);
		elapsedTime = System.currentTimeMillis() - t;
	}


	/**
	 * Creates an induced subgraph containing vertex v if and only if elements[v] is true.
	 * @param elements
	 * @return the induced subgraph
	 */
	public abstract GeneralGraph getInducedSubgraph(boolean elements[]);

	/**
	 * Transforms this graph into an induced subgraph specified by the parameter elements.
	 * @param elements
	 */
	public abstract void transformIntoInducedSubgraph(boolean elements[]);


	/**
	 * Checks if an arc is in the graph.
	 * @param s the head of the arc
	 * @param t the tail of the arc
	 * @return true if arc (s,t) is in the graph.
	 */
	public boolean arcExists(int s, int t) {
		return dir.arcExists(s, t);
	}
	


	/**
	 * 
	 * @param v a vertex
	 * @return an iterator on the vertices adjacent to v.
	 */
	public LazyIntIterator getAdj(int v) {
		return dir.getAdj(v);
	}


	/**
	 * Performs a DFS using a stack.
	 * @param visit
	 */
	public void DFS(VisitDFS visit) {
		long t = System.currentTimeMillis();
		dir.DFS(visit);
		elapsedTime = System.currentTimeMillis() - t;
	}

	
	public abstract String asciiGraphOutput();

	/**
	 * Exports this graph in the ASCII format.
	 * @param output the folder in which the ASCII graph is stored.
	 */
	public void exportAsAscii(String output) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(Utilities.adjGraphPath + output));
			bw.write(asciiGraphOutput() + "\n" + "# Nodes: " + getNN() + ". Edges: " + getNE() + ".\n");
			dir.exportAsAscii(bw);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Exports this graph in the webgraph format.
	 * @param output the folder in which the webgraph is stored.
	 */
	public void exportAsWebgraph(String output) {
		try {
			dir.exportAsWebgraph(Utilities.webGraphPath + output);
		} catch (SecurityException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException
				| ClassNotFoundException | InstantiationException | IOException
				| JSAPException e) {
			e.printStackTrace();
		}
	}


	/**
	 * @return a copy of this graph that can be used for multithreading.
	 */
	public abstract GeneralGraph getCopyForMultiThread();


	/**
	 * Reads a graph from a file, containing all edges as pairs of strings. Equal strings correspond to the same
	 * vertex (for example if the file contains only the pair (0, 1000), a graph with 2 vertices and one arc 
	 * is created).
	 * 
	 * @param filename the path of the file to read.
	 * @param separator the character used to separate elements in a pair.
	 * @throws IOException
	 */
	public static GeneralGraph readFile(String filename, char separator, GraphTypes type, boolean directed) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();
		String node;
		HashMap<String, Integer> h = new HashMap<String, Integer>();
		int i, j;
		int nn = 0;

		while (line != null) {
			if (!line.startsWith("#") && !line.startsWith("%")) {
				i = line.indexOf(separator);
				node = line.substring(0, i);

				if (!h.containsKey(node)) {
					h.put(node, nn++);
				}
				j = line.indexOf(separator, i + 1);
				if (j == -1) {
					j = line.length();
				}
				node = line.substring(i + 1, j);
				if (!h.containsKey(node)) {
					h.put(node, nn++);
				}
			}
			line = br.readLine();
		}
		br.close();
		GeneralGraph g;
		if (directed) {
			g = new Dir(nn, type);
		} else {
			g = new Undir(nn, type);
		}
		br = new BufferedReader(new FileReader(filename));

		line = br.readLine();

		while (line != null) {
			if (!line.startsWith("#") && !line.startsWith("%")) {
				i = line.indexOf(separator);
				j = line.indexOf(separator, i + 1);
				if (j == -1) {
					j = line.length();
				}
				g.addArc(h.get(line.substring(0, i)), h.get(line.substring(i + 1, j)));
			}
			line = br.readLine();
		}

		br.close();
		return g;
	}


	/**
	 * Reads a graph from a file, containing all edges as pairs of integers. The number of vertices corresponds
	 * to the maximum integer found (for example if the file contains only the pair (0, 1000), a graph with 1001
	 * vertices is created).
	 * 
	 * @param filename the path of the file to read.
	 * @param separator the character used to separate elements in a pair.
	 * @throws IOException
	 */
	public static GeneralGraph readNumericalFile(String filename, char separator, GraphTypes type, boolean directed) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();
		String node;
		int i;

		int nn = 0;
		while (line != null) {
			if (!line.startsWith("#")) {
				i = line.indexOf(separator);
				node = line.substring(0, i);
				nn = Math.max(Integer.parseInt(node), nn);

				node = line.substring(i + 1);
				nn = Math.max(Integer.parseInt(node), nn);
			}
			line = br.readLine();
		}

		nn++;

		GeneralGraph g;
		if (directed) {
			g = new Dir(nn, type);
		} else {
			g = new Undir(nn, type);
		}
		br.close();

		br = new BufferedReader(new FileReader(filename));

		line = br.readLine();

		while (line != null) {
			if (!line.startsWith("#")) {
				i = line.indexOf('\t');
				g.addArc(Integer.parseInt(line.substring(0,i)), Integer.parseInt(line.substring(i + 1)));
			}
			line = br.readLine();
		}
		br.close();
		return g;
	}

	/**
	 * Loads a graph.
	 * @param filename the file where the graph is stored.
	 * @param type the implementation required
	 * @param method the method used to read the graph
	 */
	public static GeneralGraph load(String filename, boolean directed, GraphTypes type, LoadMethods method) {
		long t = System.currentTimeMillis();
		GeneralGraph g;
		try {
			if (type == GraphTypes.ADJLIST) {
				if (method == LoadMethods.ASCII) {
					g = readFile(filename, '\t', type, directed);
				} else if (method == LoadMethods.ASCIINUMERICAL) {
					g = readNumericalFile(filename, '\t', type, directed);
				} else if (method == LoadMethods.WEBGRAPH) {
					if (directed) {
						AdjListArray dir = AdjListWebGraph.loadFromFile(filename).toAdjListGraph();
						g = new Dir(dir, dir.reverse());
					} else {
						g = new Undir(AdjListWebGraph.loadFromFile(filename).toAdjListGraph());
					}
				} else {
					throw new UnsupportedOperationException("The load method is not known.");
				}
			} else if (type == GraphTypes.WEBGRAPH) {
				if (directed) {
					g = new Dir(AdjListWebGraph.loadFromFile(filename), AdjListWebGraph.loadFromFile(filename + "_rev"));
				} else {
					g = new Undir(AdjListWebGraph.loadFromFile(filename));
				}
			} else {
				throw new UnsupportedOperationException("The type is not supported.");
			}
			g.elapsedTime = System.currentTimeMillis() - t;
			return g;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	/**
	 * Loads a graph.
	 * @param filename the file where the graph is stored.
	 */
	public static GeneralGraph load(String filename, boolean directed, LoadMethods method) {
		return load(filename, directed, Utilities.graphType, method);
	}
	/**
	 * Loads a graph.
	 * @param filename the file where the graph is stored.
	 */
	public static GeneralGraph load(String filename, boolean directed, GraphTypes type) {
		return load(filename, directed, type, Utilities.loadMethod);
	}

	/**
	 * Loads a graph.
	 * @param filename the file where the graph is stored.
	 * @param directed true if the graph to load is directed
	 */
	public static GeneralGraph load(String filename, boolean directed) {
		return load(filename, directed, Utilities.graphType);
	}
	
	
	/**
	 * Adds an arc to the graph.
	 * @param s the head of the new arc
	 * @param t the tail of the new arc.
	 */
	public abstract void addArc(int s, int t);



	public void startTime() {
		this.startTime = System.currentTimeMillis();
	}


	public void endTime() {
		this.elapsedTime = System.currentTimeMillis() - startTime;
	}
}
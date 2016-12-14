package alg.distances;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import utilities.Utilities;

/**
 * @author michele
 * This class implements the 2-Sweep and the 4-Sweep, as explained in Crescenzi et al., 
 * On computing the diameter of real-world undirected graphs
 */
public class SweepUndir {
	private graph.Undir graph;
	private int D;
	
	public int getD() {return D;}	
	
	
	public SweepUndir(graph.Undir graph) {
		this.graph = graph;
		this.D = 0;
		Utilities.outputLine("Original size: " + graph.getNN(), 1);
	}
	
	public void reset() {
		D = 0;
	}
	
	public void twoSweep(int start) {
		visit.Dist VisitStart = new visit.Dist(graph.getNN(), start);
		graph.BFS(VisitStart);
		int x = VisitStart.far;
		D = Math.max(D, VisitStart.dist[x]);
		visit.Dist VisitX = new visit.Dist(graph.getNN(), x);
		graph.BFS(VisitX);
		D = Math.max(D, VisitX.dist[VisitX.far]);
	}
	
	public void fourSweep(int start) {
		visit.Dist VisitStart = new visit.Dist(graph.getNN(), start);
		graph.BFS(VisitStart);
		int x = VisitStart.far;
		D = Math.max(D, VisitStart.dist[x]);
		visit.DistPred VisitX = new visit.DistPred(graph.getNN(), x);
		graph.BFS(VisitX);
		D = Math.max(D, VisitX.dist[VisitX.far]);
		twoSweep(VisitX.getCentralVertex());
	}
	
	public IntArrayList run(int start) {
		IntArrayList toReturn = new IntArrayList();
		this.reset();
		twoSweep(start);
		twoSweep((int) (graph.getNN() * Math.random()));
		toReturn.add(D);
		reset();
		fourSweep(start);
		toReturn.add(D);
		return toReturn;
	}
	
}

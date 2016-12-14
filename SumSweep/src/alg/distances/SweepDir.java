package alg.distances;

import utilities.Utilities;

/**
 * @author michele
 * This class implements the 2-DSweep, as explained in Crescenzi et al., On Computing the Diameter of Real-World Directed (Weighted) Graphs
 */
public class SweepDir {
	private graph.Dir graph;
	private int D;
	
	public int getD() {return D;}
	
	
	public SweepDir(graph.Dir graph) {
		this.graph = graph;
		this.D = 0;
		Utilities.outputLine("Original size: " + graph.getNN(), 1);
	}
	
	public void reset() {
		D = 0;
	}
	
	public void run(int start) {
		visit.Dist VisitStart = new visit.Dist(graph.getNN(), start);
		graph.BFS(VisitStart);
		int x = VisitStart.far;
		D = Math.max(D, VisitStart.dist[x]);
		visit.Dist VisitX = new visit.Dist(graph.getNN(), x);
		graph.BBFS(VisitX);
		D = Math.max(D, VisitX.dist[VisitX.far]);
		
		VisitStart = new visit.Dist(graph.getNN(), start);
		graph.BBFS(VisitStart);
		x = VisitStart.far;
		D = Math.max(D, VisitStart.dist[x]);
		VisitX = new visit.Dist(graph.getNN(), x);
		graph.BFS(VisitX);
		D = Math.max(D, VisitX.dist[VisitX.far]);
	}
}

package alg;

import graph.GraphTypes;
import graph.LoadMethods;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import base.DirectedTest;

/**
 * This class tests the SumSweep heuristic on directed graphs.
 */
public class SumSweepDir extends DirectedTest {
	String[] s = {"3SS", "4SS", "5SS", "6SS", "7SS", "8SS", "4Sw", "4Ran"};
	static int i = 0;

	public SumSweepDir(String filename, String directed) {
		super(filename, directed);
		String[] toAddToHeader = new String[s.length * 10 + 2];
		toAddToHeader[0] = "        NN";
		toAddToHeader[1] = "   Diam";
		for (int i = 0; i < s.length; i++) {
			for (int j = 0; j < 10; j++) {
				toAddToHeader[10 * i + 2 + j] = s[i] + "_" + (j + 1);
			}
		}
		addToHeader(toAddToHeader);
	}

	@Test
	public void testDiameterRadius() throws IOException, ParseException {
		graph.Dir graph = this.readGraphToTest(filename, GraphTypes.ADJLIST, LoadMethods.WEBGRAPH);
		graph.transformIntoBiggestWCC();
		printValue("" + graph.getNN());
	
		alg.distances.SumSweepDir g = new alg.distances.SumSweepDir(graph);
		alg.distances.SweepDir h = new alg.distances.SweepDir(graph);
		int N = 10;
		int iter = 8;
		IntArrayList results[] = new IntArrayList[s.length];
		
		for (int i = 0; i < results.length; i++) {
			results[i] = new IntArrayList();
		}
	
		for (int i = 0; i < N; i++) {
			int start = (int) (Math.random() * graph.getNN());
			g = new alg.distances.SumSweepDir(graph);
			h.reset();
			g.sumSweep(start, iter);
			IntArrayList partialResult = g.getSumSweepResults();
			h.run(start);
			int approxH = h.getD();
			
			for (int j = 2; j < partialResult.size(); j++) {
				results[j - 2].add(partialResult.get(j));
			}
			results[results.length - 2].add(approxH);

			int D = 0;
			for (int j = 0; j < 4; j++) {
				visit.Dist v = new visit.Dist(graph.getNN(), start);
				graph.BFS(v);
				D = Math.max(D, v.dist[v.far]);
				start = (int) (Math.random() * graph.getNN());
			}
			results[results.length - 1].add(D);

			
		}

		g.runAuto();
		printValue("" + g.getD());
		
		for (int i = 0; i < results.length; i++) {
			for (int dApp : results[i]) {
				printValue("" + dApp);
			}
		}		
	}
}

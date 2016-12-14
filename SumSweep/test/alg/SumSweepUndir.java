package alg;

import graph.GraphTypes;
import graph.LoadMethods;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import base.UndirectedTest;

/**
 * This class tests the SumSweep heuristic on undirected graphs.
 */
public class SumSweepUndir extends UndirectedTest {
	String[] s = {"3SS", "4SS", "5SS", "2x2S", "4Sw", "4Ran"};
	
	public SumSweepUndir(String filename, String directed) {
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
		graph.Undir graph = this.readGraphToTest(filename, GraphTypes.ADJLIST, LoadMethods.WEBGRAPH);
		graph.transformIntoBiggestCC();
		printValue("" + graph.getNN());
	
		alg.distances.SumSweepUndir g = new alg.distances.SumSweepUndir(graph);
		alg.distances.SweepUndir h = new alg.distances.SweepUndir(graph);
		int N = 10;
		int iter = 5;
		IntArrayList results[] = new IntArrayList[s.length];
		
		for (int i = 0; i < results.length; i++) {
			results[i] = new IntArrayList();
		}
	
		for (int i = 0; i < N; i++) {
			int start = (int) (Math.random() * graph.getNN());
			g = new alg.distances.SumSweepUndir(graph);
			h.reset();
			g.sumSweep(start, iter);
			IntArrayList partialResult = g.getSumSweepResults();
			IntArrayList approxH = h.run(start);
			
			for (int j = 2; j < partialResult.size(); j++) {
				results[j - 2].add(partialResult.get(j));
			}
			results[results.length - 3].add(approxH.get(0));
			results[results.length - 2].add(approxH.get(1));

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

package alg;

import static org.junit.Assert.assertEquals;
import graph.GraphTypes;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import utilities.Utilities;
import base.DirectedTest;

/**
 * This class tests the directed SumSweep and compares all bounds found with the correct eccentricities.
 * If a bound is not correct, an error is raised.
 */
public class DiameterRadiusTestDirExtended extends DirectedTest {

	public DiameterRadiusTestDirExtended(String filename, String directed) {
		super(filename, directed);
		addToHeader("        NN\t     Diameter\t    Radius\t     iterD\t     iterR"
				.split("\t"));
	}

	@Test
	public void testDiameterRadius() throws IOException, ParseException {
		graph.Dir graph = this.readGraphToTest(filename, GraphTypes.ADJLIST, Utilities.loadMethod);
		printValue("" + graph.getNN());
		
		alg.distances.SumSweepDir g = new alg.distances.SumSweepDir(graph);
		alg.distances.DistDistr forw = new alg.distances.DistDistr(g.graph, 8, 1, false);
		alg.distances.DistDistr back = new alg.distances.DistDistr(graph.getReverse(), 8, 1, false);

		g.runAuto();
		forw.computeDistanceDistribution();
		back.computeDistanceDistribution();
		
		for (int i = 0; i < graph.getNN(); i++) {
			assertEquals(g.getlF(i) <= forw.ecc[i], true);
			assertEquals(g.getlB(i) <= back.ecc[i], true);
			assertEquals(g.getuF(i) >= forw.ecc[i], true);
			assertEquals(g.getuB(i) >= back.ecc[i], true);
		}

		assertEquals(g.getD(), forw.D);
		assertEquals(g.getD(), back.D);
		
		graph = this.readGraphToTest(filename, GraphTypes.ADJLIST, Utilities.loadMethod);
		

		printValue("" + g.getD());
		printValue("" + g.getR());
		printValue("" + g.getIterD());
		printValue("" + g.getIterR());
	}
}

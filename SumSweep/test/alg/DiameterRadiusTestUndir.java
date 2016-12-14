package alg;

import graph.GraphTypes;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import utilities.Utilities;
import base.UndirectedTest;

/**
 * This class tests the Undirected SumSweep algorithm and outputs the number of BFSes required to complete the computation.
 */
public class DiameterRadiusTestUndir extends UndirectedTest {

	public DiameterRadiusTestUndir(String filename, String directed) {
		super(filename, directed);
		addToHeader("        NN\t        NE\t    Radius\t  Diameter\t     iterR\t     iterD"
				.split("\t"));
	}

	@Test
	public void testDiameterRadius() throws IOException, ParseException {

		alg.distances.SumSweepUndir g = new alg.distances.SumSweepUndir(
				readGraphToTest(filename, GraphTypes.ADJLIST, Utilities.loadMethod));
		g.run(g.maxOutDegVert(), 3);

		printValue("" + g.getNN());
		printValue("" + g.getNE());
		printValue("" + g.getR());
		printValue("" + g.getD());
		printValue("" + g.getIterR());
		printValue("" + g.getIterD());
		Utilities.outputLine("", 1);
	}
}

package alg;

import graph.GraphTypes;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import utilities.Utilities;
import base.DirectedTest;

/**
 * This class tests the Directed SumSweep algorithm and outputs the number of BFSes required to complete the computation.
 */
public class DiameterRadiusTestDir extends DirectedTest {

	public DiameterRadiusTestDir(String filename, String directed) {
		super(filename, directed);
		addToHeader("        NN\t        NE\t  Diameter\t   DiamSCC\t    Radius\t    RadSCC\t     iterD\t  iterDSCC\t     iterR\t  iterRSCC"
				.split("\t"));
	}

	@Test
	public void testDiameterRadius() throws IOException, ParseException {
	
		graph.Dir graph = this.readGraphToTest(filename, GraphTypes.ADJLIST, Utilities.loadMethod);
		graph.transformIntoBiggestWCC();
		printValue("" + graph.getNN());
		printValue("" + graph.getNE());
		alg.distances.SumSweepDir g = new alg.distances.SumSweepDir(graph);
		g.runAuto();
		
		graph = this.readGraphToTest(filename, GraphTypes.ADJLIST, Utilities.loadMethod);
		graph.transformIntoBiggestSCC();
		alg.distances.SumSweepDir gscc = new alg.distances.SumSweepDir(graph);
		gscc.runAuto();

		printValue("" + g.getD());
		printValue("" + g.getR());
		printValue("" + gscc.getD());
		printValue("" + gscc.getR());
		printValue("" + g.getIterD());
		printValue("" + gscc.getIterD());
		printValue("" + g.getIterR());
		printValue("" + gscc.getIterR());
	}
}

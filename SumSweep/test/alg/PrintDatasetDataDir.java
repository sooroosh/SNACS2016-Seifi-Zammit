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
public class PrintDatasetDataDir extends DirectedTest {

	public PrintDatasetDataDir(String filename, String directed) {
		super(filename, directed);
		addToHeader("        NN\t        NE\t    NNWCC\t    NEWCC\t    NNSCC\t    NESCC"
				.split("\t"));
	}

	@Test
	public void testDiameterRadius() throws IOException, ParseException {

		graph.Dir graph = this.readGraphToTest(filename, GraphTypes.ADJLIST, Utilities.loadMethod);
		printValue("" + graph.getNN());
		printValue("" + graph.getNE());
		graph.transformIntoBiggestWCC();
		printValue("" + graph.getNN());
		printValue("" + graph.getNE());
		graph.transformIntoBiggestSCC();
		printValue("" + graph.getNN());
		printValue("" + graph.getNE());
	}
}

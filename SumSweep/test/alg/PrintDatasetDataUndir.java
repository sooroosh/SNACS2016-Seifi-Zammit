package alg;

import graph.GraphTypes;

import java.io.IOException;
import java.text.ParseException;

import org.junit.Test;

import utilities.Utilities;
import base.UndirectedTest;

/**
 * This test prints some data of all directed graphs in the dataset.
 */
public class PrintDatasetDataUndir extends UndirectedTest {

	public PrintDatasetDataUndir(String filename, String directed) {
		super(filename, directed);
		addToHeader("        NN\t        NE\t     NNCC\t     NECC"
				.split("\t"));
	}

	@Test
	public void testDiameterRadius() throws IOException, ParseException {

		graph.Undir graph = this.readGraphToTest(filename, GraphTypes.ADJLIST, Utilities.loadMethod);
		printValue("" + graph.getNN());
		printValue("" + graph.getNE());
		graph.transformIntoBiggestCC();
		printValue("" + graph.getNN());
		printValue("" + graph.getNE());
	}
}

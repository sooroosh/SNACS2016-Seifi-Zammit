package base;

import graph.Dir;
import graph.GraphTypes;
import graph.LoadMethods;

import java.io.IOException;
import java.util.Collection;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.junit.runner.RunWith;

import utilities.Utilities;

/**
 * This class provides all parameters used to run a test on directed graphs. To
 * write a test, it is enough to extend it and write the code for the test.
 */
@RunWith(value = Parameterized.class)
public abstract class DirectedTest extends BaseTest {

	/**
	 * Generates a directed test. See BaseTest for more information.
	 * @param filename
	 * @param directed
	 */
	public DirectedTest(final String filename, final String directed) {
		super(filename, directed);
	}

	/**
	 * @return a collection containing all the file names of files to be read by
	 *         the different tests
	 */
	@Parameters()
	public static Collection<String[]> data() {
		return Utilities.convertFileListIntoStringForTest(Utilities
				.getDirectedGraphList(Utilities.loadMethod));
	}

	/* (non-Javadoc)
	 * @see base.BaseTest#readGraphToTest(java.lang.String, graph.GraphTypes)
	 */
	protected Dir readGraphToTest(String filename, GraphTypes type, LoadMethods loadMethod)
			throws IOException {
		return (Dir) super.readGraphToTest(filename, type, loadMethod);
	}
}

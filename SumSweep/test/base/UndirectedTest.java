package base;

import graph.GraphTypes;
import graph.Undir;
import graph.LoadMethods;

import java.io.IOException;
import java.util.Collection;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import org.junit.runner.RunWith;

import utilities.Utilities;

/**
 * This class provides all parameters used to run a test. To
 *         write a test, it is enough to extend it and write the code for the
 *         test.
 */
@RunWith(value = Parameterized.class)
public abstract class UndirectedTest extends BaseTest {

	/**
	 * Generates a directed test. See BaseTest for more information.
	 * @param filename
	 * @param directed
	 */
	public UndirectedTest(final String filename, final String directed) {
		super(filename, directed);
	}

	/**
	 * @return a collection containing all the file names of files to be read by
	 *         the different tests
	 */
	@Parameters()
	public static Collection<String[]> data() {
		return Utilities.convertFileListIntoStringForTest(Utilities
				.getUndirectedGraphList(Utilities.loadMethod));
	}

	/* (non-Javadoc)
	 * @see base.BaseTest#readGraphToTest(java.lang.String, graph.GraphTypes)
	 */
	@Override
	protected Undir readGraphToTest(String filename, GraphTypes type, LoadMethods loadMethod)
			throws IOException {
		return (Undir) super.readGraphToTest(filename, type, loadMethod);
	}
}

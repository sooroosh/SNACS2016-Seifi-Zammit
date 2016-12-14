package base;

import graph.Dir;
import graph.GraphTypes;
import graph.GeneralGraph;
import graph.Undir;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.io.IOException;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

import graph.LoadMethods;
import utilities.Utilities;

/**
 * This class provides all parameters used to run a test. To
 *         write a test, it is enough to extend it and write the code for the
 *         test.
 */
@RunWith(value = Parameterized.class)
public abstract class BaseTest {

	protected final String filename;
	protected final boolean directed;
	public static boolean first = true;
	private String header = "| Graph                        |";
	protected IntArrayList lengths = IntArrayList.wrap(new int[] { header.length() - 4 });
	protected int contLengths;

	
	/**
	 * This function creates a test, by knowing the filename of the graph to read and by knowing if the graph is directed.
	 * @param filename the filename.
	 * @param directed a string that must contain only "true" or "false" (it must be a string because JUnit tests 
	 * require only string parameters).
	 */
	public BaseTest(final String filename, final String directed) {
		Utilities.verb = 0;
		this.filename = filename;
		this.directed = Boolean.parseBoolean(directed);
	}

	/**
	 * Adds a set of strings to the header, and uses them to compute the length of the different cases in the output table.
	 * @param s the set of strings to add;
	 */
	protected void addToHeader(String s[]) {
		for (int i = 0; i < s.length; i++) {
			addToHeader(s[i], s[i].length());
		}
	}

	/**
	 * Adds a set of strings to the header, and sets the lengths of the different cases with the parameter provided.
	 * @param s the set of strings to add;
	 * @param length the length of each column.
	 */
	protected void addToHeader(String s[], int length[]) {
		for (int i = 0; i < s.length; i++) {
			addToHeader(s[i], length[i]);
		}
	}

	/**
	 * Adds a string to the header, and sets the length of the corresponding case with the parameter provided.
	 * @param s the string to add;
	 * @param length the length of the corresponding column.
	 */
	protected void addToHeader(String s, int length) {
		lengths.add(length);
		header = header + " " + StringUtils.leftPad(s, length) + " |";
	}

	/**
	 * Prints a value in the output table.
	 * @param value the value to print
	 * @param left if true, the value is left-aligned in the column, right-aligned otherwise.
	 */
	public void printValue(String value, boolean left) {
		int length;
		if (contLengths >= lengths.size()) {
			length = 10;
		} else {
			length = lengths.get(contLengths++);
		}
		if (left) {
			Utilities.output(StringUtils.rightPad(value, length) + " | ", 0);
		} else {
			Utilities.output(StringUtils.leftPad(value, length) + " | ", 0);
		}
	}

	/**
	 * Prints a right-aligned value in the next column of the output table.
	 * @param value the value to print.
	 */
	public void printValue(String value) {
		printValue(value, false);
	}

	/**
	 * @return a collection containing all the file names of files to be read by
	 *         the different tests
	 */
	@Parameters()
	public static Collection<String[]> data() {
		return Utilities.convertFileListIntoStringForTest(Utilities
				.getGraphList(Utilities.loadMethod));
	}

	/**
	 * Reads a graph to test.
	 * @param filename the graph to read
	 * @param type the method used to store the graph.
	 * @return the graph that has been read.
	 * @throws IOException
	 */
	protected GeneralGraph readGraphToTest(String filename, GraphTypes type, LoadMethods loadMethod) throws IOException {
		if (first) {
			Utilities.outputLine(header, 0);
			Utilities.output(header.replaceAll(" |\\w", "_"), 0);
			first = false;
		}
		GeneralGraph g;
		String prefix = "";
		String suffix = "";
		if (type == GraphTypes.WEBGRAPH
				|| ((type == GraphTypes.ADJLIST) && loadMethod == LoadMethods.WEBGRAPH)) {
			prefix = Utilities.webGraphPath;
		} else if (type == GraphTypes.ADJLIST) {
			prefix = Utilities.adjGraphPath;
			suffix = ".txt";
		}

		if (directed) {
			filename = prefix + "Directed/" + filename + suffix;
			g = Dir.load(filename, type, loadMethod);
		} else {
			filename = prefix + "Undirected/" + filename + suffix;
			g = Undir.load(filename, type, loadMethod);
		}

		if (this.contLengths == 0) {
			Utilities.output("\n| ", 0);
			printValue(filename.substring(filename.lastIndexOf('/') + 1), true);
		}
		return g;
	}
}

package utilities;

import graph.GraphTypes;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import graph.LoadMethods;

/**
 * This class contains some routines that are useful for many different parts of the code.
 */
public class Utilities {
	public static int verb = 1;
	public static String webGraphPath = "inputWebgraph/";
	public static final String adjGraphPath = "inputAscii/";
	public static GraphTypes graphType = GraphTypes.ADJLIST;
	public static LoadMethods loadMethod = LoadMethods.WEBGRAPH;

	/**Returns the value of k such that (a[k], b[k]) is lexicographically maximal among those pairs such that both a[i] and b[i] are positive.
	 * @param a the first array
	 * @param b the second array (must be at least as long as the first).
	 * @return the value of k required
	 */
	public static int argMax(int[] a, int[] b) {
		int max = -1; 

		for (int k = 0; k < a.length; k++) {
			if (a[k] >= 0 && b[k] >= 0 && (max == -1 || a[k] > a[max] || (a[k] == a[max] && b[k] > b[max]))) {
				max = k;
			}
		}
		return max;
	}
	
	/**Returns the value of k such that a[k] is maximal.
	 * @param a the array
	 * @return the value of k required
	 */
	public static int argMax(int[] a) {
		int max = 0; 

		for (int k = 1; k < a.length; k++) {
			if (a[k] > a[max]) {
				max = k;
			}
		}
		return max;
	}
	
	/**Returns the value of k such that (a[k], b[k]) is lexicographically maximal among those pairs such that both a[i] and b[i] are positive.
	 * @param a the first array
	 * @return the value of k required
	 */
	public static int argMax(double[] a) {
		int max = 0; 

		for (int k = 1; k < a.length; k++) {
			if (a[k] > a[max]) {
				max = k;
			}
		}
		return max;
	}

	
	/**Returns the value of k such that (a[k], b[k]) is lexicographically minimal
	 * among those pairs such that both a[i] and b[i] are positive.
	 * 
	 * @param a the first array
	 * @param b the second array (must be at least as long as the first).
	 * @return the value of k required
	 */
	public static int argMin(int[] a, int[] b) {
		int min = -1; 
		for (int k = 0; k < a.length; k++) {
			if (a[k] >= 0 && b[k] >= 0 && (min == -1 || a[k] < a[min] || (a[k] == a[min] && b[k] < b[min]))) {
				min = k;
			}
		}
		return min;
	}




	/**
	 * @param type the type of graph considered
	 * @return an ArrayList containing all files in the folder of directed graphs, in increasing order of size.
	 */
	public static ObjectArrayList<File> getDirectedGraphList(LoadMethods type) {
		if (type == LoadMethods.WEBGRAPH) {
			return getGraphList(Utilities.webGraphPath + "/Directed");
		} else if (type == LoadMethods.ASCII) {
			return getGraphList(Utilities.adjGraphPath + "/Directed");
		}
		return null;
	}

	/**
	 * @param type the type of graph considered
	 * @return an ArrayList containing all files in the folder of directed graphs, in increasing order of size.
	 */
	public static ObjectArrayList<File> getUndirectedGraphList(LoadMethods type) {
		if (type == LoadMethods.WEBGRAPH) {
			return getGraphList(Utilities.webGraphPath + "/Undirected");
		} else if (type == LoadMethods.ASCII) {
			return getGraphList(Utilities.adjGraphPath + "/Undirected");
		}
		return null;
	}
	/**
	 * 
	 * @return an ArrayList containing all files in the folder of undirected graphs, in increasing order of size.
	 */
	public static ObjectArrayList<File> getGraphList(String folder) {
		File[] listAll = new File(folder).listFiles();
		ObjectArrayList<File> list = new ObjectArrayList<File>();

		for (int i = 0; i < listAll.length; i++) {
			if (!listAll[i].getName().endsWith(".properties") && !listAll[i].getName().endsWith(".offsets") && !listAll[i].getName().endsWith("_rev.graph")) {
				list.add(listAll[i]);
			}
		}

		Collections.sort(list, new Comparator<File>() {
			public int compare(File a, File b ) {
				if (a.length() < b.length()) {
					return -1;
				} else if (a.length() > b.length()) {
					return 1;
				}
				return 0;
			}
		});

		return list;
	}	

	/**
	 * 
	 * @return an ArrayList containing all files in the folder of directed and undirected graphs, in increasing order of size.
	 */
	public static ObjectArrayList<File> getGraphList(LoadMethods type) {
		ObjectArrayList<File> list = new ObjectArrayList<File>();
		list.addAll(0, getDirectedGraphList(type));
		list.addAll(list.size(), getUndirectedGraphList(type));
		Collections.sort(list, new Comparator<File>() {
			public int compare(File a, File b ) {
				if (a.length() < b.length()) {
					return -1;
				} else if (a.length() > b.length()) {
					return 1;
				}
				return 0;
			}
		});
		return list;
	}


	

	/**
	 * Converts a list of files into a list of their filenames
	 * @param list the list of files
	 * @return the list of filenames
	 */
	public static Collection<String[]> convertFileListIntoStringForTest(ObjectArrayList<File> list) {
		Collection<String[]> data = new ArrayList<String[]>();

		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i).getName();
			if (name.endsWith(".graph")) {
				name = name.substring(0, name.lastIndexOf('.'));
				if (!name.endsWith("rev")) {
					data.add(new String[]{name, String.valueOf(!list.get(i).getPath().contains("Undirected"))});
				}
			}
		}
		return data;
	}
	
	/**
	 * Outputs a result, if the verbosity is enough.
	 * @param output the string to output.
	 * @param minVerb the verbosity required.
	 */
	public static void output(String output, int minVerb) {
		if (verb >= minVerb) {
			System.out.print(output);
		}
	}
	
	/**
	 * Outputs a result, if the verbosity is enough, and adds a new line.
	 * @param output the string to output.
	 * @param minVerb the verbosity required.
	 */
	public static void outputLine(String output, int minVerb) {
		output(output + "\n", minVerb);
	}
	
	public static JSAPResult parseArguments(String args[]) throws JSAPException {
		JSAP jsap = new JSAP();
		
		Switch help = new Switch("help", 'h', "help");
		jsap.registerParameter(help);

		Switch directed = new Switch("directed", 'd', "directed");
		jsap.registerParameter(directed);
		
		FlaggedOption operation = new FlaggedOption("operation").setStringParser(JSAP.STRING_PARSER)
				.setDefault("")
				.setRequired(true)
				.setShortFlag('p')
				.setLongFlag("operation");
		jsap.registerParameter(operation);
		
		FlaggedOption input = new FlaggedOption("input").setStringParser(JSAP.STRING_PARSER)
				.setRequired(true)
				.setShortFlag('i')
				.setLongFlag("input");
		jsap.registerParameter(input);
				
		FlaggedOption verb = new FlaggedOption("verb").setStringParser(JSAP.INTEGER_PARSER)
				.setDefault("1")
				.setRequired(true)
				.setShortFlag('v')
				.setLongFlag("verbose");
		jsap.registerParameter(verb);
		Utilities.verb = jsap.parse(args).getInt("verb");
		
		FlaggedOption threads = new FlaggedOption("threads").setStringParser(JSAP.INTEGER_PARSER)
				.setDefault("" + Runtime.getRuntime().availableProcessors())
				.setRequired(true)
				.setShortFlag('t')
				.setLongFlag("threads");
		jsap.registerParameter(threads);

		FlaggedOption nn = new FlaggedOption("nn").setStringParser(JSAP.INTEGER_PARSER)
				.setRequired(false)
				.setShortFlag('n')
				.setLongFlag("nn");
		jsap.registerParameter(nn);

		FlaggedOption beta = new FlaggedOption("beta").setStringParser(JSAP.DOUBLE_PARSER)
				.setRequired(false)
				.setShortFlag('b')
				.setLongFlag("beta");
		jsap.registerParameter(beta);
		
		FlaggedOption output = new FlaggedOption("output").setStringParser(JSAP.STRING_PARSER)
				.setRequired(false)
				.setShortFlag('o')
				.setLongFlag("output");
		jsap.registerParameter(output);
		return jsap.parse(args);
	}
}

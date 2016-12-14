package utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import graph.LoadMethods;

import com.martiansoftware.jsap.JSAPException;

import graph.Dir;
import graph.GraphTypes;
import graph.Undir;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.webgraph.BVGraph;

/**
 * This class contains methods used to convert graphs from ASCII into Webgraph format.
 */
public class GraphConverter {

	/**
	 * Converts all undirected graphs in the Webgraph folder into Ascii graphs
	 */
	public static void convertToAsciiUndirected() {
		ObjectArrayList<File> list = Utilities.getUndirectedGraphList(LoadMethods.WEBGRAPH);
		Utilities.outputLine("Converting " + list.size() + " undirected graphs...", 1);

		int k = 0;
		for (File f : list) {
			graph.Undir g = Undir.load(f.getPath().substring(0, f.getPath().lastIndexOf('.')), GraphTypes.WEBGRAPH);
			String output = "Undirected/" + f.getName().substring(0, f.getName().lastIndexOf('.')) + ".txt";
			Utilities.output(++k + " - " + f.getPath(), 1);
			g.exportAsAscii(output);
			Utilities.outputLine(" => " + Utilities.adjGraphPath + output, 1);
		}
	}

	/**
	 * Converts all directed graphs in the Webgraph folder into Ascii graphs
	 */
	public static void convertToAsciiDirected() {
		ObjectArrayList<File> list = Utilities.getDirectedGraphList(LoadMethods.WEBGRAPH);
		Utilities.outputLine("Converting " + list.size() + " directed graphs...", 1);
		int k = 0;
		for (File f : list) {
			graph.Dir g = Dir.load(f.getPath().substring(0, f.getPath().lastIndexOf('.')), GraphTypes.WEBGRAPH);
			String output = "Directed/" + f.getName().substring(0, f.getName().lastIndexOf('.')) + ".txt";
			Utilities.output(++k + " - " + f.getPath(), 1);
			g.exportAsAscii(output);
			Utilities.outputLine(" => " + Utilities.adjGraphPath + output, 1);
		}
	}


	/**
	 * Converts all undirected graphs in the Ascii folder into Webgraphs
	 */
	public static void convertToWebgraphUndirected() {
		ObjectArrayList<File> list = Utilities.getUndirectedGraphList(LoadMethods.ASCII);
		Utilities.outputLine("Converting " + list.size() + " undirected graphs...", 1);

		int k = 0;
		for (File f : list) {
			
			graph.Undir g = Undir.load(f.getPath(), GraphTypes.ADJLIST, LoadMethods.ASCII);
			String output = "Undirected/" + f.getName();

			if (output.lastIndexOf('.') >= 0) {
				output = output.substring(0, output.lastIndexOf('.'));
			}
			Utilities.output(++k + " - " + f.getPath(), 1);
			g.exportAsWebgraph(output);
			Utilities.outputLine(" => " + Utilities.webGraphPath + output, 1);
		}
	}

	/**
	 * Converts all directed graphs in the Ascii folder into Webgraphs
	 */
	public static void convertToWebgraphDirected() {
		ObjectArrayList<File> list = Utilities.getDirectedGraphList(LoadMethods.ASCII);
		Utilities.outputLine("Converting " + list.size() + " directed graphs...", 1);

		int k = 0;
		for (File f : list) {

			Dir g = Dir.load(f.getPath(), GraphTypes.ADJLIST, LoadMethods.ASCII);
			String output = "Directed/" + f.getName();
			if (output.lastIndexOf('.') >= 0) {
				output = output.substring(0, output.lastIndexOf('.'));
			}
			Utilities.output(++k + " - " + f.getPath(), 1);
			g.exportAsWebgraph(output);
			Utilities.outputLine(" => " + Utilities.webGraphPath + output, 1);
		}
	}



	/**
	 * Converts an Ascii webgraph to a Webgraph, by loading it from a file.
	 * @param input the path of the input file
	 * @param output the path of the output file
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IOException
	 * @throws JSAPException
	 */
	public static void convertAsciiToWebgraphImplicit(String input, String output)
			throws SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException, InstantiationException, IOException,
			JSAPException {

		String bvinput1 = "-g ArcListASCIIGraph " + input + " " + output;
		PrintStream stdout = System.out;
		System.setOut(new PrintStream("tmp/log.txt"));
		BVGraph.main(bvinput1.split(" "));
		System.setOut(stdout);
	}

	private static void sort(String input, String output) throws IOException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, JSAPException, InterruptedException{
		String cmd1 = "sort -n " + input + " -o " + "tmp/_temp1.txt";
		Runtime run1 = Runtime.getRuntime();
		Process pr1 = run1.exec(cmd1);
		pr1.waitFor();
	}

	private static void uncommentAscii(String input, String output) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(input));
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		String line = null;

		while ((line = br.readLine()) != null) {
			if (!line.startsWith("#")) {
				bw.write(line + "\n");
			}
		}
		bw.close();
		br.close();
	}

	private static void uniqueAscii(String input, String output) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(input));
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		String line;
		String oldLine="";

		while ((line = br.readLine()) != null) {
			if (!oldLine.equals(line)) {
				bw.write(line + "\n");
				oldLine = line;
			}
		}

		br.close();
		bw.close();
	}

	/**
	 * Creates a Webgraph starting from an Ascii file, without loading it into memory
	 * @param input the input file
	 * @param output the output file
	 * @throws IOException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws JSAPException
	 * @throws InterruptedException
	 */
	public static void uncommentSortUniqueAndConvert(String input, String output) throws IOException, SecurityException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException, InstantiationException, JSAPException, InterruptedException{

		uncommentAscii(input, "tmp/_temp0.txt");
		sort("tmp/_temp0.txt", "tmp/_temp1.txt");
		uniqueAscii("tmp/_temp1.txt","tmp/_temp2.txt");
		convertAsciiToWebgraphImplicit("tmp/_temp2.txt", output);
		for (int i = 0; i <= 2; i++) {
			File toRemove = new File("tmp/_temp" + i + ".txt");
			toRemove.delete();
		}
	}

	public static void main(String args[]) {
		Dir g = Dir.load("inputAscii/Directed/soc-pokec-relationshipsOK", GraphTypes.ADJLIST, LoadMethods.ASCII);
		String output = "Directed/soc-pokec-relationshipsOK";
		g.exportAsWebgraph(output);
	}

}

package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import utilities.GraphConverter;
import utilities.Utilities;

import alg.distances.SumSweepDir;
import alg.distances.SumSweepUndir;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

/**
 * This class provides the routines to use the (very simple) user interface.
 */
public class Main {
	
	/**
	 * Prints the help file on the terminal.
	 * @throws IOException
	 */
	public static void printHelp() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("README.txt"));
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
		br.close();
	}
	
	/**
	 * Launches the program, choosing the algorithm to perform based on the arguments.
	 * @param args the arguments (see README.txt for more information).
	 * @throws JSAPException
	 */
	public static void main(String args[]) throws JSAPException {
		JSAPResult config = Utilities.parseArguments(args);
		
		Utilities.verb = config.getInt("verb");
		
		String oper = config.getString("operation");
		
		if (config.getBoolean("help")) {
			oper = "help";
		}
		
		switch (oper) {
		case "sumsweepdir": 
			SumSweepDir.main(config);
			break;
		case "sumsweepdirscc": 
			SumSweepDir.mainScc(config);
			break;
		case "sumsweepundir": 
			SumSweepUndir.main(config);
			break;
		case "convertascii":
			GraphConverter.convertToAsciiDirected();
			GraphConverter.convertToAsciiUndirected();
			break;
		case "convertwebgraph":
			GraphConverter.convertToWebgraphDirected();
			GraphConverter.convertToWebgraphUndirected();
			break;
		case "help": 
			try {
				printHelp();
			} catch (IOException e) {
				System.out.println("File README.txt not found. Please, repair the folder.");
			}
			break;
		default: 
			System.out.println("The operation \"" + config.getString("operation") + "\" does not exist.");
			System.out.println("Please, run with option -h or --help to show a list of allowed operations.");
			break;
		}
	}
}

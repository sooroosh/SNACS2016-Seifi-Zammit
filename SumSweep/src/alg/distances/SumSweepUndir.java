package alg.distances;

import java.text.DecimalFormat;

import graph.GraphTypes;
import graph.Undir;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import utilities.Utilities;

/**
 * This class implements the undirected SumSweep algorithm, as explained in the article
 * Borassi et al, On the Solvability of the Six Degrees of Separation Game.
 */
public class SumSweepUndir extends Undir {
	
	private int l[];
	private int u[];
	private int totDist[];
	private int R;
	private int D;
	private int iterR = -1;
	private int iterD = -1;
	private int iter = 0;
	private int Dv;
	private int Rv;
	visit.SumSweepUnd visit;
	private int lastImprovement = 0;
	private int stillToDo = 0;
	
	public int getR() {return R;}
	public int getRv() {return Rv;}
	public int getD() {return D;}
	public int getDv() {return Dv;}
	public int getIterR() {return iterR;}
	public int getIterD() {return iterD;}
	private IntArrayList sumSweepResults;

	

	

	/**
	 * Instantiates this object using the biggest connected component of the given graph.
	 * @param graph the input graph.
	 */
	public SumSweepUndir(graph.Undir graph) {
		super(graph);
		Utilities.outputLine("Original size: " + graph.getNN(), 2);

		this.transformIntoBiggestCC();
		totDist = new int[this.getNN()];
		l = new int[this.getNN()];
		u = new int[this.getNN()];
		IntArrays.fill(u, this.getNN() - 1);
		R = this.getNN() - 1;
		D = 0;
		iter = 0;
		stillToDo = graph.getNN();

		Utilities.outputLine("Main CC size: " + this.getNN(), 2);
		
		visit = new visit.SumSweepUnd(this, 0);
	}
	
	/**
	 * Updates, if necessary, the approximated values of R and D, after an eccentricity has been computed.
	 * @param v the vertex whose eccentricity has been computed.
	 */
	private void checkNewEcc(int v) {
		if (l[v] < R) {
			R = l[v];
			Rv = v;
		}
		if (l[v] > D) {
			D = l[v];
			Dv = v;
		}
	}
	
	

	/**
	 * Prints some data computed during the SumSweep.
	 */
	public void printData() {
		int toDoUntilR = 0;
		int toDoUntilD = 0;
		for (int i = 0; i < getNN(); i++) {
			if (l[i] < R && totDist[i] >= 0) {
				toDoUntilR++;
			} 
			if (u[i] > D && totDist[i] >= 0) {
				toDoUntilD++;
			}
		}
		Utilities.outputLine("BFS " + iter + " complete!", 2);
		if (iterR == -1) {
			Utilities.outputLine("    Approximated radius: " + R + " (still to do: " + toDoUntilR + ")", 2);
		} else {
			Utilities.outputLine("    Radius: " + R, 2);
		}
		if (iterD == -1) {
			Utilities.outputLine("    Approximated diameter: " + D + " (still to do: " + toDoUntilD + ")", 2);
		} else {
			Utilities.outputLine("    Diameter: " + D, 2);
		}

		int currentValue = toDoUntilD + toDoUntilR;
		lastImprovement = stillToDo - currentValue;
		stillToDo = currentValue;
	}

	
	/** Computes a step of the SumSweep, performing a BFS. Updates the bounds.
	 * @param start the starting vertex of the BFS
	 * @param addToTotDist if true, also the total distance is updated.
	 */
	private int stepSumSweep(int start, boolean addToTotDist) {
 
		int ecc;

		totDist[start] = -1;
		visit.start = start;
		BFS(visit);
		iter++;
		ecc = visit.dist[visit.far];
		l[start] = ecc;
		u[start] = ecc;
		checkNewEcc(start);

		int ecc2 = -1;

		for (int i = 0; i < getNN(); i++) {
			if (!visit.firstBranch[i]) {
				ecc2 = Math.max(ecc2, visit.dist[i]);
			}
		}

		for (int i = 0; i < getNN(); i++) {
			if (totDist[i] >= 0) {
				int dist = visit.dist[i];

				l[i] = Math.max(l[i], Math.max(ecc - dist, dist));
		
				if (visit.firstBranch[i]) {
					u[i] = Math.min(u[i], Math.max(ecc - 2 * (visit.firstPath + 1) + dist, dist + 
							Math.max(0, ecc2 - 2 * visit.firstPath)));
				} else {
					u[i] = Math.min(u[i], Math.max(ecc - 2 * visit.firstPath + dist, ecc));
				}

				if (l[i] == u[i]) {					
					totDist[i] = -1;
					checkNewEcc(i);
				} else if (addToTotDist) {
					totDist[i] += dist;
				}
			} 
		}
		return visit.far;
	}
	
	/**
	 * Runs the SumSweep with the best parameters, according to our experiments.
	 */
	public void runAuto() {
		run(maxOutDegVert(), 3);
	}
	
	/**
	 * @return the results obtained during the initial SumSweep. In position i, there is the diameter approximation after i steps.
	 */
	public IntArrayList getSumSweepResults() {return this.sumSweepResults;}

	/**
	 * Perform some steps of the SumSweep heuristic.
	 * @param start the starting vertex of the first BFS.
	 * @param initialSSIter the number of steps performed.
	 */
	public void sumSweep(int start, int initialSSIter) {
		sumSweepResults = new IntArrayList();
		int v = this.stepSumSweep(start, true);
		sumSweepResults.add(D);
		while (iter < initialSSIter) {
			Utilities.outputLine("BFS " + iter + " complete!", 2);
			this.stepSumSweep(v, true);
			sumSweepResults.add(D);
			v = Utilities.argMax(totDist, u);
			if (v == -1) {
				break;
			}
		}
		//this.stepSumSweep(Utilities.argMin(totDist, l), true);
	}
	
	/**
	 * Computes the diameter and the radius of a graph with the SumSweep technique. To do so, it performs some steps
	 * of the SumSweep heuristic, then it starts bounding the eccentricities of the remaining vertices. In the meantime, 
	 * it keeps a lower bound on the diameter and an upper bound on the radius. As soon as the lower bound on the diameter
	 * is as high as the biggest upper bound on the eccentricity, the diameter is found. Similarly, the radius is found 
	 * when the upper bound on the radius is as low as the smallest lower bound on the eccentricity.
	 * The results are stored in the variables R and D, and they can be obtained using getR() and getD().
	 * @param start the starting vertex for the first BFS of the SumSweep heuristic
	 * @param initialSSIter the number of iterations of the first SumSweep heuristic.
	 */
	public void run(int start, int initialSSIter) {
		this.startTime();
		int v = start, w = 0, i = 0, cost[] = {1, 1, 1, 1, 1};
		double points[] = new double[5];

		Utilities.outputLine("Initial SumSweep started!", 2);
		this.sumSweep(start, initialSSIter);

		DoubleArrays.fill(points, getNN());
		//points[2] = 100;

		this.printData();

		while (true) {


			w = Utilities.argMax(u, totDist);
			v = Utilities.argMin(l, totDist);

			if (iterR == -1 && (v == -1 || l[v] >= this.R)) {
				iterR = iter;
			}
			if (iterD == -1 && (w == -1 || u[w] <= D)) {
				iterD = iter;
			}
			if (iterD != -1 && iterR != -1) {
				break;
			}

//			if (iterD != -1) {
//				points[0] = -100;
//				points[1] = -100;
//			} if (iterR != -1) {
//				points[2] = -100;
//				points[0] = -100;
//			}

			i = Utilities.argMax(points);

			switch (i) {
			case 0:
				this.stepSumSweep(v, true);
				break;
			case 1:
				this.stepSumSweep(w, true);
				break;
			case 2:
				this.stepSumSweep(v, true);
				break;
			case 3:
				this.stepSumSweep(w, true);
				break;
			case 4:
				this.stepSumSweep(Utilities.argMax(totDist, u), true);
				break;
			}
			DecimalFormat df = new DecimalFormat("0.00"); 
			Utilities.output("BFS " + iter + ": " + df.format(points[0]) + " " + df.format(points[1]) + " " + df.format(points[2]) + " " + df.format(points[3]) + " " + df.format(points[4]) + ". ", 2);

			this.printData();
			points[i] = ((double)lastImprovement / cost[i]);
			for (int j = 1; j < points.length; j++) {
				points[(i + j) % points.length] = points[(i + j) % points.length] + 2.0 / iter;
			}
		}
		Utilities.outputLine("Radius:   " + getR() + " (" + getIterR() + " iterations).", 1);
		Utilities.outputLine("Diameter: " + getD() + " (" + getIterD() + " iterations).", 1);
		this.endTime();
	}
	
	/**
	 * Runs this algorithm using the parameters provided.
	 * @param parameters the parameters used.
	 */
	public static void main(JSAPResult parameters) {
		if (parameters.getString("input") == null) {
			Utilities.output("You did not provide all the required arguments. The arguments to provide are:\n" +
					"-i (--input) (COMPULSORY): the file to load.", 0);
			return;
		}
		Utilities.outputLine("Computing the diameter and radius of graph " + parameters.getString("input") + ".", 1);
		SumSweepUndir g = new SumSweepUndir(Undir.load("inputWebgraph/Undirected/" + parameters.getString("input"), GraphTypes.ADJLIST));
		g.runAuto();
		Utilities.outputLine("Elapsed time: " + ((double) g.getElapsedTime()) / 1000 + " seconds.", 1);
	}
	
	/**
	 * Runs this algorithm using the parameters provided.
	 * @param args the parameters used.
	 */
	public static void main(String args[]) {
		try {
			main(Utilities.parseArguments(args));
		} catch (JSAPException e) {
			e.printStackTrace();
		}
	}
}

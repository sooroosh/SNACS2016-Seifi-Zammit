package alg.distances;

import java.text.DecimalFormat;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import graph.Dir;
import graph.GraphTypes;
import graph.LoadMethods;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.webgraph.LazyIntIterator;
import utilities.Utilities;
import visit.Dist;
import visit.DistMultipleInSCC;
/**
 * This class implements the directed SumSweep algorithm, as explained in the article
 * Borassi et al, Fast Diameter and Radius Computation in Real-World Graphs.
 */
public class SumSweepDir {
	private int lF[];
	private int uF[];
	private int lB[];
	private int uB[];
	private int totDistF[];
	private int totDistB[];
	private boolean accRadius[];
	private int R;
	private int D;
	private int Dv;
	private int Rv;
	private int iterR = -1;
	private int iterD = -1;
	private int iter = 0;
	public graph.Dir graph;
	private graph.Dir sccDag;
	private visit.SCC scc;
	private IntArrayList sumSweepResults;
	private int ccBonus[];
	private IntArrayList[] edgesThroughSCCF;
	private IntArrayList[] edgesThroughSCCB;

	private int stillToDo;
	private int lastImprovement = 0;

	/**
	 * @return the radius computed
	 */
	public int getR() {return R;}
	/**
	 * @return the diameter of the graph
	 */
	public int getD() {return D;}
	/**
	 * @return the number of iterations to compute the radius
	 */
	public int getIterR() {return iterR;}
	/**
	 * @return the number of iterations to compute the diameter
	 */
	public int getIterD() {return iterD;}
	/**
	 * @return a diametral vertex
	 */
	public int getDv() {return Dv;}
	
	/**
	 * @return a radial vertex
	 */
	public int getRv() {return Rv;}
	
	
	
	/**
	 * Fills the variables edgesThroughSCCF and edgesThroughSCCB by storing an edge in the graph for each pair of connected SCCs
	 */
	private void findEdgesThroughSCC() {
		int v = 0, w;
		int arcs[] = new int[2 * scc.getNCC()];
		IntArrays.fill(arcs, -1);
		LazyIntIterator iter;
		edgesThroughSCCF = new IntArrayList[scc.getNCC()];
		edgesThroughSCCB = new IntArrayList[scc.getNCC()];
		
		
		for (int i = 0; i < this.scc.getNCC(); i++) {
			edgesThroughSCCF[i] = new IntArrayList();
			while (v < graph.getNN() && scc.cc[v] == i) {
				iter = graph.getAdj(v);
				while ((w = iter.nextInt()) != -1 ) {
					if (scc.cc[w] != scc.cc[v] && (arcs[2 * scc.cc[w]] == -1 || (graph.getInDeg(v) + graph.getOutDeg(w) 
							> graph.getInDeg(arcs[2 * scc.cc[w]]) + graph.getOutDeg(arcs[2 * scc.cc[w] + 1])))) {
						arcs[2 * scc.cc[w]] = v;
						arcs[2 * scc.cc[w] + 1] = w;
					}
				}
				v++;
			}
			iter = this.sccDag.getAdj(i);
			while ((w = iter.nextInt()) != -1 ) {
				edgesThroughSCCF[i].add(arcs[2 * w]);
				edgesThroughSCCF[i].add(arcs[2 * w + 1]);
				arcs[2 * w] = -1;
				arcs[2 * w + 1] = -1;
			}
		}
		

		v = 0;
		for (int i = 0; i < this.scc.getNCC(); i++) {
			edgesThroughSCCB[i] = new IntArrayList();
			while (v < graph.getNN() && scc.cc[v] == i) {
				iter = graph.getInc(v);
				while ((w = iter.nextInt()) != -1 ) {
					if (scc.cc[w] != scc.cc[v] && (arcs[2 * scc.cc[w]] == -1 || (graph.getOutDeg(v) + graph.getInDeg(w) 
							> graph.getOutDeg(arcs[2 * scc.cc[w]]) + graph.getInDeg(arcs[2 * scc.cc[w] + 1])))) {
						arcs[2 * scc.cc[w]] = v;
						arcs[2 * scc.cc[w] + 1] = w;
					}
				}
				v++;
			}
			iter = this.sccDag.getInc(i);
			while ((w = iter.nextInt()) != -1 ) {
				edgesThroughSCCB[i].add(arcs[2 * w]);
				edgesThroughSCCB[i].add(arcs[2 * w + 1]);
				arcs[2 * w] = -1;
				arcs[2 * w + 1] = -1;
			}
		}
	}
	
	/**
	 * Instantiates this object using the biggest weakly connected component of the given graph.
	 * @param graph the input graph.
	 */
	public SumSweepDir(graph.Dir graph) {
		this.graph = graph;
		Utilities.outputLine("Size: " + graph.getNN() + " nodes, " + graph.getNE() + " edges.", 2);
		this.graph.transformIntoBiggestWCC();
		Utilities.outputLine("Main WCC size: " + graph.getNN() + " nodes, " + graph.getNE() + " edges.", 2);

		scc = this.graph.findSCC();
		this.sccDag = this.graph.collapseVertices(scc.cc, scc.getNCC());
		scc = this.graph.findSCC();

		computeAccRadius();

		totDistF = new int[graph.getNN()];
		totDistB  = new int[graph.getNN()];
		lF = new int[graph.getNN()];
		lB = new int[graph.getNN()];
		uF = new int[graph.getNN()];
		uB = new int[graph.getNN()];
		IntArrays.fill(uF, graph.getNN() + 1);
		IntArrays.fill(uB, graph.getNN() + 1);

		Utilities.outputLine("Strongly connected components: " + scc.getNCC() + ".", 1);

		iter = 0;
		iterR = -1;
		iterD = -1;
		R = graph.getNN();
		stillToDo = graph.getNN();
		ccBonus = new int[scc.sizes.size()];
		findEdgesThroughSCC();
	}


	/**
	 * Fills the array accRadius with all vertices that are candidates to be radial vertices
	 */
	public void computeAccRadius() {

		IntArrayList vertInMaxSizeCC = new IntArrayList();

		for (int v = 0; v < graph.getNN(); v++) {
			if (scc.sizes.getInt(scc.cc[v]) == scc.sizes.getInt(scc.maxSizeCC)) {
				vertInMaxSizeCC.add(v);
			}
		}

		visit.ConnBFS visit = new visit.ConnBFS(graph.getNN(), vertInMaxSizeCC.toIntArray());
		graph.BBFS(visit);
		accRadius = visit.visited;

	}

	/**
	 * Prints some data 
	 * @param visitF
	 * @param visitB
	 */
	public void printVisitData(visit.Dist visitF, visit.Dist visitB) {

		int visitedF = 0, visitedB = 0, visitedA = 0;
		for (int v = 0; v < this.graph.getNN(); v++) {
			if (visitF.dist[v] >= 0) {
				visitedF++;
			}
			if (visitB.dist[v] >= 0) {
				visitedB++;
			}
			if (visitF.dist[v] >= 0 && visitB.dist[v] >= 0) {
				visitedA++;
			}
		}
		Utilities.outputLine("(visited F: " + visitedF + ", visited B: " + visitedB + ", always visited: " + visitedA + ").", 2);

	}

	/**
	 * Prints some data computed during the SumSweep.
	 */
	public void printData() {
		int toDoUntilR = 0;
		int toDoUntilDF = 0;
		int toDoUntilDB = 0;

		for (int i = 0; i < graph.getNN(); i++) {
			if (lF[i] < R && totDistF[i] >= 0 && this.accRadius[i]) {
				toDoUntilR++;
			} 
			if (uF[i] > D && totDistF[i] >= 0) {
				toDoUntilDF++;
			}
			if (uB[i] > D && totDistB[i] >= 0) {
				toDoUntilDB++;
			}
		}
		if (iterR == -1) {
			Utilities.output("    Approximated radius: " + R + " (still to do: " + toDoUntilR + ")\n", 2);
		} else {
			Utilities.output("    Radius: " + R + "\n", 2);
		}
		if (iterD == -1) {
			Utilities.output("    Approximated diameter: " + D + " (still to do F: " + toDoUntilDF + ", still to do B: " + toDoUntilDB + ")\n", 2);
		} else {
			Utilities.output("    Diameter: " + D + "\n", 2);
		}

		int currentValue = Math.min(toDoUntilDF, toDoUntilDB) + toDoUntilR;
		lastImprovement = stillToDo - currentValue;
		stillToDo = currentValue;
	}



	/**
	 * Updates, if necessary, the approximated values of R and D, after a forward eccentricity has been computed.
	 * @param v the vertex of which the forward eccentricity is computed.
	 */
	private void checkNewEccF(int v) {
		if (lF[v] < R && this.accRadius[v]) {
			R = lF[v];
			Rv = v;
		}
		if (lF[v] > D) {
			D = lF[v];
			Dv = v;
		}
	}

	/**
	 * Updates, if necessary, the approximated values of R and D, after a backward eccentricity has been computed.
	 * @param v the vertex of which the forward eccentricity is computed.
	 */
	private void checkNewEccB(int v) {
		if (lB[v] > D) {
			D = lB[v];
			Dv = v;
		}
	}



	/**
	 * Finds the best pivot vertices for a SingleCCUpperBound
	 * @return an array containing in position i the pivot of component i
	 */
	private int[] findBestPivot() {
		int pivot[] = new int[this.scc.getNCC()];
		int j = 0;
		long currentBest, current;

		for (int i = 0; i < pivot.length; i++) {
			currentBest = 4 * graph.getNN();
			while (j < graph.getNN() && scc.cc[j] == i) {
				current = (lF[j] + lB[j]);

				if (totDistF[j] < 0) {
					current += graph.getNN();
				}
				if (totDistB[j] < 0) {
					current += graph.getNN();
				}

				if (currentBest > current || (currentBest == current && totDistF[j] + totDistB[j] < totDistF[pivot[i]] + totDistB[pivot[i]])) {
					currentBest = current;
					pivot[i] = j;
				}
				j++;
			}
		}
		return pivot;
	}


	/**
	 * Finds the best vertex to run a stepSumSweepBoth (that is, the vertex such that lF(i) + lB(i) is minimum).
	 * @return the best vertex to perform a stepSumSweepBoth
	 */
	private int findPivotForSingleCCDiam(int pivot[]) {
		int[] sccBonus = this.computeSCCBonus();
		return pivot[Utilities.argMax(sccBonus, sccBonus)];
	}

	/** Computes a step of the SumSweep, performing a backward BFS. Updates the bounds.
	 * @param start the starting vertex
	 */
	private void stepSumSweepBackward(int start) {

		if (start == -1) {
			return;
		}

		Utilities.output("Using backward BFS from " + start + " ", 2);

		visit.Dist visit = new visit.Dist(graph.getNN(), start);
		int visited = 0;

		totDistB[start] = -2;
		graph.BBFS(visit);
		for (int v = 0; v < this.graph.getNN(); v++) {
			if (visit.dist[v] >= 0) {
				visited++;
			}
		}
		Utilities.outputLine("(visited: " + visited + "). ", 2);

		int eccB;

		iter++;
		eccB = visit.dist[visit.far];

		lB[start] = eccB;
		uB[start] = eccB;

		checkNewEccB(start);


		for (int i = 0; i < graph.getNN(); i++) {
			if (totDistF[i] >= 0) {

				lF[i] = Math.max(lF[i], visit.dist[i]);

				if (visit.dist[i] != -1) {
					totDistF[i] += visit.dist[i];
				}
			} 
		}
	}

	/**
	 * Check if the lower and upper bounds computed are equal for some vertices. Updates eccentricity if necessary.
	 */
	private void checkNewBounds() {
		for (int i = 0; i < graph.getNN(); i++) {
			if (totDistF[i] >= 0 && lF[i] == uF[i]) {
				totDistF[i] = -1;
				checkNewEccF(i);
			} 
			if (totDistB[i] >= 0 && lB[i] == uB[i]) {
				totDistB[i] = -1;
				checkNewEccB(i);
			} 
		}
	}


	/** Computes a step of the SumSweep, performing a forward BFS. Updates the bounds.
	 * @param start the starting vertex
	 */
	private void stepSumSweepForward(int start) {
		Utilities.output("Using forward BFS from " + start + " ", 2);

		if (start == -1) {
			return;
		}

		visit.Dist visit = new visit.Dist(graph.getNN(), start);
		int visited = 0;

		totDistF[start] = -2;
		graph.BFS(visit);
		for (int v = 0; v < this.graph.getNN(); v++) {
			if (visit.dist[v] >= 0) {
				visited++;
			}
		}

		Utilities.outputLine("(visited: " + visited + "). ", 2);
		int eccF;

		iter++;
		eccF = visit.dist[visit.far];

		lF[start] = eccF;
		uF[start] = eccF;

		totDistF[start] = -1;

		checkNewEccF(start);

		for (int i = 0; i < graph.getNN(); i++) {

			if (totDistB[i] >= 0) {

				lB[i] = Math.max(lB[i], visit.dist[i]);

				if (visit.dist[i] != -1) {
					totDistB[i] += visit.dist[i];
				}
			} 
		}
	}




	/**
	 * Performs a BFS and updates all required bounds
	 * @param start the starting vertex
	 * @param forward if true, the BFS is performed forward.
	 * @return a visit.DistWithSCC containing all necessary information.
	 */
	private visit.DistWithSCC singleBFS(int start, boolean forward) {
		visit.DistWithSCC visit = new visit.DistWithSCC(graph.getNN(), start, scc.cc);
		int ecc;
		if (forward) {
			graph.BFS(visit);
			ecc = visit.dist[visit.far];

			totDistF[start] = -2;
			lF[start] = ecc;
			uF[start] = ecc;
			checkNewEccF(start);
		} else {
			graph.BBFS(visit);
			ecc = visit.dist[visit.far];

			totDistB[start] = -2;
			lB[start] = ecc;
			uB[start] = ecc;
			checkNewEccB(start);
		}
		return visit;
	}

	/**
	 * Upper bounds the eccentricity of all pivot vertices 
	 * @param visitDistF
	 * @param visitDistB
	 * @param visitInSCCF
	 * @param visitInSCCB
	 * @param pivot
	 * @return an array containing in position 2*i an upper bound on the forward eccentricity of the pivot of the SCC i, 
	 * in position 2*i+1 an upper bound on the backward eccentricity of the same pivot.
	 */
	private int[] findEccPivot(Dist visitDistF, Dist visitDistB, DistMultipleInSCC visitInSCCF, DistMultipleInSCC visitInSCCB, int pivot[]) {
		int ecc[] = new int[2 * this.sccDag.getNN()];
		int eccFalse[] = new int[2 * this.sccDag.getNN()];
		int w, j;
		LazyIntIterator iter;
	
		for (int v = 0; v < sccDag.getNN(); v++) {

			if (visitDistF.dist[pivot[v]] != -1) {
				eccFalse[2 * v] = 0;
			} else {
				eccFalse[2 * v] = visitInSCCF.eccStart[v];
				iter = sccDag.getAdj(v);
				for (j = 0; j < sccDag.getOutDeg(v); j++) {
					w = iter.nextInt();
					if (visitDistF.dist[pivot[w]] == -1) {
						eccFalse[2 * v] = Math.max(eccFalse[2 * v], eccFalse[2 * w] + visitInSCCF.dist[this.edgesThroughSCCF[v].getInt(2 * j)] + 1 + visitInSCCB.dist[this.edgesThroughSCCF[v].getInt(2 * j + 1)]);
					}
				}
			}
			if (visitDistB.dist[pivot[v]] == -1) {
				ecc[2 * v] = visitInSCCF.eccStart[v];
				iter = sccDag.getAdj(v);
				
				for (j = 0; j < sccDag.getOutDeg(v); j++) {
					w = iter.nextInt();
					ecc[2 * v] = Math.max(ecc[2 * v], ecc[2 * w] + visitInSCCF.dist[this.edgesThroughSCCF[v].getInt(2 * j)] + 1 + visitInSCCB.dist[this.edgesThroughSCCF[v].getInt(2 * j + 1)]);
				}
			} else {

				ecc[2 * v] = Math.max(visitDistB.dist[pivot[v]] + visitDistF.dist[visitDistF.far], visitInSCCF.eccStart[v]);
				iter = sccDag.getAdj(v);
				for (j = 0; j < sccDag.getOutDeg(v); j++) {
					w = iter.nextInt();
					if (visitDistF.dist[pivot[w]] == -1) {
						ecc[2 * v] = Math.max(ecc[2 * v], eccFalse[2 * w] + visitInSCCF.dist[this.edgesThroughSCCF[v].getInt(2 * j)] + 1 + visitInSCCB.dist[this.edgesThroughSCCF[v].getInt(2 * j + 1)]);
					}
				}
			}
			ecc[2 * v] = Math.min(ecc[2 * v], uF[pivot[v]]);
			eccFalse[2 * v] = Math.min(eccFalse[2 * v], uF[pivot[v]]);	
		}

		for (int v = sccDag.getNN() - 1; v >= 0; v--) {
			if (visitDistB.dist[pivot[v]] != -1) {
				eccFalse[2 * v + 1] = 0;
			} else {
				eccFalse[2 * v + 1] = visitInSCCB.eccStart[v];
				iter = sccDag.getInc(v);
				for (j = 0; j < sccDag.getInDeg(v); j++) {
					w = iter.nextInt();
					if (visitDistB.dist[pivot[w]] == -1) {
						eccFalse[2 * v + 1] = Math.max(eccFalse[2 * v + 1], eccFalse[2 * w + 1] + visitInSCCF.dist[this.edgesThroughSCCB[v].getInt(2 * j)] + 1 + visitInSCCB.dist[this.edgesThroughSCCB[v].getInt(2 * j + 1)]);
					}
				}
			}
			if (visitDistF.dist[pivot[v]] == -1) {
				
				ecc[2 * v + 1] = visitInSCCB.eccStart[v];
				iter = sccDag.getInc(v);
				
				for (j = 0; j < sccDag.getInDeg(v); j++) {
					w = iter.nextInt();
					ecc[2 * v + 1] = Math.max(ecc[2 * v + 1], ecc[2 * w + 1] + visitInSCCB.dist[this.edgesThroughSCCB[v].getInt(2 * j)] + 1 + visitInSCCF.dist[this.edgesThroughSCCB[v].getInt(2 * j + 1)]);
				}
			} else {
				ecc[2 * v + 1] = Math.max(visitDistF.dist[pivot[v]] + visitDistB.dist[visitDistB.far], visitInSCCB.eccStart[v]);
				iter = sccDag.getInc(v);
				for (j = 0; j < sccDag.getInDeg(v); j++) {
					w = iter.nextInt();
					if (visitDistF.dist[pivot[w]] == -1) {
						ecc[2 * v + 1] = Math.max(ecc[2 * v + 1], eccFalse[2 * w + 1] + visitInSCCB.dist[this.edgesThroughSCCB[v].getInt(2 * j)] + 1 + visitInSCCF.dist[this.edgesThroughSCCB[v].getInt(2 * j + 1)]);
					}
				}
			}
			ecc[2 * v + 1] = Math.min(ecc[2 * v + 1], uB[pivot[v]]);
			eccFalse[2 * v + 1] = Math.min(eccFalse[2 * v + 1], uB[pivot[v]]);	
		}
		return ecc;
	}



	/** 
	 * Performs a step using the singleCCUpperBound technique.
	 */
	private void singleCCUpperBound() {
		int[] scc = this.scc.cc;
		int[] pivot = this.findBestPivot();
		int start = this.findPivotForSingleCCDiam(pivot);

		if (start == -1) {
			return;
		}

		Utilities.output("Using all CC upper bound from " + start + " ", 2);

		visit.DistWithSCC visitDistF = this.singleBFS(start, true);
		visit.DistWithSCC visitDistB = this.singleBFS(start, false);

		this.printVisitData(visitDistF, visitDistB);

		visit.DistMultipleInSCC visitInSCCF = new visit.DistMultipleInSCC(graph.getNN(), scc);
		visitInSCCF.run(graph, pivot, start, visitDistF.eccInSCC, true);
		visit.DistMultipleInSCC visitInSCCB = new visit.DistMultipleInSCC(graph.getNN(), scc);
		visitInSCCB.run(graph, pivot, start, visitDistB.eccInSCC, false);

		int[] eccPivot = findEccPivot(visitDistF, visitDistB, visitInSCCF, visitInSCCB, pivot);

		for (int v = 0; v < graph.getNN(); v++) {

			if (visitDistF.dist[v] >= 0 && visitDistB.dist[v] >= 0) {
				uF[v] = Math.min(uF[v], eccPivot[2 * scc[v]] + visitDistB.dist[v]); 
				uB[v] = Math.min(uB[v], eccPivot[2 * scc[v] + 1] + visitDistF.dist[v]); 
			} else {
				uF[v] = Math.min(uF[v], eccPivot[2 * scc[v]] + visitInSCCB.dist[v]); 
				uB[v] = Math.min(uB[v], eccPivot[2 * scc[v] + 1] + visitInSCCF.dist[v]); 
			}
		}

		if (this.scc.getNCC() == 1) {
			iter += 2;
		} else {
			iter += 3;
		}
	}



	/**
	 * Runs the SumSweep with the best parameters, according to our experiments.
	 */
	public void runAuto() {
		run(graph.maxOutDegVert(), 6);
	}


	/**
	 * @return the results obtained during the initial SumSweep. In position i, there is the diameter approximation after i steps.
	 */
	public IntArrayList getSumSweepResults() {
		return this.sumSweepResults;
	}

	/**
	 * Perform some steps of the SumSweep heuristic.
	 * @param start the starting vertex of the first BFS.
	 * @param initialSumSweepIter the number of steps performed.
	 */
	public void sumSweep(int start, int initialSumSweepIter) {

		initialSumSweepIter = Math.min(initialSumSweepIter, graph.getNN());
		this.stepSumSweepForward(start);
		Utilities.outputLine("BFS " + iter + " complete!", 2);

		this.sumSweepResults = new IntArrayList();
		this.sumSweepResults.add(D);

		for (int i = 1; i < initialSumSweepIter; i++) {
			if (i % 2 == 0) {
				this.stepSumSweepForward(Utilities.argMax(totDistF, lF));
			} else {
				this.stepSumSweepBackward(Utilities.argMax(totDistB, lB));
			}
			this.sumSweepResults.add(D);
			Utilities.outputLine("BFS " + iter + " complete!", 2);
		}

		int lFRad[] = new int[graph.getNN()];
		for (int k = 0; k < graph.getNN(); k++) {
			if (this.accRadius[k]) {
				lFRad[k] = lF[k];
			} else {
				lFRad[k] = -1;
			}
		}
		this.stepSumSweepForward(Utilities.argMin(totDistF, lFRad));
		Utilities.outputLine("BFS " + iter + " complete!", 2);
	}

	/**
	 * @return an array containing, in position i, the number of open vertices in component i.
	 */
	private int[] computeSCCBonus() {
		int stillToDoF = 0, stillToDoB = 0;
		int toReturn[] = new int[scc.getNCC()];

		for (int x = 0; x < ccBonus.length; x++) {
			toReturn[x] = 0;
		}

		for (int x = 0; x < graph.getNN(); x++) {
			if (uF[x] > D) {
				stillToDoF++;
			} else if (uB[x] > D) {
				stillToDoB++;
			}
		}

		if (stillToDoF < stillToDoB) {

			for (int x = 0; x < graph.getNN(); x++) {
				if ((lF[x] < R && this.accRadius[x]) || uF[x] > D) {
					toReturn[scc.cc[x]]++;
				}
			}
		} else {
			for (int x = 0; x < graph.getNN(); x++) {
				if ((lF[x] < R && this.accRadius[x]) || uB[x] > D) {
					toReturn[scc.cc[x]]++;
				}
			}
		}
		return toReturn;
	}


	/**
	 * Computes the diameter and the radius of a graph with the SumSweep technique. To do so, it performs some steps
	 * of the SumSweep heuristic, then it starts bounding the eccentricities of the remaining vertices. In the meantime, 
	 * it keeps a lower bound on the diameter and an upper bound on the radius. As soon as the lower bound on the diameter
	 * is as high as the biggest upper bound on the eccentricity, the diameter is found. Similarly, the radius is found 
	 * when the upper bound on the radius is as low as the smallest lower bound on the eccentricity.
	 * The results are stored in the variables R and D, and they can be obtained using getR() and getD().
	 * @param start the starting vertex for the first BFS of the SumSweep heuristic
	 * @param initialSumSweepIter the number of iterations of the first SumSweep heuristic.
	 */
	public void run(int start, int initialSumSweepIter) {
		this.graph.startTime();
		int v = start, w1 = 0, w2 = 0, i = 0, cost[] = {1, 1, 1, 1, 1};
		double points[] = new double[5];

		Utilities.outputLine("Initial SumSweep started!", 2);
		this.sumSweep(start, initialSumSweepIter);

		DoubleArrays.fill(points, graph.getNN());
		//points[2] = 100;

		this.printData();
		while (true) {
			
			int lFRad[] = new int[graph.getNN()];
			for (int x = 0; x < graph.getNN(); x++) {
				if (this.accRadius[x]) {
					lFRad[x] = lF[x];
				} else {
					lFRad[x] = -1;
				}
			}

			w1 = Utilities.argMax(uF, totDistF);
			w2 = Utilities.argMax(uB, totDistB);
			v = Utilities.argMin(lFRad, totDistF);

			if (iterR == -1 && (v == -1 || lF[v] >= this.R)) {
				iterR = iter;
			}
			if (iterD == -1 && (w1 == -1 || uF[w1] <= D || w2 == -1 || uB[w2] <= D)) {
				iterD = iter;
			}
			if (iterD != -1 && iterR != -1) {
				break;
			}
			DecimalFormat df = new DecimalFormat("0.00"); 
			Utilities.output("BFS " + iter + ": " + df.format(points[0]) + " " + df.format(points[1]) + " " + df.format(points[2]) + " " + df.format(points[3]) + " " + df.format(points[4]) + ". ", 2);


			if (iterD != -1) {
				points[0] = -100;
				points[1] = -100;
			} if (iterR != -1) {
				points[2] = -100;
				points[2] = -100;
			}

			i = Utilities.argMax(points);

			switch (i) {
			case 0: // ONLY DIAM
				this.singleCCUpperBound();
				break;
			case 1: // ONLY DIAM
				this.stepSumSweepForward(w1);
				break;
			case 2: // ONLY RAD
				this.stepSumSweepForward(v);
				break;
			case 3: // BOTH
				this.stepSumSweepBackward(w2);
				break;
			case 4:
				this.stepSumSweepBackward(Utilities.argMax(totDistB, uB));
				break;
			}
			this.checkNewBounds();
			
			this.printData();
			points[i] = ((double)lastImprovement / cost[i]);
			for (int j = 1; j < points.length; j++) {
				points[(i + j) % points.length] = points[(i + j) % points.length] + 2.0 / iter;
			}
		}
		Utilities.outputLine("Radius:   " + getR() + " (" + getIterR() + " iterations).", 1);
		Utilities.outputLine("Diameter: " + getD() + " (" + getIterD() + " iterations).", 1);
		this.graph.endTime();
	}
	/**
	 * Runs this algorithm using the parameters provided.
	 * @param parameters the parameters used.
	 */
	public static void mainScc(JSAPResult parameters) {
		if (parameters.getString("input") == null) {
			Utilities.output("You did not provide all the required arguments. The arguments to provide are:\n" +
					"-i (--input) (COMPULSORY): the file to load.", 0);
			return;
		}
		Utilities.outputLine("Computing the diameter and radius of the biggest SCC of graph " + parameters.getString("input") + ".", 1);
		Dir graph = Dir.load("inputWebgraph/Directed/" + parameters.getString("input"), GraphTypes.ADJLIST, LoadMethods.WEBGRAPH);
		graph.transformIntoBiggestSCC();
		SumSweepDir g = new SumSweepDir(graph);
		g.runAuto();
		Utilities.outputLine("Elapsed time: " + ((double) g.graph.getElapsedTime()) / 1000 + " seconds.", 1);
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
		SumSweepDir g = new SumSweepDir(Dir.load("inputWebgraph/Directed/" + parameters.getString("input"), GraphTypes.ADJLIST, LoadMethods.WEBGRAPH));
		g.runAuto();
		Utilities.outputLine("Elapsed time: " + ((double) g.graph.getElapsedTime()) / 1000 + " seconds.", 1);
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
	
	public int getlF(int i) {return lF[i];}
	public int getuF(int i) {return uF[i];}
	public int getlB(int i) {return lB[i];}
	public int getuB(int i) {return uB[i];}
}

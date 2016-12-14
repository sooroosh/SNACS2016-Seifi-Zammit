package alg.distances;

import java.util.Random;

import it.unimi.dsi.fastutil.ints.IntArrays;

/**
 * This class computes the distance distribution of a graph by performing a BFS from each vertex.
 */
public class DistDistr extends Thread {

	public int R, D, start, end, nThreads;
	public int[][] dist;
	public int[] ecc;
	private graph.GeneralGraph g;
	public int[] toBeAnalyzed;
	public int nSamples;
	private boolean saveAllDistances;


	/**
	 * Creates a new DistDistr object.
	 * @param g the graph
	 * @param nThreads the number of parallel threads launched
	 * @param nThreadsBFS the number of parallel threads launched during a BFS
	 */
	public DistDistr(graph.GeneralGraph g, int nThreads, int nThreadsBFS, boolean saveAllDistances) {
		this.g = g;
		this.nThreads = nThreads;
		this.saveAllDistances = saveAllDistances;
		ecc = new int[g.getNN()];
	}


	private DistDistr(DistDistr d, int start, int end) {
		this.g = d.g;
		this.start = d.start;
		this.end = d.end;
		this.dist = d.dist;
		this.ecc = d.ecc;
		this.toBeAnalyzed = d.toBeAnalyzed;
		this.start = start;
		this.end = end;
		this.saveAllDistances = d.saveAllDistances;
	}
	
	
	/**
	 * Samples the distance distribution of this graph.
	 */
	public void computeDistanceDistribution() {
		this.computeDistanceDistribution(this.g.getNN());
	}
	

	/**
	 * Computes the distance distribution of this graph.
	 */
	public void computeDistanceDistribution(int nSamples) {
		g.startTime();
		nSamples = Math.min(nSamples, g.getNN());

		int i;
		if (this.saveAllDistances) {
			dist = new int[nSamples][g.getNN()];
		}
		D = -1;
		R = g.getNN() + 1;
		int batchSize = nSamples / nThreads;
		DistDistr threads[] = new DistDistr[nThreads];
		this.nSamples = nSamples;
		
		toBeAnalyzed = new int[g.getNN()];
		for (int j = 0; j < toBeAnalyzed.length; j++) {
			toBeAnalyzed[j] = j;
		}
		
		toBeAnalyzed = IntArrays.shuffle(toBeAnalyzed, new Random());

		try {
			for (i = 0; i < nThreads - 1; i++) {
				threads[i] = new DistDistr(this, i * batchSize, (i+1) * batchSize);
				threads[i].start();
			}
			threads[i] = new DistDistr(this, i * batchSize, nSamples);
			threads[i].start();

			for (i = 0; i < nThreads; i++) {
				threads[i].join();
				D = Math.max(D, threads[i].D);
				R = Math.min(R, threads[i].R);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		g.endTime();
	}


	/**
	 * Tests if a new radial/diametral vertex is found.
	 * @param ecc the eccentricity of the vertex.
	 */
	public void testNewEcc(int ecc) {
		D = Math.max(D, ecc);
		R = Math.min(R, ecc);
	}

	/**
	 * Computes some of the necessary BFSes, from vertex start to vertex end. In case, the BFSes performed are parallel.
	 */
	@Override
	public void run() {		
		visit.Dist visit = new visit.Dist(g.getNN(), 0);

		for (int i = start; i < end; i++) {
			visit.setStart(this.toBeAnalyzed[i]);
			g.BFS(visit);
			
			for (int j = 0; j < g.getNN(); j++) {
				if (this.saveAllDistances && dist != null) {
					dist[i][j] = visit.dist[j];
				}
			}
			ecc[toBeAnalyzed[i]] = visit.dist[visit.far];
			testNewEcc(visit.dist[visit.far]);
		}
	}
}

/*
 * Created on 30-Dec-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package gad;


import java.util.*;
/**
 * @author Simon
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class NKProblem extends Problem {
	
	int n; // number of nodes
	int k; // number of connections from any node
	int num_f;

	int[][] m; // connectivity matrix
	double[][] f; // contributory fitness of each allele 

	Random r;// want this fixed so same NKnet produced

	//protected 
	
	public NKProblem() {
		super();
		n = Config.getInt("N");
		k = Config.getInt("K");
	}

	public int getK() { return k; }

	public void initialise() {
		//super.initialise();
		log.debug("Initialising NK net");
		n = Config.getInt("GenomeLength");
		num_f = n; // for time being set num of fitness components same as genome length

		r = new Random(12345); // same net each time please!

		log.debug("random seed set");

		// set contributory allele fitness value
		f = new double[num_f][];
		for(int i = 0; i < n; i++) {
			f[i] = new double[] { r.nextDouble(), r.nextDouble() };
		}

		m = new int[n][num_f]; // assuming this will be initialised with zeros
		// setup k unique connection - asymetric
		for(int i = 0; i < n; i++) { // at each locus
			for(int j = 0; j < k; j++) { // make k connections
				int c;
				do { // find new connection to make
					c = (int)(r.nextDouble() * n);
				} while(m[i][c] == 1);
				m[i][c] = 1;
			}
		}    
		log.debug("NK net initialised");
		r = new Random();
	}

	public double evaluate(Genome g) {
		// sum fitnesses
		double sum = 0;
		for(int i = 0; i < n; i++) { // for each locus
			for(int j = 0; j < num_f; j++) {
				if(m[i][j] == 1) { // see if connected to fitness component
					sum += f[i][((IntGenome)g).get(j)]; // add if it is
					
				}
				
			}
			//if(((BitGenome)g).get(i) == 1) sum++;
		}
		return sum / k / n;
		//return sum / n;
	}
	/*
	 public static void main(String[] args) {
	 Population p = new Population(10);
	 p.setLength(10);
	 NKGA obj = new NKGA(p);
	 obj.initialise(2);
	 BitGenome g = new BitGenome(10);
	 System.out.println(g.toString() + " = " + obj.fitness(g));
	 
	 }
	 */
}

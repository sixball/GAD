/*
 * Created on 14-Jun-2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package gad;

/**
 * @author Simon
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class TransGeneticAlgorithm extends SearchAlgorithm {

	GeneSetModel pool;

	GeneSet evalSet;

	GeneSet setOfSets;
	
	public CreditMap credit;
	
	private double[] roulette;
	private double rouletteSum;

	/**
	 * The pool represents the available structures that can be drawn from to
	 * create the temporary population that is used for evaluation.
	 */
	public TransGeneticAlgorithm() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TransGeneticAlgorithm(SearchModel model, Problem problem) {
		super(model, problem);
		//log.debug("constructing tga");
		pool = (GeneSetModel) model;
		// kludge
	}

	public void initialise() {
		// create genepool
		//  this done with GAD.main now : pool = new GeneSetModel(config);
		//pool.initialise();
		credit = new CreditMap();
		evalSet = new GeneSet();
	}

	public void OLDsearch(int steps) {
		cancelled = false;
		//displayTimer.start();
		for (int i = 0; i < steps; i++) {
			if (cancelled == true)
				break;

			iteration++;

			//if(iteration % 10 == 0) prunePool();

			// generate evaluation set of individuals from pool
			generate();
			// must evaluate NEW individuals only

			// evaluate all individuals
			evaluateAll();

			// do credit assignment
			// done by above creditEvalSet();

			//sort(setOfSets);
			/*
			 * // sample top strata for (int j = 0; credit.get(setOfSets.get(0)) ==
			 * credit .get(setOfSets.get(j)) && j < setOfSets.size(); j++) {
			 * 
			 * int samplings = 4; for (int s = 0; s < samplings; s++) {
			 * pool.takeGroupFrom(setOfSets.get(j)); } } // and take top
			 * individual into pool (cheap elitism)
			 * pool.addIfNew(setOfSets.get(0)); // start from bottom and work up
			 * int soss = setOfSets.size(); for (int j = soss - 1;
			 * credit.get(setOfSets.get(soss - 1)) == credit
			 * .get(setOfSets.get(j)) && j >= 0; j--) { // remove from pool all
			 * sets directly contained in loser
			 * 
			 * pool.ungroupFrom(setOfSets.get(j)); // make sure this does what
			 * we think! }
			 */

			/*
			 * generate candidate grouping from population and keep them if they
			 * are fit evaluate genesets based
			 */
			GeneSet candidate = evalSet.get(r.nextInt(evalSet.size()))
					.groupFrom(2);
			int sum = 0;
			int freq = 0;
			log.debug("candidate");
			log.debug(candidate.flatten() + "\nfound in");
			/*
			 * just on current population
			for (int j = 0; j < evalSet.size(); j++) {
				if (candidate.matches(evalSet.get(j))) {
					log.debug(evalSet.get(j).flatten().toString() + " = "
							+ fitness.get(evalSet.get(j)));
					sum += fitness.get(evalSet.get(j));
					freq++;
				}
			}*/
			/*
			 * on historical population
			Set ancestors = fitness.entrySet();
			for (Iterator j = ancestors.iterator(); j.hasNext();) {
				Map.Entry ancestor = (Map.Entry)j.next();
				//log.debug(ancestor.getKey().getClass().getName());
				if (candidate.matches(((GeneSet)ancestor.getKey()))) {
					//log.debug(((GeneSet)ancestor.getKey()).flatten().toString() + " = "
						//	+ ancestor.getValue());
					sum += fitness.get((GeneSet)ancestor.getKey());
					freq++;
				}
			}
			*/ 
			log.debug("freq=" + freq + ", mean=" + sum
					/ (double) freq);
			log.debug("fitness size : " + fitness.size());
		}
		doRecords();

		fitnessAfter.put((int) evaluations, (int) fitness.maxValue());
		//displayTimer.stop();
		model.notifyListeners();
		notifyListeners();
	}

	public void search(int steps) {
		cancelled = false;
		//displayTimer.start();
		for (int step = 0; step < steps; step++) {
			if (cancelled == true)
				break;
			
			iteration++;
			//if(iteration % 10 == 0) prunePool();
			
			generate();// generate evaluation set of individuals from pool
			
			// need sorted evaluated individuals
			evaluateAll();
			sort(evalSet, fitness);
			
			credit.clear(); // this for the candidate modules
			
			/*
			 * create set of samplings (this is where the bias comes in)
			 */
			//setupSampling(0, biasParam);
			GeneSet sampleSet = new GeneSet();
			for (int s = 0; s < Config.getInt("SampleFrequency"); s++) {
				//sampleSet.add(evalSet.get(sample()).groupFrom(2));
				sampleSet.add(evalSet.get(r.nextInt(evalSet.size())).groupFrom(2));
			}
			
			// evaluate the buggers against ALL ancestors
			Set ancestors = fitness.entrySet();
			for (int s = 0; s < sampleSet.size(); s++) {
				int sum = 0;
				int freq = 0;
				
				for (Iterator it = ancestors.iterator(); it.hasNext();) {
					Map.Entry ancestor = (Map.Entry)it.next();
					//log.debug(ancestor.getKey().getClass().getName());
					if (sampleSet.get(s).matches(((GeneSet)ancestor.getKey()))) {
						//log.debug(((GeneSet)ancestor.getKey()).flatten().toString() + " = "
						//+ ancestor.getValue());
						sum += fitness.get((GeneSet)ancestor.getKey());
						freq++;
					}
				}
				//log.debug("candidate\n" + sampleSet.get(s).flatten() + "\nfound in " + freq);
				
				// store credit
				credit.set(sampleSet.get(s), sum/(double)freq);
				//log.debug(sampleSet.get(s).flatString() + " worth " 
					//	+ credit.get(sampleSet.get(s)));
			}
			
			// sort and possibly incorporate
			sort(sampleSet, credit);
			
			// debug showing sorted samplings
			for (int s = 0; s < sampleSet.size(); s++) {
				log.debug(sampleSet.get(s).flatString() + " worth " 
							+ credit.get(sampleSet.get(s)));
			}
			log.debug("");
			
			for(int i=0; i < 1; i++) {
				log.debug("incorporating " + sampleSet.get(i).flatten());
				pool.add(sampleSet.get(i));
			}
			
			log.debug("fitness history size: " + fitness.size());
			
			doRecords();
			
			fitnessAfter.put((int) evaluations, (int) fitness.maxValue());
			//displayTimer.stop();
			model.notifyListeners();
			notifyListeners();
		}
	}
	
	public void generate() {
	generate(Config.getInt("PopulationSize"));
}

public void generate(int n) {
	//evalSet = new GeneSet();
	evalSet.clear(); // need persistent ref to this for e.g. geneset view
	for (int i = 0; i < n; i++) {
		GeneSet newbie = pool.getIndividual();
			//newbie.removeDupes();
			evalSet.add(newbie);
		}
		//log.debug("evalSet.size()="+evalSet.size());
	}

	/*
	 * obsolete public void prunePool() { for(int i = 0; i < pool.size(); i++) {
	 * pool.get(i).removeDupes(); } }
	 */
	public void evaluateAll() {
		// working on population sample
		// generate from scratch but preserve ref
		//evalSet.clear();

		   	//log.debug("evaluating evalSetsize = " + evalSet.size());
		
		// this for historical evaluation!! credit.clear();
		//fitness.clear();

		// only adding individial if not already in fitness map
		for (int i = 0; i < evalSet.size(); i++) {
			if(!fitness.containsSame(evalSet.get(i))) {
				evaluate(evalSet.get(i));
			}
		}
	}

	public void evaluate(GeneSet gs) {
		//	fitness[evalSet.indexOf(g)] ;
		PartialIntGenome pig = gs.flatten();
		credit.set(gs, problem.evaluate(pig));
		//log.debug("creditting..\n " + gs + "with "+credit.get(gs));

		//fitness.set(pig, credit.get(gs));

		// needed?
		fitness.set(gs, credit.get(gs));

		evaluations++;
	}
	
	// old
	public void creditGeneSet(GeneSet gs) {
		//log.debug("credit size = " + credit.size());
		if (credit.credits(gs))
			return;

		// TODO: double linked list might be useful here... Later optimisation?

		// get list of all genesets that contain gs
		GeneSet containers = new GeneSet();
		for (int i = 0; i < setOfSets.size(); i++) {
			if (setOfSets.get(i).contains(gs))
				containers.add(setOfSets.get(i));
		}

		//  for each container
		double sum = 0;
		int count = 0;
		for (int i = 0; i < containers.size(); i++) {
			// ensure container creditted
			creditGeneSet(containers.get(i));

			// sum credit of c
			sum += credit.get(containers.get(i));
			count++;
		}

		if (count == 0)
			log.debug("nothing contains: " + gs.flatString());

		// assign mean to gs
		credit.set(gs, sum / count);
	}

	// *** OLD ****
	// credit genetic substructures across population
	public void creditEvalSet() {
		// geneset take mean fitness of genesets (perhaps individuals)
		// in which they appear

		// credit all individuals. assuming evaluate all has been called we can
		// copy fitness over - NOT NECESSARY, see evaluate method!

		// get level set of all geneset in population
		// NOTE: including individuals but excluding evalSet
		setOfSets = evalSet.setOfSets();

		setOfSets.remove(evalSet);
		//log.debug("SOS:" + setOfSets);
		for (int i = 0; i < setOfSets.size(); i++) {
			if (!(credit.credits(setOfSets.get(i))))
				creditGeneSet(setOfSets.get(i));
			//log.debug("creditted " + setOfSets.get(i) + "\nwith " +
			// credit.get(setOfSets.get(i)));
			// TODO:needs checking! what about duplicates etc?
		}
	}
	
	//	 descending order using bubble-sort
	public void sort(GeneSet g, CreditMap map) { // use better sort algorithm
		for (int i = 0; i < g.size(); i++) {
			for (int j = i; j < g.size(); j++) {
				if (map.get(g.get(i)) < map.get(g.get(j))) {
					GeneSet temp = g.get(i);
					g.set(i, g.get(j));
					g.set(j, temp);
				}
			}
		}
	}
	
//	 descending order using bubble-sort
	public void sort(GeneSet g, ValueMap map) { // use better sort algorithm
		for (int i = 0; i < g.size(); i++) {
			for (int j = i; j < g.size(); j++) {
				if (map.get(g.get(i)) < map.get(g.get(j))) {
					GeneSet temp = g.get(i);
					g.set(i, g.get(j));
					g.set(j, temp);
				}
			}
		}
	}
	
	public GeneSet getEvalSet() {
		return evalSet;
	}

	public void showAll() {
		GeneSet ls = setOfSets;
		//.getLevelSet();
		for (int i = 0; i < ls.size(); i++) {
			System.out.println(ls.get(i).flatString() + " -> "
					+ credit.get(ls.get(i)) + "\n");
		}
	}

	public void showModelCredits() {
		System.out.println("MODEL CREDITS");
		for (int i = 0; i < pool.size(); i++) {
			GeneSet g = pool.get(i);
			if (!(g instanceof Gene)) {
				System.out.println(g.toString());
				if (credit.credits(g))
					log.debug("-------------------------------------> "
							+ credit.get(g));
				else
					log.debug("(not creditted)");
			}
		}
	}

	public void doRecords() {
		maxFitnessHistory.add(maxFitness());
		minFitnessHistory.add(minFitness());
		evaluationHistory.add((int) getEvaluations());

		allTimeMax = Math.max(allTimeMax, maxFitness());
		allTimeMin = Math.min(allTimeMin, minFitness());
	}

	// microrun now adapted for mean-based fitness
	private String microrun() {
		//		 create genepool
		GeneSetModel pool = new GeneSetModel();
		pool.initialise();

		StringBuffer sb = new StringBuffer();
		sb.append("Performing micro-run on 8-bit HIFF");
		sb.append("<table border=\"1\">\n<tr>");
		//System.out.println("Initial pool:\n" + tga.pool);
		sb.append("</pre>");

		// reproducing core of search method
		int column = 0;
		int columnsPerRow = 20;
		for (int iteration = 0; iteration < Config.getInt("MaxIterations"); iteration++) {
			if (column % columnsPerRow == 0 && column > 0)
				sb.append("</tr><tr style=\"page-break-before: always\">");
			sb.append("<td valign=\"top\"><pre>");
			sb.append("step " + iteration + "\n");
			generate();
			evaluateAll(); // fitness of individuals
			//creditEvalSet(); // do credit assignment	
			
			//sb.append(credit);
			//sort(setOfSets);
			sort(evalSet, fitness);
			
			int samplings = 4;
			GeneSet sampleSet = new GeneSet();
			
			// make candidate grouping from top strata
			//if (tga.credit.get(tga.setOfSets.get(0)) > tga.credit
			//.get(tga.setOfSets.get(1))) {
			sb.append("<td bgcolor=\"#eeeeee\" valign=\"top\"><b><pre>");

			//System.out.println("new\nsamples:");
			// sample top strata
			// watch out for all same credit
			for (int i = 0; 
				credit.get(evalSet.get(0)) == credit.get(evalSet.get(i))	
					&& i < evalSet.size(); i++) {

				System.out.println("sample\nfrom:" + evalSet.get(i).flatten());
				for (int s = 0; s < samplings; s++) {
					sampleSet.add(evalSet.get(i).groupFrom(2));
					//pool.takeGroupFrom(evalSet.get(i));
				}
			}
			//sb.append("</pre></b></td>");

			// evaluate samplings
			for(int i = 0; i < sampleSet.size(); i++) {
				System.out.println(sampleSet.get(i) + "\n" 
						+meanOfContainers(sampleSet.get(i)));
				credit.set(sampleSet.get(i), meanOfContainers(sampleSet.get(i)));
			}
			
			// and take distinguished individual into pool if not
			// already i
			//	pool.add(tga.evalSet.get(0));
			//sb.append("<td bgcolor=\"#eeeeee\" valign=\"top\"><b><pre>");
			pool.sortForView();
			sb.append(evalSet);
			sb.append(credit);
			sb.append("</pre></b></td>");
			column++;
			if (column % columnsPerRow == 0 && column > 0)
				sb.append("</tr><tr style=\"page-break-before: always\">");
			//}

			// start from bottom and work up
			int soss = evalSet.size();
			for (int i = soss - 1; credit.get(evalSet.get(soss - 1)) == credit
					.get(evalSet.get(i))
					&& i >= 0; i--) {

				// remove from pool all sets directly contained in loser
				//pool.ungroupFrom(evalSet.get(i));
				// make sure this does what we think!
			}

			//tga.showModelCredits();
			sb.append("</pre></td>");
			column++;
		}
		sb.append("</tr></table>");
		return sb.toString();
	}

	public double meanOfContainers(GeneSet gs) {
		int sum = 0;
		int freq = 0;
		//log.debug("candidate");
		//log.debug(candidate.flatten() + "\nfound in");
		for (int j = 0; j < evalSet.size(); j++) {
			if (gs.matches(evalSet.get(j))) {
				//log.debug(evalSet.get(j).flatten().toString() + " = "
				//		+ credit.get(evalSet.get(j)));
				sum += credit.get(evalSet.get(j));
				freq++;
			}
		}
		return sum / (double) freq;
	}

	/*
	 * generates a list of candidate groupings and ranks them according to
	 * apparent credit (and notes number of length 2 BBs contained)
	 */
	private String testPopSampling() {
		StringBuffer sb = new StringBuffer();
		sb.append("Testing Population Sampling\n");

		// The length may vary : set in main()
		//Config.set("GenomeLength", 8);

		GeneSet candSet = new GeneSet();

		//		 create genepool
		GeneSetModel pool = new GeneSetModel();
		pool.initialise();

		// create population (eval set)
		GeneSet pop = new GeneSet();
		for (int i = 0; i < Config.getInt("PopulationSize"); i++) {
			pop.add(pool.getIndividual());
		}
		sb.append("Pop:" + pop);

		for (int i = 0; i < 10; i++) {
			GeneSet candidate = pop.get(r.nextInt(pop.size())).groupFrom(2);
			int sum = 0;
			int freq = 0;
			log.debug("candidate");
			log.debug(candidate.flatten() + "\nfound in");
			for (int j = 0; j < pop.size(); j++) {
				if (candidate.matches(pop.get(j))) {
					log.debug(evalSet.get(j).flatten().toString() + " = "
							+ credit.get(pop.get(j)));
					sum += credit.get(pop.get(j));
					freq++;
				}
			}
			log.debug("sum=" + sum + ", freq=" + freq + ", mean=" + sum
					/ (double) freq);

			candSet.add(candidate);
			credit.set(candidate, sum / (double) freq);
		}

		sb.append(credit);

		return sb.toString();
	}

	public String testNumberOfModulesInPopulation() {
		StringBuffer sb = new StringBuffer();

		// create genepool
		GeneSetModel pool = new GeneSetModel();
		pool.initialise();

		int trials = 10000;
		int popSize = 50;
		int moduleSize = Config.getInt("GenomeLength");
		for (int ms = 2; ms <= moduleSize; ms *= 2) {
			for (int ps = 1; ps <= popSize; ps++) {
				int sum = 0;
				System.out.println("ps:" + ps + "/" + popSize + " ms:" + ms
						+ "/" + moduleSize);
				for (int t = 0; t < trials; t++) {
					// create pop
					GeneSet evalSet = new GeneSet();
					for (int i = 0; i < ps; i++) {
						evalSet.add(pool.getIndividual());
					}

					// add number of modules in sample set to sum
					//System.out.println("for popsize="+ps+" found "+
					//	HIFFProblem.numberOfModules(evalSet, 2)
					//	+ " modules in "+ evalSet);
					sum += HIFFProblem.numberOfModules(evalSet, ms);
				}
				sb.append(sum / (double) trials + "\n");
			}
			sb.append("\n\n");
		}
		return sb.toString();
	}

	/*
	 * Initially only approximating fitness on single containing individual
	 */
	public void testSampleFitnessAgainstModuleCount(PrintWriter w) {
		//		 create genepool
		GeneSetModel pool = new GeneSetModel();
		pool.initialise();

		for (int t = 0; t < Config.getInt("Trials"); t++) {
			// create indy to be sampled
			GeneSet indy = pool.getIndividual();

			// sample it
			GeneSet sample = indy.groupFrom(2);
			System.out.println(sample.flatten());

			// fitness, mod count
			double f = problem.evaluate(indy.flatten());
			//ihm.put()
			//	+ "\t" + ((HIFFProblem)problem).numberOfModules(sample.flatten(),
			// 2));
		}
	}

	/**
	 * Experiment 1. Evaluate how different sampling biases, based on individual
	 * fitness, affect the module count.
	 * 
	 * variables: genomeLength
	 * 
	 * @param w
	 *            results output
	 */
	public void testSampling(PrintWriter w) {
		// create genepool
		GeneSetModel pool = new GeneSetModel();
		pool.initialise();

		int popSize = Config.getInt("PopulationSize");
		int sf = 10;

		// change this to change the bias
		// inbuilt sampling: Selection selection = new ProportionateSelection(this);
		w.println("# no bias in sampling. " + sf +" samples");

		// for varying genome-length
		//for (int l = 4; l <= Config.getInt("GenomeLength"); l*=2) {
		int l = Config.getInt("GenomeLength");
		
			// for fixed number of sample frequencies
			//for (double biasParam = 0; biasParam <= 1.00001; biasParam += 0.05) {
		for(int ps = 1; ps <= popSize; ps+=1) {
	
				// over n trials
				int sum = 0;
				for (int t = 0; t < Config.getInt("Trials"); t++) {

					//GeneSet evalSet = pool.createPopulation(popSize);
					generate(ps); // supercedes above
					
					// need sorted evaluated individuals
					evaluateAll();
					sort(evalSet, fitness);
					
					// create set of samplings (this is where the bias comes in)
					//setupSampling(0, biasParam);
					GeneSet sampleSet = new GeneSet();
					for (int s = 0; s < sf; s++) {
						//sampleSet.add(evalSet.get(sample()).groupFrom(2));
						sampleSet.add(evalSet.get(r.nextInt(evalSet.size())).groupFrom(2));
					}
					sum += HIFFProblem.numberOfModules(sampleSet, 2);
				}

				// output in form: ps, unique_mod_count
				String output = new String(ps + "\t" 
						+ sum/(double)Config.getInt("Trials"));
				System.out.println(output);
				w.println(output);
			}

		//}
	}
	
	private int sample() {
		// using pre-specified distribution
		//		 spin
		double x = Math.random() * rouletteSum;

		//System.out.println(x);
		
		// settle
		int choice;
		for (choice = 0; x > roulette[choice]; choice++);
		//System.out.println("choosing " + choice);
		return choice;
	}
	
	// construct roulette wheel
	// no longer required to sum to 1.0 as using rouletteSum
	private void setupSampling(int bias, double biasParam) {
//		 construct 'roulette wheel'
		// should sum to 1.0 NOT
		roulette = new double[evalSet.size()];
		double [] pd;
		switch(bias) {
		case 1:
			pd = sampleProportionallyUnscaled();
			break;
		case 2:
			pd = sampleProportionallyScaled();
			break;
		case 3:
			pd = sampleElite(biasParam);
			break;
				
		case 4:
			pd = sampleRankedExp(biasParam);
			break;
				
		default: // uniform sampling
			pd = sampleUniformly();
		}
		
		roulette[0] = pd[0];
		rouletteSum = roulette[0];
		for (int i = 1; i < evalSet.size(); i++) {
			roulette[i] = pd[i] + roulette[i - 1];
			//log.debug("roulette"+i+"="+roulette[i]);
		}
		rouletteSum = roulette[evalSet.size() - 1];
	}
	
	private double [] sampleUniformly() {
		double [] pd = new double[evalSet.size()];
		for(int i = 0; i < evalSet.size(); i++) {
			pd[i] = 1;
		}
		//rouletteSum = evalSet.size();
		return pd;
	}
	
	private double[] sampleProportionallyUnscaled() {
		double [] pd = new double[evalSet.size()];
		for(int i = 0; i < evalSet.size(); i++) {
			pd[i] = credit.get(evalSet.get(i));
		}
		return pd;
	}
	
	private double[] sampleProportionallyScaled() {
		double min = credit.minValue();
		// automatically scaled if base-lined
		double [] pd = new double[evalSet.size()];
		for(int i = 0; i < evalSet.size(); i++) {
			pd[i] = credit.get(evalSet.get(i));
		}
		return pd;
	}
	
	private double[] sampleElite(double ratio) {
		double [] pd = new double[evalSet.size()];
		for(int i = 0; i < evalSet.size(); i++) {
			pd[i] = i < evalSet.size()*ratio ? 1:0;
		}
		return pd;
	}
	
	private double[] sampleRankedExp(double decay) {
		double [] pd = new double[evalSet.size()];
		pd[0] = decay;
		for(int i = 1; i < evalSet.size(); i++) {
			pd[i] = pd[i-1] * decay;
		}
		return pd;
	}

	/**
	 * Tests how the effectiveness of the mean-based ranking varies with the
	 * sample frequency.
	 * 
	 * Experiment 2.
	 */
	public void testSampleRanking(PrintWriter w) {
		// create genepool
		GeneSetModel pool = new GeneSetModel();
		pool.initialise();

		int trials = Config.getInt("Trials");
		int popSize = Config.getInt("PopulationSize");
		int sampleFrequency = 100;

		w.println("\n#max sample freq\t" + sampleFrequency);

		w.println("\n# rank, sample freq, BBfreq, mean fitness\n");

		//for (int sf = 1; sf <= sampleFrequency; sf++) {
		int sf = sampleFrequency;
		for(int ps = 1; ps <= popSize; ps++) {
		int sumBBfreq[] = new int[sf]; // count BBs found in samples
			int sumMeanFitness[] = new int[sf]; // count fitness estimation
			for (int t = 0; t < trials; t++) {

				// create pop to be sampled
				evalSet = pool.createPopulation(ps);

				// create set of samplings
				GeneSet sampleSet = new GeneSet();
				for (int s = 0; s < sf; s++) {
					int rand = r.nextInt(evalSet.size());
					sampleSet.add(evalSet.get(rand).groupFrom(2));
				}

				// pop is evaluated. rather essential
				evaluateAll();

				// to rank them...
				// score samplings and store to save time
				for (int i = 0; i < sampleSet.size(); i++) {
					credit.set(sampleSet.get(i), meanOfContainers(sampleSet
							.get(i)));
				}

				// sort samplings
				sort(sampleSet, fitness);

				// tally up BBs found and mean fitnesses
				for (int i = 0; i < sampleSet.size(); i++) {
					sumBBfreq[i] += ((HIFFProblem) problem).numberOfModules(
							sampleSet.get(i).flatten(), 2);
					sumMeanFitness[i] += credit.get(sampleSet.get(i));
					// evaluateAll() takes care of this:
					// credit.remove(sampleSet.get(i));
				}
			}
			// output mean module count of ranked set
			for (int i = 0; i < sf; i++) {
				// rank, popsize, bbcount, mean-fitness
				String output = new String(i + "\t" + ps + "\t" + sumBBfreq[i]
						/ (double) trials + "\t" + sumMeanFitness[i]
						/ (double) trials + "\n");
				System.out.print(output);
				w.print(output);
			}
			w.print("\n");
			System.out.println();
		}
	}

	public static void main(String[] args) {
		/*
		 * CHecklist:
		 * test method
		 * output name
		 */
		//Config config = new Config();
		Config.set("GenomeLength", 32);
		Config.set("PopulationSize", 200);
		Config.set("GenesSpecified", 0);
		// shouldn't need to do this
		Config.set("MaxIterations", 10);
		Config.set("Trials", 1000);
		System.out.println();
		IntGenome.MAX = 1;
		// create instance
		Problem problem = new HIFFProblem();

		// create genepool
		GeneSetModel pool = new GeneSetModel();
		pool.initialise();
		TransGeneticAlgorithm tga = new TransGeneticAlgorithm(pool, problem);
		// set problem
		tga.initialise();

		//tga.log.debug(tga.testPopSampling());

		// output microrun for printing
		//		 write out to file
		PrintWriter w;
		//String outputFile = "microrun-plus.html";
		String outputFile = "popsizesampling-"
				//String outputFile = "fitnessvsmodcount-L"
				//		String outputFile = "bbtally-L"
				+ "L" + Config.getInt("GenomeLength")
				+"P"+Config.getInt("PopulationSize")
				+ "T" + Config.getInt("Trials") + ".dat";

		try {

			w = new PrintWriter(new FileWriter(outputFile));
			Config.writeTo(w);
			w.println("\n");
			//w.println(tga.microrun());
			//tga.testSampleFitnessAgainstModuleCount(w);
			//tga.testSampling(w);
			tga.testSampleRanking(w);
			w.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("written to:\n" + outputFile);

		//tga.search(Config.getInt("MaxIterations"));

		/*-
		 System.out.println("Testing creditting");

		 System.out.println("first create some groups");
		 tga.generate();
		 System.out.println("evalSet:\n" + tga.evalSet);
		 for (int i = 0; i < 5; i++) {
		 pool.takeGroupFrom(tga.evalSet.get(r.nextInt(tga.evalSet.size())));
		 }
		 

		 System.out.println("then groups of groups");
		 tga.generate();
		 System.out.println("evalSet:\n" + tga.evalSet);
		 for (int i = 0; i < 10; i++) {
		 pool.takeGroupFrom(tga.evalSet.get(r.nextInt(tga.evalSet.size())));
		 }
		 
		 System.out.println("pool with some groups in:\n" + pool);
		 
		 tga.evaluateAll();
		 
		 System.out.println(tga.credit);
		 tga.showModelCredits();
		 */
		/*
		 * //add a new random set GeneSet newSet = tga.pool.newSet(2);
		 * newSet.add(tga.pool.newSet(2)); tga.pool.add(newSet); // display pool
		 * System.out.println("pool:\n" + tga.pool.poolString()); // generate
		 * population tga.generate(); System.out.println("evalSet:\n" +
		 * tga.evalSet.popString()); // evaluate population
		 * System.out.println("evaluating all"); tga.evaluateAll(); // credit
		 * assignment System.out.println("\ncreditting all");
		 * tga.creditevalSet();
		 * 
		 * System.out.println("sos:\n" + tga.setOfSets.popString());
		 * tga.sort(tga.evalSet); System.out.println("creditted (" +
		 * tga.credit.size() + ") :");
		 *  
		 */
		//tga.showAll();
		/*
		 * System.out.println(tga.pool.toString() + "\nmax fitness = " +
		 * tga.credit.maxValue()); int optimal = Config.getInt("GenomeLength");
		 * for (int i = optimal / 2; i >= 1; i /= 2) { optimal += i; }
		 * System.out.println("optimal = " + optimal);
		 */
	}
}
package gad;


public class SEAMGeneticAlgorithm extends GeneticAlgorithm {

	int numberOfContexts;
	boolean removeEntities = true;

	// for record keeping
	double maxFitness, minFitness;

	public SEAMGeneticAlgorithm(SearchModel pop, Problem prob) {
		super((PopulationModel)pop, prob);
		numberOfContexts = Config.getInt("NumberOfContexts");
		reset();
	}

	public void initialise() {
		//required from GA

		fitness.clear();
		evaluateAll();

		reset();
		doRecords();

		pop.notifyListeners();
		notifyListeners();

		//initialisePopulation();
	}
	
	// what needs to happen between runs in a batch
	public void reset() {
		initialisePopulation();
		maxFitness = Double.MIN_VALUE;
		minFitness = Double.MAX_VALUE;
		iteration = 0;
		evaluationHistory.clear();
		evaluations = 0;
	}

	// builds a complete 'backdrop' genome for individuals to be evaluated
	// against.
	private PartialIntGenome createContext() {

		PartialIntGenome context = new PartialIntGenome();
		context.setSize(0); // totally unspecified
		context.initialise();
		//	log.debug("creating context of length " + context.length());

		int[] order = new int[pop.size()];

		for (int i = 0; i < pop.size(); i++)
			order[i] = i; // 123..
		for (int i = 0; i < pop.size(); i++) { // shuffle
			int j = r.nextInt(pop.size());
			int t = order[i];
			order[i] = order[j];
			order[j] = t;
		}

		boolean complete = false;
		for (int i = 0; i < pop.size(); i++) {
			complete = true; // until found otherwise
			PartialIntGenome donor =
				(PartialIntGenome) pop.getIndividual(order[i]);
			//log.debug(context.toString());
			for (int l = 0; l < donor.length(); l++) {
				if (!context.specifies(l)) {
					if (!donor.specifies(l))
						complete = false;
					else
						context.set(l, donor.get(l));
				}
			}
			if (complete)
				break;
		};
		//log.debug(context.toString());
		if (!complete)
			log.debug(
				"context still only partially specified - population too poor!");
		return context;
	}

	// required for adequate, ordered sub-solutions
	// really actually an extension of Population
	public void initialisePopulation() {
		// generating population size = genomelength * allele number
		pop.initialise(0);
		for (int i = 0; i < Config.getInt("GenomeLength"); i++) {
			for (int a = 0; a <= 1; a++) {
				PartialIntGenome newbie = new PartialIntGenome();
				newbie.setSize(0);
				newbie.initialise();
				newbie.set(i, a);
				pop.addIndividual(newbie);
			}
		}
	}

	/*
	 * this is the overridden method that makes this GA distinct from the
	 * superclass.
	 */
	public void generate() {
		// select two entities
		PartialIntGenome a =
			(PartialIntGenome) pop.getIndividual(r.nextInt(pop.size()));
		// ensure uniform selection
		PartialIntGenome b =
			(PartialIntGenome) pop.getIndividual(r.nextInt(pop.size()));
		while (a == b)
			b = (PartialIntGenome) pop.getIndividual(r.nextInt(pop.size()));

		attemptJoin(a, b);
	}
	
	/* 
	 * useful for methods which all have own ways of selecting a and b
	 * returns true if join successful
	*/
	private boolean attemptJoin(PartialIntGenome a, PartialIntGenome b) {
//		 test stability of join
		if (isStableJoin(a, b)) {
			// log.debug("stable join at iteration " + iteration + " - time = "
			// + new Date());
			// add a+b
			pop.addIndividual(PartialIntGenome.compose(a, b));
			// remove a and b
			if (removeEntities) {
				pop.removeIndividual(a);
				pop.removeIndividual(b);
			}
			return true;
		}
		else return false;
	}

	public void search(int steps) {
		displayTimer.start();
		for (int i = 0; i < steps; i++) {
			if (cancelled == true)
				break;
			generate();
			doRecords();
			//fitnessAfter.put((int)evaluations, (int)maxFitness);
			maxFitnessHistory.set((int)iteration, maxFitness);
			evaluationHistory.add((int)evaluations);
			iteration++;
			//log.debug("max fitness at " + iteration + " iterations (" + evaluations + " evaluations) is " + maxFitness);
		}
		displayTimer.stop();
		firePopulationChanged();
		fireAlgorithmChanged();
	}

	// this overrides method in SearchAlgorithm which uses a fitness map
	public double maxFitness() {
		return maxFitness;
	}
	
	private boolean isStableJoin(PartialIntGenome a, PartialIntGenome b) {
		// produce a + b
		PartialIntGenome composition = PartialIntGenome.compose(a, b);
		boolean compositeDominates = false;
		// test a + b against all contexts. Record best fitness.
		for (int i = 0; i < numberOfContexts; i++) {
			PartialIntGenome context = createContext();
			// remember compose gives first parameter priority! Context fills
			// in..
			int cf =
				(int) problem.evaluate(
					PartialIntGenome.compose(composition, context));
			int af =
				(int) problem.evaluate(PartialIntGenome.compose(a, context));
			int bf =
				(int) problem.evaluate(PartialIntGenome.compose(b, context));

			// tot up evaluations
			evaluations += 3;
			
			if (af > cf || bf > cf)
				return false; // c is >= a,b
			if (af < cf && bf < cf)
				compositeDominates = true; // c beats both

			maxFitness = Math.max(Math.max(af, bf), cf);
			minFitness = Math.min(Math.min(af, bf), cf);
		}
		// iff a+b dominates a and b return true
		//if(compositeDominates) log.debug("maxFitness = " + maxFitness);
		return compositeDominates;
	}

	public void doRecords() {
		maxFitnessHistory.add(maxFitness);

		allTimeMax = Math.max(allTimeMax, maxFitness);
		allTimeMin = Math.min(allTimeMin, minFitness);
	}

	public static void main(String[] argv) {

		IntGenome.MAX = 1;
		IntGenome.MIN = 0;

		Config.readFrom("test_seam.gac");
		PopulationModel pop = new PopulationModel();
		pop.initialise();
		System.out.println(pop.toString());
		SEAMGeneticAlgorithm ga =
			new SEAMGeneticAlgorithm(pop, new Problem());

		ga.createContext();
	}
}

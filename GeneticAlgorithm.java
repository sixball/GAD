package gad;


public class GeneticAlgorithm extends PopulationSearch {
	//protected PopulationModel pop; // specific version of super.pop
	protected PopulationModel nextPop;

	protected ValueMap selectionProbability;
	protected double[] roulette;

	// TODO: have a list of PopStat objects!
	// TODO: may have operator history in future!

	// should do cool selection invokation with Method.. until then, using
	// variable and consts
	int generationScheme;
	int selectionScheme;
	static final int UNIFORM_SELECTION = 0;
	static final int PROPORTIONAL_SELECTION = 1;
	static final int TRUNCATE_SELECTION = 2;
	static final int RANK_SELECTION = 3;
	static final int TOURNAMENT_SELECTION = 4;

	static final int MUTATION_SEARCH = 0;
	static final int CROSSOVER_SEARCH = 1;
	static final int TEST_SEARCH = 2;
	static final int GENERATIONAL_SEARCH = 3;
	static final int STEADY_STATE_SEARCH = 4;

	Selection selection;

	double eliteRatio;
	double rankDecay;
	double mutationRate; // per locus basis
	int tournamentSize;

	// if this class is abstract then so is the implicit public empty-param
	// constructor
/*
	protected GeneticAlgorithm(Config config) {
		// doing nuthin with config
		super();
		this.config = config;
	}
*/
	// NOT ANY MORE! precon: pop created and initialised
	public GeneticAlgorithm(SearchModel pop, Problem problem) {
		super((PopulationModel)pop, problem);
		this.pop = (PopulationModel)pop;
		// should have:
		// selectionClass = Class.forName(conf.getString("SelectionScheme"));
		// Constructor con = new Constructor(
	}

	public void initialise() { // defaults
		//log.debug("initialising Genetic Algorithm...");
		mutationRate = Config.getDouble("MutationRate");
		eliteRatio = Config.getDouble("EliteRatio");
		rankDecay = Config.getDouble("RankDecay");

		//setRankDecay(0.95);
		//setTournamentSize(3);

		generationScheme = GENERATIONAL_SEARCH;
		//generationScheme = STEADY_STATE_SEARCH;

		fitness.clear();
		evaluateAll();

		//log.debug("creating selection probabilities");
		//selection = new ProportionateSelection(this);
		//selection = new UniformSelection(this);
		selection = new RankedSelection(this, getRankDecay());
		//selection = new EliteSelection(this, (int)(getEliteRatio() *
		// pop.getSize()));
		//log.debug("created selection probabilities");
		if (pop == null)
			log.error("pop is null as ga tries to initialise");

		reset();
		doRecords();

		pop.notifyListeners();
		notifyListeners();
	}

	/**
	 * Reset statistics associated with GA, e.g. fitness records.
	 */
	public void reset() {
		if (pop == null)
			log.error("pop is null as ga tries to reset");
		maxFitnessHistory.clear();
		minFitnessHistory.clear();
		meanFitnessHistory.clear();

		allTimeMax = Double.MIN_VALUE;
		allTimeMin = Double.MAX_VALUE;

		evaluations = 0;
		iteration = 0;

		if (selection != null)
			selectionProbability = selection.probabilities();
	}
	
	public void search(int steps) {
		cancelled = false;
		displayTimer.start();
		for (int i = 0; i < steps; i++) {
			if (cancelled == true)
				break;
			iteration++;
			nextPop = new PopulationModel(); // need config for genome type
			nextPop.clear(); // need empty population too
			makeRoulette();
			pop.sampleAll();
			//checkFitnessMap("pre-generate");
			generate(); // must evaluate NEW individuals only
			//checkFitnessMap("post-generate");
			pop.copyFrom(nextPop); // shallow copy BACK
			//checkFitnessMap("post copy-back");
			pop.shuffleOrder();
			// important to prevent 'legacy selection' and needs to be
			// immediately followed by evaluation.
			//checkFitnessMap("post shuffle order");
			selectionProbability = selection.probabilities();
			// doing this cos display needs to be up-to-date
			//evaluate();
			//pop.sort(fitness);
		}
		displayTimer.stop();
		pop.notifyListeners();
		notifyListeners();
	}
	/**
	 * Used to specify a fitness-based halting criterion.
	 * 
	 * public void runUntilFitnessIs(double f) { while(maxFitness() < f) {
	 * evolve(1); } }
	 */

	public void checkFitnessMap(String point) {
		log.debug("checking fitness map at point: " + point);
		for (int i = 0; i < pop.size(); i++) {
			log.debug("checking fitness of individual " + i);
			fitness(pop.getIndividual(i)); // discard return value
		}
	}

	public void setSelectionScheme(int s) {
		if (s >= 0 && s < 5)
			selectionScheme = s;
		if (s == UNIFORM_SELECTION)
			selection = new UniformSelection(this);
		if (s == PROPORTIONAL_SELECTION)
			selection = new ProportionateSelection(this);
		if (s == TRUNCATE_SELECTION)
			selection = new EliteSelection(this);
		if (s == RANK_SELECTION)
			selection = new RankedSelection(this, rankDecay);

		notifyListeners();
	}
	public int getSelectionScheme() {
		return selectionScheme;
	}

	public void setEliteRatio(double r) {
		if (r >= 0 && r <= 1)
			eliteRatio = r;
		if (selectionScheme == TRUNCATE_SELECTION)
			selection = new EliteSelection(this);
	}
	public double getEliteRatio() {
		return eliteRatio;
	}

	public void setRankDecay(double r) {
		if (r >= 0 && r <= 1)
			rankDecay = r;
	}
	public double getRankDecay() {
		return rankDecay;
	}

	public void setMutationRate(double r) {
		if (r >= 0)
			mutationRate = r;
	}
	public double getMutationRate() {
		return mutationRate;
	}

	public void setTournamentSize(int s) {
		if (s >= 2)
			tournamentSize = s;
	}
	public int getTournamentSize() {
		return tournamentSize;
	}

	public ValueMap getSelectionProbabilities() {
		return selectionProbability;
	}

	public void generate() {
		switch (generationScheme) {
			case MUTATION_SEARCH :
				selectWithMutate();
				break;

			case CROSSOVER_SEARCH :
				selectWithCrossover();
				break;

			case TEST_SEARCH :
				testSearch();
				break;

			case GENERATIONAL_SEARCH :
				/*
				 * The standard GA model. Selects individuals for next
				 * population. Performs crossover and mutation. Includes
				 * elitism.
				 */
				
				// selection
				makeRoulette();

				StochasticUniformSampling(pop.size());
				// add best at end to avoid corruption

				//log.debug("after sampling, nextpop.size() = " +
				// nextPop.size());

				// recombination
				// NOTE: each chromosome has a fixed probability of undergoing
				// crossover
				// but must only take part in one crossover at most.
				// Since a crossover involves two individuals, the crossover
				// per each side is half.
				// maintaining a list of crossed individuals
				GenomeList toCross = new GenomeList();
				for (int i = 0; i < pop.size(); i++)
					toCross.add(nextPop.getIndividual(i));
				
				// TODO: fix this for odd numbers
				for (int i = 0; i < pop.size(); i += 2) {
					if (r.nextDouble()
						< Config.getDouble("CrossoverProbability")) {
						// uniform crossover probability
						//log.debug("toCross.getSize()=" +toCross.getSize());
						Genome p1 = toCross.get(r.nextInt(toCross.getSize()));
						toCross.remove(p1);
						Genome p2 = toCross.get(r.nextInt(toCross.getSize()));
						toCross.remove(p2);

						OffspringPair osp =
							((IntGenome) p1).uniformRecombine((IntGenome) p2);

						// replace parent with children
						nextPop.removeIndividual(p1);
						nextPop.removeIndividual(p2);
						nextPop.addIndividual(osp.offspring1);
						nextPop.addIndividual(osp.offspring2);

						// update fitness map
						fitness.remove(p1);
						// could leave old parents but would bloat memory
						fitness.remove(p2);
						// note: should only be doing evaluations here (and
						// during initialisation)
						evaluate(osp.offspring1);
						evaluate(osp.offspring2);
					}
				}
				
				// mutation
				pop.mutateAll(Config.getDouble("MutationRate"));
				
				// elitism
				Genome dropout = nextPop.getRandom();
				nextPop.removeIndividual(dropout);
				fitness.remove(dropout);
				Genome best = (Genome) pop.getBest(fitness).clone();
				nextPop.addIndividual(best);
				fitness.set(best, fitness.get((Genome) pop.getBest(fitness)));

				// finally, remove old pop from fitness map
				fitness.remove(pop);
				break;

			case STEADY_STATE_SEARCH :
				/*
				 * The standard GA model. Selects parents, recombines and
				 * mutates then return one random offspring to population.
				 */
				nextPop.copyFrom(pop);

				int p1 = pick();
				int p2 = pick();
				do {
					p2 = pick();
				} while (p1 == p2);
				// what type of crossover below?
				Genome offspring =
					((IntGenome) pop.getIndividual(p1)).uniformRecombine(
						(IntGenome) pop.getIndividual(p2)).offspring1;
				offspring.mutate(Config.getDouble("MutationRate"));
				dropout = nextPop.getRandom();
				nextPop.removeIndividual(dropout);
				fitness.remove(dropout);
				nextPop.addIndividual(offspring);
				evaluate(offspring);
				break;
		}
		doRecords(); // NOTE: records updated here!
	}

	/**
	 * Constructs a `roulette wheel' based on selection probability
	 * distribution for current generation. Used by <code>pick()</code>. The
	 * roulette is just a normalised, culmulative version of the selection
	 * probability.
	 */
	protected void makeRoulette() {
		// get probability distribution for current generation
		ValueMap pd = selection.probabilities();

		// construct 'roulette wheel'
		// should sum to 1.0
		roulette = new double[pop.size()];
		roulette[0] = pd.get(pop.getIndividual(0));
		for (int i = 1; i < pop.size(); i++) {
			roulette[i] = pd.get(pop.getIndividual(i)) + roulette[i - 1];
			//log.debug("roulette"+i+"="+roulette[i]);
		}
	}

	/**
	 * Picks an individual based on the roulette. Used by selection methods.
	 */
	protected int pick() {
		// spin...
		double x = Math.random();

		// settle
		int choice;
		for (choice = 0; x > roulette[choice]; choice++);
		// REMEMBER: sum to 1!
		return choice;
	}

	protected void StochasticUniformSampling(int samples) {
		//log.debug("taking " +samples+" samples in SUS");
		makeRoulette(); // need to ensure this is done...

		nextPop.clear();

		double point = r.nextDouble() / samples; // first sample
		// error when start is nearly 1
		//log.debug("start in SUS = " + point);
		//log.debug("end in SUS = " + roulette[samples - 1]);
		//log.debug("popsize = " + pop.size());
		//nextPop.addIndividual((Genome)pop.getIndividual())
		for (int i = 0; i < samples; i++) {
			//  could probably do this more efficiently
			int choice;
			for (choice = 0;
				point + i / (double) samples > roulette[choice];
				choice++);
			//log.debug("popsize = " + pop.size());
			Genome copy = (Genome) pop.getIndividual(choice).clone();
			nextPop.addIndividual(copy);
			// copy the fitness values as well - no new evaluations required!
			fitness.set(copy, fitness.get(pop.getIndividual(choice)));
		}
		//log.debug("after SUS nextPop size = " + nextPop.getSize());
	}

	protected void standardSelect() {

	}

	/**
	 * Straightforward selection, plus mutation.
	 */
	private void selectWithMutate() {
		// generate next generation
		for (int place = (int) (pop.size() * eliteRatio);
			place < pop.size();
			place++) {

			// copy offspring into next generation
			Genome offspring = pop.getIndividual(pick());
			nextPop.addCopyOf(offspring);

			// mutate offspring
			nextPop.mutate(place, mutationRate);
		}
	}

	// PRETTY MUCH DEPRECATED into testSearch
	// does uniform crossover and some mutation
	private void selectWithCrossover() {
		eliteCarry();

		for (int place = (int) (pop.size() * eliteRatio);
			place < pop.size() - 1;
			place += 2) {

			int p1 = pick();
			int p2;
			do {
				p2 = pick();
			} while (p1 == p2);

			// create offspring from crossover with set probability
			OffspringPair offspring;

			if (Math.random() < 0.6) {
				offspring =
					((IntGenome) (pop.getIndividual(p1))).uniformRecombine(
						(IntGenome) pop.getIndividual(p2));
			} else {
				offspring =
					new OffspringPair(
						(IntGenome) pop.getIndividual(p1),
						(IntGenome) pop.getIndividual(p2));
			}

			// mutate offspring
			 ((IntGenome) offspring.offspring1).mutate(mutationRate);
			((IntGenome) offspring.offspring2).mutate(mutationRate);

			// place into next generation
			nextPop.addCopyOf(offspring.offspring1);
			if (nextPop.size() < pop.size())
				nextPop.addCopyOf(offspring.offspring2);
		}
	}

	public void testSearch() {
		//log.error("Test Selection not implemented!");
		selectWithCrossover();
	}

	/**
	 * Sets up the next generation carrying though the elite set from the
	 * previous generation.
	 */
	protected void eliteCarry() {
		pop.sort(fitness);
		// necessary unless we have some sort of indirect indexing

		int eliteSize = (int) (eliteRatio * pop.size());
		//log.debug("elite ratio = " + eliteRatio + "elitesize = "
		// +eliteSize);
		for (int place = 0; place < eliteSize; place++) {
			//	log.debug("carrying");
			nextPop.addCopyOf(pop.getIndividual(place));
		}
	}
}
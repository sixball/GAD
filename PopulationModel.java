package gad;


import java.lang.reflect.*;

public class PopulationModel extends SearchModel {

	public String name;

	// why protected?
	protected static int nameCounter = 0;
	//protected int size;
	protected GenomeList individual;
	protected boolean[] sample;
	protected int highlight;
	protected Class genomeClass;
	//protected static int length;

	public int size() {
		return individual.getSize();
	}

	public String name() {
		return name;
	}

	/**
	 * Get the Highlight value.
	 * 
	 * @return the Highlight value.
	 */
	public int getHighlight() {
		return highlight;
	}

	/**
	 * Set the Highlight value.
	 * 
	 * @param newHighlight
	 *            The new Highlight value.
	 */
	public void setHighlight(int newHighlight) {
		if (size() <= newHighlight || newHighlight < 0)
			return;
		this.highlight = newHighlight;
		notifyListeners();
	}

	public PopulationModel(String name) {
		this(0);
		this.name = name;
	}

	public PopulationModel() {
		this(Config.getInt("PopulationSize"));
	}

	public PopulationModel(int size) {
		individual = new GenomeList(size);
		sample = new boolean[size];
	}

	/* redundant?
	public PopulationModel(String name, int size) {
		this(size);
		this.name = name;
	}
*/
	//  public static void setLength(int length) { Population.length = length; }
	//public static int length() { return length; }
	public void setSampling(int i, boolean v) {
		sample[i] = v;
	}

	public boolean isSampling(int i) {
		if (i > 0 && i < sample.length)
			return sample[i];
		return false;
	}

	public void sampleAll() {
		for (int i = 0; i < size(); i++) {
			sample[i] = true;
		}
		notifyListeners();
	}

	public void sampleBest(int n) {
		for (int i = 0; i < size(); i++) {
			if (i < n)
				sample[i] = true;
			else
				sample[i] = false;
		}
		notifyListeners();
	}

	public void sort(ValueMap v) {
		// just a simple sort..
		// TODO: implement with quicksort
		Genome tmpG;
		//double tmpF;

		for (int i = 0; i < size(); i++) {
			//	int min = i;
			for (int j = i; j < size(); j++) {
				if (v.get(getIndividual(j)) > v.get(getIndividual(i))) {
					//	}
					/*
					 * swap fitness value -- not required with FitnessMap
					 */
					// swap genome
					tmpG = getIndividual(i);
					setIndividual(i, getIndividual(j));
					setIndividual(j, tmpG);
				}
			}
		}
	}

	public PopulationModel getSamplePopulation() {
		int sampleSize = 0;
		for (int i = 0; i < size(); i++) {
			if (isSampling(i))
				sampleSize++;
		}

		PopulationModel subPop = new PopulationModel(sampleSize);

		int next = 0;
		for (int i = 0; i < sampleSize; i++) {
			while (sample[next] == false)
				next++;
			subPop.setIndividual(i, getIndividual(next++));
		}

		return subPop;
	}

	// catches all own exceptions
	/*
	 * public void initialise() { if(getSize() > 0) initialise(getSize()); }
	 * 
	 * gets GenomeType from config and calls initialise(size) with size from config
	 */
	public void initialise() {
		
		try {
			this.genomeClass = Class.forName((String) Config.get("GenomeType"));
		} catch (ClassNotFoundException e) {
			log.error("No class of type: " + (String) Config.get("GenomeType"));
			System.exit(0);
		}
		//log.debug("genomeClass in pop.initialise is " + genomeClass);
		initialise(Config.getInt("PopulationSize"));
	}

	public void initialise(int size) {

		//log.debug("initialising population with size="+ size);
		
		sample = new boolean[size]; // set this all to true later...

		Constructor con = null;
		//	Method m = null;

		try {
			con = genomeClass.getConstructor(null);
		} catch (Exception e) {
			log.error(
				"in population.initialise there is no constructor(null) for genomeClass: "
					+ genomeClass);
		}

		// initialise all
		individual = new GenomeList(size); //empty initially
		for (int i = 0; i < size; i++) {
			sample[i] = true;

			try {
				Genome newIndividual =
					(Genome) con.newInstance(null);
				//log.debug("newIndividual is of class " +
				// newIndividual.getClass());
				newIndividual.initialise();
				//log.debug("new individial initialised in population: " +
				// newIndividual + " Size now " + size());

				individual.add(newIndividual);
			} catch (InvocationTargetException e) {
				log.error("cannot invoke " + con);
				log.error(e.getTargetException().getMessage());
			} catch (InstantiationException e) {
				log.error("cannot instantiate");
			} catch (IllegalAccessException e) {
				log.error("illegal access");
			}
			//individual.get(i).initialise();
		}
	}
	/*
	 * public void initialise(Class genomeClass, int length, int size) {
	 * setLength(length); initialise(genomeClass, size); }
	 */
	public void initialise(Class genomeClass, int size) {
		// check is subclass of Genome
		if (Genome.class.isAssignableFrom(genomeClass)) {
			this.genomeClass = genomeClass;
			initialise(size);
		} else
			log.debug("cannot initialise " + genomeClass);
	}

	public String[] toStringArray() {
		String[] array = new String[size()];
		for (int i = 0; i < size(); i++) {
			array[i] = individual.get(i).toString();
		}
		return array;
	}

	public Genome getIndividual(int i) {
		return individual.get(i);
	} //not safe

	// set genome ref at i to point to specified genome object
	public void setIndividual(int i, Genome g) {
		individual.set(g, i);
	}

	/**
	 * Copy genome to passed index.
	 */
	public void copyIndividual(int to, Genome original) {
		if (original == null)
			log.debug("original is null in copyindy");
		if (individual == null)
			log.debug("individual is null in copyindy");
		//log.debug("indy[to] is a " + this.getClass().getName());
		//log.debug("original is a " +
		// ((Genome)original.clone()).getClass().getName());

		setIndividual(to, (Genome) original.clone());
	}

	/**
	 * Overwrite one genome with copy of other
	 */
	public void copyIndividual(int from, int to) {
		individual.set((Genome) individual.get(from).clone(), to);
	}

	public void addCopyOf(Genome g) {
		individual.add((Genome) g.clone());
	}

	public void addIndividual(Genome g) {
		individual.add(g);
	}

	public void removeIndividual(Genome g) {
		individual.remove(g);
	}

	public void removeIndividual(int i) {
		individual.remove(i);
	}

	public void clear() {
		individual.clear();
	}

	// changes references - shallow copy
	public void copyFrom(PopulationModel src) {
		individual.clear(); // = new GenomeList();
		for (int i = 0; i < src.size(); i++) {
			individual.add(src.getIndividual(i));
		}
	}

	// creates new refs to original population
	public PopulationModel copy() {
		PopulationModel copy = new PopulationModel(size());
		for (int i = 0; i < size(); i++) {
			copy.setIndividual(i, individual.get(i));
		}
		return copy;
	}

	// mutate individual at i
	public void mutate(int i, double freq) {
		individual.get(i).mutate(freq);
	}

	// mutate whole population - mutation per locus
	public void mutateAll(double freq) {
		for (int i = 0; i < size(); i++) {
			individual.get(i).mutate(freq);
		}
	}

	public void shuffleOrder() {
		for (int i = 0; i < size(); i++) {
			int r = (int) (Math.random() * size());
			Genome t = individual.get(i);
			setIndividual(i, individual.get(r));
			setIndividual(r, t);
		}
	}

	public Genome getBest(ValueMap v) {
		int best = 0;
		for (int i = 0; i < size(); i++) {
			if (v.get(individual.get(i)) > v.get(individual.get(best)))
				best = i;
		}
		return individual.get(best);
	}

	public Genome getRandom() {
		return individual.get(r.nextInt(size()));
	}

	public int indexOf(Genome g) {
		return indexOf(g);
	}

	/**
	 * Restrict the population sample where the allele at the specified locus
	 * has the specified value. Also return the sample as a sub-population
	 * 
	 * NEED TO GENERALISE ALLELE!
	 */
	public PopulationModel select(int a, int l) {
		int members = 0;

		// select + count
		for (int i = 0; i < size(); i++) {
			if (sample[i] && ((IntGenome) getIndividual(i)).get(l) == a) {
				sample[i] = true;
				members++;
			} else
				sample[i] = false;
		}

		// sub pop
		PopulationModel subPop = new PopulationModel(members);
		for (int i = 0, next = 0; i < members; i++) {
			while (sample[next] == false)
				next++;
			subPop.setIndividual(i, getIndividual(next));
		}

		return subPop;
	}

	/**
	 * Restrict the population sample where the allele at the specified locus
	 * does not have the specified value.
	 */
	public PopulationModel deselect(int a, int l) {
		int members = 0;

		for (int i = 0; i < size(); i++) {
			if (sample[i] && (((IntGenome) individual.get(i)).get(l) == a)) {
				sample[i] = true;
				members++;
			} else
				sample[i] = false;
		}

		// sub pop
		PopulationModel subPop = new PopulationModel(members);
		for (int i = 0, next = 0; i < members; i++) {
			while (sample[next] == false)
				next++;
			subPop.setIndividual(i, individual.get(next));
		}
		return subPop;
	}

	public void showNulls() {
		for (int i = 0; i < size(); i++) {
			log.ifNull(individual.get(i), "Individual " + i + " in population");
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			sb.append(individual.get(i).toString() + '\n');
		}
		return sb.toString();
	}

}

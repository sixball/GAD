/*
 * Created on 20-Jul-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gad;


/**
 * @author Simon
 * 
 * Wraps GeneSet into a Search Model and therefore provides population-type
 * functionality
 */
public class GeneSetModel extends SearchModel {

	GeneSet pool;

	public GeneSetModel() {
		super();
		pool = new GeneSet();
	}

	public void initialise() {
		//log.debug("initialising geneset pool");
		// add all values for all genes!
		// assuming intGenome
		pool.clear();
		for (int l = 0; l < Config.getInt("GenomeLength"); l++) {
			// range of possible values for genome should be stored
			// statically in Gene class
			for (int a = IntGenome.MIN; a <= IntGenome.MAX; a++) {
				pool.add(new Gene(a, l));
			}
		}
	}

	// a population method
	// shuffle up order to get random individuals
	// BUT DO NOT bias toward bigger genesets!
	public GeneSet getIndividual() {
		pool.shuffleOrder();
		//sortBySize();
		GeneSet ind = new GeneSet();
		for (int i = 0; i < pool.size(); i++) {

			if (!ind.conflicts(pool.get(i)))
				ind.overlay(pool.get(i));
			//if (!ind.intersects(pool.get(i)))
			//ind.add(get(i));

			if (ind.isComplete())
				return ind;
			//log.debug(String.valueOf(i));
		}
		log.error("Population deficient!");
		return ind;
	}

	/*
	 * Produces a GeneSet with the specified number of complete
	 * individuals from the given pool.
	 */
	public GeneSet createPopulation(int n) {
		GeneSet pop = new GeneSet();
		for (int i = 0; i < n; i++) {
			pop.add(getIndividual());
		}
		return pop;
	}
	
	// note: tries to avoid duplicates
	public void add(GeneSet g) {
		if (!pool.directlyContains(g))
			pool.add(g);
	}

	// tries hard to avoid duplicates
	public void addIfNew(GeneSet g) {
		for (int i = 0; i < pool.size(); i++) {
			if (!(pool.get(i) instanceof Gene)) { // no point looking
				if (pool.get(i).concursWith(g))
					return; // already in!
			}
		}
		pool.add(g); // not in - so add it.
	}

	public int size() {
		return pool.size();
	}

	public void takeGroupFrom(GeneSet g) {
		GeneSet sample = g.groupFrom();
		if (sample != null)
			add(sample);
	}

	//	 takes the oldgroup within this geneset and moves all the elements
	// into this geneset direct - erases a
	// NYI
	public void ungroup(GeneSet oldGroup) {
		if (pool.indexOf(oldGroup) == -1)
			;
		//log.error("trying to ungroup group not in this group");
		else {
			for (int i = 0; i < oldGroup.size(); i++) {
				pool.add(oldGroup.get(i));
				oldGroup.genes.remove(i); // necessary?
			}
			pool.remove(oldGroup);
		}
	}

	// removes from pool all sets directly contained in the given set g
	// note: cannot remove a gene!
	public void ungroupFrom(GeneSet g) {
		// nn g.shuffleOrder();
		for (int i = 0; i < g.size(); i++) {
			if (!(g.get(i) instanceof Gene)) {
				pool.remove(g.get(i));
				//return;
			}
		}
		//log.debug("no groups in geneset to remove!");
	}

	public GeneSet getGeneSet() {
		return pool;
	} // buggers encapsulation

	public GeneSet get(int i) {
		return pool.get(i);
	}

	public void sortForView() {
		// sort biggest then allelic value left-to-right
		for (int i = 0; i < pool.size() - 1; i++) {
			for (int j = i + 1; j < pool.size(); j++) {
				if (pool.get(i).visualCompareTo(pool.get(j)) > 0)
					pool.swap(i, j);
			}
		}
	}

	// in descending order
	public void sortBySize() {
		for (int i = 0; i < pool.size() - 1; i++) {
			for (int j = i + 1; j < pool.size(); j++) {
				if (pool.get(i).allelicSize() < pool.get(j).allelicSize())
					pool.swap(i, j);
			}
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("pool(" + pool.size() + ")\n");
		for (int i = 0; i < pool.size(); i++) {
			sb.append(get(i).toString() + "\n");
			//if(i < pool.size() - 1) sb.append("\n"); // gap between
			//sb.append("\n=====================\n");
			//
			//			sb.append(pool.get(i));
			//			sb.append("\n-=-=-=-=-=-=-=-=");
			//			sb.append("\n");
		}

		return sb.toString();
	}

	public static void main(String[] arg) {

		GeneSetModel gsm = new GeneSetModel();
		Config.readFrom("tga.txt");
		// initialise the model and display it
		gsm.initialise();
		log.debug("pool post-initialisation");
		System.out.println(gsm.toString());

		// get an individual, display and group from it n times
		GeneSet indy = gsm.getIndividual();
		log.debug("Indy:\n" + indy);
		for (int i = 0; i < 10; i++) {
			GeneSet newGroup = indy.groupFrom();
			log.debug("new group:\n" + newGroup + "\nsize: " + newGroup.size());
			gsm.pool.add(newGroup);
		}
		log.debug("pool with groups from indy in it:\n" + gsm);
		// viewSort and display
		gsm.sortForView();
		log.debug("post-viewsort:\n" + gsm);

		// make a multi-level individual
		log.debug("multi-level individual test");
		GeneSet level1 = gsm.getIndividual();
		log.debug("level 1:" + level1);
		GeneSet level2 = gsm.getIndividual();
		log.debug("level 2:" + level2);
		level1.add(level2);
		log.debug("level 1>2:" + level1);

		// ungroup
		// pick random geneset from pool and ungroup it
		GeneSet togo = gsm.get(r.nextInt(gsm.size()));
		log.debug("ungrouping test");
		// 
	}
}
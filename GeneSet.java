/*
 * Created on 05-Jun-2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package gad;

/**
 * @author Simon
 * 
 * Ummm, this models the individual AND the population.... Note: no Alleles are
 * ever lost!
 *  
 */

import java.util.*;

public class GeneSet extends Genome {

	Logger log = new Logger();

	Random r = new Random();

	// the inherited PIG provides the alleles -- argh! why? -- out of date
	ArrayList genes;

	protected GeneSet() {
		genes = new ArrayList();
	}

	public Object clone() {
		GeneSet newbie = new GeneSet();
		for (int i = 0; i < size(); i++) {
			newbie.add(get(i));
		}
		return newbie;
	}

	// returns true iff specifies same variables with same values
	public boolean equals(GeneSet g) {
		//log.debug("comparing \n" + g.flatString() + "\n" + flatString());
		for(int i = 0; i < Config.getInt("GenomeLength"); i++) {
			if(specifies(i) != g.specifies(i)) return false;
			if(specifies(i) && geneAt(i).allele() != g.geneAt(i).allele()) return false;
		}
		return true;
	}
	
	public boolean concursWith(GeneSet g) {
		for(int i = 0; i < Config.getInt("GenomeLength"); i++) {
			if(specifies(i) && g.specifies(i) &&
					geneAt(i).allele() != g.geneAt(i).allele()) return false;
		}
		return true;
	}
	
	public void initialise(int i) {
		// do nothing
	}

	public void shuffleOrder() {
		for (int i = 0; i < genes.size(); i++) {
			int j = r.nextInt(genes.size());
			Object o = genes.get(j);
			genes.set(j, genes.get(i));
			genes.set(i, o);
		}
	}
	
	public int visualCompareTo(GeneSet gs) {
		// first by size (bigger wins)
		if (allelicSize() > gs.allelicSize())
			return -1;
		if (allelicSize() < gs.allelicSize())
			return 1;
		/*
		 * // then by first specified allele (leftmost wins) if
		 * (leftmostSpecified() < gs.leftmostSpecified()) return -1; if
		 * (leftmostSpecified() > gs.leftmostSpecified()) return 1;
		 *  // then by value of leftmost allele (leftmost wins) if
		 * (leftmostGene().allele() < gs.leftmostGene().allele()) return -1; if
		 * (leftmostGene().allele() > gs.leftmostGene().allele()) return 1;
		 */
		
		for (int i = 0; i < allelicSize(); i++) {
			Gene lg1 = leftGene(i);
			Gene lg2 = gs.leftGene(i);
			
			// first specified
			if(lg1.locus() < lg2.locus()) return -1;
			if(lg1.locus() > lg2.locus()) return 1;
			
			// then value
			if(lg1.allele() < lg2.allele()) return -1;
			if(lg1.allele() > lg2.allele()) return 1;
		}
		
		return 0; // cannot differentiate
	}

	public int allelicSize() {
		int sum = 0;
		for (int i = 0; i < Config.getInt("GenomeLength"); i++) {
			if (specifies(i))
				sum++;
		}
		return sum;
	}

	private Gene leftmostGene() {
		//log.debug("in leftmostGene:" + flatten());
		for (int i = 0; i < Config.getInt("GenomeLength"); i++) {
			if (specifies(i))
				return geneAt(i);
		}
		log.error("no genes, never mind leftmost!");
		return null;
	}

	private Gene leftGene(int fromLeft) {
		//log.debug("in leftmostGene:" + flatten());
		for (int i = 0; i < Config.getInt("GenomeLength"); i++) {
			if (specifies(i))
				fromLeft--;
			if (fromLeft < 0)
				return geneAt(i);
		}
		log.error("not enough specified genes for leftGene()");
		return null;
	}

	// leftmost specified locus
	private int leftmostSpecified() {
		//log.debug("in leftmostGene:" + flatten());
		for (int i = 0; i < Config.getInt("GenomeLength"); i++) {
			//log.debug("checking spec of pos:"+i);
			if (specifies(i))
				return i;
			//				return alleleAt(i);
		}
		log.error("no genes, never mind leftmost!");
		return -1;
	}

	public void swap(int i, int j) {
		GeneSet t = get(i);
		set(i, get(j));
		set(j, t);
	}

	// an individual method
	public boolean isComplete() {
		for (int i = 0; i < Config.getInt("GenomeLength"); i++) {
			if (!specifies(i))
				return false;
		}
		return true;
	}

	public boolean specifies(int l) {
		for (int i = 0; i < genes.size(); i++) {
			//log.debug(get(i).getClass().getName());
			if (get(i).specifies(l))
				return true;
		}
		return false;
	}

	/*
	 * // this should let geneAt do the work! public int alleleAt(int l) {
	 * //log.debug("getting allele at pos "+l); for (int i = 0; i <
	 * genes.size(); i++) { if (genes.get(i) instanceof Gene && ((Gene)
	 * genes.get(i)).locus() == l) return ((Gene) genes.get(i)).allele(); else
	 * if (genes.get(i) instanceof GeneSet && ((GeneSet)
	 * genes.get(i)).specifies(l)) { int allele = ((GeneSet)
	 * genes.get(i)).alleleAt(l); if(allele != -1) return allele; // found it! } }
	 * return -1; }
	 */

	public Gene geneAt(int l) {
		//log.debug("in geneAt with geneset:" + flatten());
		//log.debug("genes.size=" + genes.size());
		for (int i = 0; i < genes.size(); i++) { // go through all genesets
			if (genes.get(i) instanceof Gene // if a gene we can test the locus
					&& ((Gene) genes.get(i)).locus() == l) {
				//log.debug("the gene is " + get(i));
				return (Gene) genes.get(i); // and return it if the same
			}
			if (genes.get(i) instanceof GeneSet // else, if specifies, get it!
					&& ((GeneSet) genes.get(i)).specifies(l)) {
				//log.debug("we know it's in there at pos:" + l);
				Gene gene = ((GeneSet) genes.get(i)).geneAt(l);
				if (gene != null)
					return gene;
			}
		}
		//log.error("no gene found at " + l);
		return null;
	}

	// squishes the whole set into one PIG
	public PartialIntGenome flatten() {
		PartialIntGenome flat = new PartialIntGenome();
		flat.initialise();
		// the alleles
		for (int l = 0; l < Config.getInt("GenomeLength"); l++) {
			int allele = -1;
			if(geneAt(l) != null) allele = geneAt(l).allele();
			flat.set(l, allele);
		}
		return flat;
	}

	// how the conflicts() work:
	// recursively descends to gene level of other, then recursively
	// checking for conflict with this gene
	public boolean conflicts(GeneSet other) {
		//log.debug(other.getClass().toString());

		// Q: why does not automatically invoke more specific method?
		if (other instanceof Gene)
			return conflicts((Gene) other);

		for (int i = 0; i < other.size(); i++) {
			if (conflicts(other.get(i)))
				return true;
		}
		return false;
	}

	public boolean conflicts(Gene other) {
		//log.debug("checking conflict at " + other.locus());
		for (int i = 0; i < size(); i++) {
			if (get(i).conflicts(other))
				return true;
		}
		return false;
	}

	// adds a geneset to this which only contains the elements of
	// ga not already found in this
	// this is suspected of buggering up creditting...see notes
	public void overlay(GeneSet gs) {
		// start with all elements
		GeneSet novel = (GeneSet) gs.clone();

		// remove elements from novel that are not
		for (int i = 0; i < novel.size(); i++) {
			// better to use an iterator here? then no need for i--?
			if (this.contains(novel.get(i))) {
				novel.remove(i--);
			}
		}

		// add novel elements as a new set
		add(novel);
	}
	
	public boolean intersects(GeneSet gs){
		for(int i = 0; i < Config.getInt("GenomeLength"); i++) {
			if(specifies(i) && gs.specifies(i)) return true;
		}
		return false;
	}

	public boolean contains(GeneSet gs) {
		for (int i = 0; i < size(); i++) {
			if (get(i) == gs || get(i).contains(get(i)))
				return true;
		}
		return false;
	}

	public boolean directlyContains(GeneSet gs) {
		for (int i = 0; i < size(); i++) {
			if (get(i) == gs)
				return true;
		}
		return false;
	}

	public boolean contains(Gene g) {
		for (int i = 0; i < size(); i++) {
			if (get(i) == g)
				return true;
		}
		return false;
	}
	
	// true if this and g agree on allelic values where they
	// are simultaneously specified
	public boolean matches(GeneSet g) {
		for (int i = 0; i < Config.getInt("GenomeLength"); i++) {
			if(specifies(i) && g.specifies(i)
					&& geneAt(i).allele() != g.geneAt(i).allele())
				return false;
		}
		return true;
	}

	public void add(GeneSet other) {
		genes.add(other);
	}

	public void add(Gene other) {
		genes.add(other);
	}

	public GeneSet get(int i) {
		return (GeneSet) genes.get(i);
	}

	public void set(int i, GeneSet g) {
		genes.set(i, g);
	}

	public void add(Object o) {
		genes.add(o);
	}

	public void remove(int i) {
		genes.remove(i);
	}

	public void remove(Object o) {
		genes.remove(o);
	}

	public void clear() {
		genes.clear();
	}

	public int size() {
		return genes.size();
	}

	public int indexOf(GeneSet gs) {
		return genes.indexOf(gs);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < genes.size(); i++) {
			sb.append("\n");
			if (!(get(i) instanceof Gene)) {
				// follows 'mail-forwarding'
				// convention
				sb.append(">");
				sb.append(get(i).toString());
				sb.append("\n<");
			} else
				sb.append(get(i).toString());
		}
		return sb.toString();
	}

	public String flatString() {
		return flatten().toString();
	}

	public String popString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < genes.size(); i++) {
			if (get(i) instanceof Gene)
				sb.append(get(i).toString());
			else
				sb.append(get(i).flatString());
			sb.append("\n-=-=-=-=-=-=-=-\n");
		}
		return sb.toString();
	}

	/*- lists all genesets
	public GeneSet getLevelSet() {
		GeneSet level = new GeneSet();
		// first add all genesets at this level
		for (int i = 0; i < genes.size(); i++) {
			level.add(genes.get(i));
		}

		// then all at level above, recursively
		for (int i = 0; i < genes.size(); i++) {
			if (genes.get(i) instanceof GeneSet)
				level.genes
						.addAll(((GeneSet) genes.get(i)).getLevelSet().genes);
			// will this work? needs checking...
		}
		return level;
	}
	*/

	// like getLevelSet but without genes (or duplicates NYI)
	// SUSPECT CODE
	public GeneSet setOfSets() {
		GeneSet sos = new GeneSet();
		//sos.add(this);
		for (int i = 0; i < size(); i++) {
			if (!(get(i) instanceof Gene)) {
				sos.add(get(i));
				sos.genes.addAll(get(i).setOfSets().genes);
			}
		}

		// remove duplicates
		/*
		 * NYI for(int i = 0; i < size(); i++) { for(int j = i j < size(); i) }
		 */
		return sos;
	}
	
	public String creditString(CreditMap credit) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < genes.size(); i++) {
			
		}
		return sb.toString();
	}
	
	//	 get a root-level set of n elements from g
	// 1 < n < g.size()
	public GeneSet groupFrom(int n) {
		if(n < 2 || n > size()) {
			log.debug("trying to group " + n + "elements from a set of size " + size());
			return this;
		}

		GeneSet newGroup = new GeneSet();

		shuffleOrder(); // want to pick n random sets
		for (int i = 0; i < n; i++) {
			newGroup.add(get(i));
			//remove(i);
		}
		//log.debug("returning grouping " + newGroup + "\nfrom group "+ this);
		//pool.add(newGroup);
		return newGroup;
	}

	// calls groupFrom(n) where n is a random number
	// between 2 and groupsize - 1
	// Calling method must test for null return value - returned if size = 1
	public GeneSet groupFrom() {
		//log.debug("grouping from:" + this +"\nwhere size=" + size());
		if (size() == 1) return null; // no can do: group already there!
		else if (size() == 2) return null;// no can do either! NOT (GeneSet) clone(); // the whole enchilada
		else { // presume size > 2
			return groupFrom(r.nextInt(size() - 2) + 2);
			//return groupFrom(2);
		}
	}
	
	/*-// works by taking level set and removing all but n elements
	public GeneSet newSet(int n) {
		GeneSet union = getLevelSet();
		while (union.genes.size() > n)
			union.genes.remove(r.nextInt(union.genes.size()));
		return union;
	}
*/
	// takes n random root-level elements of this geneset and
	//groups them - draws a set at root-level
	// applied (this) at population level
	public void group(int n) {
		shuffleOrder(); // want to pick n random sets
		GeneSet newGroup = new GeneSet();
		for (int i = 0; i < n; i++) {
			newGroup.add(get(i));
			remove(i);
		}
		add(newGroup);
	}

	// removes 'bottom-up' i.e. outermost, less specific first
	/*
	 * Each element i in this geneset compared to the successive elements, j if
	 * j == i or j contains i then i is removed, otherwise all dupes in i are
	 * removed, recursively BOLLOCKS
	 * 
	 * Actually: remove any genesets at THIS level that are found at higher
	 * levels THEN recursively step up.
	 * 
	 * Problem: some geneset occuring in two separate branches
	 */
	public void removeDupes() {
		// remove dupes at this level
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < size(); j++) {
				// avoid infinite recursion
				if (i == j)
					break;

				// ensure no dupes on same level
				if (get(i) == get(j)) {
					remove(i);// NOT remove(get(i/j))!
					i--;
					j--; // ensure we don't skip
				}

				// go higher level
				else if (get(j).contains(get(i))) {
					remove(get(i));
					i--;
					j--; // no skip please
				}
			}
		}

		// recursing up
		for (int i = 0; i < size(); i++) {
			get(i).removeDupes(); // okay with Genes?
		}
	}

	public static void main(String[] args) {

	}
}
/*
 * Created on 07-Jun-2004
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

 * IMPORTANT to note the Gene extends GeneSet and all this implies.
 * i.e. careful to override methods appropriately.
 *  */
public class Gene extends GeneSet {

	private int locus;
	private int allele;
	
	static String nonSpecString = ".";

	public Gene(int allele, int locus) {
		//genes = null; // don't need inherited arraylist
		this.allele = allele;
		this.locus = locus;
	}
	
	public Object clone() {
		return new Gene(allele, locus);
	}

	public int locus() {
		return locus;
	}
	public int allele() {
		return allele;
	}

	public boolean specifies(int l) {
		return locus == l;
	}
	
	/*
	// overrides GeneSet method
	public int alleleAt(int l) {
		if(l != locus) log.error("this Gene has locus " + locus +", not " + l);
		return allele;
	}
	*/
	public Gene geneAt(int l) {
		if(l != locus) log.error("this Gene has locus " + locus +", not " + l);
		return this;
	}
	
	public boolean conflicts(Gene g) {
		return locus == g.locus && allele != g.allele;
	}

	public String toString() {
		//return new String("(" + allele + " @ " + locus + ")");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < locus; i++)
			sb.append(nonSpecString);
		sb.append(allele);
		for (int i = locus + 1; i < Config.getInt("GenomeLength"); i++)
			sb.append(nonSpecString);
		return sb.toString();
	}

	public PartialIntGenome flatten() {
		PartialIntGenome flat = new PartialIntGenome();
		flat.initialise();

		flat.set(locus, allele);
		return flat;
	}
}
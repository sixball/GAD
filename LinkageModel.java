package gad;


import java.util.*;

// this represents a candidate model for the underlying linkage for a problem

public class LinkageModel extends PopulationModel {

    //TreeSet linkage;
    HashSet linkage;
    int length;
    
    HashSet listeners;
    
    Logger log;

    public LinkageModel(int length) {
	this.length = length;
	//	linkage = new TreeSet();
	linkage = new HashSet();
	//listeners = new HashSet();
	log = new Logger();
    }

    public int length() { return length; }

    public void addLinkage(Linkage l) {
	linkage.add(l);
	//notifyListeners();
    }

    public void addLinkage() {
	Linkage newLinkage = new Linkage(length, Math.random());
	//newLinkage.perturb(0.1);
	
	linkage.add(newLinkage);
    }

    //public TreeSet getLinkages() { return linkage; }
    public HashSet getLinkages() { return linkage; }
    
    public String toString() {
	StringBuffer sb = new StringBuffer();
	for(Iterator it = linkage.iterator(); it.hasNext();) {
	    sb.append(((Linkage)it.next()).toString());
	}
	return sb.toString();
    }

    public IntGenome generate(IntGenome p1, IntGenome p2) {
	// assuming fixed length genome
	IntGenome offspring = new IntGenome();
	offspring.initialise();
	return offspring;
    }
    /*
    public IntGenome generate() {
    /*
      generation may use MANY parents in order to conform to linkage contraints, naturally biased toward fitter individuals.
    */
    //	return new IntGenome(length);	
    // }
    //*/
    public OffspringPair recombine(IntGenome p1, IntGenome p2) {
	//System.out.println(((Linkage[])this.linkage.toArray()).getClass());
	Object[] linkage = this.linkage.toArray();
	
	// set each linkage to bind to random parent
	int[] binding = new int[linkage.length];
	for(int i = 0; i < binding.length; i++) {
	    binding[i] = (int)(Math.random() * 2);
	}
	
	// start with most dominant?
	double switchingThreshold = 0;
	double acceptanceThreshold = 0;

	// for set maximum number of passes
	for(int pass = 0; pass < 10; pass++) {
	    double overallConflict = 0; // degree of conflict
	    for(int i = 0; i < linkage.length - 1; i++) {
		double conflict = 0;
		for(int j = i + 1; j < linkage.length; j++) {
		    if(binding[i] == binding[j]) break; // same parent

		    // potential for conflict -- so, quantify..
		    // product of weightings
		    for(int l = 0; l < length; l++) {
			conflict += ((Linkage)linkage[i]).get(l) 
			    * ((Linkage)linkage[j]).get(l);
		    }
		    overallConflict += conflict; // pre-switching
		    if(conflict > switchingThreshold) {
			if(((Linkage)linkage[i]).dominance
			   > ((Linkage)linkage[j]).dominance)
			    binding[i] = binding[j];
			else binding[j] = binding[i];
		    }
		}
	    }
	    //System.out.println("Overall conflict = " + overallConflict);
	    if(overallConflict <= acceptanceThreshold) break;
	}
	// got to get this right -- do proper testing with main

	// construct crossover template
	int[] coPattern = new int[length];
	//int[] coPattern2 = new int[length];
	// initially specify random crossover
for(int l = 0; l < length; l++) 
	    coPattern[l] = (int)(Math.random() * 2);
	
	// for each linkage
	for(int i = 0; i < linkage.length; i ++) {
	    // if you're hard enough...
	    double r = Math.random();
	    // go through each locus copying to pattern where linkage indicated
	    for(int l = 0; l < length; l++) {
		if(((Linkage)linkage[i]).get(l) > r) {
		    coPattern[l] = binding[i];
		}
	    }
	}
	/*
	// display to check
	System.out.println();
	System.out.print("coPattern: ");
	for(int l = 0; l < length; l++) 
	    System.out.print(coPattern[l] + " ");
	System.out.println();
	*/
	// do crossover
	IntGenome o1 = (IntGenome)p1.clone();
	IntGenome o2 = (IntGenome)p2.clone();
	for(int i = 0; i < length; i++) {
	    if(coPattern[i] != 0) {
		o1.set(i, p2.get(i));
		o2.set(i, p1.get(i));
	    }
	}
	
	return new OffspringPair(o1, o2);
    }
    /*
    public void addListener(LinkageModelListener lml) {
	listeners.add(lml);
    }

    public void notifyListeners() {
	for(Iterator it = listeners.iterator; it.hasNext();) 
	    ((LinkageModelListener)it.next()).update();
    }
    */

    public void modelR1(int sections, double weight) {
	log.debug("explicitly modelling r1 linkage");
	// create a model explicitly for R1 function
	for(int i = 0; i < sections; i++) {
	    Linkage newLinkage = new Linkage(length, weight);
	    for(int l = 0; l < length; l++) {
		if((int)((sections * l)/length) == i) 
		    newLinkage.set(l, 0.5 + (weight / 2.0));
		else newLinkage.set(l, 0.5 - (weight / 2.0));
	    }
	    linkage.add(newLinkage);
	}
    }

    /* test correctness of linkage generation
    public static void main(String[] argv) {
	Logger log = new Logger();
	int length = 20;
	LinkageModel m = new LinkageModel(length);
	Linkage l1 = new Linkage(length, 0.5);
	Linkage l2 = new Linkage(length, 0.5);
	//	IntGenome p1 = new IntGenome(config);
	//IntGenome p2 = new IntGenome(config);
	
	// set these to all one/zero for easier checking
	for(int i = 0; i < length; i++) {
	    p1.set(i, 0);
	    p2.set(i, 1);
	}

	// set a couple of linkage patterns
	for(int i = 0; i < length; i++) {
	  l1.set(i, (int)(2.0 * i / length)); // two halves
	  l2.set(i, i%2);// odd-even
	}

	m.addLinkage(l1);
	m.addLinkage(l2);

	log.debug("p1: " + p1);
	log.debug("p2: " + p2);
	log.debug("mo: " + m);
	OffspringPair osp = m.recombine(p1, p2);
	log.debug("o1: " + osp.offspring1);
	log.debug("o2: " + osp.offspring2);
    }
    */
}

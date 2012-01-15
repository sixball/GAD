package gad;

/*
  NOTES:
  + Linkage arrays can be permanently associated with the genomes or be drawn from a separate population, normally we take the first option.
  + For summer project we have a single, population-level linkage.
  + Diversity maintenance: Replace any individual that, in the first case, is the same, or, in the second case, has less fitness.

*/


import java.util.*;

public class LinkageGA extends RoyalRoadGA {

    ProbPop ops;
    boolean separateLinkage;
    int numOps;

    public LinkageGA(PopulationModel pop, Problem problem) {
	super(pop, problem);
	separateLinkage = false;
	numOps = 10;
	if(separateLinkage) ops = new ProbPop(numOps, pop.getIndividual(0).length());
	log.debug("LinkageGA separateLinkage is " + separateLinkage);
    }

    public void reset() {
	super.reset();
	if(separateLinkage) ops = new ProbPop(numOps, pop.getIndividual(0).length());
    }
    
    public ProbArray[] getOperators() {
	ProbArray[] array;

	if(separateLinkage) {
	    array = new ProbArray[ops.size()];
	    for(int i = 0; i < ops.size(); i++) {
		array[i] = (ProbArray)ops.get(i);
	    }
	}
	else {
	    array = new ProbArray[pop.size()];
	    for(int i = 0; i < pop.size(); i++) {
		array[i] = (ProbArray)((LinkageGenome)pop.getIndividual(i)).getLinkage();
	    }
	}
	
	return array;
    }
    
    // overriding the superclass method
    public void testSearch() {
	if(separateLinkage) {
	    generateWithExternalLinkage();
	}
	generateWithInternalLinkage();	
    }

    /* 
       HOW THIS WORKS...
       1. bind each selected genome with an operator (many to one)
       2. copy product of operation to next generation
       3. add fitness gain to operator
       4. replace operator with min fitness with a varient of one of the fittest.
       5. mutate all offpring with small rate
    */
    public void generateWithExternalLinkage() {
       	int eliteSize = (int)(eliteRatio * pop.size());

 	int[] binding = new int[pop.size()];
	double[] opCredit = new double[ops.size()]; // initially zero
	ProbPop newOps = new ProbPop();

	// associate each genome with an operator
	for(int i = 0; i < pop.size(); i++) {
	    binding[i] = (int)(Math.random() * ops.size());
	}
	
	//	log.debug("generating next generation");
 	for(int place = eliteSize; place < pop.size(); place++) {
	    
	    // copy offspring into next generation
	    int p1 = pick();
	    int p2;
	    do {
		p2 = pick();
	    } while (p1 == p2);

	    // generate offspring -- suss...
	    OffspringPair offspring = ((ProbArray)ops.get(binding[p1])).cross((LinkageGenome)pop.getIndividual(p1), (LinkageGenome)pop.getIndividual(p2));
	    
	    // deterministic crowding
	   	    
	    // mutate offspring
	    offspring.offspring1.mutate(mutationRate);
	    offspring.offspring2.mutate(mutationRate);
	    
	    // pairing
	    if(((IntGenome)pop.getIndividual(p1)).blockDistanceTo((IntGenome)offspring.offspring1) + ((IntGenome)pop.getIndividual(p2)).blockDistanceTo((IntGenome)offspring.offspring2) < ((IntGenome)pop.getIndividual(p1)).blockDistanceTo((IntGenome)offspring.offspring2) + ((IntGenome)pop.getIndividual(p2)).blockDistanceTo((IntGenome)offspring.offspring1)) {
		// pairing p1 with c1 and p2 with c2 
		double cf1 = fitness(offspring.offspring1);
		if(cf1 >= fitness.get(pop.getIndividual(p1))) {
		    nextPop.setIndividual(p1, offspring.offspring1);
		    fitness.set(pop.getIndividual(p1), cf1);
		}
		double cf2 = fitness(offspring.offspring2);
		if(cf2 >= fitness.get(pop.getIndividual(p2))) {
		    nextPop.setIndividual(p2, offspring.offspring2);
		    fitness.set(pop.getIndividual(p2), cf2);
		}
		    
	    }
	    else { // pairing p1 with c2 and p2 with c1
		double cf2 = fitness(offspring.offspring2);
		if(cf2 >= fitness.get(pop.getIndividual(p1))) {
		    nextPop.setIndividual(p1, offspring.offspring2);
		    fitness.set(pop.getIndividual(p1), cf2);
		}
		double cf1 = fitness(offspring.offspring1);
		if(cf1 >= fitness.get(pop.getIndividual(p2))) {
		    nextPop.setIndividual(p2, offspring.offspring1);
		    fitness.set(pop.getIndividual(p2), cf1);
		    //TODO: can we remove these fitness set operations?  fitness maps to individuals
		}
	    } 

	    // weigh benefits...
	    double benefit = 0;//fitness(offspring.offspring1) - (fitness[p1] + fitness[p2]) / 2.0;
	    
	    // credit to operator
	    opCredit[binding[place]] += benefit;
	    
	    //  mutate offspring a little
	    //offspring.mutate(mutationRate);

	    // and place offspring into next generation
	    //nextPop.copyIndividual(place, offspring);
	   
	    /*
	      if(!newOps.contains(ops.get(binding[p1]))) {
	      newOps.add(ops.get(binding[p1]));
	      }
	      
	      else if(ops.size() < 10){
	      ProbArray babyOp = (ProbArray)((ProbArray)ops.get(binding[p1])).clone();
	      babyOp.perturb(mutationRate);
	      newOps.add(babyOp);
	      } 
	      }
	      //	    log.debug("Filling place " + place + " with selection " + p + " bound to " + binding[p]);
	      
	      ops = newOps;
	      log.debug("ops size = " + ops.size());
	    */	
	    
	    // find best and worst operators
	    // need to avoid legacy selection
	    int best = (int)(Math.random() * ops.size());
	    int worst = (int)(Math.random() * ops.size());
	    for(int i = 0; i < ops.size(); i++) {
		if(opCredit[best] < opCredit[i]) best = i; // sticky comparisons
		if(opCredit[worst] > opCredit[i]) worst = i;
	    }

	    // replace worst operator with clone of best
	    ops.set(worst, ((ProbArray)ops.get(best)).clone());

	    // make it a variant
	    ((ProbArray)ops.get(worst)).perturb(mutationRate);
	}
    }


    // NOTE: Uses static population, i.e. elite size = pop size - 1 
    // ALSO: does own fitness evals.  may be able to cut them out of main loop
    public void generateWithInternalLinkage() 
    {
	int eliteSize = (int)(eliteRatio * pop.size());
	//for(int place = eliteSize; place < pop.getSize(); place++) {
	    nextPop.copyFrom(pop); 
	    
	    int p1 = pick();
	    int p2;
	    do {
		p2 = pick();
	    } while (p1 == p2);
	    
	    // NOTE: Deterministic Crowding requires UNIFORM selection
	    
	    OffspringPair offspring = ((LinkageGenome)pop.getIndividual(p1)).linkageCrossing((LinkageGenome)pop.getIndividual(p2));
	    //OffspringPair offspring = ((LinkageGenome)pop.getIndividual(p1)).uniformCrossing((LinkageGenome)pop.getIndividual(p2)); 
	    //OffspringPair offspring = ((LinkageGenome)pop.getIndividual(p1)).onePointCrossing((LinkageGenome)pop.getIndividual(p2)); 
 	    if(offspring == null) log.error("offspring = null");	    

	    // deterministic crowding
	    //pop.uniformCrossover(p1, p2); // does no replacement
	    
	    // mutate offspring
	    offspring.offspring1.mutate(mutationRate);
	    offspring.offspring2.mutate(mutationRate);
	    
	    // pairing
	    if(((IntGenome)pop.getIndividual(p1)).blockDistanceTo((IntGenome)offspring.offspring1) + ((IntGenome)pop.getIndividual(p2)).blockDistanceTo((IntGenome)offspring.offspring2) < ((IntGenome)pop.getIndividual(p1)).blockDistanceTo((IntGenome)offspring.offspring2) + ((IntGenome)pop.getIndividual(p2)).blockDistanceTo((IntGenome)offspring.offspring1)) {
		// pairing p1 with c1 and p2 with c2 
		/*
	    	double cf1 = fitness(offspring.offspring1);
		if(cf1 >= fitness[p1]) {
		    nextPop.setIndividual(p1, offspring.offspring1);
		    fitness[p1] = cf1;
		}
		double cf2 = fitness(offspring.offspring2);
		if(cf2 >= fitness[p2]) {
		    nextPop.setIndividual(p2, offspring.offspring2);
		    fitness[p2] = cf2;
		}
		    
	    }
	    else { // pairing p1 with c2 and p2 with c1
		double cf2 = fitness(offspring.offspring2);
		if(cf2 >= fitness[p1]) {
		    nextPop.setIndividual(p1, offspring.offspring2);
		    fitness[p1] = cf2;
		}
		double cf1 = fitness(offspring.offspring1);
		if(cf1 >= fitness[p2]) {
		    nextPop.setIndividual(p2, offspring.offspring1);
		    fitness[p2] = cf1;
		}*/
	    } 

	    /* 
	    // note offspring fitness
	    double offspringFitness = fitness(offspring);
	    int toReplace = leastFit();//pop.getSize() - 1;
	    
	    // if offspring same as worst then replace a clone
	    if(offspringFitness == minFitness()) {
	    IntList clones = new IntList();
	    for(int i = 0; i < pop.getSize(); i++) {
	    if(((LinkageGenome)pop.getIndividual(i)).isPhenotypicClone(offspring)) {
	    clones.add(i);
	    }
	    }
	    
	    // pick one of the clones to be replaced, or default to last if all same
	    if(clones.size() > 0) {
	    toReplace = clones.get((int)(Math.random() * clones.size()));
	    }
	    }
	    */	     
	    	    
	    //	     nextPop.copyIndividual(place, offspring);
	     //fitness[toReplace] = offspringFitness;
	     //	}
    }
}


// TODO: make this a wrapper, not a subclass!
class ProbPop extends ArrayList {

    public ProbPop() {
	super();
    }

    public ProbPop(int n, int l) {
	for(int i = 0; i < n; i++) {
	    add(new ProbArray(l));
	}
    }

    public Object get(int i) {
	return (ProbArray)super.get(i);
    }

    /*
    public ProbArray pickRandom() {
	return get(Math.random(size()));
    }
    */
}
    

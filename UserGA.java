package gad;


public class UserGA extends GeneticAlgorithm {
	
	LinkageModel model;
	
	public UserGA(PopulationModel pop, Problem problem) {
		super(pop, problem);
		selection = new UniformSelection(this);
		this.model = new LinkageModel(Config.getInt("GenomeLength"));
	}
	
	public LinkageModel getModel() {
		return model;
	}
	
	public void testSearch() {
		//int eliteSize = (int)(eliteRatio * pop.getSize());
		
		// 	for(int place = eliteSize; place < pop.getSize(); place++) {
		// steady-state, so copy whole population
		nextPop.copyFrom(pop);
		
		// copy offspring into next generation
		int p1 = pick();
		int p2;
		do {
			p2 = pick();
		} while (p1 == p2);
		
		//	    nextPop.copyIndividual(place, pop.getIndividual(p1));
		
		
		// generate offspring
		//OffspringPair offspring = model.recombine((IntGenome)pop.getIndividual(p1), (IntGenome)pop.getIndividual(p2));
		
		OffspringPair offspring = ((IntGenome)pop.getIndividual(p1)).twoPointRecombine((IntGenome)pop.getIndividual(p2));
		
		// deterministic crowding
		
		// mutate offspring
		offspring.offspring1.mutate(mutationRate);
		offspring.offspring2.mutate(mutationRate);
		
		// pairing
		if(((IntGenome)pop.getIndividual(p1)).blockDistanceTo((IntGenome)offspring.offspring1) + ((IntGenome)pop.getIndividual(p2)).blockDistanceTo((IntGenome)offspring.offspring2) < ((IntGenome)pop.getIndividual(p1)).blockDistanceTo((IntGenome)offspring.offspring2) + ((IntGenome)pop.getIndividual(p2)).blockDistanceTo((IntGenome)offspring.offspring1)) {
			// pairing p1 with c1 and p2 with c2 
			double cf1 = fitness(offspring.offspring1);
			if(cf1 >= fitness(p1)) {
				nextPop.setIndividual(p1, offspring.offspring1);
			}
			double cf2 = fitness(offspring.offspring2);
			if(cf2 >= fitness(p2)) {
				nextPop.setIndividual(p2, offspring.offspring2);
			}
			
		}
		else { // pairing p1 with c2 and p2 with c1
			double cf2 = fitness(offspring.offspring2);
			if(cf2 >= fitness(p1)) {
				nextPop.setIndividual(p1, offspring.offspring2);
			}
			double cf1 = fitness(offspring.offspring1);
			if(cf1 >= fitness(p2)) {
				nextPop.setIndividual(p2, offspring.offspring1);
			}
		}
	}
}

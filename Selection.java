package gad;


/**
This abstract class represents the selection scheme for a GA.  
For this, it needs to specify a probability distribution.  
It does NOT concern itself with search operators, e.g. crossover or mutation.
*/

public abstract class Selection {
    
	SearchAlgorithm ga;
	ValueMap f; // reference to fitness map
	Logger log;
	
	public Selection(SearchAlgorithm ga) {
		this.ga = ga;
		f = ga.getFitnessMap();
		log = new Logger();
	}
	
	public abstract ValueMap probabilities();
} 
    

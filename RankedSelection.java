package gad;


/* 
   NOTE: Only has to be constructed for a new GA or a change in selection probability distribution type.
 */

public class RankedSelection extends OrderedSelection {

    double decay; // decay rate for exponential fall-off
    
    public RankedSelection(GeneticAlgorithm ga, double decay) {
    	super(ga);
    	this.decay = decay;
    	//log.debug("in ranked selection constructor");
    	// generate distribution
    	double sum = 0;
    	double level = decay;
   	
    	for(int i = 0; i < distribution.length; i++) {
    		distribution[i] = level;
    		sum += distribution[i];
    		level *= decay;
    	}
    	
    	// now scale selection probability distribution to sum to one
    	for(int i = 0; i < distribution.length; i++) {
    		distribution[i] *= (1.0 / sum);
    	}
    }

    public ValueMap probabilities() {
	ValueMap p = new ValueMap();

	// order the individuals
	order();

	// copy probabilities from exp. distribution to returned distribution, using t
	for(int i = 0; i < distribution.length; i++) {
	    p.set(pos[i], distribution[i]);
	}

	// debug check sum to one
	/*double sum = 0;
	for(int i = 0; i < distribution.length; i++) {
		sum +=  distribution[i];
	}
	log.debug("sum of pd = " + sum);
	*/
	return p;
    }
}	

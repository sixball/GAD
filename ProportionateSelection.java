package gad;


import java.util.Iterator;

public class ProportionateSelection extends Selection {

    public ProportionateSelection(SearchAlgorithm ga) {
    	super(ga);
    }

    public ValueMap probabilities() {
    	
    	// get current fitnesses
    	f = ga.getFitnessMap();
    	
    	ValueMap p = new ValueMap();
    	
    	// find normalizing factor
    	double sum = f.sumValues();

    	if(sum == 0) 
    	{	
    		for(Iterator it = f.popSet().iterator(); it.hasNext();) {
    			//Genome g = (Genome)it.next();		
    			p.set(it.next(), 1.0 / f.size());
    		}
    	}
	
    	// finally set (scaled) distribution
    	else for(Iterator it = f.popSet().iterator(); it.hasNext();) {
    		Object g = it.next();
    		p.set(g, f.get(g) / sum);
    	}	
    	return p;
    }
}


package gad;


import java.util.Iterator;

public class UniformSelection extends Selection{
	
	public UniformSelection(GeneticAlgorithm ga) {
		super(ga);
	}
	
	public ValueMap probabilities() {
		ValueMap p = new ValueMap();
		// finally set (scaled) distribution
		for(Iterator it = f.popSet().iterator(); it.hasNext();) {
			Genome g = (Genome)it.next();
			p.set(g, 1.0 / f.size());
		}	
		return p;
	}
} 
    

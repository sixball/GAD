package gad;


public class EliteSelection extends OrderedSelection {
	
	int size;
	
	public EliteSelection(GeneticAlgorithm ga) {
		super(ga);
	}
	/*
	public EliteSelection(GA ga, double eliteRatio) {
		this(ga, (int)(ga..size() * eliteRatio));
	}
	*/
	public EliteSelection(GeneticAlgorithm ga, int size) {
		super(ga);
		this.size = size;
	
		// generate distribution
		for(int i = 0; i < distribution.length; i++) {
			if(i < size) distribution[i] = 1.0 / size;
			else distribution[i] = 0;
		}
		log.debug("in eliteselection constructor");
	}

	public ValueMap probabilities() {
		ValueMap p = new ValueMap();
		
		// get correct ordering
		order();
		
		for(int i = 0; i < pos.length; i++) {
			p.set(pos[i], 1.0 / pos.length);
		}
		
		return p;
	} 
}

package gad;


public class RoyalRoadGA extends GeneticAlgorithm {

    int sections;
    int length;
    int[] order; // for shuffling

  public RoyalRoadGA(PopulationModel pop, Problem problem) {
	super(pop, problem);
	sections = Config.getInt("NumberOfSections");
	length = Config.getInt("GenomeLength");
    }
    
    public void initialise() {
	super.initialise();
	order = new int[length];
	for(int i = 0; i < length; i++) order[i] = i; 

	//shuffle();
    }
    
    public void shuffle() {
	log.debug("Shuffling R1");
	for(int i = 0; i < length; i++) {
	    int j = (int)(Math.random() * length);
	    int t = order[j];
	    order[j] = order[i];
	    order[i] = t;
	}
    }

    // This can be done a couple of ways: either credit for sections which are uniformly ones or, the more general case: credit for homogenous sections, i.e. 0 or 1.
    public double fitness(Genome g) {
	if(sections == 0) return 0;
	int sectionLength = length / sections;
	double sum = 0;
	for(int i = 0; i <= length - sectionLength; i += sectionLength) {
	    int check = 1;
	    for(int j = 0; j < sectionLength; j++) {
		// homogenous case
		if(((IntGenome)g).get(order[i + j]) != ((IntGenome)g).get(order[i])) {
		
		    // just ones case
		    //if(((IntGenome)g).get(i + j) == 0) {

		// demo case
		// if(((IntGenome)g).get(i + j) != ((IntGenome)g).get(i)) {
		    check = 0;
		    break;
		}
	    }
	
	    sum += check;
	}
	return sum * (sectionLength / (double)length);
    }

    public int numSections() {
	return sections;
    }
  /*
    public static void main(String[] args) {
	Population p = new Population(10);
	p.setLength(16);
	RoyalRoadGA obj = new RoyalRoadGA(p);
	obj.initialise(4);
	BitGenome g = new BitGenome(16);
	System.out.println(g.toString() + " = " + obj.fitness(g));
     }
  */
}

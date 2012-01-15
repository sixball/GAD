package gad;


public class R1Problem extends Problem
{
  int sections;
  int sectionLength;
  int length;
  
  int[] order; // for shuffling
    
  //Population pop;

  public R1Problem()
  {
    super();
  }
  
  public void initialise() {
    sectionLength = Config.getInt("SectionLength");
    length = Config.getInt("GenomeLength");
    sections = length / sectionLength;
    log.debug(sections + " sections for R1");
    order = new int[length];
    for(int i = 0; i < length; i++) order[i] = i; 

    //shuffle();
  }

    // should be activated via config
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
  public double evaluate(Genome g) {
      if(g == null) log.error("trying to evaluate a null genome in R1Problem");
    if(sections == 0) return 0;
    double sum = 0;
    for(int i = 0; i <= g.length() - sectionLength; i += sectionLength) {
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
    return sum /(double)sections;
  }

  public int numSections() {
    return sections;
  }
}

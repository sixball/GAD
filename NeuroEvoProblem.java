package gad;


public class NeuroEvoProblem extends Problem 
{ 
  int gridSize;
  int numberOfProteins;
  
  public NeuroEvoProblem()
  {
    super();
    gridSize = Config.getInt("GridSize");
    numberOfProteins = Config.getInt("NumberOfProteins");
  }

    public void initialise() {
	//none required;
    }

  public double evaluate(Genome genome) {

    NeuroGenome g = (NeuroGenome)genome;
    //log.debug("genomes are type : " + genome.getClass().getName());
    
    //    int numberOfProteins = ((NeuroGenome)g).getNumProteins();

    int fitness;
    Organism organism = new Organism(g);
			
    fitness = gridSize*gridSize;

    for(int i=0; i<gridSize; i++) {
      for(int j=0; j<gridSize; j++) {
	
	if( i>=1 && i<4 && j>0 && j<10 ) {
	  if(organism.getCell(i, j).CellAlive()) {
	    if(organism.getCell(i, j).getProtein(0)==1.0 
	       && organism.getCell(i, j).getProtein(1)==0.0 
	       && organism.getCell(i, j).getProtein(2)==0.0) fitness--; 
	  }
	}
					

	else if(i>=4 && i<7 && j>0 && j<10 ) {
	  if(organism.getCell(i, j).CellAlive()) {
	    if(organism.getCell(i, j).getProtein(0)==0.0 
	       && organism.getCell(i, j).getProtein(1)==1.0 
	       && organism.getCell(i, j).getProtein(2)==0.0) fitness--; 
	  }
	}
	else if(i>=7 && i<10 && j>0 && j<10 ) {
	  if(organism.getCell(i, j).CellAlive()) {
	    if(organism.getCell(i, j).getProtein(0)==0.0 
	       && organism.getCell(i, j).getProtein(1)==0.0 
	       && organism.getCell(i, j).getProtein(2)==1.0) fitness--; 
	  }
	}
				
	else if(!organism.getCell(i, j).CellAlive()) fitness--;
      }
    }
    //    log.debug("raw fitness from neuroevoproblem : " + fitness);
    return 1.0 - (fitness / (double)(gridSize * gridSize));
  }
}

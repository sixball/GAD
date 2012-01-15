package gad;


import java.util.Random;


public class NeuroGenome extends IntGenome 
{
  static Random r = new Random();

  // set IntGenome.MAX and MIN for display purposes

    int numGenes, numProteins, numGeneTypes;

    public NeuroGenome() {
	super();
	numGenes = Config.getInt("NumberOfGenes");
	numGeneTypes = Config.getInt("NumberOfGeneTypes");
	numProteins = Config.getInt("NumberOfProteins");
    }

    private void checkMaxMin()
  {
    MAX = Math.max(numGenes, numProteins);
    MIN = 0;//Math.min(numGenes, numProteins);
  }
  

  /**
   * Get the value of numGenes.
   * @return value of numGenes.
   */
  public int getNumGenes() {
    return numGenes;
  }
  
  /**
   * Set the value of numGenes.
   * @param v  Value to assign to numGenes.
   *
  public static void setNumGenes(int  v) {
    numGenes = v;
    checkMaxMin();
  }
  */
   /**
   * Get the value of numProteins.
   * @return value of numProteins.
   *
  public static int getNumProteins() {
    return numProteins;
  }
  
  /**
   * Set the value of numProteins.
   * @param v  Value to assign to numProteins.
   *
  public static void setNumProteins(int  v) {
    if(v < 16) numProteins = 16;
    else numProteins = v;
    checkMaxMin();
  }
   */
  /**
   * Get the value of numGeneTypes.
   * @return value of numGeneTypes.
   */
  public int getNumGeneTypes() {
    return numGeneTypes;
  }
  
  public void initialise() 
    {
      //      setNumProteins(numProteins);
      //LENGTH = numGenes * 3;
      
      if(length % 3 != 0) log.error("length must be multiple of 3");
      else numGenes = length / 3;
      
      data = new int[length];
      for(int i = 0; i < length; i+=3) {
	data[i] = r.nextInt(numProteins);
	data[i+1] = r.nextInt(numGeneTypes);
	data[i+2] = r.nextInt(numProteins);
      }
    }

  public void mutate() 
  {
    double freq = Config.getDouble("MutationRate");
    for(int i = 0; i < data.length; i++) {
      if(Math.random() < freq) mutatePoint(i);
    }
  }

  public void mutatePoint(int i) 
    {
      //int i = r.nextInt(length);
      if(i % 3 == 1) data[i] = r.nextInt(numGeneTypes);
      else data[i] = r.nextInt(numProteins);
    }
  
  public void mutateGene()
    {
      int i = r.nextInt(numGenes);
      data[i*3] = r.nextInt(numProteins);
      data[i*3+1] = r.nextInt(numGeneTypes);
      data[i*3+2] = r.nextInt(numProteins);
    }

  public void duplicateGene()
    {
      int i = r.nextInt(numGenes);
      int j = r.nextInt(numGenes);
      data[j*3] = data[i*3];
      data[j*3+1] = data[i*3+1];
      data[j*3+2] = data[i*3+2];
    }

  public NeuroGenome mutantOf() 
  {
    NeuroGenome m = (NeuroGenome)this.clone();
    m.mutate();
    return m;
  }

  public Object clone() {
    NeuroGenome newbie = new NeuroGenome();
    newbie.data = new int[data.length];
    for(int i = 0; i < data.length; i++) {
      newbie.data[i] = data[i];
    }
    return newbie;
  }
}


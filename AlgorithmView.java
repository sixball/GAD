package gad;


public abstract class AlgorithmView extends View implements AlgorithmListener
{
  SearchAlgorithm al;
  
  public AlgorithmView(SearchAlgorithm al)
  {
    super();
    this.al = al;
  }
  
  public SearchAlgorithm getAlgorithm() 
  {
    return al;
  }
}

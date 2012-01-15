package gad;


/**
   This interface evaluates the 'fitness' of candidate solutions. It produces a value between 0 and 1, where 1 is best.
*/
public interface Evaluator 
{
  public abstract double evaluate(Genome g);
}

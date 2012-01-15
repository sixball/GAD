package gad;


public class NeuroGeneticAlgorithm extends GeneticAlgorithm 
{
  public NeuroGeneticAlgorithm(SearchModel pop, Problem problem) 
  {
    super(pop, problem);
    
    setSelectionScheme(RANK_SELECTION);
  }
  

  public void evolve(int steps) 
  {
    cancelled = false;
    displayTimer.start();
    for(int i = 0; i < steps; i++) {
      if(cancelled == true) break;
      iteration++;
      evaluateAll();
      pop.sort(fitness);
      selectionProbability = selection.probabilities();
      makeRoulette();
      pop.sampleAll();
      crossoverStage();
      mutationStage();
      doRecords();
      
    }
    displayTimer.stop();
  }

  public void crossoverStage() 
  {
      int p1 = pick();
      int p2;
      do {
	p2 = pick();
      } while (p1 == p2);

      OffspringPair op = ((IntGenome)pop.getIndividual(p1)).twoPointRecombine((IntGenome)pop.getIndividual(p2));
      
      IntGenome best = (IntGenome)pop.getIndividual(Math.min(p1, p2));
      
      double bestFitness = problem.evaluate(best);
      if(problem.evaluate(op.offspring1) >= bestFitness) 
      {
	best = op.offspring1;
      }
      else if(problem.evaluate(op.offspring2) >= bestFitness) 
      {
	best = op.offspring2;
      }

      pop.setIndividual(pop.size() - 1, best);
  }
  
  public void mutationStage()
  {
    for(int i = 0; i < pop.size(); i++) 
    {
      NeuroGenome original = (NeuroGenome)pop.getIndividual(i);
      NeuroGenome mutant = original.mutantOf();
      if(problem.evaluate(mutant) >= problem.evaluate(original)) 
      {
	pop.setIndividual(i, mutant);
      }
    }
  }
}

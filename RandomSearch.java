/*
 * Created on 14-Jul-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package gad;


/**
 * @author Simon
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RandomSearch extends GeneticAlgorithm {

	Genome best;

	/**
	 * @param pop
	 * @param problem
	 * @param config
	 */
	public RandomSearch(SearchModel pop, Problem problem) {
		super(pop, problem);
		// TODO Auto-generated constructor stub
	}
	
	public void initialise() {
		best = new IntGenome();
		best.initialise();
		pop.initialise();
	}
	
	public void search(int steps) {
		cancelled = false;
		//displayTimer.start();
		for (int i = 0; i < steps; i++) {
			if (cancelled == true)
				break;
			
			iteration++;
		
			fitness.clear();
			
			// create new pop
			pop.initialise();
			
			if(pop == null) log.debug("pop in RandomSearch.search");
			
			// insert best
			pop.setIndividual(0, best);
			
			// evaluate all
			evaluateAll();
			
			// replace best if challenger is as good
			//System.out.println("fitness.maxValue=" + fitness.maxValue());
			//System.out.println("fitness.get(best)=" + fitness.get(best));
			if(fitness.get(best) < fitness.maxValue()) {
				best = pop.getBest(fitness);
				//System.out.println("new best random fitness is " + fitness(best));
			}
		}
		doRecords();
		
		fitnessAfter.put((int)evaluations, (int)fitness.maxValue());
		//displayTimer.stop();
		//pop.notifyListeners();
		notifyListeners();
	}

	public static void main(String[] args) {
	}
}

package gad;

/**
   Factored-out general search functionality for possible future work for different search algorithms.
*/

import java.io.*;
import java.awt.event.*;
import javax.swing.Timer;

public abstract class PopulationSearch extends SearchAlgorithm
{
    PopulationModel pop;
   
    Timer displayTimer;

    protected PopulationSearch() {
    }
    
    public PopulationSearch(PopulationModel pop, Problem problem) {
	super(pop, problem);

	maxFitnessHistory = new DoubleList();
	minFitnessHistory = new DoubleList();
	meanFitnessHistory = new DoubleList();
	evaluationHistory = new IntList();

	//fitness = new double[pop.getSize()];
	fitness = new ValueMap();

	displayTimer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    firePopulationChanged();
		    fireAlgorithmChanged();
		}
	    });
    }
    
    protected double fitness(int i) {
    	return fitness(pop.getIndividual(i));
    }
    
    public double fitness(Genome g) {
    	log.ifNull(g, "genome g in populationSearch");
    	return problem.evaluate(g);
    }
    

   
    public double meanFitness() {
    	// should check fitness profile exists...
    	double sum = 0;
    	for(int i = 0; i < pop.size(); i++) sum += fitness(i);
    	return sum / pop.size();
    };

    public int mostFit() {
	int best = 0;
	double max = fitness(best);
	for(int i = 1; i < pop.size(); i++) 
		if(max <= fitness(i) && Math.random() > 0.5) {
			max = fitness(i);
			best = i;
		}
	return best;
    }

    public int leastFit() {
	int worst = 0;
	double min = fitness(worst);
	for(int i = 1; i < pop.size(); i++) 
		if(min > fitness(i)) {
			min = fitness(i);
			worst = i;
		}
	return worst;
    }

    // use this sparingly to avoid MANY unnecessary evaluations and swollen hashmap
    public void evaluateAll() {
//    	log.debug("evaluating popsize = " + pop.size());
    	if(pop == null) log.debug("pop in Population.evalAll");
		
    	for(int i=0; i < pop.size(); i++) {
    		evaluate(pop.getIndividual(i));
    	}
    }

    public void evaluate(Genome g) {
    //	fitness[pop.indexOf(g)] ;
    	fitness.set(g, problem.evaluate(g));
    	evaluations++;
    }
    
    
    // presupposes current evaluation
    public void doRecords() {
    	maxFitnessHistory.add(maxFitness());
    	minFitnessHistory.add(minFitness());	
    	meanFitnessHistory.add(meanFitness());
    	evaluationHistory.add((int)getEvaluations());
    	
    	allTimeMax = Math.max(allTimeMax, maxFitness());
    	allTimeMin = Math.min(allTimeMin, minFitness());
    }
    
    
    public boolean writeFitnessHistory(String fitnessFile) {
    	PrintWriter w;
    	try {
    		w = new PrintWriter(new FileWriter(fitnessFile));
    	} catch(IOException e) {
    		log.error(e.getMessage());
    		return false;
    	}
    	
    	for(int set = 0; set < 3; set++) {
    		DoubleList dl = null;
    		switch(set) {
    		
    		case 0:
    			dl = maxFitnessHistory;
    			w.println("# max fitness");
    			break;
    			
    		case 1:
    			dl = meanFitnessHistory;
    			w.println("# mean fitness");
    			break;
    			
    		case 2:
    			dl = minFitnessHistory;
    			w.println("# min fitness");
    			break;
    		
    		default:
    			// should never happen
    			break;
    		}
    		
    		log.debug("writing " + iteration + " iterations");
    		
    		for(int i = 0; i < iteration; i++) {
    			String s = String.valueOf(dl.get(i));
    			w.println(s);
    		}
    		if(set < 2) {
    			w.println();
    			w.println();
    		}
    	}
    	
    	w.close();
    	
    	return w.checkError();
    }

    public void firePopulationChanged() {
    	pop.notifyListeners();
    }
}


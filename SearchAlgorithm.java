package gad;


import java.util.*;

public abstract class SearchAlgorithm {
	protected long iteration;
	protected long evaluations;
	protected ValueMap fitness;
	protected DoubleList maxFitnessHistory;
	protected DoubleList minFitnessHistory;
	protected DoubleList meanFitnessHistory;
	protected IntList evaluationHistory;
	protected double allTimeMax = Double.MIN_VALUE;
	protected double allTimeMin = Double.MAX_VALUE;

	IntHashMap fitnessAfter; // maps eval number to best f

	ArrayList views;

	static Random r;

	public boolean cancelled;

	SearchModel model;

	Problem problem;

	Logger log;

	protected SearchAlgorithm() {
		log = new Logger();
	}

	public SearchAlgorithm(SearchModel model, Problem problem) {
		this.model = model;
		this.problem = problem;
		log = new Logger();
		views = new ArrayList();
		r = new Random();
		fitness = new ValueMap();
		fitnessAfter = new IntHashMap();
		maxFitnessHistory = new DoubleList();
		minFitnessHistory = new DoubleList();
		evaluationHistory = new IntList();
	}

	public abstract void initialise();

	public void reset() {
		allTimeMax = Double.MIN_VALUE;
		allTimeMin = Double.MAX_VALUE;
		
		maxFitnessHistory.clear();
		minFitnessHistory.clear();
		evaluationHistory.clear();

		evaluations = 0;
		iteration = 0;
	}

	public abstract void search(int i);

	public void addListener(AlgorithmListener listener) {
		views.add(listener);
	}

	public void notifyListeners() {
		for (ListIterator i = views.listIterator(); i.hasNext();) {
			((AlgorithmListener) i.next()).algorithmChanged();
		}
	}

	public long getEvaluations() {
		return evaluations;
	}

	public double fitnessAfter(int e) {
		return fitnessAfter.get(e);
	}

	public long getIteration() {
		return iteration;
	}

	public Problem getProblem() {
		return problem;
	}

	public double allTimeMax() {
		return allTimeMax;
	}

	public double allTimeMin() {
		return allTimeMin;
	}
	 // NOTE: Selection needs reference to original
    public ValueMap getFitnessMap() { 
    	//if(fitness.size() != pop.size()) 
    		//log.error("fitness map size = " + fitness.size() + " whereas pop size = " + pop.size());
    	return fitness; 
    }
    
    public DoubleList maxFitnessHistory() { 
    	return maxFitnessHistory; 
    }
    public DoubleList minFitnessHistory() { 
    	return minFitnessHistory; 
    }
    public DoubleList meanFitnessHistory() { 
    	return meanFitnessHistory; // this could be dodgey
    }
    
    public IntList evaluationHistory() {
    	return evaluationHistory;
    }

	public double maxFitness() {
		return fitness.maxValue();
	}

	public double minFitness() {
		return fitness.minValue();
	}
	
	public abstract void doRecords();

	public void fireAlgorithmChanged() {
		notifyListeners();
	}
}
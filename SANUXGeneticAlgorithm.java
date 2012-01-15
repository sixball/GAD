package gad;


/** 
Reproduced from SANUX by Dr Shengxiang Yang 
*/
public class SANUXGeneticAlgorithm extends GeneticAlgorithm {
	
	double minP = 0;
	double maxP = 0.5;
	
	public static final int TRIANGULAR_FUNCTION = 1;
	public static final int TRAPEZOID_FUNCTION = 2;
	public static final int EXPONENTIAL_FUNCTION = 3;
	
	int function;
	int length;
	
	public SANUXGeneticAlgorithm(PopulationModel pop, Problem problem, Config config) 
	{
		super(pop, problem);	
		log.debug("Using SANUX Genetic Algorithm");
	}
	
	public void initialise() {
		super.initialise();
		setSelectionScheme(PROPORTIONAL_SELECTION);
		generationScheme = TEST_SEARCH;
		length = Config.getInt("GenomeLength");
		String setFunction = (String)Config.get("SANUXFunction");
		if(setFunction.equals("triangular")) function = TRIANGULAR_FUNCTION;
		if(setFunction.equals("trapezoid")) function = TRAPEZOID_FUNCTION;		
		if(setFunction.equals("exponential")) function = EXPONENTIAL_FUNCTION;
		reset();
	}
	
	public ProbArray swappingProbability(int function) 
	{
		ProbArray pd = new ProbArray(length);
		for(int l = 0; l < length; l++) 
		{
			int freq = 0;
			for(int i = 0; i < pop.size(); i++) 
			{
				if(((IntGenome)pop.getIndividual(i)).get(l) == 1) freq++;
			}
			
			double f1 = freq / (double)pop.size();
			
			
			switch(function) 
			{
			case TRIANGULAR_FUNCTION:
				pd.set(l, maxP - 2.0 * Math.abs(f1 - 0.5) * (maxP - minP));
				break;
				
			case TRAPEZOID_FUNCTION:
				double a = 0.1;
				double b = 0.4;
				if(Math.abs(f1 - 0.5) <= a) pd.set(l, maxP);
				if(a < Math.abs(f1 - 0.5) && Math.abs(f1 - 0.5) < b) 
				{
					pd.set(l, (b - Math.abs(f1 - 0.5)) / (b - a));
				}
				if(Math.abs(f1 - 0.5) >= b) pd.set(l, minP);
				break;
				
			case EXPONENTIAL_FUNCTION:
				double alpha = 0.5;
				double beta = 0.04;
				pd.set(l, alpha * Math.exp(-Math.pow((f1 - 0.5), 2) / beta));
				break;
				
			default:
				log.error("Swapping probability function not known");
				
			}
		}
		
		return pd;
	}
	
	public void testSearch() 
	{
		ProbArray swapProb = swappingProbability(TRIANGULAR_FUNCTION);
		
		eliteCarry();
		
		for(int place = (int)(pop.size() * eliteRatio);
		place < pop.size(); place += 2) {
			
			int p1 = pick();
			int p2;
			do {
				p2 = pick();
			} while (p1 == p2);
			
			// generate offspring
			// create offspring from crossover with set probability
			OffspringPair offspring;
					
			if(Math.random() < 0.6) 
			{
				offspring = ((IntGenome)pop.getIndividual(p1)).nonUniformRecombine((IntGenome)pop.getIndividual(p2), swapProb);
				//offspring = ((IntGenome)pop.getIndividual(p1)).uniformRecombine((IntGenome)pop.getIndividual(p2));
				//offspring = ((IntGenome)pop.getIndividual(p1)).twoPointRecombine((IntGenome)pop.getIndividual(p2));
			}
			else
			{
				offspring = new OffspringPair((IntGenome)pop.getIndividual(p1), (IntGenome)pop.getIndividual(p2));
			}
			
			// mutate offspring
			((IntGenome)offspring.offspring1).mutate(mutationRate);
			((IntGenome)offspring.offspring2).mutate(mutationRate);
			
			// replace parents in next generation
			nextPop.addCopyOf(offspring.offspring1);
			if(place + 3 < pop.size()) {
				nextPop.addCopyOf(offspring.offspring2);
			}
		}
	}
}


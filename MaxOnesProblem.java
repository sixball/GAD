package gad;


public class MaxOnesProblem extends Problem implements Evaluator 
{
    public MaxOnesProblem() {
    	super();
    }

    public void initialise(){
    	// nothing required
    }
    
    public double evaluate(Genome g)  {
	double sum = 0;
	for(int i = 0 ; i < g.length(); i++) 
	    {
		if(((IntGenome)g).get(i) == 1) sum++;
	    }
	return sum / g.length();
    }
}

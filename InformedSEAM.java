/*
 * Created on 09-Jan-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gad;


/**
 * @author Simon
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InformedSEAM extends SEAMGeneticAlgorithm {
	
	PopulationModel evalSet;
	
	/**
	 * @param pop
	 * @param prob
	 */
	public InformedSEAM(SearchModel pop, Problem prob) {
		super(pop, prob);
		// TODO Auto-generated constructor stub
	}
	
	public void reset() {
		evalSet = new PopulationModel();
		super.reset();
	}
	
	/*
	 * different from SEAM in that it selects pairs to evaluate informed by a
	 * set of past individuals
	 */
	public void generate() {
		
	}

}

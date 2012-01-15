package gad;


import java.util.Iterator;
import java.util.Set;

/**

This abstract class can be subclassed by selection schemes which are 
based on the relative ordering of individuals, e.g. Ranked or Elite.
*/

public abstract class OrderedSelection extends Selection {
	
	Genome[] pos; // relative position in ordering
	double distribution[];
	
	// is this necessary? Isn't it implicit?
	public OrderedSelection(GeneticAlgorithm ga) {
		super(ga);
		distribution = new double[f.size()];
	}
	
	public void order() {
		// fitnesses may or may not be sorted, so using an index table
		pos = new Genome[f.size()];
		Set pop = f.popSet();
		int count = 0;
		for(Iterator it = pop.iterator(); it.hasNext(); count++) pos[count] = (Genome) it.next();
		
		// very simple sort of pos so that it indexes elements in order of fitness
		// should use a more efficient algorithm really..
		Genome tmp;
		for(int i = 0; i < pos.length - 1; i++) {
			for(int j = i; j < pos.length; j++) {
				if(f.get(pos[i]) < f.get(pos[j])) {
					
					// swap index
					tmp = pos[i];
					pos[i] = pos[j];
					pos[j] = tmp;
				}
			}
		}
	}
}

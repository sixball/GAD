/*
 * Created on 28-Jan-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package gad;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Simon
 *
 * This maps genomes to double values.  Used for fitness value and selection probablilities.
 */
public class ValueMap {
	HashMap map;
	
	Logger log;
	
	public ValueMap(){
		log = new Logger();
		map = new HashMap();
	}
	
	public void clear() {
		map.clear();
	}
	
	public double get(Object g) {
		if(g == null) log.error("genome in valuemap get");
		if(map == null) log.error("map is null in valuemap get");
		if(!map.containsKey(g)) log.error("No value for genome!");
		return ((Double)map.get(g)).doubleValue();
	}
	
	public void set(Object g, double f) {
		map.put(g, new Double(f));
	}
	
	public void remove(Object g) {
		map.remove(g);
	}
	
	public void remove(PopulationModel pop){
		for(int i = 0; i < pop.size(); i++) {
			remove(pop.getIndividual(i));
		}
	}
	
	public int size() {
		return map.size();
	}
	
	public Set popSet() {
		return map.keySet();
	}
	
	public double maxValue() {
		double max = 0;
		for(Iterator it = map.values().iterator(); it.hasNext();) {
			double next = ((Double)it.next()).doubleValue();
			if(next > max) max = next;
		}
		return max;
	}		
	
	public double minValue() {
		double min = Double.MAX_VALUE;
		for(Iterator it = map.values().iterator(); it.hasNext();) {
			double next = ((Double)it.next()).doubleValue();
			if(next > min) min = next;
		}
		return min;
	}		
	
	public double sumValues() {
		double sum = 0;
		for(Iterator it = map.values().iterator(); it.hasNext();) {
			sum += ((Double)it.next()).doubleValue();
		}
		return sum;
	}
	
	public Set entrySet() {
		return map.entrySet();
	}
	
	// checks to see if map has value for identical obj
	public boolean containsSame(Object obj) {
		for(Iterator it = map.keySet().iterator(); it.hasNext();  ) {
			if(((GeneSet)it.next()).equals((GeneSet)obj)) return true;
		}
		return false;
	}
}

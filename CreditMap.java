/*
 * Created on 14-Aug-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gad;


import java.util.*;

/**
 * @author Simon
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
//basically copied from valuemap which maps from genomes to values.
//genomes are not quite the same as GenomeSets or Genes, hence the adaption..
class CreditMap {
	HashMap map;

	Logger log;

	public CreditMap() {
		log = new Logger();
		map = new HashMap();
	}

	public void clear() {
		map.clear();
	}

	public double get(GeneSet g) {
		if (g == null)
			log.error("genome in valuemap get");
		if (map == null)
			log.error("map is null in valuemap get");
		if (!map.containsKey(g))
			log.error("No value for geneset:\n" + g);
		return ((Double) map.get(g)).doubleValue();
		//return (Math.floor(d * 100.0)) / 100.0; // to 2 dp
	}

	public void set(GeneSet g, double f) {
		map.put(g, new Double(f));
		//log.debug("creditting \n" + g.flatString() + " with " + f);
	}

	public void remove(GeneSet g) {
		map.remove(g);
	}

	public boolean credits(GeneSet g) {
		Double test = (Double) map.get(g);
		//if (test != null && !map.containsKey(g))
		//	log.error("contradiction in creditted()");
		return map.containsKey(g);
	} /*
	   * public void remove(Population pop){ for(int i = 0; i < pop.size(); i++) {
	   * remove(pop.getIndividual(i)); } }
	   */

	public int size() {
		return map.size();
	}

	public Set popSet() {
		return map.keySet();
	}

	public double maxValue() {
		double max = 0;
		for (Iterator it = map.values().iterator(); it.hasNext();) {
			double next = ((Double) it.next()).doubleValue();
			if (next > max)
				max = next;
		}
		return max;
	}
	
	public double minValue() {
		double min = 0;
		for (Iterator it = map.values().iterator(); it.hasNext();) {
			double next = ((Double) it.next()).doubleValue();
			if (next > min)
				min = next;
		}
		return min;
	}

	public double sumValues() {
		double sum = 0;
		for (Iterator it = map.values().iterator(); it.hasNext();) {
			sum += ((Double) it.next()).doubleValue();
		}
		return sum;
	}

	public String toString() {
		// ordered by descending value
		// performed by repeatedly adding the highest item to another hashmap
		// then outputting and removing the item
		StringBuffer sb = new StringBuffer();

		ArrayList listed = new ArrayList();

		sb.append("Credit:\n");
		
		for (int i = 0; i < size(); i++) {
			Object key = null;
			Object highestLeft = null;
			for (Iterator j = map.keySet().iterator(); j.hasNext();) {
				key = j.next();
				if (!listed.contains(key)) { // new key, not already listed
					
					// assign if we don't have a key already or value higher
					if (highestLeft == null || 
							get((GeneSet) key) > get((GeneSet) highestLeft))
						highestLeft = key;
				}
			}
			sb.append(highestLeft +  "\n--> "
					+ (Math.floor(get((GeneSet) highestLeft) * 100.0) / 100.0)
					+ "\n");
			listed.add(highestLeft);
		}
		return sb.toString();
	}

}
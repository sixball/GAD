package gad;


/**
 * 
 * Partial Int Genome extends Int Genome but values below 0 are considered
 * 'unspecified'. The size of the PIG is the number of specified alleles.
 *  
 */

//import java.util.Random;

public class PartialIntGenome extends IntGenome {

	int size = 0;
/*
	public PartialIntGenome() {
		initialise();
	}
*/
	public PartialIntGenome() {
		super();
		if(Config.isSet("GenesSpecified")) 
				size = Config.getInt("GenesSpecified");
		else size = 0;
		initialise();
	}

	public void setSize(int s) {
		size = s;
	}
	public int getSize() {
		return size;
	}

	public void initialise() {
		int length = Config.getInt("GenomeLength");
		data = new int[length];
		// initialise to unspecified
		for (int i = 0; i < length; i++)
			data[i] = -1;

		int[] order = new int[length]; // first size of these will be specified

		for (int i = 0; i < length; i++)
			order[i] = i; // 123..
		for (int i = 0; i < length; i++) { // shuffle
			int j = r.nextInt(length);
			int t = order[i];
			order[i] = order[j];
			order[j] = t;
		}

		// initialise specified genes
		for (int i = 0; i < size; i++) {
			log.ifNull(r, "random r");
			if (r == null)
				log.error("r = null");
			log.ifNull(data, "data");
			log.ifNull(order, "order");
			data[order[i]] = r.nextInt(MAX - MIN + 1) + MIN;
		}
	}

	public boolean specifies(int i) {
		return (data[i] >= 0);
	}

	static PartialIntGenome compose(PartialIntGenome a, PartialIntGenome b) {
		// favours 'a' in a conflict
		PartialIntGenome o = (PartialIntGenome) a.clone();
		for (int i = 0; i < a.length(); i++) {
			if (!a.specifies(i) && b.specifies(i))
				o.set(i, b.get(i));
		}
		return o;
	}

	// b is fully specified and can always fill in gaps
	static PartialIntGenome compose(PartialIntGenome a, IntGenome b) {
		PartialIntGenome o = (PartialIntGenome) a.clone();
		for (int i = 0; i < a.length(); i++) {
			if (!a.specifies(i))
				o.set(i, b.get(i));
		}
		return o;
	}

	public Object clone() {
		PartialIntGenome newbie = new PartialIntGenome();
		newbie.data = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			newbie.data[i] = data[i];
		}
		newbie.size = size;
		return newbie;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			if (specifies(i))
				sb.append(String.valueOf(data[i]));
			else
				sb.append("-");
			if (i != data.length - 1)
				sb.append("");
		}
		return sb.toString();
	}
	/*
	 * public static void main(String[] argv) { IntGenome.MAX = 20;
	 * IntGenome.MIN = 5;
	 * 
	 * Population pop = new Population(10); // pop size = 10
	 * pop.initialise(PartialIntGenome.class, 8, 10);
	 *  // PartialIntGenome pig = new PartialIntGenome(10, 2);
	 * log.debug(pig.toString()); }
	 */
}

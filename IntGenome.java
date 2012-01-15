package gad;


public class IntGenome extends Genome {
	
	/* the allowable range */
	public static int MAX = 1;
	public static int MIN = 0;
	
	protected int[] data;
	
//	public IntGenome() {
	//}
	
	public IntGenome() {
		super();//super(config);
	}
	
	// inititialises a genome using the class data members
	public void initialise(int length) {
		data = new int[length];
		for(int i = 0; i < length; i++) {
			data[i] = r.nextInt(MAX - MIN + 1) + MIN; 
		}
	}
	
	public void initialise() {
		initialise(Config.getInt("GenomeLength"));
	}
	
	public int length() { return data.length; }
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < data.length; i++) {
			sb.append(String.valueOf(data[i]) + " ");
		}
		return sb.toString();
	}
	
	public int get(int i) { 
		if(i < 0 || i >= data.length) 
			log.error("Requesting value at position " + i 
					+ ".  Values can only be between 0 and " 
					+ data.length);
		return data[i]; 
	}
	
	public void set(int i, int a) { data[i] = a;} // should do checks...
	
	// NOTE: a mutated value can NOT be the same as its prior value, 
	// mutation rate is effectively less otherwise.
	public void mutate(double freq) {
		for(int i = 0; i < data.length; i++) {
			if(r.nextDouble() < freq) {
				int newValue;
				do {
					newValue = r.nextInt(MAX - MIN + 1) + MIN;
				} while (data[i] == newValue);
				data[i] = newValue;
			}
		}
		// to implement
		// stepmutate and jumpmutate
	}
	
	
	// assume same length for this and mate
	// 2-point
	public IntGenome crossedWith(IntGenome mate) {
		IntGenome offspring = new IntGenome();
		//offspring.initialise(data.length);
		int x1 = (int)(Math.random() * data.length);
		int x2;
		do {
			x2 = (int)(Math.random() * data.length);
		} while(x1 == x2);
		for(int i = 0; i < length(); i++) {
			if(i < x1 && i > x2) offspring.data[i] = data[i];
			else offspring.data[i] = mate.data[i];
		}
		return offspring;
	}
	
	// assume same length for this and mate
	// uniform
	public IntGenome uniformCrossing(IntGenome mate) {
		IntGenome offspring = new IntGenome();
		for(int i = 0; i < data.length; i++) {
			if(Math.random() < 0.5) offspring.data[i] = data[i];
			else offspring.data[i] = mate.data[i];
		}
		return offspring;
	}
	
	public OffspringPair uniformRecombine(IntGenome mate) {
		IntGenome o1 = (IntGenome)this.clone();
		IntGenome o2 = (IntGenome)mate.clone();
		for(int i = 0; i < data.length; i++) {
			if(Math.random() < 0.2) {
				o1.set(i, mate.get(i));
				o2.set(i, this.get(i));
			}
		}	
		return new OffspringPair(o1, o2);
	}
	
	public OffspringPair nonUniformRecombine(IntGenome mate, ProbArray pd) {
		IntGenome o1 = (IntGenome)this.clone();
		IntGenome o2 = (IntGenome)mate.clone();
		for(int i = 0; i < data.length; i++) {
			if(Math.random() < pd.get(i)) {
				o1.set(i, mate.get(i));
				o2.set(i, this.get(i));
			}
		}	
		return new OffspringPair(o1, o2);
	}
	
	public OffspringPair twoPointRecombine(IntGenome mate) {
		IntGenome o1 = (IntGenome)this.clone();
		IntGenome o2 = (IntGenome)mate.clone();
		int x1 = (int)(Math.random() * data.length);
		int x2;
		do {
			x2 = (int)(Math.random() * data.length);
		} while(x1 == x2);
		for(int i = 0; i < data.length; i++) {
			if(i < x1 && i > x2) {
				o1.set(i, mate.get(i));
				o2.set(i, this.get(i));
			}
		}	
		return new OffspringPair(o1, o2);
	}
	
	
	public Object clone() {
		IntGenome newbie = new IntGenome();
		newbie.data = new int[data.length];
		for(int i = 0; i < data.length; i++) {
			newbie.data[i] = data[i];
		}
		return newbie;
	}
	
	public boolean isPhenotypicClone(IntGenome g) {
		for(int i = 0; i < g.data.length; i++) {
			if(data[i] != g.data[i]) return false;
		}
		return true;
	}
	
	// returns the 'block' distance for the two genomes.  
	// Equal to Hamming distance for binary 
	public int blockDistanceTo(IntGenome g) {
		int sum = 0;
		for(int i = 0; i < g.data.length; i++) {
			sum += Math.abs(g.data[i] - data[i]);
		}
		return sum;
	}
}

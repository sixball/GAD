package gad;


public class TSPGenome extends IntGenome {
	
	final public static int TWO_OPT = 0;
	final public static int THREE_OPT = 1;  
	
	public TSPGenome() {
		super();
		data = new int[Config.getInt("GenomeLength")];
	}
	
	public void initialise() {
		
		// generated ordered sequence 1..n
		for(int i = 0; i < length(); i++) data[i] = i;
		
		// shuffle
		int t, j;
		for(int i = 0; i < length; i++) {
			j = (int)(Math.random() * length());
			t = data[j];
			data[j] = data[i];
			data[i] = t;
		}
	}
	
	public void mutate(double freq) {
		for(int i = 0; i < (int)freq; i++) {
			mutate();
		}
	}
	
	public void mutate() {
		twoOpt();
	}
	
	public void twoOpt() {
		// generate 2 non-adjacent points, i < j
		int i, j;
		do {
			i = (int)(Math.random() * length());
			j = (int)(Math.random() * length());
		} while((j - i) < 1);
		
		// make copy of genome
		TSPGenome copy = (TSPGenome)this.clone();
		
		// starting at i, copy section in reverse until j is reached
		for(int c = 0; c < j-i; c++) {
			copy.data[i+c] = data[j-c-1];
		}
		
		// assign copy to this
		this.data = copy.data;
	}
	
	public void threeOpt() {
		// seem to be various different ways of doing three-opt... 
		
		// generate 3 non-adjacent points, i < j < k
		int i, j, k;
		do {
			i = (int)(Math.random() * length);
			j = (int)(Math.random() * length);
			k = (int)(Math.random() * length);
		} while((j - i) < 2 || (k - j) < 2);
		
		// make copy of genome
		TSPGenome copy = (TSPGenome)this.clone();
		
		// starting at i, copy section in reverse until j is reached
		for(int c = 0; c < j-i; c++) {
			copy.data[i+c] = data[j-c-1];
		}
		
		// starting at j, copy section in reverse until k is reached
		for(int c = 0; c < k-j; c++) {
			copy.data[j+c] = data[k-c-1];
		}
		
		// assign copy to this
		this.data = copy.data;
	}
	
	// kludge crossover 1
	public TSPGenome orderCrossover(TSPGenome other) {
		TSPGenome offspring = new TSPGenome();
		int[] newData = new int[length];
		
		// copy section from this
		int start, end, i, j;
		do {
			start = (int)(Math.random() * length);
			end = (int)(Math.random() * length);
		} while(end - start < 3);  // min section length is 2
		
		int[] visited = new int[end - start];
		for(i = start; i < end; i++) {
			newData[i] = data[i];
			//    visited[i - start] = data[i];
		}
		
		//	int[] available = new int[data.length - end + start];
		j = end; // other counter
		// get available cities
		do {
			if(i == data.length) i = 0; // wrap around
			
			// check availability
			boolean available = true;
			for(int k = start; k < end; k++) {
				if(data[k] == other.data[i]) {
					available = false;
					break;
				}
			}
			if(available) newData[j++] = other.data[i];
			if(j == newData.length) j = 0;
			i++;
		} while(i != end);
		offspring.data = newData;
		return offspring;
	}
	
	//public static int length() { return length; }
	
	public Object clone() {
		TSPGenome copy = new TSPGenome();
		copy.data = new int[data.length];
		for(int i = 0; i < data.length; i++) copy.data[i] = data[i];
		return copy;
	}
	
	public static void main(String[] arg) {
		TSPGenome mom = new TSPGenome();
		TSPGenome dad = new TSPGenome();
		mom.initialise(10);
		dad.initialise(10);
		
		
		log.debug("order crossover");
		log.debug("mom = " + mom);
		log.debug("dad = " + dad);
		TSPGenome jnr = mom.orderCrossover(dad);
		log.debug("jnr = " + jnr);
	}
}

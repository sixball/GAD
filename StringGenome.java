package gad;


// TODO: Only allow mutations to change to valid alphabet values (without bias)

public class StringGenome extends Genome implements Cloneable {
	
	static protected String alphabet = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	static protected int length;
	protected String data;
	
	static Logger log = new Logger();
	
	public void initialise() {
		// does nowt
	}
	
	public void initialise(int length) {
		log.debug("StringGenome.initialise()");
		StringGenome.length = length;
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<length; i++) {
			buffer.append(alphabet.charAt((int)(Math.random()*alphabet.length())));
		}
		data = buffer.toString();
	}
	
	public Object clone() {
		StringGenome copy = new StringGenome();
		copy.data = new String(data);
		//copy.length = length;
		return copy;
	}
	
	public Genome duplicate() {
		StringGenome copy = new StringGenome();
		copy.data = new String(data);
		//copy.length = length;
		return copy;
	}
	
	static public String getAlphabet() { return alphabet; } // not safe!
	static public void setAlphabet(String a) { alphabet = a; }
	
	
	public String toString() { return data; }
	
	public String getGenome() { return data; }
	
	public char get(int i) { return data.charAt(i); }
	
	//static public int length() { return length; }
	/*
	
	public void mutate() {
		uniformMutate(1);
		//stepMutate(1);
	}
	*/
	public void uniformMutate(int freq) {
		StringBuffer buffer = new StringBuffer(data);
		for(int i=0; i<freq; i++) {
			buffer.setCharAt((int)(Math.random() * buffer.length()),
					alphabet.charAt((int)(Math.random() * alphabet.length())));    
		}
		data = new String(buffer);
	}
	
	// pass number of alleles to change (may do same more than one if freq > 1)
	public void stepMutate(int freq) {
		//log.debug(data + " before mutation");
		StringBuffer buffer = new StringBuffer(data);
		for(int i=0; i<freq; i++) {
			char newChar;
			int toChange;
			do {
				toChange = (int)(Math.random() * data.length());
				int move = (int)(Math.random() * 2) * 2 - 1; 
				//log.debug("move = " + move,0);
				newChar = (char)((int)(data.charAt(i) + move));
				if(alphabet.indexOf(newChar) == -1)
					log.error("Trying to mutate " + data.charAt(toChange) + " to '" + newChar + "'");
			} while(alphabet.indexOf(newChar) == -1);  // if newChar not in alphabet then try again
			buffer.setCharAt(toChange, newChar);
		}
		data = buffer.toString();
	}
	
	// pass probability of an allele changing
	public void stepMutate(double freq) {
		//log.debug(data + " before mutation");
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<data.length(); i++) {
			if(Math.random() < freq) buffer.append(data.charAt(i));
			else {
				int move = (int)(Math.random() * 2) * 2 - 1;
				buffer.append((char)((int)(data.charAt(i) + move)));
			}
			// replace gene with a random value
		}
		data = new String(buffer);
		//log.debug(data + " after mutation");
	}
	
}




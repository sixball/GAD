package gad;


// reminds me of the MnS operators...

public class Linkage extends LinkageDistribution implements Comparable {
                           
    double dominance;

    public Linkage(int length, double dominance) {
	super(length);
	this. dominance = dominance;
    }

    public double getDominance() { return dominance; }

    // most dominant first
    public int compareTo(Object o) {
	if(((Linkage)o).dominance < dominance) return 1;
	if(((Linkage)o).dominance > dominance) return -1;
	return 0;
    }

    public String toString() {
	return super.toString() + " @ " + String.valueOf(dominance);
    }
}

/*
  The dominance value will determine the relative probability of this linkage taking precedence over another at generation time.
*/

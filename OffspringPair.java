package gad;


/**
   This class is defined to bundle up the product of recombination. Needs to be generalised -- but then Allele needs to be sorted out...
*/
public class OffspringPair {
    
    public IntGenome offspring1;
    public IntGenome offspring2;

    public OffspringPair() {
	//offspring1 = new IntGenome();
	//offspring2 = new IntGenome();
    }	

    public OffspringPair(IntGenome g1, IntGenome g2) {
	offspring1 = g1;
	offspring2 = g2;
    }
}

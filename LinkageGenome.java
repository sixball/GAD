package gad;


public class LinkageGenome extends IntGenome {

    ProbArray ld;

    public LinkageGenome() {
	super();
	IntGenome.MIN = 0;
	IntGenome.MAX = 1;
	ld = new ProbArray(data.length);
    }

    // It seems this must be done in each subclass.  Is there a better way..?
    public Object clone() {
	LinkageGenome newbie = new LinkageGenome();
	newbie.data = new int[data.length];
	for(int i = 0; i < data.length; i++) {
	    newbie.data[i] = data[i];
	}
	newbie.ld = (ProbArray)ld.clone();
	return newbie;
    }

    public void initialise(int length) {
	super.initialise(length);
	ld = new ProbArray(length);
    }

    public void mutate(double freq) {
	//log.debug("linkage mutate");
	//super.mutate();
	for(int i = 0; i < length(); i++) {
	    //	    if(Math.random() < ld.get(i)) data[i] = 1 - data[i];
	    // REMEMBER: ProbArray is for CO NOT mutation
	    if(Math.random() < freq) data[i] = 1 - data[i];
	}
	ld.perturb(freq);
    }

    public Object getLinkage() {
	return ld.clone();
    }

    public double getLinkage(int i) {
	return ld.get(i);
    }

    public OffspringPair uniformCrossing(LinkageGenome mate) {
	OffspringPair offspring = new OffspringPair();
	offspring.offspring1 = (LinkageGenome)this.clone();//new LinkageGenome(length());
	offspring.offspring2 = (LinkageGenome)mate.clone();//new LinkageGenome(length());
	for(int i = 0; i < length(); i++) {
	    if(Math.random() < 0.5) {
		offspring.offspring1.data[i] = mate.data[i];
		offspring.offspring2.data[i] = data[i];
	    }
	}
	return offspring;
    }

    public OffspringPair onePointCrossing(LinkageGenome mate) {
	OffspringPair offspring = new OffspringPair();
	offspring.offspring1 = (LinkageGenome)this.clone();
	offspring.offspring2 = (LinkageGenome)mate.clone();
	int p1 = (int)(Math.random() * (data.length - 1)); // need SOME crossing
       
	for(int i = 0; i < length(); i++) {
	    if(i > p1) {
		offspring.offspring1.data[i] = mate.data[i];
		offspring.offspring2.data[i] = data[i];
	    }
	}
	return offspring;
    }

    // REQUIRES MUCH THOUGHT!
    // currently directly inherits linkage
    public OffspringPair linkageCrossing(LinkageGenome mate) {
	OffspringPair offspring = new OffspringPair();
	offspring.offspring1 = (LinkageGenome)this.clone();
	offspring.offspring2 = (LinkageGenome)mate.clone();
	// various ways to do this:
	// simplest case: take this ld as dominant
	// place most dominant linkage in one genome, lesser in other (ala vekaria)
	// sample proportional to dominance
	for(int i = 0; i < length(); i++) { // offspring 1 is dominant
	    //if(ld.get(i) < mate.ld.get(i)) {
	    if(Math.random() * (ld.get(i) + mate.ld.get(i)) < ld.get(i)) {
		offspring.offspring1.data[i] = mate.data[i];
		offspring.offspring2.data[i] = data[i];
	    }
	}	

	return offspring;
    }

    public static void main(String[] args) {
    }
}

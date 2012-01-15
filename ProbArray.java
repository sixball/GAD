package gad;


public class ProbArray {

    double[] p;

    Logger log;

    public ProbArray(int length) {
	log = new Logger();
	
	p = new double[length];

	for(int i = 0; i < length; i++) {
	    p[i] = 0.5; // magic number
	}
    }

    public double get(int i) {
	return p[i];
    }

    public void set(int l, double v) {
	p[l] = v;
    }

    public void perturb(double rate) {
	for(int i = 0; i < p.length; i++) {
	    if(Math.random() < 0.05) {
	    //if(true) { // ALWAYS drift
		
		//p[i] += 2.0 * (Math.random() - 0.5); // more magic numbers
		p[i] = (int)(Math.random() * 2.0);

		p[i] = Math.max(0, p[i]);
		p[i] = Math.min(1, p[i]);
	    }
	}
    }

    public void mutate(Genome g) {
	for(int i = 0; i < g.length(); i++) {
	    if(Math.random() < p[i]) g.mutateAtLocus(i);
	}
    } 

    public OffspringPair cross(LinkageGenome p1, LinkageGenome p2) {
	OffspringPair osp = new OffspringPair();
	osp.offspring1 = (LinkageGenome)p1.clone();
	osp.offspring2 = (LinkageGenome)p2.clone();
	
	for(int i = 0; i < p1.length(); i++) {
	    if(Math.random() < p[i]) { 
		osp.offspring1.data[i] = p2.data[i];
		osp.offspring2.data[i] = p1.data[i];
	    }
	}
	return osp;
    }

    public Object clone() {
	ProbArray o = new ProbArray(p.length);
	for(int i = 0; i < p.length; i++) {
	    o.p[i] = p[i];
	}
	return o;
    }

    public int length() {
	return p.length;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	for(int i = 0; i < p.length; i++) {
	    sb.append(p[i] + " ");
	}
	return sb.toString();
    }
}

package gad;

public class LinkageDistribution {

    double[] p;

    Logger log;

    public LinkageDistribution(int length) {
	log = new Logger();
	
	p = new double[length];

	for(int i = 0; i < length; i++) {
	    p[i] = 0.0;
	}
    }

    public double get(int i) {
	return p[i];
    }

    public void set(int l, double a) {
	p[l] = a;
    }

    // follows a delta rule type action
    public void perturb(double d) {
	for(int i = 0; i < p.length; i++) {
	    double r = Math.random() * 2.0 - 1.0;
	    if(r > 0) { // gonna increase
		p[i] += d * r * (1.0 - p[i]);
	    }
	    else { // gonna decrease
		p[i] += d * r * p[i];
	    }
	}
    }

    public Object clone() {
	LinkageDistribution o = new LinkageDistribution(p.length);
	for(int i = 0; i < p.length; i++) {
	    o.p[i] = p[i];
	}
	return o;
    }

    public String toString() {
	StringBuffer sb = new StringBuffer();
	for(int i = 0; i < p.length; i++) {
	    sb.append(p[i] + " ");
	}
	return sb.toString();
    }

    public static void main(String[] arg) {
	LinkageDistribution o = new LinkageDistribution(8);
	o.log.debug(o.toString());
	LinkageDistribution o2 = (LinkageDistribution)o.clone();
	o.perturb(0.1);
	o.log.debug(o.toString());
	o2.log.debug(o2.toString());
    }
}

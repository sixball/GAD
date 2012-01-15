package gad;


import java.io.*;


public class TSProblem extends Problem 
{
  private double x[];
  private double y[];

  private String source;

  private int searchOp;

    public TSProblem() {
	super();
	searchOp = TSPGenome.TWO_OPT; // default
	String so = Config.getString("SearchOperation");
	if(so == "twoOpt") searchOp = TSPGenome.TWO_OPT; // default
	if(so == "threeOpt") searchOp = TSPGenome.THREE_OPT;
	// order crossover?
	

	initialise();
    }

    public void initialise() {
	//super.initialise();
	//setSelectionScheme(TOURNAMENT_SELECTION);

	if(source == null) {
	    placeCities();
	}
    }
    /*
    public void initialise(in) {
	// ignore param
	initialise(config);
    }
    */
    public void placeCities() {
	source = "<uniform random distribution>"; 
	int length = Config.getInt("GenomeLength");
	x = new double[length];
	y = new double[length];
	for(int i = 0; i < length; i++) {
	    x[i] = Math.random();
	    y[i] = Math.random();
	}
    }

    /** 
	Loads 'cities' from the specified file, taking the first n.  Returns true if successful and false otherwise.
    */
    public boolean loadCities(String fileName, int n) {

	source = fileName;

	//TSPGenome.setLength(n);
	
	x = new double[n];
	y = new double[n];

	try {
	    File f = new File(fileName);
	    StreamTokenizer st = new StreamTokenizer(new FileReader(f));
	    st.parseNumbers();
	    for(int i = 0; i < n; i++) {
		try {
		    st.nextToken();
		    x[i] = st.nval;
		} catch(IOException e) {}
		try {
		    st.nextToken();
		    y[i] = st.nval;
		} catch(IOException e) {}
	    }
	} catch(FileNotFoundException e) {
	    log.error("Failed to load cities from " + fileName);
	    source = "File not found!";
	    //TSPGenome.setLength(0);
	    x = null;
	    y = null;
	    return false;
	}
	return true;
    }
    
    public double[] getX() { 
	if(x != null) return (double[])x.clone();
	else {
	    log.error("No x co-ords to return");
	    return null;
	}
    }

    public double[] getY() { 
	if(y != null) return (double[])y.clone();
	else {
	    log.error("No y co-ords to return");
	    return null;
	}
    }

    public String getSource() { return new String(source); } // hopefully clones

    public double evaluate(Genome g) {
	return 1.0 / cost((TSPGenome)g);
    }

    public double cost(TSPGenome g) {
	double sum = 0;
	if(g.length() == 0) log.debug("zero length TSPGenome in TSProblem.cost()");
	for(int c = 1; c < g.length(); c++) { // for each city
	    sum += dist(g.get(c-1), g.get(c));
	}
	// and back to start
	sum += dist(g.get(g.length() - 1), g.get(0));
	return sum;
    }
    
    private double dist(int i, int j) {
	// pythagorus
	return Math.sqrt(Math.pow(x[i] - x[j], 2) + Math.pow(y[i] - y[j], 2));
    }
}

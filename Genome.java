package gad;

/**
This defines an abstract Genome class for a linear type genome.
*/


import java.util.*;

public abstract class Genome implements Cloneable {
	
	static Logger log;
	static Random r;
	
	Allele[] data; // TODO: not used in GeneSet - ought to be moved downwards to LinearGenome or Something
	
	// TODO: work out how this config is assigned
	//static Config config;
	int length;
	
	//protected Genome() {
	//}
	/*
	public Genome(Config config) {
		Genome.config = config;
		length = config.getInt("GenomeLength");
		if(r == null) r = new Random();
		log = new Logger();
		//data = new Allele[config.getInt("GenomeLength")];	
	}
	*/
	
	public Genome() {
		//Genome.config = config;
		//length = config.getInt("GenomeLength");
		if(r == null) r = new Random();
		log = new Logger();
		//data = new Allele[config.getInt("GenomeLength")];	
	}
	
	public void initialise() {
		//	initialise(LENGTH);
		log.debug("No initialistion defined in Genome");
	}
	
	public abstract void initialise(int l);
	
	// careful with this one.  use only before initialising ALL Genomes
	//public static void setLength(int l) { LENGTH = l; }
	
	public int length() { return length; }
	
	// may use growLength later for increasing complexity genomes	
	
	public abstract Object clone();
	
	public abstract String toString();
	/*
	public Allele get(int i) {
		if(i >= length() || i < 0) log.error("Allele index out of range");
		
		return data[i];
	}
	
	public void set(Allele a, int i)
	{
		if(i >= length() || i < 0) log.error("Allele index out of range");
		
		data[i] = a;//(Allele)a.clone();
	}
	*/
	public void mutate() {
		log.debug("No mutation defined");
	}
	
	public void mutate(double freq) {
		log.debug("No mutation defined");
	}
	
	public void mutateAtLocus(int i) {
		log.debug("MutateAtLocus() not defined.");
	}
	
	public Genome crossedWith(Genome g) {
		log.debug("No crossover defined");
		return null;
    }
}

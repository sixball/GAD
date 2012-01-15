package gad;

import java.util.*;
import java.io.*;

class Cell implements Serializable, Cloneable {
	
	private double Proteins[];
	private double ProteinRelease[];
	private double DiffusionRate[];
	private boolean	Global[];
	private boolean CellExists;
	
	public Object clone() throws CloneNotSupportedException {
    		return super.clone();
  	}

	public Cell(int i, int j, int numProteins) {
		Proteins = new double [numProteins];
		ProteinRelease = new double [numProteins];
		DiffusionRate = new double [numProteins];
		Global = new boolean [numProteins];
		Arrays.fill(ProteinRelease, -1);
		Arrays.fill(Global, false);
		CellExists=false;
	}

	public boolean isGlobal(int x) {
		return Global[x];
	}

	public double getDiffusionRate(int x) {
		return DiffusionRate[x];
	}
	
	public double getProteinRelease(int x) {
		return ProteinRelease[x];
	}
		
	public double getProtein(int x) {
		return Proteins[x];
	}

	public void setGlobal(int x) {
		Global[x]= true;
	}

	public void setDiffusionRate(double p, int x) {
		DiffusionRate[x]=p;
	}

	public void setProteinRelease(double p, int x) {
		ProteinRelease[x] = p;
	}
		
	public void setProtein(double p, int x) {
		Proteins[x] = p;
	}	




	public boolean CellAlive() {
		return CellExists;
	}




	public void killCell(int i, int j) {
		CellExists=false;
		for(int x=0; x<Proteins.length; x++) {
			if(!Global[x]) {
				Proteins[x] = 0;
				ProteinRelease[x] = -1;
			}
		}
	}



		
	public void createCell() {
		CellExists=true;
	}
	
		
}

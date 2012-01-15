package gad;


class Organism implements Cloneable {
	
  int gridSize;
  int numProteins;
    int numGenes;

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public Cell o[][];
  /*
  public Organism(int gridSize, int numProteins) {
    //this.gridSize = gridSize;
    this.numProteins = numProteins;
    o = new Cell [gridSize][gridSize]; 
    for(int i=0; i<gridSize; i++) {
      for(int j=0; j<gridSize; j++) {
	o[i][j] = new Cell(i, j, numProteins);
      }
    }
  }
  */ 	
  public Cell getCell(int i, int j) {
    return o[i][j];
  }
		
  public void setCell(Cell c, int i, int j) {
    o[i][j] = c;
  }
  /*
  public void clear(int numProteins) {
    for(int i=0; i<gridSize; i++) {
      for(int j=0; j<gridSize; j++) {
	o[i][j] = new Cell(i, j, numProteins);
      }
    }
  }
  */	
  public int calcGradient(int k, int i, int j) {
    double Cik =0;
    int grad = 4;
    int p, q;
    double arr[] = new double[9];
    for(int x=-1; x<=1; x++) {
      for(int y=-1; y<=1; y++) {
	if(i+x<0) p=gridSize-1;
	else if(i+x==gridSize) p=0;
	else p= i+x;

	if(j+y<0) q=gridSize-1;
	else if(j+y==gridSize) q=0;
	else q= j+y;				

	arr[(3*(x+1))+(y+1)] = o[p][q].getProtein(k);
      }
    }

    double gradients[] = new double[9];
		
    gradients[0] = arr[0]+arr[3]+arr[1]-arr[8]-arr[5]-arr[7];
    gradients[1] = arr[0]+arr[1]+arr[2]-arr[6]-arr[7]-arr[8];
    gradients[2] = arr[1]+arr[2]+arr[5]-arr[3]-arr[6]-arr[7];
    gradients[3] = arr[0]+arr[3]+arr[6]-arr[2]-arr[5]-arr[8];
    gradients[4] = 0;
    gradients[5] = -gradients[3];
    gradients[6] = -gradients[2];
    gradients[7] = -gradients[1];
    gradients[8] = -gradients[0];

	
		
    for(int x=0; x<9; x++) {
      for(int y=0; y<9; y++) {
	if(y%2==0 && gradients[y]>Cik) {
	  Cik = gradients[y];
	  grad = y;
	}
	else if(y%2==1 && gradients[y]>=Cik) {
	  Cik = gradients[y];
	  grad = y;
	}
				
	else if(gradients[4]>=Cik) {
	  Cik = gradients[4];
	  grad = 4;
	}

      }		
    }
    return grad;
  }

  public void copyCellTo(Organism b, int i, int j){
    Cell cell = new Cell(i, j, numProteins);		
    if(getCell(i, j).CellAlive()) cell.createCell();
    for(int k = 0; k < numProteins; k++) { 
      cell.setProtein(getCell(i, j).getProtein(k), k);
      cell.setProteinRelease(getCell(i, j).getProteinRelease(k), k);
      cell.setDiffusionRate(getCell(i, j).getDiffusionRate(k), k);
      if(getCell(i, j).isGlobal(k)) cell.setGlobal(k);
    }		
    b.setCell(cell, i, j);
  }

  public void copyFrom(Organism o) 
  {
    for(int i = 0; i < gridSize; i++) 
    {
      for(int j = 0; j < gridSize; j++) 
      {
	o.copyCellTo(this, i, j);
      }
    }
  }
  


  public void moveWithGrad(Organism neworg, int k, int i, int j, int numProteins) {
    //this method currenty just writes over any cells in its way, 
    // need to impliment code which shuffles cell around	
    if(getCell(i, j).CellAlive()) {
      int ti=0, tj=0;	
      switch (calcGradient(k, i, j)) {
      case 0: ti = i-1; tj = j-1; break;
      case 1: ti = i-1; tj = j; break;
      case 2: ti = i-1; tj = j+1; break;
      case 3: ti = i; tj = j-1; break;
      case 4: ti = i; tj = j; break; //cannot move to existing place 
      case 5: ti = i; tj = j+1; break;
      case 6: ti = i+1; tj = j-1; break;
      case 7: ti = i+1; tj = j; break;
      case 8: ti = i+1; tj = j+1; break;
      }
			
			
      if(ti<0) ti = gridSize-1;
      if(ti==gridSize) ti=0;
      if(tj<0) tj = gridSize-1;
      if(tj==gridSize) tj=0;

			
      double st, sr, dr;		
      if(getCell(i, j).CellAlive()) neworg.getCell(ti, tj).createCell();
      for(int ch=0; ch<numProteins; ch++) {
	if(!getCell(i, j).isGlobal(ch)) {	
	  st = getCell(i, j).getProtein(ch);
	  sr = getCell(i, j).getProteinRelease(ch);
	  dr = getCell(i, j).getDiffusionRate(ch);
	
	  neworg.getCell(ti, tj).setDiffusionRate(dr, ch);
	  neworg.getCell(ti, tj).setProtein(st, ch);
	  neworg.getCell(ti, tj).setProteinRelease(sr, ch);
	}
	//if(organism.getCell(i, j).isGlobal(k)) neworg.getCell(i, j).setGlobal(k);	
					
      }
      neworg.getCell(i, j).killCell(i,j);		
    }		
		
  }



  public void moveAgainstGrad(Organism neworg, int k, int i, int j, int numProteins) {
    //this method currenty just writes over any cells in its way, need to implement code which shuffles cell around	
    if(getCell(i, j).CellAlive()) {
      int ti=0, tj=0;	
      switch (calcGradient(k, i, j)) {
      case 0: ti = i+1; tj = j+1; break;
      case 1: ti = i+1; tj = j; break;
      case 2: ti = i+1; tj = j-1; break;
      case 3: ti = i; tj = j+1; break;
      case 4: ti = i; tj = j; break; //cannot move to existing place 
      case 5: ti = i; tj = j-1; break;
      case 6: ti = i-1; tj = j+1; break;
      case 7: ti = i-1; tj = j; break;
      case 8: ti = i-1; tj = j-1; break;
      }
			
			
      if(ti<0) ti = gridSize-1;
      if(ti==gridSize) ti=0;
      if(tj<0) tj = gridSize-1;
      if(tj==gridSize) tj=0;

      double st, sr, dr;		
      if(getCell(i, j).CellAlive()) neworg.getCell(ti, tj).createCell();
      for(int ch=0; ch<numProteins; ch++) {
	if(!getCell(i, j).isGlobal(ch)) {	
	  st = getCell(i, j).getProtein(ch);
	  sr = getCell(i, j).getProteinRelease(ch);
	  dr = getCell(i, j).getDiffusionRate(ch);
	
	  neworg.getCell(ti, tj).setDiffusionRate(dr, ch);
	  neworg.getCell(ti, tj).setProtein(st, ch);
	  neworg.getCell(ti, tj).setProteinRelease(sr, ch);
	}
	//if(organism.getCell(ti, tj).isGlobal(ch)) neworg.getCell(i, j).setGlobal(ch);	
				
      }
      neworg.getCell(i, j).killCell(i,j);		
    }		
		
  }




  public void divideWithGrad(Organism neworg, int k, int i, int j, int numProteins) {
    //this method currenty just writes over any cells in its way, need to impliment code which shuffles cell around	
    if(getCell(i, j).CellAlive()) {
      int ti=0, tj=0;	
      switch (calcGradient(k, i, j)) {
      case 0: ti = i-1; tj = j-1; break;
      case 1: ti = i-1; tj = j; break;
      case 2: ti = i-1; tj = j+1; break;
      case 3: ti = i; tj = j-1; break;
      case 4: ti = i; tj = j; break; //cannot move to existing place 
      case 5: ti = i; tj = j+1; break;
      case 6: ti = i+1; tj = j-1; break;
      case 7: ti = i+1; tj = j; break;
      case 8: ti = i+1; tj = j+1; break;
      }

      if(ti<0) ti = gridSize-1;
      if(ti==gridSize) ti=0;
      if(tj<0) tj = gridSize-1;
      if(tj==gridSize) tj=0;
			
      double st, sr, dr;
					
      if(getCell(i, j).CellAlive()) neworg.getCell(ti, tj).createCell();
      for(int ch=0; ch<numProteins; ch++) {
	if(!getCell(i, j).isGlobal(ch)) {	
	  st = getCell(i, j).getProtein(ch);
	  sr = getCell(i, j).getProteinRelease(ch);
	  dr = getCell(i, j).getDiffusionRate(ch);
	
	  neworg.getCell(ti, tj).setDiffusionRate(dr, ch);
	  neworg.getCell(ti, tj).setProtein(st, ch);
	  neworg.getCell(ti, tj).setProteinRelease(sr, ch);
	}
				
	//if(organism.getCell(i, j).isGlobal(ch)) neworg.getCell(ti, tj).setGlobal(ch);	
				
      }		
			
    }				
  }

  public void divideAgainstGrad(Organism neworg, int k, int i, int j, int numProteins) {
    //this method currenty just writes over any cells in its way, need to impliment code which shuffles cell around	
    if(getCell(i, j).CellAlive()) {
      int ti=0, tj=0;	
      switch (calcGradient(k, i, j)) {
      case 0: ti = i+1; tj = j+1; break;
      case 1: ti = i+1; tj = j; break;
      case 2: ti = i+1; tj = j-1; break;
      case 3: ti = i; tj = j+1; break;
      case 4: ti = i; tj = j; break; //cannot move to existing place 
      case 5: ti = i; tj = j-1; break;
      case 6: ti = i-1; tj = j+1; break;
      case 7: ti = i-1; tj = j; break;
      case 8: ti = i-1; tj = j-1; break;
      }

			
      if(ti<0) ti = gridSize-1;
      if(ti==gridSize) ti=0;
      if(tj<0) tj = gridSize-1;
      if(tj==gridSize) tj=0;			
      double st, sr, dr;
					
      if(getCell(i, j).CellAlive()) neworg.getCell(ti, tj).createCell();
		
      for(int ch=0; ch<numProteins; ch++) {
	if(!getCell(i, j).isGlobal(ch)) {	
	  st = getCell(i, j).getProtein(ch);
	  sr = getCell(i, j).getProteinRelease(ch);
	  dr = getCell(i, j).getDiffusionRate(ch);
	
	  neworg.getCell(ti, tj).setDiffusionRate(dr, ch);
	  neworg.getCell(ti, tj).setProtein(st, ch);
	  neworg.getCell(ti, tj).setProteinRelease(sr, ch);
	}
	//if(organism.getCell(ti, tj).isGlobal(ch)) neworg.getCell(i, j).setGlobal(ch);	
					
      }	
			
    }
  }

  public Organism(int gridSize, int numProteins)
  {
    this.gridSize = gridSize;
    this.numProteins = numProteins;
    o = new Cell [gridSize][gridSize];
    
    for(int i=0; i<gridSize; i++) {
      for(int j=0; j<gridSize; j++) {
	o[i][j] = new Cell(i, j, numProteins);
      }
    }
  }
  
  public Organism(NeuroGenome g)
  {
    this(Config.getInt("GridSize"), Config.getInt("NumberOfProteins"));
    numGenes = Config.getInt("NumberOfGenes");
    // have to do it in this order since constructors must come first
    /*
    o[][] = new Cell [gridSize][gridSize];
    
    for(int i=0; i<gridSize; i++) {
      for(int j=0; j<gridSize; j++) {
	o[i][j] = new Cell(i, j, numProteins);
      }
    }
    */

    // now grow
    Cell stem1;
		
    for(int i=0; i<gridSize; i++) {
      for(int j=0; j<gridSize; j++) {
	stem1 = new Cell(i, j, numProteins);
	stem1.setProteinRelease(0.0,5);		
	stem1.setProteinRelease(0.1,6);		
	stem1.setProteinRelease(0.2,7);
							
	stem1.setProteinRelease(0.3,8);					
	stem1.setProteinRelease(0.4,9);					
	stem1.setProteinRelease(0.5,10);					
	stem1.setProteinRelease(0.6,11);		
	stem1.setProteinRelease(0.7,12);				
			
	stem1.setProteinRelease(0.8,13);					
	stem1.setProteinRelease(0.9,14);					
	stem1.setProteinRelease(1.0,15);

	double x = (double)(gridSize-1-i)/(double)(gridSize-1);
	double y = (double)(gridSize-1-j)/(double)(gridSize-1);
	stem1.setProteinRelease(x,3);		
	stem1.setProtein(x,3);
	stem1.setGlobal(3);
	stem1.setProteinRelease(y,4);		
	stem1.setProtein(y,4);
	stem1.setGlobal(4);	
	setCell(stem1, i, j);
      }
    }


    stem1 = getCell(gridSize/2, gridSize/2);	
    stem1.setProteinRelease(0,0);					
    stem1.setProteinRelease(1,1);					
    stem1.setProteinRelease(0,2);					
    stem1.createCell();
    setCell(stem1, gridSize/2, gridSize/2);
	    
    for(int growth=0; growth<gridSize; growth++) {
		
      //System.out.println("growth step " + growth);
      
      double proteinLevelA;
      double proteinLevelB;
      int geneType;
	
      Organism neworg = new Organism(gridSize, numProteins);
      
	
      //System.out.println("before diffusion \n" + this);
      
      //init new grid and chemical concentrations
      for(int i=0; i<gridSize; i++) {
	for(int j=0; j<gridSize; j++) {
	  copyCellTo(neworg, i, j);
	  for(int k=0; k<numProteins; k++) {
	    if(getCell(i, j).getProteinRelease(k) >= 0.0) {
	      getCell(i, j).setProtein(getCell(i,j).getProteinRelease(k), k);
	    }
	  }
	}
      }
	
      //diffuse
      for(int i=0; i<gridSize; i++) {
	for(int j=0; j<gridSize; j++) {
	  for(int k=0; k<numProteins; k++) {
	    int p, q;
	    double da0 = 0;

	    //calculating the laplacian
	    for(int x=-1; x<=1; x++) { 
	      for(int y=-1; y<=1; y++) {
		if(x==0 && y==0) da0 -= 8 * getCell(i, j).getProtein(k);
		else {
		  if(i+x<0) p =gridSize-1;
		  else if(i+x==gridSize) p =0;
		  else p = i+x;			
							
		  if(j+y<0) q =gridSize-1;
		  else if(j+y==gridSize) q =0;
		  else q = j+y;
		  da0 += getCell(p, q).getProtein(k);
		}
	      }
	    }
	    da0 = (da0/16) * getCell(i, j).getDiffusionRate(k);
	    da0 += getCell(i, j).getProtein(k);
	    neworg.getCell(i, j).setProtein(da0, k);
	  }
	}
      }
    
      //System.out.println("after diffusion \n" + this);

      for(int i=0; i<gridSize; i++) {
	for(int j=0; j<gridSize; j++) {
	  //System.out.println("finding cell " + i + ", " + j + " is " 
	  // + getCell(i, j).CellAlive());
	  if(getCell(i, j).CellAlive()) {	

	    for(int k=0; k < numGenes*3; k+=3) {
	
	      //System.out.println("one cell is alive!");
	      

	      //get repressive protein
	      proteinLevelA = getCell(i, j).getProtein(g.get(k));
						
	      //get genome type
	      geneType = g.get(k+1);
					
	      //get organismucer protein
	      proteinLevelB = getCell(i, j).getProtein(g.get(k+2));
									
			
	      //perform gene function
	      switch (geneType) {
										
		//if gene type is structural		
										
		//move 	
	      case 0: 
		if(proteinLevelA==0) moveWithGrad(neworg, g.get(k+2), i, j, numProteins); break;  	//move in x dirn//     
	      case 1: 
		if(proteinLevelA==0) moveAgainstGrad(neworg, g.get(k+2), i, j, numProteins); break;  	//move in opp of x dirn// 
		
											
		//Divide				 	
	      case 2: 
		if(proteinLevelA==0) divideWithGrad(neworg, g.get(k+2), i, j, numProteins); break;	//divide in x dirn of grad//     
	      case 3: 
		if(proteinLevelA==0) divideAgainstGrad(neworg, g.get(k+2), i, j, numProteins); break;	//divide in opp dirn of x grad//			 

		
		//Die
	      case 4: 
		if(proteinLevelA==0) neworg.getCell(i,j).killCell(i,j); break;		
		
									
		//diffuse proteins
	      case 5: neworg.getCell(i, j).setDiffusionRate(proteinLevelA, g.get(k+2)); break;
									
				
		//if gene type is regulative
	      case 6:  if(proteinLevelA==0) proteinLevelB=0; break;
	      case 7:  if(proteinLevelA!=0) proteinLevelB=0; break;
	      case 8:  if(proteinLevelA==0) proteinLevelB=1; break;
	      case 9:  if(proteinLevelA!=0) proteinLevelB=1; break;
	      case 10:  if(proteinLevelA!=1) proteinLevelB=0; break;
	      case 11:  if(proteinLevelA==1) proteinLevelB=0; break;
	      case 12:  if(proteinLevelA!=1) proteinLevelB=1; break;
	      case 13:  if(proteinLevelA==1) proteinLevelB=1; break;

	      case 14:  proteinLevelB = proteinLevelB-proteinLevelA; break;    				 	
	      case 15: proteinLevelB = proteinLevelB+proteinLevelA; break;
	      case 16: proteinLevelB = 1-proteinLevelA; break;
									
	      case 17: if(proteinLevelA<proteinLevelB) proteinLevelB=0; break;
	      case 18: if(proteinLevelA>proteinLevelB) proteinLevelB=0; break;
	      case 19: if(proteinLevelA<proteinLevelB) proteinLevelB=1; break;
	      case 20: if(proteinLevelA>proteinLevelB) proteinLevelB=1; break;
	      case 21: if(calcGradient(g.get(k), i, j)==calcGradient(g.get(k+2), i, j)) proteinLevelB=0; break;
	      case 22: if(calcGradient(g.get(k), i, j)+calcGradient(g.get(k+2), i, j)==8) proteinLevelB=0; break;
									
	      case 23: if(calcGradient(g.get(k), i, j)==1) proteinLevelB=0; break;
	      case 24: if(calcGradient(g.get(k), i, j)==3) proteinLevelB=0; break;
	      case 25: if(calcGradient(g.get(k), i, j)==4) proteinLevelB=0; break;
	      case 26: if(calcGradient(g.get(k), i, j)==5) proteinLevelB=0; break;
	      case 27: if(calcGradient(g.get(k), i, j)==7) proteinLevelB=0; break;
	      case 28: proteinLevelB = proteinLevelA; break;
	      case 29: if(proteinLevelA==proteinLevelB) proteinLevelB = 0; break;
	      case 30: if(proteinLevelA!=proteinLevelB) proteinLevelB = 0; break;

		//differentiate cells
		//red	
	      case 31: 
		if(proteinLevelA==0) {
		  neworg.getCell(i, j).setProteinRelease(1,0);
		  neworg.getCell(i, j).setProteinRelease(0,1);
		  neworg.getCell(i, j).setProteinRelease(0,2);
		}
		break;

	      case 32: 
		if(proteinLevelA==0) {
		  neworg.getCell(i, j).setProteinRelease(0,0);
		  neworg.getCell(i, j).setProteinRelease(1,1);
		  neworg.getCell(i, j).setProteinRelease(0,2);
		}
		break;
	
	      case 33: 
		if(proteinLevelA==0) {
		  neworg.getCell(i, j).setProteinRelease(0,0);
		  neworg.getCell(i, j).setProteinRelease(0,1);
		  neworg.getCell(i, j).setProteinRelease(1,2);
		}	
		break;							
	      }
	      
	      if(proteinLevelB<0) proteinLevelB=0;
	      if(proteinLevelB>1) proteinLevelB=1;
	      getCell(i, j).setProtein(proteinLevelB, g.get(k+2));
	    }
	  }
	}
      }	
	
      //init new grid and chemical concentrations
      for(int i=0; i<gridSize; i++) {
	for(int j=0; j<gridSize; j++) {
	  for(int k=0; k<numProteins; k++) {
	    if(neworg.getCell(i,j).getProteinRelease(k) >= 0.0) {
	      neworg.getCell(i, j).setProtein(neworg.getCell(i,j).getProteinRelease(k), k);
	    }
	  }
	}
      }   
      //System.out.println("neworg = \n" + neworg);
      
      copyFrom(neworg);//this = neworg;	
    }
    //return this;
  }
   
  public String toString() 
  {
    StringBuffer sb = new StringBuffer();
    
    for(int i = 0; i < gridSize; i++) 
    {
      for(int j = 0; j < gridSize; j++) 
      {
	if(getCell(i,j).CellAlive()) sb.append("O");
	else sb.append(".");
      }
      sb.append("\n");
    }
    return sb.toString();
    
  }
}

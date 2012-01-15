package gad;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class SEAMView extends ModelView {

    //SEAMGeneticAlgorithm ga;
	PopulationModel pop;

    public SEAMView(SearchModel model, SearchAlgorithm ga) {
	super(model);
	pop = (PopulationModel) model;
	//this.ga = (SEAMGeneticAlgorithm)ga;
     
	setTitle("SEAM View");
	setSize(new Dimension(400, 600));

	getContentPane().setLayout(new BorderLayout());

	getContentPane().add(new MatrixGraph(), "Center");

	JButton initButton = new JButton("Initialise");
	initButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      initialise();
	    }
	  });

	getContentPane().add(initButton, "South");
    }

  private void initialise() 
  {
    //ga.initialisePopulation(); 
    //pop.notifyListeners();
  }
  

    public void modelChanged() {
	repaint();
    }

    public class MatrixGraph extends JPanel 
    {
	public MatrixGraph() {
	    setMinimumSize(new Dimension(100, 100));
	    setPreferredSize(new Dimension(300, 200));
	}
	    
	public void paint(Graphics g) {
	    if(pop.size() == 0) log.debug("Empty population in SEAM.MatrixGraph.paint()");
	    //if(pop.length() == 0) log.debug("zero length population in MatrixGraph.paint()");
	    double yScale = getHeight() / pop.size();
	    double xScale = getWidth() / (double)pop.getIndividual(0).length();
		
	    for(int i = 0; i < pop.size(); i++) {
		    
		for(int j = 0; j < pop.getIndividual(i).length(); j++) {
			
		    int val = ((IntGenome)pop.getIndividual(i)).get(j);
		    Color col = getColor(val);
		    //if(!pop.isSampling(i)) col = col.darker();
		    //col = col.brighter();
			
		    g.setColor(col);
		    // allele
		    if(val < 0) { // interpreted as unspecified
			g.clearRect((int)(j * xScale), 
				    (int)(i * yScale), 
				    (int)Math.ceil(xScale), 
				    (int)Math.ceil(yScale));
		    }
		    else 
			g.fillRect((int)(j * xScale), 
				   (int)(i * yScale), 
				   (int)Math.ceil(xScale), 
				   (int)Math.ceil(yScale));
		}
	    }
	    /*	
	    g.setColor(Color.black);
	    g.drawRect(0, (int)(pop.getHighlight() * yScale) - 1, 
		       getWidth() - 1, (int)(yScale));
	    */
	}
    }
     private Color getColor(int c) {
	// calculate color and return it
      // uses range of hue AND intensity for colour-blindness
      int range = IntGenome.MAX - IntGenome.MIN; // ignoring negative values
    
      if(range < 0) {
	  
	  Color[] col = new Color[]{Color.black, Color.white, Color.red, Color.blue, Color.green};
	  return col[c];
      }

      return Color.getHSBColor((float)((c / (double)range) * 0.75), 
	    (float)0.8, 
			       (float)1.0);//(float)((c / (double)range)));
    }
}

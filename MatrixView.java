package gad;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;


public class MatrixView extends PopulationView implements AlgorithmListener {

  GeneticAlgorithm ga;
  boolean sorted;
  JCheckBox sortedCB;

  public MatrixView(PopulationModel pop, GeneticAlgorithm ga) {
    super(pop);
    this.ga = ga;
    
    setTitle("Data Matrix");
    setSize(new Dimension(400, 600));//(int)(400 / pop.getSize())));
    //setResizable(false);
     
    getContentPane().setLayout(new BorderLayout());
    FitnessProfile fp = new FitnessProfile();
    MatrixGraph mg = new MatrixGraph();
    mg.addMouseMotionListener(new MatrixListener(mg));
    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mg, fp);
    splitPane.setOneTouchExpandable(true);
    splitPane.setDividerLocation(150);
    log.debug("" + splitPane.getDividerSize());
 
    JSlider sampleRangeSlider = new JSlider(JSlider.VERTICAL, 0, pop.size(), pop.size());
    sampleRangeSlider.setInverted(true);
    sampleRangeSlider.setMajorTickSpacing(20);
    //setMinorTickSpacing(5);
    //setPaintTicks(true);
    //setPaintLabels(true);
    //setBorder(BorderFactory.createEmptyBorder(0,0,2,0));
    
    JPanel controlPanel = new JPanel();
    JButton initButton = new JButton("Initialise");
    initButton.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		initialise();
	    }
	});
    
    sortedCB = new JCheckBox("sorted", sorted);
    sortedCB.addActionListener(new OptionsListener());
    
    initButton.addActionListener(new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		repaint();
    	}
    });
  
    
    controlPanel.add(initButton);
    controlPanel.add(sortedCB);

    sampleRangeSlider.addChangeListener(new FitnessListener());
    getContentPane().add(splitPane, BorderLayout.CENTER);
    getContentPane().add(sampleRangeSlider, BorderLayout.EAST);
    getContentPane().add(controlPanel, BorderLayout.SOUTH);
  }
  
  public class OptionsListener implements ActionListener {
  	public void actionPerformed(ActionEvent e) {
  		sorted = sortedCB.isSelected();
  		repaint();
  	}
  }
  
  private void initialise() {
  	pop.initialise();
  	//ga.reset();
  	ga.initialise();
  	pop.notifyListeners();
  	ga.notifyListeners();
  }
  
  public class FitnessListener implements ChangeListener  
  {
  	public void stateChanged(ChangeEvent e) {
  		JSlider source = (JSlider)e.getSource();
  		pop.sampleBest((int)source.getValue());
  		//log.debug("fitness listener returns " + (int)source.getValue());
  		pop.notifyListeners();
  	}
  }
  
  public class MatrixListener extends MouseMotionAdapter {
	
	private JPanel matrix;

	private MatrixListener() {}

	public MatrixListener(JPanel matrix) {
	    this.matrix = matrix;
	}
	
	public void mouseDragged(MouseEvent e) {
	    double yScale = matrix.getHeight() / pop.size();
	    pop.setHighlight((int)(e.getY() / yScale));
	}
    }

    public class MatrixGraph extends JPanel 
    {
	public MatrixGraph() {
	    setMinimumSize(new Dimension(100, 100));
	    setPreferredSize(new Dimension(300, 200));
	}
      
      public void paint(Graphics g) {
	if(pop.size() == 0) log.debug("Empty population in MatrixGraph.paint()");
	//if(pop.length() == 0) log.debug("zero length population in MatrixGraph.paint()");
	double yScale = getHeight() / pop.size();
	double xScale = getWidth() / (double)pop.getIndividual(0).length();
	
	for(int i = 0; i < pop.size(); i++) {
	  	  
	  for(int j = 0; j < pop.getIndividual(i).length(); j++) {
	    
	    int val = ((IntGenome)pop.getIndividual(i)).get(j);
	      Color col = getColor(val);
	      if(!pop.isSampling(i)) col = col.darker();
	      col = col.brighter();
	      
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
	
	g.setColor(Color.black);
	g.drawRect(0, (int)(pop.getHighlight() * yScale) - 1, 
		   getWidth() - 1, (int)(yScale));
      }
    }
	
    protected class FitnessProfile extends JPanel {

	public boolean isScaled;
	
	public FitnessProfile() {
	    isScaled = true;
	    //setMinimumSize(new Dimension(50, pop.getSize()));
		int pixelsPerRow = Math.round(500 / pop.size());
	    setSize(new Dimension(300, pixelsPerRow * pop.size()));
	}
	
	public void setScaled(boolean v) { isScaled = v; }
	    
	    public void paint(Graphics g) {
	    // store min/max fitnesses, since they require computation to find.
		double min, max;
		
		ValueMap fm = ga.getFitnessMap();

		// should be sorted or no
		pop.sort(fm);

		if(isScaled) {
		    min = ga.minFitness();
		    max = ga.maxFitness();
		}
		else {
		    min = 0;
		    max = 1;
		}
		
		double xScale = getWidth() / (max - min);
		double yScale = getHeight() / pop.size();

		
		// clear display
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		//ga.checkFitnessMap("pre plot fitnesses in matrixview paint");
		// plot fitnesses of current population -- careful!
		for(int i = 0; i < pop.size(); i++) {
			double f = fm.get(pop.getIndividual(i)); // fitness of individual i
		  if(pop.isSampling(i)) g.setColor(Color.gray);
		  else g.setColor(Color.lightGray);
		  if(yScale >= 2) g.fillRect((int)(0),//(fp[i] - min) * xScale),
					     (int)(i * yScale), 
					     (int)((f - min) * xScale),
					     (int)(yScale) - 1);
		  else g.drawLine((int)(0),//(fp[i] - min) * xScale), 
				  (int)(i * yScale),
				  (int)((f - min) * xScale),
				  (int)(i * yScale));
		}

		//ga.checkFitnessMap("pre get selectionprob in matrixview paint");
		
		// plot selection probabilities
		ValueMap sp = ga.getSelectionProbabilities();
		if(sp != null) {

			log.debug("selection prob size in matrix view is "+ sp.size());
		    // get max value for selection probability
		    double spMax = sp.maxValue(); 
		 
		    double pScale = getWidth() / spMax; // selection probability scaling
		    //log.debug("pScale = " + pScale + " spMax = " +spMax);
		    // plot it
		    g.setColor(new Color(255, 0, 0, 100));
		    for(int i = 0; i < pop.size() - 1; i++) {
		    	//log.debug("painting individual " + i + " in fitness profile");
		    	log.ifNull(pop.getIndividual(i), "individual i in pop in matrix profile paint");
			log.ifNull(pop, "pop in matrix profile paint");
		    	g.fillRect((int)(0),
				   (int)(i * yScale),
				   (int)(sp.get(pop.getIndividual(i)) * pScale),
				   (int)(yScale));

		    }  
		}
	    }
    }

    public void modelChanged() {
	//synchronized(pop) {
	    repaint();
	    //}
    }

    public void algorithmChanged() {
	repaint();
    }

  private Color getColor(int c) {
	// calculate color and return it
      // uses range of hue AND intensity for colour-blindness
      int range = IntGenome.MAX - IntGenome.MIN; // ignoring negative values
    
      if(range < 6) {
	  
	  Color[] col = new Color[]{Color.black, Color.white, Color.red, Color.blue, Color.green};
	  return col[c];
      }

      return Color.getHSBColor((float)((c / (double)range) * 0.75), 
	    (float)0.8, 
			       (float)1.0);//(float)((c / (double)range)));
    }
}

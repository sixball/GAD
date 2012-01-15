package gad;


import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.*;

public class AlleleDiversityView extends PopulationView implements ModelListener {
    
    HashMap[] alleles; // becoming a pain in the bum

     boolean scalePalette;
    //boolean showConfigs;
    int configSpacing;
    int shownConfigs;

    HashSet startLex;
    HashSet currentLex;

    JComboBox selectCombo;
    JCheckBox showConfigsCB;

    PopulationModel subpop;
    GeneticAlgorithm ga;
    // using GA to indicate fitness info
    public AlleleDiversityView(PopulationModel pop, GeneticAlgorithm ga) {
	super(pop);

	this.ga = ga;

	setTitle("Allele Diversity View");
	//  default option setResizable(true);
	//	setClosable(true);

	getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
	JPanel optionsPanel = new JPanel(); // create options panel
	optionsPanel.setMaximumSize(new Dimension(1000, 30));
	optionsPanel.setMinimumSize(new Dimension(100, 20));
	selectCombo = new JComboBox(new String[]{"Keep allele", "Remove allele"});
	optionsPanel.add(selectCombo);
	JButton revertB = new JButton("Revert");
	revertB.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    revert();
		}
	    });
	optionsPanel.add(revertB);
	/*
	showConfigsCB = new JCheckBox("Configurations", false);
	showConfigsCB.addActionListener(new ConfigsOptionListener());
	optionsPanel.add(showConfigsCB);
	
	final JComboBox configSpacingCombo = new JComboBox(new String[] {"1", "2", "4", "8"});
	configSpacingCombo.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    configSpacing = Integer.parseInt((String)configSpacingCombo.getSelectedItem());
		    repaint();
		}
	    });
	optionsPanel.add(configSpacingCombo);
	optionsPanel.add(new JLabel("spacing"));
	*/

	JSlider sampleRangeSlider = new JSlider(JSlider.HORIZONTAL, 0, pop.size(), 1);
	sampleRangeSlider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    JSlider source = (JSlider)e.getSource();
		    shownConfigs = (int)source.getValue();
		    modelChanged();
		}
	    });
	/*
	final JTextField shownConfigsTF = new JTextField(String.valueOf(shownConfigs));
	shownConfigsTF.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    shownConfigs = Double.parseDouble(shownConfigsTF.getText());
		    log.debug("Shown configs is now " + shownConfigs);
		}
	    });
	optionsPanel.add(new JLabel("show top"));
	optionsPanel.add(shownConfigsTF);
	optionsPanel.add(new JLabel("of configurations"));
	*/
	optionsPanel.add(sampleRangeSlider);

	//getContentPane().add(new AlleleRangePanel());
	getContentPane().add(new BlocksPanel());
	//getContentPane().add(new Scrollbar(Scrollbar.HORIZONTAL, configNum, 10, 0, ))
	getContentPane().add(optionsPanel);
	//getContentPane().add(new ScalePanel());

	// kludge
	//	subpop = pop.copy(); // consider entire population
	subpop = pop;
	alleles = new HashMap[subpop.size()];

	scalePalette = true;
	shownConfigs = 1;
	configSpacing = 1;
	//configNum = subpop.getSize();

	startLex = currentLex;

	modelChanged();
    }
    /*
    public class ConfigsOptionListener implements ActionListener {
	public void actionPerformed(ActionEvent e) {
	    showConfigs = showConfigsCB.isSelected();
	    update();
	}
    }
    */
    public void revert() {
	pop.sampleAll();
	pop.notifyListeners();
	
	//update();
    }

    // recreates data structure - currently hashmap array
    public void recompute() {
       subpop = pop.getSamplePopulation();
       
       alleles = new HashMap[length];
       for(int i=0; i < length; i++) alleles[i] = new HashMap();
       
       // consider each locus
       Integer freq;
       Integer allele; // HashMap needs Objects
       for(int locus = 0; locus < length; locus++) {

	    // add allele-frequency info from each individual (at set locus) to hashmap
	    for(int i = 0; i < subpop.size(); i++) {

       		allele = new Integer(((IntGenome)subpop.getIndividual(i)).get(locus));
		if(alleles[locus].containsKey(allele)) {
		    freq = new Integer(1 + ((Integer)(alleles[locus].get(allele))).intValue());
		}
		else freq = new Integer(1);// first instance of allele at locus
		alleles[locus].put(allele, freq); // overwriting if necessary
	    }
	}
    }

    public void modelChanged() {
	// recompute the population...
	recompute();
	
	//update the lexicon
	makeLexicon();
	
	if(!scalePalette) currentLex = startLex;

	// update components
	repaint();
    }

    public void makeLexicon() {
	// get ALL alleles at all loci
	HashSet currentLex = new HashSet();
	for(int i = 0; i < length; i++) {
	    currentLex.addAll(alleles[i].keySet());
	}
	/*
	Object[] oa = alleleSet.toArray();
	StringBuffer sb = new StringBuffer();
	for(int i = 0; i < oa.length; i++) {
	    sb.append(String.valueOf(((Character)oa[i]).charValue()));
	}
	currentLex = sb.toString();
	*/
    }


    public void select(int c, int l) {
      pop.select(c, l);
      recompute();
      repaint();
    }

    public void deselect(int c, int l) {
      pop.deselect(c, l);
      recompute();
      repaint();
    }
 
    public class BlocksPanel extends JPanel {
	
	// helpful intermediates, calculated in paint
	int top[][];
	int height[][];
	int value[][];

	public BlocksPanel() {
	    setPreferredSize(new Dimension(300, 200));
	    //initialise intermediates
	    top = new int[length][];
	    height = new int[length][];
	    value = new int[length][];

	    addMouseListener(new BlocksListener(this));
	}   

	public void paint(Graphics g) {
	

	  if(alleles == null) return; // nothing to work with;
	  	  
	  double xStep = getWidth() / (double)length;
	  //	    double yStep = getHeight() / (double)pop.getSize();
	  
	  int columnGap = 1;
	  
	  // assuming column is locus
	  for(int locus = 0; locus < length; locus++) {
	    Set alleleSet = alleles[locus].keySet();
	    double yPos = 0;//(getHeight() / 2 ) - (alleleSet.size() * yStep);
	    int range = alleles[locus].size();
	    
	    top[locus] = new int[range];
	    height[locus] = new int[range];
	    value[locus] = new int[range];
		
	    int i = 0; // cannot declare in for-loop header??
	    for(Iterator it = alleleSet.iterator(); it.hasNext(); i++) {
	      Object a = it.next();
	      // calculate weighting of this allele, proportional to it's frequency
	      int p = ((Integer)alleles[locus].get(a)).intValue(); // frequency of allele
	      double weighty = p / (double)subpop.size(); // it's proportion
	      
	      top[locus][i] = (int)yPos;
	      height[locus][i] = (int)(weighty * getHeight());
	      value[locus][i] = ((Integer)a).intValue();
	      
	      g.setColor(getColor(value[locus][i]));
	      // draw block
	      g.fillRect((int)(locus * xStep + columnGap/2), top[locus][i], (int)xStep - columnGap, height[locus][i] - 1);
	      
	      // draw allele inside box
	      g.setColor(Color.black);
	      String as = String.valueOf(value[locus][i]);
	      int aWidth = g.getFontMetrics().stringWidth(as);
	      double xPos = (locus * xStep) + (xStep / 2.0) - (aWidth / 2.0);
	      //g.drawString(as, (int)xPos, top[locus][i] + g.getFontMetrics().getHeight());
	      
	      yPos += (weighty * getHeight());
	    }
	  }
	  if(shownConfigs > 0) paintConfigs(g); 
	}
      
      public void paintConfigs(Graphics g) {

	    double xStep = getWidth() / (double)length;
	    // draw links
	    // stack them
	    int[][] count = new int[length][subpop.size()];

	    g.setColor(Color.white);

	    double over = 0.4;
	    //int configs = Math.min(10, subpop.getSize());
	    int configs = Math.min(subpop.size(), shownConfigs);
	    for(int i = configs - 1; i >=0; i--) {
		//intensity proportional to rank
		float intensity = (float)(1.0 - (i / (double)configs));
		g.setColor(Color.getHSBColor((float)1.0, (float)0.0, (float)intensity));
		
		// draw line connecting each allele
		for(int l = 1; l < length; l++) {

		    // get the corresponding allelic values for the start/end
		    int c1 = ((IntGenome)subpop.getIndividual(i)).get(l-1);
		    int c2 = ((IntGenome)subpop.getIndividual(i)).get(l);

		    // 
		    int a1;
		    for(a1 = 0; value[l-1][a1] != c1; a1++);//value[l-1].indexOf(c1);
		    int a2;
		    for(a2 = 0; value[l][a2] != c2; a2++);//(new String(value[l])).indexOf(c2);
		    int intercept1 = (int)(top[l-1][a1] + count[l-1][a1] + height[l-1][a1]*0.5); 
		    int intercept2 = (int)(top[l][a2] + count[l][a2] + height[l][a2]*0.5);
		    count[l-1][a1] = count[l-1][a1] - configSpacing;
		    g.drawLine((int)((l-1)*xStep + xStep*over), intercept1,
			       (int)((l-1)*xStep + xStep*(1-over)), intercept1);
		    g.drawLine((int)((l-1)*xStep + xStep*(1-over)), intercept1,
			       (int)((l * xStep) + xStep*over), intercept2);

		    if(l == length - 1) {
			g.drawLine((int)(l*xStep + xStep*over), intercept2,
			       (int)(l*xStep + xStep*(1-over)), intercept2);
			count[l][a2] = count[l][a2] - configSpacing;
		    }
		}
	    }
	}

      public class BlocksListener extends MouseAdapter {
	  // NOTE: private functions aim for simplicity rather than speed.

	  BlocksPanel p;
	  
	  public BlocksListener(BlocksPanel p) {
	      this.p = p;
	    }
	  
	  public void mouseClicked(MouseEvent e) {
	      if(selectCombo.getSelectedIndex() == 0) select(getValue(e), getLocus(e));
		else deselect(getValue(e), getLocus(e));
	      //update();
	      
	      pop.notifyListeners();
	    }

	    private int getLocus(MouseEvent e) {
		return (int)(e.getX() / (double)p.getWidth() * length);
	    }

	    private int getPos(MouseEvent e) {
		log.debug("top[0] = " + top[getLocus(e)][0]);
		int pos = 0;
		while(top[getLocus(e)][pos]+height[getLocus(e)][pos] < e.getY()) {
		    log.debug("top[" + (pos+1) + "] = " + top[getLocus(e)][pos+1] + " < " + e.getY());
		    pos++;
		}
		return pos;
	    }

	    private int getValue(MouseEvent e) {
		return value[getLocus(e)][getPos(e)];
	    }
	}
	
	public void update(Graphics g) {
	    paint(g);	// no clearing
	    
	}
    }

    private Color getColor(int c) {
	// ensure lexicon is up-to-date
	//if(currentLex == null) log.error("currentLex is null in getColor()");
	
	// calculate color and return it
	
	int range = IntGenome.MAX - IntGenome.MIN;

	return Color.getHSBColor((float)((c / (double)range) * 0.75), 
	    (float)0.8, 
	    (float)((c / (double)range)));
    //return new Color((float)c,(float)c,(float)c);
    }
}

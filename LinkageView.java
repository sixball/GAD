package gad;


import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

public class LinkageView extends PopulationView implements ModelListener {
    
    UserGA ga;

    LinkageControl linkageControl;

    // should rename these, are they 'models' or just records?
    PairwiseLinkageModel pl_model;
    AlleleFrequencyModel afModel;

    public LinkageView(PopulationModel pop, GeneticAlgorithm ga) {
	super(pop);
	this.ga = (UserGA)ga;

	linkageControl = new LinkageControl((UserGA)ga);

	setTitle("Linkage View");
	setPreferredSize(new Dimension(360, 500));
	setResizable(true);
	setClosable(true);
	

	getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

	JTabbedPane tabbedPane = new JTabbedPane();

	LocusLinkageGraph linkDisplay = new LocusLinkageGraph();

	JScrollPane scrollPane = new JScrollPane(new GeneAssociationGraph());
      
	tabbedPane.add("Locus Linkage", linkDisplay);
	tabbedPane.add("Gene Association", scrollPane);
	//tabbedPane.add("Pair Statistics", new JPanel());
	// plot frequency against group size
	
	getContentPane().add(tabbedPane);
	getContentPane().add(linkageControl);

	/*
	JSlider sampleRangeSlider = new JSlider(JSlider.HORIZONTAL, 0, pop.getSize(), pop.getSize());
	sampleRangeSlider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    JSlider source = (JSlider)e.getSource();
		    
		    sampleRange = (int)source.getValue();
		    update();
		}
	    });
	sampleRangeSlider.setMajorTickSpacing(20);
	sampleRangeSlider.setMinorTickSpacing(5);
	sampleRangeSlider.setPaintTicks(true);
	sampleRangeSlider.setPaintLabels(true);
	sampleRangeSlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
	getContentPane().add(sampleRangeSlider);
	*/
    }
	
    public void modelChanged() {
	pl_model = new PairwiseLinkageModel();
	afModel = new AlleleFrequencyModel();
	repaint();
    }

    /**
       Plots gene-pairs, grouped and ranked by frequency of occurrence in population.
    */
    // basically pulls pl_model inside out! 
    public class GeneAssociationGraph extends JPanel {
	
	// needs to include grouping.  will do this by setting up a hashtable that maps frequency to allele-pair
	// may need to be contained in a scrollable pane

	TreeMap groups;

	public GeneAssociationGraph() {
	    setPreferredSize(new Dimension(300, 300));
	    groups = new TreeMap(); // define comparator inline?

	}

	public void doGroups() {

	    if(pl_model == null) return;

	    groups = new TreeMap(); // define comparator inline?

	    // iterate through pl_model
	    for(int l = 0; l < length - 1; l++) {
		Iterator it1 = pl_model.link[l].keySet().iterator();
		while(it1.hasNext()) {
		    Integer l_allele = (Integer)it1.next();
		    HashMap[] alleleFreq = (HashMap[])pl_model.link[l].get(l_allele);
		    for(int l2 = 0; l2 < alleleFreq.length; l2++) {
			Iterator it2 = alleleFreq[l2].keySet().iterator();
			while(it2.hasNext()) {
			    Integer r_allele = (Integer)it2.next();

			    int freq = ((Double)alleleFreq[l2].get(r_allele)).intValue();
			    // create gene-pair
			    Gene left = new Gene(l_allele.intValue(), l);
			    Gene right = new Gene(r_allele.intValue(), l2 + l + 1);
			    GenePair genePair = new GenePair(left, right);
	    
			    // if freq is new, this is gonna be a new set
			    if(!groups.containsKey(new Integer(freq))) {	    
				//    create newset
				TreeSet newSet = new TreeSet();	    
				//    put new mapping (freq, newset) in groups
				groups.put(new Integer(freq), newSet);
			    }

			    // get target set (maybe new set)
			    TreeSet targetSet = (TreeSet)groups.get(new Integer(freq));
			    // put freq, gene-pair into target set
			    targetSet.add(genePair);
			}
		    }
		}
	    }
	}

	public void paint(Graphics g) {

	    double xScale = getWidth() / length;
	    int depth = 8; // allele bar depth

	    int newHeight = depth + (g.getFontMetrics().getHeight() + 1) * groups.size();
	    
	    Iterator git = groups.keySet().iterator();
	    while(git.hasNext()) {
		Integer freq = (Integer)git.next();
		//log.debug(((HashSet)groups.get(freq)).size() + " with frequency " + freq);
		newHeight += (depth + 1) * ((TreeSet)groups.get(freq)).size();
	    }

	    // set new size
	    setMinimumSize(new Dimension(350, newHeight));
	    setPreferredSize(new Dimension(350, newHeight));

	    // blank out
	    g.clearRect(0, 0, getWidth(), getHeight());

	    doGroups();  // set up groups for displaying
	    
	    // get ready to iterate thru groups
	    git = groups.keySet().iterator();

	    int height = getHeight(); // cursor

	    while(git.hasNext()) {
		int freq = ((Integer)git.next()).intValue();

		Iterator sit = ((TreeSet)groups.get(new Integer(freq))).iterator();
	
		// mark and label group border
		height--;
		g.setColor(Color.black);
		g.drawLine(0, height, getWidth(), height);
		g.drawString(String.valueOf(freq), 0, height);
		height -= g.getFontMetrics().getHeight();

		while(sit.hasNext()) {
		    GenePair gp = (GenePair)sit.next();
		    
		    g.setColor(Color.red); // default error color -- should never happen!

			// color first of pair
			if(gp.left.allele() == 0) g.setColor(Color.black);
			if(gp.left.allele() == 1) g.setColor(Color.white);

			// draw first gene
			g.fillRect((int)(gp.left.locus() * xScale), 
				   height - depth,
				   (int)xScale - 1,//(int)((gp.left.locus + 1) * xScale) - 1,
				   depth);

			// color second of pair
			if(gp.right.allele() == 0) g.setColor(Color.black);
			if(gp.right.allele() == 1) g.setColor(Color.white);

			// draw second gene
			g.fillRect((int)(gp.right.locus() * xScale), 
				   height - depth,
				   (int)xScale - 1,//(int)((gp.right.locus + 1) * xScale) - 1,
				   depth);

			g.setColor(Color.darkGray);
			g.drawLine((int)((gp.left.locus() + 1) * xScale),
				   (int)(height - (depth / 2)),
				   (int)((gp.right.locus()) * xScale) - 1,
				   (int)(height - (depth / 2)));


		    //   log.debug("left gene = (" + gp.left.allele + "@" + gp.left.locus + ") " +
		    //      "right gene = (" + gp.right.allele + "@" + gp.right.locus + ")");
		  
		// move up the page
		height -= (depth + 1);  
		}
	    }
	} 
	//validate();
    }

    // currently only pair-wise
    public class LocusLinkageGraph extends JPanel {
	
	// used in colour-scaling
	double maxLinkage;
	double minLinkage;

	public LocusLinkageGraph() {
	    setPreferredSize(new Dimension(300, 300));

	    addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
			Linkage l = new Linkage(length, 0.5);
			int l1 = (int)(e.getX() / (double)getWidth() * length);
			int l2 = (int)(e.getY() / (double)getHeight() * length); 
			l.set(l1, 1.0);
			l.set(l2, 1.0);
			ga.getModel().addLinkage(l);
			linkageControl.refresh();
		    }
		});
	}

	public void paint(Graphics g) {

	    // scale but include blank ll(x, x) lines
	    double xScale = getWidth() / (double)length;
	    double yScale = getHeight() / (double)length;

	    // blank out
	    //g.setColor(Color.white);
	    g.clearRect(0, 0, this.getWidth(), getHeight());

	    if(pl_model == null) return; // model not yet constructed

	    findMinMaxLinkage();

	    for(int l = 0; l < length - 1; l++) {
		for(int l2 = l +1; l2 < length; l2++) {
		   		    
		    // paint it			    
		    double c = linkage(l, l2); // this is hideously inefficient
		    
		    c = (c - minLinkage) * 255.0 / (maxLinkage - minLinkage);
		    int rc = (int)c;//(int)Math.min(c, 255);
		    int oc = 0;//(int)Math.max(Math.min(c - 255, 255), 0);
		    
		    g.setColor(new Color((int)c, oc, oc)); 
		    
		    g.fillRect((int)(l * xScale), 
			       (int)(l2 * yScale), 
			       (int)(Math.ceil(xScale)), 
			       (int)(Math.ceil(yScale))); 

		    // and reflection...
		     g.fillRect((int)(l2 * xScale), 
			       (int)(l * yScale), 
			       (int)(Math.ceil(xScale)), 
			       (int)(Math.ceil(yScale))); 
		}
	    }
	}	 
	
	public double linkage(int l1, int l2) {
	    // dammit, we would be using IntHashMap, except how to handle iterators?
	    HashMap md_l_allele = afModel.getFrequencyDistribution(l1);
	    HashMap md_r_allele = afModel.getFrequencyDistribution(l2); // marginal distributions
	    
	    double value = 0;

	    Iterator lIt = md_l_allele.keySet().iterator();
	    while(lIt.hasNext()) {
		int l_allele = ((Integer)lIt.next()).intValue();
		double l_rel_freq = (((Integer)md_l_allele.get(new Integer(l_allele))).intValue() / (double)pop.size());

		// construct joint distribution for l_allele, from pl_model:
		HashMap jd = pl_model.jointDistribution(l_allele, l1, l2);
		
		// get ready to iterate through all actual r_alleles
		Iterator rIt = md_r_allele.keySet().iterator();

		while(rIt.hasNext()) {
		    Integer r_allele = (Integer)rIt.next();
		    int r_freq = ((Integer)md_r_allele.get(r_allele)).intValue();
		    
		    double actual = 0;
		    if(jd.containsKey(r_allele)) actual = ((Double)jd.get(r_allele)).doubleValue();
		    double expected = ((double)r_freq) * l_rel_freq;
		    
		    // add difference between actual and expected
		    value += Math.pow(actual - expected, 2) / (double)expected;
		    //value += Math.abs(actual - expected);
		
		}
	    }
	    //log.debug("link(" + l1 + ", " + l2 + ") value = "  + value);
	    return value;
	}

	// inefficient.. gah!
	public void findMinMaxLinkage() {
	    maxLinkage = Double.MIN_VALUE;
	    minLinkage = Double.MAX_VALUE;
	    double linkage;
	    for(int i = 0; i < length - 1; i++) {
		for(int j = i + 1; j < length; j++) {
		    linkage = linkage(i, j);
		    maxLinkage = Math.max(maxLinkage, linkage);
		    minLinkage = Math.min(minLinkage, linkage);
		}
	    }
	}
    }

    // holds distribution of alleles at each locus
    class AlleleFrequencyModel {
	HashMap[] af;

	public AlleleFrequencyModel() {
	    af = new HashMap[length];    	// do allele frequencies
		
	    for(int l = 0; l < length; l++) {
		af[l] = new HashMap();
		for(int i = 0; i < pop.size(); i++) {
		    if(pop.isSampling(i)) {
			Integer allele = new Integer(((IntGenome)pop.getIndividual(i)).get(l));
			int freq = 0;
			if(af[l].containsKey(allele)) {
			    freq = ((Integer)af[l].get(allele)).intValue();
			}
			af[l].put(allele, new Integer(freq + 1));
		    }
		}
	    }
	}

	public int getFrequency(int allele, int locus) {
	    if(af[locus].containsKey(new Integer(allele))) {
		return ((Integer)af[locus].get(new Integer(allele))).intValue();
	    }
	    else return 0;
	}

	public HashMap getFrequencyDistribution(int locus) {
	    return af[locus];
	}
    }

    // holds frequency of all gene pairs in all individuals
    class PairwiseLinkageModel {

	// start with simple, pairwise linkage.
	public HashMap[] link;

	boolean fitnessWeighted = false;

	public double maxLinkage;
	
	public PairwiseLinkageModel() {
	    maxLinkage = 0;

	    // for each gene a, record instances of association with gene b
	   
	    link = new HashMap[length];
	    
	    // for each locus l
	    for(int l = 0; l < length - 1; l++) {
		link[l] = new HashMap();

		// for each individual in population
		for(int i = 0; i < pop.size(); i++) {

		    // skip if not in sample
		    if(!pop.isSampling(i)) break;

		    Integer l_allele = new Integer(((IntGenome)(pop.getIndividual(i))).get(l));
		    // if this is a new LHS allele
		    if(!link[l].containsKey(l_allele)) {
			// we have new left-side gene
			// construct new array for remaining loci, 
			//element is hashmap of allele-frequency pairs 
			HashMap[] newLinkage = new HashMap[length - l - 1]; // should have -1
			
			for(int l2 = (l + 1); l2 < length; l2++) {
			    Integer r_allele = new Integer(((IntGenome)(pop.getIndividual(i))).get(l2));
			    //  start new allele-frequency map
			    newLinkage[l2 - l - 1] = new HashMap();
			    if(fitnessWeighted) {
				newLinkage[l2 - l - 1].put(r_allele, new Double(ga.fitness(i)));
			    }
			    else {
				newLinkage[l2 - l - 1].put(r_allele, new Double(1.0));
			    }
			}
			link[l].put(l_allele, newLinkage); 
		    }			    

		    // else increment linkage between gene at l and genes at l2, creating new hashmaps  as required
		    else {
			// so, get array of current allele-frequency mappings
			HashMap[] alleleFreq = (HashMap[])link[l].get(l_allele);	
			for(int l2 = (l + 1); l2 < length; l2++) {
			    Integer r_allele = new Integer(((IntGenome)(pop.getIndividual(i))).get(l2));
			    double oldFreq = 0;
			    // get old frequency if we have it
			    if(alleleFreq[l2 - l - 1].containsKey(r_allele)) {
				oldFreq = ((Double)alleleFreq[l2 - l - 1].get(r_allele)).doubleValue(); 
			    }

			    // increment frequency
			    double newFreq;
			    if(fitnessWeighted) newFreq = oldFreq + ga.fitness(i);
			    else newFreq = oldFreq + 1.0;
			    alleleFreq[l2 - l - 1].put(r_allele, new Double(newFreq));
			    maxLinkage = Math.max(maxLinkage, newFreq);
			}
		    }
		}
	    }
	}

	// give a hashmap of dist. of alleles at locus2, with allele at locus1
	public HashMap jointDistribution(int allele, int locus1, int locus2) {
	    // presume in proper order
	    if(link[locus1].containsKey(new Integer(allele))) {
		HashMap[] af = (HashMap[])link[locus1].get(new Integer(allele));
		//log.debug("jd(" + allele + ", " + locus1 + ", " + locus2 + ") = " + af[locus2 - locus1 - 1]);
		return af[locus2 - locus1 - 1];
	    }
	    else return new HashMap();
	}
		
	public void show() {
	    log.debug("checking output");
	    // debug check
	    int uniqueAssociations = 0;
	    for(int l = 0; l < length - 1; l++) {
		Iterator it1 = link[l].keySet().iterator();
		while(it1.hasNext()) {
		    Integer l_allele = (Integer)it1.next();
		    HashMap[] alleleFreq = (HashMap[])link[l].get(l_allele);
		    for(int l2 = 0; l2 < alleleFreq.length; l2++) {
			log.debug("l = " + l2 + ", l2 = " + l2);
			Iterator it2 = alleleFreq[l2].keySet().iterator();
			while(it2.hasNext()) {
			    Integer r_allele = (Integer)it2.next();
			    uniqueAssociations++;
			}
		    }
		}
	    }   
	}
	
	public void order() {
	    log.debug("checking output");
	    // debug check
	    for(int l = 0; l < length - 1; l++) {
		Iterator it1 = link[l].keySet().iterator();
		while(it1.hasNext()) {
		    Integer l_allele = (Integer)it1.next();
		    HashMap[] alleleFreq = (HashMap[])link[l].get(l_allele);
		    for(int l2 = 0; l2 < alleleFreq.length; l2++) {
			log.debug("l = " + l2 + ", l2 = " + l2);
			Iterator it2 = alleleFreq[l2].keySet().iterator();
			while(it2.hasNext()) {
			    Integer r_allele = (Integer)it2.next();
			    log.debug("linkage between (" + l_allele +" @ " + l + " and (" 
				      + r_allele + " @ " + (l2+l) + ") is " + alleleFreq[l2].get(r_allele));
			}
		    }
		}
	    }   
	}
    }
    
    /** 
	LinkageControl displays and exposes the Linkage Model to the user.  They may add, remove or modify the linkage constraints here in conjunction with the Linkage analysis.
    */
    class LinkageControl extends JPanel {
	
	LinkageModel model;
	LinkageControlGraph linkageControlGraph;
	
	public LinkageControl(UserGA ga) {
	    super();
	    model = ga.getModel();	    
	    setBorder(new TitledBorder(new EtchedBorder(), "Linkage Control"));
	    linkageControlGraph = new LinkageControlGraph();
	    
	    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    add(linkageControlGraph);
	    JPanel controlPanel = new JPanel(new FlowLayout());
	    JButton addRandomLinkageB = new JButton("Add Random");
	    addRandomLinkageB.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			addLinkage();
		    }
		});
	    controlPanel.add(addRandomLinkageB);
	    controlPanel.add(new JComboBox(new String[]{"Add", "Remove", "Modify"}));
	    add(controlPanel);
	}
	
	public void addLinkage() {
	    model.addLinkage();
	    refresh();
	}

	public void refresh() {
	    linkageControlGraph.removeAll();
	    
	    for(Iterator it = model.getLinkages().iterator(); it.hasNext();) {
		
		Linkage linkage = (Linkage)(it.next());
		linkageControlGraph.add(new LinkageGraph(linkage));
	    }
	    revalidate();
	    repaint();
	}
	
	public class LinkageControlGraph extends JPanel {
	    
	    public LinkageControlGraph() {
		setPreferredSize(new Dimension(400, 200));
		setMinimumSize(new Dimension(200, 50));
		addMouseListener(new LinkageListener(this, model));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    }
	}
	
	public class LinkageGraph extends JPanel {
	    
	    Linkage linkage;
	    
	    public LinkageGraph(Linkage linkage) {
		this.linkage = linkage;
		setPreferredSize(new Dimension(300, 20));
		setMinimumSize(new Dimension(200, 2));
	    }
	    
	    public void paint(Graphics g) {
		double xScale = getWidth() / model.length();
		
		for(int l = 0; l < model.length(); l++) {
		    float val = (float)linkage.get(l);
		    g.setColor(new Color(val, val, val));
		    g.fillRect((int)(l * xScale), 0,(int)(xScale), getHeight());
		}
	    }
	}
	
	public class LinkageListener extends MouseAdapter {
	    LinkageControlGraph l;
	    LinkageModel m;
	    
	    public LinkageListener(LinkageControlGraph l, LinkageModel m) {
		this.l = l;
		this.m = m;
	    }
	    
	    public void mousePressed(MouseEvent e) {
		//m.setLink(getLocus(e), 1.0 - (e.getY() / (double)l.getHeight()));
		//l.select((int)(getLocus(e), 1.0 - ( / getHeight()));
		l.repaint();
	    }
	    
	    public void mouseClicked(MouseEvent e) {
		//m.setLink(getLocus(e), 1.0 - (e.getY() / (double)l.getHeight()));
		l.repaint();
	    }
	    
	    private int getLocus(MouseEvent e) {
		return (int)(e.getX() / (double)l.getWidth() * model.length());
	    }
	}
    }
}



class GenePair implements Comparable {
    
    public Gene left;
    public Gene right;

    public GenePair(Gene left, Gene right) {
	this.left = left;
	this.right = right;
    }

    // pair a is less than b if locus 2 is less 
    // if loci are same then compare locus 2
    public int compareTo(Object o) {
	GenePair c = (GenePair)o;
	if(left.locus() > c.left.locus()) return -1;
	if(left.locus() == c.left.locus()) return (c.right.locus() - right.locus());
	return 0;
    }

    public String toString() {
	return new String(left.toString() + " - " + right.toString());
    }
}



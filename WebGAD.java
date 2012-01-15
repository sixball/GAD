package gad;


/**
   WebGAD: WebGenetic Algorithm Developer (version 0.4)
   Copyright(2002) Simon Hammond (simonham@yahoo.com)
*/


import java.net.*;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class WebGAD extends JApplet {

    PopulationModel pop;
    static GeneticAlgorithm ga;
  static Problem problem;
  

    static int popSize;
    static int genomeLength;
    static double mutationRate;
    static double eliteRatio;

    static double gaParam; // free variable

    static int maxIterations;
    static double haltingFitness;

    static Config config;
    static Logger log = new Logger();

    public void init() {

      //	GAD gad = new GAD();

	//setup config from applet params!
	//Config config = new Config();
	Config.set("Problem", "TSProblem");
	Config.set("PopulationSize", 100);
	Config.set("GenomeLength", 80);

	pop = new PopulationModel();

	// set range of alleles
	IntGenome.MAX = 1;
	IntGenome.MIN = 0;

	try {
	    pop.initialise();
	} catch(Exception e) 
	{
	    log.error(e.getMessage());
	}

	//Problem problem = new MaxOnesProblem();
	//Problem problem = new R1Problem(pop.length(), (int)gaParam);
	//LinkageModel model = new LinkageModel(genomeLength);model.modelR1((int)gaParam, 0.8);
	//ga = new UserGA(pop, model, new R1Problem(pop.length(), (int)(gaParam)));//genomeLength / gaParam))); //passing reference to pop for ga to work on
	//gad.ga = new SANUXGeneticAlgorithm(gad.pop, problem);
	//gad.ga = new GA(gad.pop, new R1Problem(gad.pop.length(), 4));
	problem = new Problem();
	ga = new GeneticAlgorithm(pop, problem);
	
	
	
	//((UserGA)ga).initialise((int)gaParam);
	//((SANUXGeneticAlgorithm)ga).initialise((int)gaParam);
	ga.setMutationRate(mutationRate);
	ga.setEliteRatio(eliteRatio);
	
	// write this better to be more flexible and testing for .gac with 'String.endsWith()start();
	
    }

  /**
     Extends JDesktopPane with added cool functionality:
     <ul>
     <li>menus
     <li>Simple layout management.
     <li>Population and GA linking for notification.
     </ul>
  */
    
    public void addView(View view) {
	//viewPane.add(view);
      view.setVisible(true);
      view.show();
      view.pack();
      
      // position view
      /*view.setLocation(x, y);
	if(x+view.getWidth() > y+view.getHeight()) 
	{
	y += view.getHeight();
	}
	else x += view.getWidth();
      */
      log.debug("parent is " + view.getClass().getSuperclass().getName());
      
      if(view.getClass().getSuperclass().getName() == "PopulationView") {
	PopulationView pView = (PopulationView)view;
	if(pView.getPopulation() != null)
	  pView.getPopulation().addListener(pView);
	else log.debug("pop is null");
      }

      if(view.getClass().getSuperclass().getName() == "AlgorithmView") {
	AlgorithmView aView = (AlgorithmView)view;
	if(aView.getAlgorithm() != null)
	  aView.getAlgorithm().addListener(aView);
	else log.debug("algorithm is null");
      }
}
  
  
  class ButtonPanel extends JPanel
  {
      public ButtonPanel() {
	  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      }

      public void addButton(JButton button)
      {
	  button.setHorizontalTextPosition(SwingConstants.CENTER);
	  button.setVerticalTextPosition(SwingConstants.BOTTOM);
	  button.setBorderPainted(false);
	  button.setFocusPainted(false);
	  
	  add(button);  
      }
  }
  
  /**
     Runs GAD in it's full interactive glory.
  */
  public void start() {
       
    final JFrame frame;
    final ViewPane viewPane;
    JMenuBar menuBar;
   
    ButtonPanel buttonPanel;
    JButton fitnessButton;
    JButton matrixButton;
    JButton ADVButton;
    JButton linkageButton;
    JButton TSPButton;
    
    
    // Set up the GUI
    frame = new JFrame("GAD: Genetic Algorithm Demonstrator");
    viewPane = new ViewPane();    

    // button panel
    buttonPanel = new ButtonPanel();    
    
    try {
      fitnessButton = new JButton("Fitness", new ImageIcon(new URL(getCodeBase() + "FitnessButton.jpg")));
    
    fitnessButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  viewPane.add(new FitnessView(pop, ga));
	}
      });
    buttonPanel.addButton(fitnessButton);

  } catch(MalformedURLException e) {}
  
  try {
    matrixButton = new JButton("Matrix", new ImageIcon(new URL(getCodeBase() + "MatrixButton.jpg")));
    
    matrixButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  viewPane.add(new MatrixView(pop, ga));
	}
      });
    buttonPanel.addButton(matrixButton);

  } catch(MalformedURLException e) {}

  try {
    TSPButton = new JButton("TSP", new ImageIcon(new URL(getCodeBase() + "TSPButton.jpg")));
    
    TSPButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  viewPane.add(new TSPView(pop, problem));
	}
      });
    buttonPanel.addButton(TSPButton);

  } catch(MalformedURLException e) {}
  /*
  try {
     
    ADVButton = new JButton("Allele Diversity", new ImageIcon(new URL(getCodeBase() + "ADVButton.jpg")));
    
    ADVButton.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  addView(new AlleleDiversityView(pop, ga));
	}
      }); 
    viewPanel.addButton(ADVButton);

  } catch(MalformedURLException e) {}
  */

  try {
      linkageButton = new JButton("Linkage", new ImageIcon(new URL(getCodeBase() + "LinkageButton.jpg")));
      
      linkageButton.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		  viewPane.add(new LinkageView(pop, ga));
	      }
	  }); 
      buttonPanel.addButton(linkageButton);
  } catch(MalformedURLException e) {}
  
  buttonPanel.setSize(new Dimension(120, 600));
    buttonPanel.setVisible(true);

    //viewPane.setSize(new Dimension(410, 180));
    viewPane.setVisible(true);

    frame.getContentPane().add(buttonPanel, BorderLayout.WEST);

    frame.setSize(new Dimension(800, 800));

    frame.setLayeredPane(viewPane);
    //frame.getContentPane().add(Pane, BorderLayout.CENTER);
    viewPane.add(buttonPanel);

    // Exit VM with window close
    frame.addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) { System.exit(0); }
      });
    
    // set up menubar
    menuBar = new JMenuBar();
    JMenuItem menuItem;
    
    // different views
    JMenu viewMenu = new JMenu("Views");
 
    menuItem = new JMenuItem("Table");
    menuItem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  addView(new DataMatrixView(pop));
	}
      });
    viewMenu.add(menuItem);
       
    menuItem = new JMenuItem("Matrix");
    menuItem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  addView(new MatrixView(pop, ga));
	}
      });
    viewMenu.add(menuItem);
    
    menuItem = new JMenuItem("Fitness");
    menuItem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  addView(new FitnessView(pop, ga));
	}
      });
    menuItem = new JMenuItem("TSP View");
    menuItem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  addView(new TSPView(pop, problem));
	}
      });
    viewMenu.add(menuItem);
    
    menuItem = new JMenuItem("Control");
    menuItem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  viewPane.add(new GeneticAlgorithmControl(ga));
	}
      });
    viewMenu.add(menuItem);
    /*
    menuItem = new JMenuItem("SANUX");
    menuItem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  //vPane.add(new SANUXView(ga, config));
	}
      });
    viewMenu.add(menuItem);
    
    menuItem = new JMenuItem("Linkage");
    menuItem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  new LinkageView(pop, ga).show();
	}
      });
    viewMenu.add(menuItem);
    */
    menuItem = new JMenuItem("Log View");
    menuItem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  //vPane.add(new LogView());
	}
      });
    viewMenu.add(menuItem);

    menuBar.add(viewMenu);
    /*JMenu helpMenu = new JMenu("Help");
    menuItem = new JMenuItem("About GAD");
    menuItem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  (new Dialog(frame, "hello", false)).show();
	}  
      });
    helpMenu.add(menuItem);
	
    menuBar.add(helpMenu);	
    */
    frame.setJMenuBar(menuBar);

    // Default views

    // default control panel
    GeneticAlgorithmControl control = new GeneticAlgorithmControl(ga);
    viewPane.add(control);
    
    LogView logView = new LogView();
    control.setLog(logView);    

    MatrixView matrixView = new MatrixView(pop, ga);
    viewPane.add(matrixView);
	
    frame.setSize(800, 400);
    frame.setVisible(true);    
  }
}

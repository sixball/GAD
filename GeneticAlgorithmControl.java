package gad;


import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

import java.util.*;
import javax.swing.Timer;

// TODO: clarify relationship between AlgorithmView and AlgorithmListener
public class GeneticAlgorithmControl extends AlgorithmView implements AlgorithmListener {

    JButton initialiseButton;
    JButton stepButton;
    JButton runButton;
    JComboBox epochCombo;
    JTextField targetField;
    JTextField genField;
    JTextField evalField;

    int epochs;
    
    boolean doSort;
    boolean isRunning;

    EvolveThread evolveThread;
    //Thread displayThread;
    Timer displayTimer;

    // GA parameters
    int genomeLength;

    PopulationModel pop;
    GeneticAlgorithm ga;

    ArrayList views;

    //Logger devLog;
    LogView logView;

    public GeneticAlgorithmControl(GeneticAlgorithm newGA) {
	super(newGA);
	ga = newGA; // is al in superclass redundant?

	epochs = 1;

	// views interested in changes in th algorithm.  Includes this.
	views = new ArrayList();
	addListener(this);

	//displayTimer = new Timer(500, this);

	doSort = false;
	isRunning = false;
	logView = new LogView();

	setTitle("Control Panel");
	setSize(new Dimension(300, 100));
	setResizable(false);
    
	JTabbedPane tabbedPane = new JTabbedPane();
	JPanel bottomPanel = new JPanel();
	
	JPanel mainPanel = new JPanel();
	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

	genField = new JTextField(5);
	genField.setEditable(false);
	genField.setBackground(Color.white);

	evalField = new JTextField(8);
	evalField.setEditable(false);
	evalField.setBackground(Color.white);
	
	bottomPanel.add(new JLabel("Evaluations", SwingConstants.RIGHT));
	bottomPanel.add(evalField);
	bottomPanel.add(new JLabel("Iteration", SwingConstants.RIGHT));
	bottomPanel.add(genField);
	
	JPanel bottomBox = new JPanel();
	
	// run button
	runButton = new JButton("Run");
	runButton.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    //pop.notifyListeners();
		    isRunning = !isRunning;
		    if(isRunning) {
			ga.cancelled = false;
			runButton.setText("Stop");
			evolveThread = new EvolveThread(epochs);
			evolveThread.setPriority(Thread.MIN_PRIORITY);
			evolveThread.start();
			//displayTimer.start();
		    }
		    else {
			runButton.setText("Run");
			ga.cancelled = true;
			//displayTimer.stop();
		    }
		}
	    });

	bottomPanel.add(runButton);
	
	epochCombo = new JComboBox(new String[] {"1", "10", "100", "1000", "10000", "many"});
	epochCombo.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if(epochCombo.getSelectedIndex() == 5) { epochs = Integer.MAX_VALUE; }
		    else epochs = Integer.parseInt((String)epochCombo.getSelectedItem());
		}
	    });
	//    epochCombo.alignright

	bottomPanel.add(epochCombo);

	Box writeBox = new Box(BoxLayout.X_AXIS);
	writeBox.setSize(new Dimension(50, 12));
	JButton writeButton = new JButton("Write to");
	final JTextField outputFileTF = new JTextField("gad.dat");
	writeButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    ga.writeFitnessHistory(outputFileTF.getText());
		    logView.makeEntry("Output written  to " + outputFileTF.getText());
		}
	    });

	writeBox.add(writeButton);
	writeBox.add(outputFileTF);
	
	JButton resetButton = new JButton("Reset");
	resetButton.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
				ga.reset();
				ga.fireAlgorithmChanged();
		}
	});

	//	mainPanel.add(topBox, BorderLayout.NORTH);
	mainPanel.add(writeBox);
	mainPanel.add(resetButton);
	mainPanel.add(Box.createVerticalStrut(80));
	mainPanel.add(bottomBox);
	

	tabbedPane.add("Operating", mainPanel);

	// GA tabbed panel
	JPanel GAPanel = new JPanel();
	GAPanel.setLayout(new FlowLayout());

	JPanel paramPanel; // this is switchable, depending on selection scheme


	/*
	  Elite Selection Panel
	*/
	JPanel elitePanel = new JPanel();
	elitePanel.setLayout(new BoxLayout(elitePanel, BoxLayout.Y_AXIS));
	elitePanel.add(new JLabel("Elite Ratio", SwingConstants.RIGHT));
	final JTextField eliteRatioTF = new JTextField(String.valueOf(ga.getEliteRatio()));
	eliteRatioTF.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    ga.setEliteRatio(Double.parseDouble(eliteRatioTF.getText()));
		    logView.makeEntry("Elite ratio is now " + ga.getEliteRatio());
		}
	    });
	elitePanel.add(eliteRatioTF);
	
	elitePanel.add(new JLabel("Mutations (per locus)", SwingConstants.RIGHT));
	final JTextField mutationRateTF = new JTextField(String.valueOf(ga.getMutationRate()));
	mutationRateTF.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    ga.setMutationRate(Double.parseDouble(mutationRateTF.getText()));
		    logView.makeEntry("Mutation rate is now " + ga.getMutationRate());
		}
	    });
	elitePanel.add(mutationRateTF);

	JPanel selectionPanel = new JPanel();
	selectionPanel.setBorder(new TitledBorder(new EtchedBorder(), "Selection"));
	selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));

	// radio button group of selection schemes
	ButtonGroup schemeGroup = new ButtonGroup();
	JRadioButton propSelection = new JRadioButton("Proportional");
	propSelection.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    ga.setSelectionScheme(GeneticAlgorithm.PROPORTIONAL_SELECTION);
		    logView.makeEntry("Using proportional selection");
		}
	    });
	schemeGroup.add(propSelection);
	selectionPanel.add(propSelection);

	JRadioButton rankSelection = new JRadioButton("Ranked");
	rankSelection.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    ga.setSelectionScheme(GeneticAlgorithm.RANK_SELECTION);
		    logView.makeEntry("Using ranked selection");
		}
	    });
	schemeGroup.add(rankSelection);
	selectionPanel.add(rankSelection);

	JRadioButton eliteSelection = new JRadioButton("Elite");
	eliteSelection.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    ga.setSelectionScheme(GeneticAlgorithm.TRUNCATE_SELECTION);
		    //paramPanel = elitePanel;
		    logView.makeEntry("Using elite selection");
		}
	    });
	schemeGroup.add(eliteSelection);
	selectionPanel.add(eliteSelection);


	/*
	  Tournament Selection
	*/
	JPanel tournamentPanel = new JPanel();
	tournamentPanel.setLayout(new BoxLayout(tournamentPanel, BoxLayout.Y_AXIS));
	tournamentPanel.add(new JLabel("Tournament Size"));
	final JTextField tournamentSizeTF = new JTextField(String.valueOf(ga.getTournamentSize()));
	tournamentSizeTF.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    ga.setTournamentSize(Integer.parseInt(tournamentSizeTF.getText()));
		    logView.makeEntry("Tournament size is now " + ga.getTournamentSize());
		}
	    });
	tournamentPanel.add(tournamentSizeTF);
	tournamentPanel.add(new JLabel("Mutation Rate"));
	//tournamentPanel.add(mutationRateTF);

	JRadioButton tournamentSelection = new JRadioButton("Tournament");
	tournamentSelection.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    ga.setSelectionScheme(GeneticAlgorithm.TOURNAMENT_SELECTION);
		    //   paramPanel = tournamentPanel;
		    logView.makeEntry("Using tournament selection");
		}
	    });
	//schemeGroup.add(tournamentSelection);
	selectionPanel.add(tournamentSelection);
	
	tournamentSelection.setSelected(true);
	//tournamentSelection.doClick();
	GAPanel.add(selectionPanel);

	tabbedPane.add("GA Control", GAPanel);
	
	paramPanel = elitePanel;
	
	GAPanel.add(paramPanel);

	JPanel genomePanel = new JPanel();
	ButtonGroup operatorGroup = new ButtonGroup();

	JCheckBox twoOpt = new JCheckBox("2-opt");
	twoOpt.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    //ga.setOperator(TSPGenome.TWO_OPT);
		    logView.makeEntry("Using 2-opt operator");
		}
	    });
	genomePanel.add(twoOpt);

	//	tabbedPane.add("Genome", genomePanel);
	//tabbedPane.add("Royal Road", new RoyalRoadGAPanel((RoyalRoadGA)ga, log));

	getContentPane().setLayout(new BorderLayout());
	getContentPane().add(tabbedPane, "Center");
	getContentPane().add(bottomPanel, "South");
    }


    public void algorithmChanged() {
    	//log.debug("updating control");
    	genField.setText(String.valueOf(ga.getIteration()));
    	evalField.setText(String.valueOf(ga.getEvaluations()));
    }

    public void setLog(LogView newLogView) { logView = newLogView; }
    
    public class EvolveThread extends Thread {
	
	int steps;
	boolean cancelled;

	public EvolveThread(int steps) {
	    this.steps = steps;
	}
	
	public void run() {
		ga.search(steps);
		runButton.setText("Run");
		isRunning = false;
	}
    }
    
    public void actionPerformed(ActionEvent e) {
    	pop.notifyListeners();
    	//ga.notifyListeners();
    	//notifyListeners();
    }
    
    public void addListener(AlgorithmListener listener) {
    	views.add(listener);
    }
    /*    
    public void notifyListeners() {
	for(ListIterator i = views.listIterator(); i.hasNext();) {
	    ((AlgorithmListener)i.next()).update();
	}
    }
    */
}

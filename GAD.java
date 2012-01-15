package gad;

/**
 * GAD: Genetic Algorithm Developer (version 0.5) Copyright(2003) Simon Hammond
 * (simonham@yahoo.com)
 */

import gad.SANUXView;
import gad.SEAMView;
import gad.SearchAlgorithm;
import gad.SearchModel;
import gad.TSPView;
import gad.TSProblem;
import gad.TransGeneticAlgorithm;
import gad.ViewPane;

import java.lang.reflect.*;
import java.util.*;
import java.text.*;
import java.io.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class GAD {

	SearchModel model;

	SearchAlgorithm ga;

	Problem problem;

	static int maxIterations;

	static double haltingFitness;

	static Logger log = new Logger();

	public static void main(String[] argv) {

		GAD gad = new GAD();

		if (argv.length == 0)
			Config.readFrom("default.txt");
		else {
			if (argv[0].endsWith(".gac"))
				Config.readFrom(argv[0]);
			else {
				System.out
						.println("Usage: GAD [<config_file.gac> [<runs> <output_file>]]");
				System.exit(0);
			}
		}

		/*
		 * The Model
		 */

		// get the class
		Class modelClass = null;
		try {
			modelClass = Class.forName((String) Config.get("SearchModel"));
		} catch (ClassNotFoundException e) {
			log.error("No search model defined!");
			System.exit(1);
		}

		// get the constructor
		Constructor constr = null;
		try {
			constr = modelClass.getConstructor(null);
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage());
		}

		// construct an instance
		try {
			gad.model = (SearchModel) constr.newInstance(null);
		} catch (InstantiationException e) {
			log.error(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
		} catch (InvocationTargetException e) {
			log.error(e.getMessage());
		}

		// initialise
		gad.model.initialise();

		/*
		 * The Population // define what sort of population we are using gad.pop =
		 * new Population(gad.config);
		 * 
		 * 
		 * gad.pop.initialise();
		 */

		/*
		 * The Problem
		 */
		Class probClass = null;
		try {
			probClass = Class.forName((String) Config.get("Problem"));
		} catch (ClassNotFoundException e) {
			log.error("No problem defined!");
			System.exit(1);
		}

		constr = null;
		try {
			constr = probClass.getConstructor(null);
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage());
		}

		try {
			gad.problem = (Problem) constr.newInstance(null);
		} catch (InstantiationException e) {
			log.error(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
		} catch (InvocationTargetException e) {
			log.error(e.getMessage());
		}

		log.ifNull(gad.problem, "gad.problem");

		gad.problem.initialise(probClass);

		/*
		 * The Algorithm
		 */
		// firstly, to construct the right sort of algorithm
		Class<?> algClass = null; // GA is the default
		try {
			String algName = (String) Config.get("SearchAlgorithm");
			if (algName == null) {
				algClass = GeneticAlgorithm.class;
				log.debug("No search algorithm specified - defaulting to "
						+ algClass.getName());
			} else {
				algClass = Class.forName(algName);
				log.debug("Using search algorithm: "
						+ algClass.getName());
			}
		} catch (ClassNotFoundException e) {
			log.debug("class not found!" + e.toString());
		}

		constr = null;
		try {
			constr = algClass.getConstructor(new Class[] { 
					SearchModel.class, Problem.class });
		} catch (NoSuchMethodException e) {
			log.error("no constructor for algorithm obtained");
			log.error(e.getMessage());
		}

		// we have contructor for alg

		try {
			// want to generalise to SearchAlgorithm
			log.ifNull(constr, "constr");
			log.debug(gad.problem.getClass().toString());
			gad.ga = (SearchAlgorithm) constr.newInstance(new Object[] {
					(SearchModel)gad.model, gad.problem });
			log.ifNull(gad.ga, "gad.ga");
		} catch (InstantiationException e) {
			log.error("cannot instantiate gad.ga");
			log.error(e.getMessage());
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
		} catch (InvocationTargetException e) {
			// this being thrown - why?
			log.error("invocation target exception:"
					+ e.getTargetException().getMessage() + e.getCause().getMessage());
			log.error(e.getMessage());
		}

		// secondly, to initialise the right sort of algorithm
		Method m = null;
		try {
			m = algClass.getMethod("initialise", null);
		} catch (NoSuchMethodException e) {
			log.error(algClass.toString() + " has no initialise() method!");
		} // should never throw!

		try {
			//log.ifNull(gad.ga, "gad.ga");
			m.invoke(gad.ga, null);
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		// sadly, cannot just do this: gad.ga.initialise();

		// write this better to be more flexible and testing for .gac with
		// 'String.endsWith()
		if (argv.length < 3) {
			gad.runInteractive();
		} else
			gad.runBatchMode(Integer.parseInt(argv[1]), argv[2]);
	}

	public void runBatchMode(int runs, String output) {
		log.debug("Sampling " + runs + " runs.");
		log.debug("Output file is " + output);

		maxIterations = Config.getInt("MaxIterations");
		haltingFitness = Config.getDouble("HaltingFitness");

		String outputType = (String) Config.get("OutputType");
		if (outputType.equals("FitnessEvaluation"))
			runFitnessEvaluations(runs, output);
		else if (outputType.equals("MeanTime"))
			runMeanTime(runs, output);
		else
			log.error("Output format '" + outputType + "' not recognised");
		//log.debug(String.valueOf(outputType.equals("meantime")));
		log.debug("Results output to " + output);
	}

	/*
	 * OBSOLETE: but contains useful estimation of time code
	 * 
	 * Produces the ubiquitous fitness-time graph averaged over the specified
	 * number of runs.
	 * 
	 * Two sets of data are produced: mean fitness and best fitness. For each
	 * set the row shows the Iteration (e.g. generation), Evaluations and
	 * Fitness.
	 */
	public void runFitnessTime(int runs, String output) {
		DoubleList sumMeanFitness = new DoubleList();
		DoubleList sumMaxFitness = new DoubleList();

		log.error("GAD.runFitnessTime is obsolete!!!!");
		
		// have to set initially to zero
		for (int i = 0; i < maxIterations; i++) {
			sumMeanFitness.add(0);
			sumMaxFitness.add(0);
		}

		long startTime = System.currentTimeMillis();

		// carry out specified number of runs
		for (int i = 0; i < runs; i++) {
			System.out.print("\nTrial " + (i+1) + " of " + runs);

			// must remember to initialise population and reset GA (in that
			// order!)
			//pop.initialise(Config.getInt("PopulationSize"));
			model.initialise();
			ga.initialise();
			ga.reset();

			// let's go!
			boolean foundMax = false;
			//while(haltingFitness > ga.maxFitness() && ga.getIteration() <
			// maxIterations) {
			//while(ga.getIteration() < maxIterations

			// repeat until we've used all evaluations
			while (ga.getEvaluations() < Config.getInt("MaxEvaluations")) {
				ga.search(1);

				// progress dots
				if (ga.getIteration() % 100 == 0)
					System.out.print(".");

				if (!foundMax && ga.maxFitness() == 1) {
					foundMax = true;
					System.out.println("\nhalting with fitness "
							+ ga.maxFitness() + " after iteration "
							+ ga.getIteration() + " and " + ga.getEvaluations()
							+ " evaluations");
				}
			}

			// show best if we've not found maximum
			if (!foundMax)
				System.out.println("\nbest fitness after "
						+ ga.getEvaluations() + " evaluations is "
						+ ga.maxFitness());

			// estimate remaining time
			long etc = (((System.currentTimeMillis() - startTime) / (i + 1)) * (runs
					- i - 1));
			System.out.println("ETC: "
					+ new Date(etc + System.currentTimeMillis()));

			// add results of latest run to sum
			for (int g = 0; g < ga.getIteration(); g++) {
				/*
				 * double newSumMean = sumMeanFitness.get(g) +
				 * ga.meanFitnessHistory().get(g); sumMeanFitness.set(g,
				 * newSumMean);
				 */
				double newSumBest = sumMaxFitness.get(g)
						+ ga.maxFitnessHistory().get(g);
				sumMaxFitness.set(g, newSumBest);
			}
			//log.debug("sumMaxFItness (end) is " + sumMaxFitness.get((int)ga.getIteration()-1));
		}

		// TODO: justify assumption that the rate of evaluations for a single
		// run is representative of all the runs.
		IntList evaluationHistory = ga.evaluationHistory();

		// write out to file
		int dp = 4; // decimal places
		PrintWriter w;
		try {
			w = new PrintWriter(new FileWriter(output));

			// header info
			DateFormat defaultDate = DateFormat.getDateTimeInstance();
			w.println("# " + defaultDate.format(new Date()));
			Config.writeTo(w);
			w.println("# Time taken : "
					+ (System.currentTimeMillis() - startTime) / 1000
					+ " seconds.");
			w.println("# Mean of " + runs + " runs.");
			w.println("\n# iteration, evaluations, mean fitness");
			// mean mean fitness history
			for (int i = 0; i < ga.getIteration(); i++) {
				String meanString = String
						.valueOf(sumMeanFitness.get(i) / runs);

				if (meanString.length() > (2 + dp))
					meanString = meanString.substring(0, 2 + dp);

				w.println(i + "\t" + evaluationHistory.get(i) + "\t"
						+ meanString);
			}

			w.println("\n\n# iteration, evaluations, max fitness");

			//log.debug("evalHistory.size() = " + evaluationHistory.size());

			// mean max fitness history
			for (int i = 0; i < ga.getIteration(); i++) {
				String meanString = String.valueOf(sumMaxFitness.get(i) / runs);
				if (meanString.length() > (2 + dp))
					meanString = meanString.substring(0, 2 + dp);
				w.println(i + "\t" + evaluationHistory.get(i) + "\t"
						+ meanString);
			}

			// close file
			w.close();

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void runFitnessEvaluations(int runs, String output) {
		//DoubleList sumMaxFitness = new DoubleList();
		double [] sumMaxFitness = new double[maxIterations];
		DoubleList sumNumEvaluations = new DoubleList();
		
		// have to set initially to zero, since they are incremented
		for (int i = 0; i < maxIterations; i++) {
			sumNumEvaluations.add(0);
		}

		long startTime = System.currentTimeMillis();

		// carry out specified number of runs
		for (int run = 0; run < runs; run++) {
			System.out.println("\nExecuting run " + run);

			// must remember to initialise population and reset GA (in that
			// order!)
			//pop.initialise(config.getInt("PopulationSize"));
			model.initialise();
			ga.initialise();
			ga.reset();

			// let's go!
			boolean foundMax = false;
			//while(haltingFitness > ga.maxFitness() && ga.getIteration() <
			// maxIterations) {
			while (ga.getIteration() < maxIterations) {
				ga.search(1);
			}

			if (!foundMax)
				System.out.println("best fitness at generation "
						+ ga.getIteration() + " is " + ga.maxFitness());

			//estimate remaining time	
			long etc = (((System.currentTimeMillis() - startTime) / (run + 1)) * (runs
					- run - 1));
			System.out.println("ETC: "
					+ new Date(etc + System.currentTimeMillis()));
			
			// add results of latest run to sum
			for (int i = 0; i < ga.getIteration(); i++) {
				sumMaxFitness[i] += ga.maxFitnessHistory.get(i);
				sumNumEvaluations.set(i, sumNumEvaluations.get(i) 
							+ ga.evaluationHistory.get(i));
			}
		}

		// TODO: justify assumption that the rate of evaluations for a single
		// run is representative of all the runs.
		//IntList evaluationHistory = ga.evaluationHistory();

		// write out to file
		int dp = 4; // decimal places
		PrintWriter w;
		try {
			w = new PrintWriter(new FileWriter(output));

			// header info
			DateFormat defaultDate = DateFormat.getDateTimeInstance();
			w.println("# " + defaultDate.format(new Date()));
			Config.writeTo(w);
			w.println("# Time taken : "
					+ (System.currentTimeMillis() - startTime) / 1000
					+ " seconds.");
			w.println("# Mean of " + runs + " runs.");
			w.println("\n# mean fitness");
			/*
			 * // mean mean fitness history for(int i = 0; i < maxIterations;
			 * i++) { String meanString = String.valueOf(sumMeanFitness.get(i) /
			 * runs);
			 * 
			 * if(meanString.length() > (2 + dp)) meanString =
			 * meanString.substring(0, 2 + dp);
			 * 
			 * w.println(i + "\t" + evaluationHistory.get(i) + "\t" +
			 * meanString); }
			 */
			w.println("\n\n# max fitness");

			// max evals fitness 
			for (int i = 0; i < ga.getIteration(); i++) {
				String meanString = String.valueOf(sumMaxFitness[i] / runs);
				if (meanString.length() > (2 + dp))
					meanString = meanString.substring(0, 2 + dp);
				//w.println(i + "\t" + ga.fitnessAfter(i) + "\t" + meanString);
				w.println(i + "\t" + (sumNumEvaluations.get(i)/runs) + "\t" + meanString);
			}

			// close file
			w.close();

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void runMeanTime(int runs, String output) {
		long[] runTime = new long[runs];
		double[] finalFitness = new double[runs];

		long startTime = System.currentTimeMillis();

		// carry out specified number of runs
		for (int i = 0; i < runs; i++) {
			System.out.print("Executing run " + i + "...");

			// must remember to initialise population and reset GA (in that
			// order!)
			model.initialise();
			ga.reset();

			// let's go!
			while (haltingFitness > ga.maxFitness()
					&& ga.getIteration() < maxIterations) {
				ga.search(1);
				//ga.evaluateAll();
			}

			// record max fitness at termination
			finalFitness[i] = ga.maxFitness();

			System.out.println("fitness of " + finalFitness[i]
					+ " at iteration " + ga.getIteration());
			runTime[i] = ga.getIteration();
		}

		// Now the stats: this all assumes that maxIterations has not been
		// reached..

		// find mean runtime
		double sum = 0;
		for (int i = 0; i < runs; i++) {
			sum += runTime[i];
		}
		double meanTime = sum / (double) runs;

		// find standard deviation
		sum = 0;
		for (int i = 0; i < runs; i++) {
			sum += Math.pow(meanTime - runTime[i], 2);
		}
		double sdTime = Math.sqrt(sum / (double) (runs - 1));

		// find mean terminating fitness
		sum = 0;
		for (int i = 0; i < runs; i++) {
			sum += finalFitness[i];
		}
		double meanFitness = sum / (double) runs;

		// find standard deviation
		sum = 0;
		for (int i = 0; i < runs; i++) {
			sum += Math.pow(meanFitness - finalFitness[i], 2);
		}
		double sdFitness = Math.sqrt(sum / (double) (runs - 1));

		// display on screen
		System.out.println("Mean runtime is " + meanTime + " (" + sdTime + ")");
		System.out.println("Mean terminating fitness is " + meanFitness + " ("
				+ sdFitness + ")");

		// write out to file
		PrintWriter w;
		try {
			w = new PrintWriter(new FileWriter(output));

			// header info
			DateFormat defaultDate = DateFormat.getDateTimeInstance();
			w.println("# " + defaultDate.format(new Date()));

			Config.writeTo(w);

			w.println("# Time taken : "
					+ (System.currentTimeMillis() - startTime) / 1000
					+ " seconds.");
			w.println("# Mean of " + runs + " runs.");
			/*
			 * just write config object w.println("# Pop. size : " +
			 * config.getInt("PopulationSize")); w.println("# Genome length : " +
			 * config.getInt("GenomeLength")); w.println("# Mutation rate : " +
			 * config.getDouble("MutationRate")); w.println("# Elite ratio : " +
			 * config.getDouble("EliteRatio"));
			 */
			w.println("\n#halting fitness");
			w.println(haltingFitness);

			w.println("\n# mean time of run");
			w.println(meanTime);

			w.println("\n# standard deviation of run-times");
			w.println(sdTime);

			w.println("\n# mean fitness at termination");
			w.println(meanFitness);

			w.println("\n# standard deviation of terminating fitness");
			w.println(sdTime);

			// close file
			w.close();

		} catch (IOException e) {
			log.error(e.getMessage());
		}
		System.out.println(String
				.valueOf((System.currentTimeMillis() - startTime) / 1000)
				+ " seconds elapsed.");
		//System.out.println("\b");// bell
	}

	/**
	 * Extends JDesktopPane with added cool functionality:
	 * <ul>
	 * <li>menus
	 * <li>Simple layout management.
	 * <li>Model and GA linking for notification.
	 * </ul>
	 */

	class ButtonPanel extends JPanel {
		public void addButton(JButton button) {
			button.setHorizontalTextPosition(SwingConstants.CENTER);
			button.setVerticalTextPosition(SwingConstants.BOTTOM);
			button.setBorderPainted(false);
			button.setFocusPainted(false);

			//button.addActionListener(new ActionListener()
			add(button);
		}
	}

	/**
	 * Runs GAD in it's full interactive glory.
	 */
	public void runInteractive() {

		final JFrame frame;
		final ViewPane vPane;
		JMenuBar menuBar;

		ButtonPanel viewPanel;
		JButton fitnessButton;
		JButton matrixButton;
		JButton ADVButton;
		JButton linkageButton;
		JButton TSPButton;

		// Set up the GUI
		frame = new JFrame("GAD: Genetic Algorithm Demonstrator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		vPane = new ViewPane();

		// view panel
		viewPanel = new ButtonPanel();
		viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS));

		if (model instanceof PopulationModel) {
			fitnessButton = new JButton("Fitness", new ImageIcon(
					"FitnessButton.jpg"));
			viewPanel.addButton(fitnessButton);
			fitnessButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new FitnessView((PopulationModel) model,
							(GeneticAlgorithm) ga));
				}
			});
		}

		if (model instanceof PopulationModel) {
			matrixButton = new JButton("Matrix", new ImageIcon(
					"MatrixButton.jpg"));
			viewPanel.addButton(matrixButton);
			matrixButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new MatrixView((PopulationModel) model,
							(GeneticAlgorithm) ga));
				}
			});
		}

		if (model instanceof PopulationModel) {
			ADVButton = new JButton("Allele Diversity", new ImageIcon(
					"ADVButton.jpg"));
			viewPanel.addButton(ADVButton);
			ADVButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new AlleleDiversityView((PopulationModel) model,
							(GeneticAlgorithm) ga));
				}
			});
		}

		if (model instanceof LinkageModel) {
			linkageButton = new JButton("Linkage", new ImageIcon(
					"LinkageButton.jpg"));
			viewPanel.addButton(linkageButton);
			linkageButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new LinkageView((LinkageModel) model,
							(GeneticAlgorithm) ga));
				}
			});
		}

		if (problem instanceof TSProblem) { // not watertight!
			TSPButton = new JButton("TSP", new ImageIcon("TSPButton.jpg"));
			viewPanel.addButton(TSPButton);
			TSPButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new TSPView((PopulationModel) model, problem));
				}
			});
		}

		viewPanel.setSize(new Dimension(100, 100));
		viewPanel.setVisible(true);

		frame.getContentPane().add(viewPanel, BorderLayout.WEST);

		//frame.setLayeredPane(vPane);
		frame.getContentPane().add(vPane, BorderLayout.CENTER);

		// Exit VM with window close
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// set up menubar
		menuBar = new JMenuBar();
		JMenuItem menuItem;

		// different views
		JMenu viewMenu = new JMenu("Views");

		if (model instanceof PopulationModel) {
			menuItem = new JMenuItem("Table");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new DataMatrixView((PopulationModel) model));
				}
			});
			viewMenu.add(menuItem);
		}

		if (model instanceof PopulationModel) {
			menuItem = new JMenuItem("Matrix");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new MatrixView((PopulationModel) model,
							(GeneticAlgorithm) ga));
				}
			});
			viewMenu.add(menuItem);
		}

		if (model instanceof PopulationModel) {
			menuItem = new JMenuItem("Fitness");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new FitnessView((PopulationModel) model,
							(GeneticAlgorithm) ga));
				}
			});
			viewMenu.add(menuItem);
		}

		menuItem = new JMenuItem("Control");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vPane.add(new GeneticAlgorithmControl((GeneticAlgorithm) ga));
			}
		});
		viewMenu.add(menuItem);

		menuItem = new JMenuItem("SANUX");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vPane.add(new SANUXView((GeneticAlgorithm) ga));
			}
		});
		viewMenu.add(menuItem);

		if (model instanceof LinkageModel) {
			menuItem = new JMenuItem("Linkage");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new LinkageView((LinkageModel) model,
							(GeneticAlgorithm) ga));
				}
			});
			viewMenu.add(menuItem);
		}

		menuItem = new JMenuItem("Log View");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				vPane.add(new LogView());
			}
		});
		viewMenu.add(menuItem);

		if (problem instanceof NeuroEvoProblem) {
			menuItem = new JMenuItem("Neuro View");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new NeuroView((PopulationModel) model));
				}
			});
		}

		viewMenu.add(menuItem);

		if (problem instanceof HIFFProblem && model instanceof PopulationModel) {
			menuItem = new JMenuItem("SEAM View");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new SEAMView((PopulationModel) model,
							(GeneticAlgorithm) ga));
				}
			});
			viewMenu.add(menuItem);
		}

		if (model instanceof GeneSetModel) {
			menuItem = new JMenuItem("GeneSet View");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new GeneSetView((GeneSetModel) model, (TransGeneticAlgorithm) ga));
				}
			});
			viewMenu.add(menuItem);
		}

		if (problem instanceof TSProblem) {
			menuItem = new JMenuItem("TSP View");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					vPane.add(new TSPView((PopulationModel) model, problem));
				}
			});
			viewMenu.add(menuItem);
		}

		menuBar.add(viewMenu);
		/*
		JMenu helpMenu = new JMenu("Help");
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
		Control control = new Control((SearchAlgorithm) ga);
		//vPane.add(control);
		LogView logView = new LogView();

		//SANUXView sanuxView = new SANUXView(ga);
		//MatrixView matrixView = new MatrixView(pop, ga);

		control.setLog(logView);

		vPane.add(control);
		//vPane.add(logView);

		//vPane.add(new MatrixView(model, ga));
		//vPane.add(new FitnessView(model, ga));
		//vPane.add(sanuxView);
		//vPane.add(new SEAMView(model, ga));
		//vPane.add(new GeneSetView((GeneSetModel)model, (TransGeneticAlgorithm) ga));

		frame.setBounds(0, 0, 800, 800);
		frame.setVisible(true);
	}
}
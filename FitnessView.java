package gad;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class FitnessView extends ModelView implements ModelListener, AlgorithmListener {
	
	GeneticAlgorithm ga;
	JPanel profile;
	JPanel time;
	JPanel contour;
	JCheckBox scaleContourCB;
	
	long counter;
	
	PopulationModel pop;
	
	public FitnessView(SearchModel model, GeneticAlgorithm ga) {
		
		super(model);
		pop = (PopulationModel)model;
		this.ga = ga;
		setTitle("Fitness View (" + pop.name() + ")");
		setResizable(true);
		
		// set overall layout for this frame
		//	getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		// create and add fitness panels
		tabbedPane.add("Profile", new ProfileFitnessPanel("Fitness Profile"));
		tabbedPane.add("Time", new TimeFitnessPanel("Fitness-Time"));
		//tabbedPane.add("Contour", new ContourFitnessPanel("Contour Fitness"));
		tabbedPane.setSelectedIndex(1); // start with fitness-time since profile already in matrix view
		getContentPane().add(tabbedPane);
		
		setSize(new Dimension(500, 250));
		counter = 0; // still needed?
	}
	
	public void algorithmChanged() {
		repaint();
		log.debug("updating fitness view");
	}
	
	public void modelChanged() {
		repaint();
	}
	
	protected class FitnessPanel extends JPanel {
		
		public FitnessPanel() {
			this("");
		}
		
		public FitnessPanel(String title) {
			setLayout(new BorderLayout());
			setBorder(new TitledBorder(new EtchedBorder(), title));
			setPreferredSize(new Dimension(200, 200));
			setMinimumSize(new Dimension(100,150));
		}
	}
	
	protected class ProfileFitnessPanel extends FitnessPanel {
		
		JCheckBox scaledCB;
		ProfileFitnessGraph profile;
		
		public ProfileFitnessPanel(String title) {
			super(title);
			
			profile = new ProfileFitnessGraph();
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(profile);
			
			JPanel optionsPanel = new JPanel();
			optionsPanel.setLayout(new FlowLayout());
			optionsPanel.setMaximumSize(new Dimension(1000, 20));
			
			JCheckBox sortedCheckBox = new JCheckBox("sorted");
			//optionsPanel.add(sortedCheckBox);
			
			scaledCB = new JCheckBox("scaled", true);
			scaledCB.addActionListener(new optionsListener());
			
			/*
			 
			scaledCB.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {
					//profile.(scaledCB.isSelected());
					repaint();
				}
			}
			);
			*/
			
			optionsPanel.add(scaledCB);
			add(optionsPanel);
		}
		
		public class optionsListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				profile.isScaled = scaledCB.isSelected();
				repaint();
			}
		}
		
		public void update(Graphics g) {
			//profile.setScaled(scaledCB.isSelected());
			paint(g);
		}
		
		public class ProfileFitnessGraph extends JPanel {
			
			public boolean isScaled;
			
			public ProfileFitnessGraph() {
				isScaled = true;
			}
			
			public void setScaled(boolean v) { isScaled = v; }
			
			public void paint(Graphics g) {
				// store min/max fitnesses, since they require computation to find..
				
				double min, max;
				
				if(isScaled) {
					min = ga.minFitness();
					max = ga.maxFitness();
				}
				else {
					min = 0;
					max = 1;
				}
				
				// gap for max and min labels on graph
				int fpGap =  g.getFontMetrics().getHeight();
				
				double xScale = (profile.getWidth()) / (pop.size());
				double yScale = (profile.getHeight() - 2 * fpGap) / (max - min);
				ValueMap fm = ga.getFitnessMap();
				
				// clear display
				g.setColor(Color.white);
				g.fillRect(0, 0, profile.getWidth(), profile.getHeight());
				/*
				
				// draw elite-carry zone
				g.setColor(Color.lightGray);
				int tw = (int)(xScale * Math.ceil(pop.getSize() * (1.0 - ga.getEliteRatio())));// graphical width of zone;
				g.fillRect(getWidth() - tw, 0,
						tw,
						(int)(getHeight() - fpGap));
				*/
				// plot fitnesses of current population -- careful!
				for(int i = 0; i < pop.size(); i++) {
					double f = fm.get(pop.getIndividual(i)); // fitness of individual i
					/* if(i <= (int)(ga.getEliteRatio() * pop.getSize())) {// elite group
				
					g.setColor(Color.lightGray);
					g.fillRect((int)(i * xScale), 
							getHeight() - (int)((fp[i] - min) * yScale + fpGap), 
							(int)xScale,
							(int)((fp[i] - min) * yScale));
				}
				*/
				if(pop.isSampling(i)) g.setColor(Color.gray);
				else g.setColor(Color.lightGray);
				
				if(xScale >= 2) g.fillRect((int)(i * xScale), 
						getHeight() - (int)((f - min) * yScale + fpGap), 
						(int)xScale - 1,
						(int)((f - min) * yScale));
				else g.drawLine((int)(i * xScale),
						getHeight() - fpGap, 
						(int)(i * xScale),
						getHeight() - (int)((f - min) * yScale) - fpGap);
			}
			
			
			// plot selection probabilities
			ValueMap sp = ga.getSelectionProbabilities();
			if(sp != null) {
				
				// get max value for selection probability
				double spMax = sp.maxValue(); 
			
				double pScale = (getHeight() - 2 * fpGap) / spMax; // selection probability scaling
				// plot it
				g.setColor(new Color(255, 0, 0, 100));
				for(int i = 0; i < pop.size(); i++) {
					g.fillRect((int)(i * xScale),
							profile.getHeight() - (int)(sp.get(pop.getIndividual(i)) * pScale) - fpGap,
							(int)xScale,
							(int)(sp.get(pop.getIndividual(i)) * pScale));
					
				}
			}
			
			// label max on profile
			g.setColor(Color.black);
			String maxString = String.valueOf(max);
			if(maxString.length() > 6) maxString = maxString.substring(0, 5);// to 3sf
			g.drawString(maxString, 0, fpGap);
			
			// label min on profile
			String minString = String.valueOf(min);
			if(minString.length() > 6) minString = minString.substring(0, 5);// to 3sf
			g.drawString(minString, 0, profile.getHeight());
			
			// mark and label mean on profile
			g.setColor(Color.black);
			int meanPos = getHeight() - 
			(int)((ga.meanFitness() - min) * yScale) - fpGap;
			String meanString = String.valueOf(ga.meanFitness());
			if(meanString.length() > 6) meanString = meanString.substring(0, 5);// to 3sf
			g.drawLine(0, meanPos, profile.getWidth(), meanPos);
			
			g.drawString(meanString, 
					profile.getWidth() - g.getFontMetrics().stringWidth(meanString), 
					meanPos);
		}
	}
}

public class TimeFitnessPanel extends FitnessPanel {
	
	JCheckBox meanRangeCB; // instead of just sampling
	boolean meanRange;
	JLabel legend;
	
	public TimeFitnessPanel(String title) {
		super(title);
		time = new TimeFitnessGraph();
		add(time, BorderLayout.CENTER);
		legend = new JLabel("<html><center><font color=white>-max</font> <font color=blue>-mean</font> <font color=black>-min</font></center></html>");
		add(legend, BorderLayout.SOUTH);
		
		meanRange = false;
	}
	
	public class TimeFitnessGraph extends JPanel {
		
		public void paint(Graphics g) {
			// simple fitness-time graph.
			// BEWARE : contains unpleasant scenes of code duplication!
			int height = time.getHeight();
			int yScale = (int)(height / (ga.allTimeMax() - ga.allTimeMin()));
			g.clearRect(0, 0, time.getWidth(), height);
			DoubleList mf = ga.meanFitnessHistory();
			DoubleList bf = ga.maxFitnessHistory();
			DoubleList wf = ga.minFitnessHistory();
			
			int historyLength = Math.max(Math.max(bf.size(), wf.size()), mf.size());
			//log.debug("history length = " + historyLength);
			if(historyLength < 1) return; // nothing to paint!
			
			// update label
			StringBuffer label = new StringBuffer("<html>");
			
			String fitnessString; // to truncate if necessary
			
			fitnessString = String.valueOf(bf.getLast());
			if(fitnessString.length() > 5) fitnessString = fitnessString.substring(0, 5);
			if(bf.size() > 1) label.append("<font color=white>max: " 
					+ fitnessString + "</font>&nbsp; &nbsp;");
			fitnessString = String.valueOf(mf.getLast());
			if(fitnessString.length() > 5) fitnessString = fitnessString.substring(0, 5);
			if(mf.size() > 1) label.append("<font color=blue>mean: " 
					+ fitnessString + "</font>&nbsp; &nbsp;");
			fitnessString = String.valueOf(wf.getLast());
			if(fitnessString.length() > 5) fitnessString = fitnessString.substring(0, 5);
			if(wf.size() > 1) label.append("<font color=black>min: " 
					+ fitnessString+ "</font>");
			
			label.append("</html>");
			/*
			
			legend.setText("<html><font color=white>max: " 
					+ String.valueOf(bf.getLast()).substring(0, 5)
					+ "</font>&nbsp; &nbsp; <font color=blue>mean: " 
					+ String.valueOf(mf.getLast()).substring(0, 5)
					+ "</font>&nbsp; &nbsp; <font color=black>min: " 
					+ String.valueOf(wf.getLast()).substring(0, 5)
					+ "</font></html>");
			*/
			//int m0 = (int)mf.get(0);
			
			int m0, m1;
			int b0, b1;
			int w0, w1;
			
			if(bf.size() > 0) b0 = (int)bf.get(0);
			
			double tstep = time.getWidth() / (double)historyLength; // xstep per epoch
			int epochStep = (int)(Math.max(1, historyLength / (double)time.getWidth()));
			for(int i = epochStep; i < historyLength; i+= epochStep) {
				/* 
				
				if(meanRange) {
					double summ = 0;
					for(int j = i; i < i + epochStep; i++) {
						summ += mf.get(j);
					}
					
					m1 = height - (int)(((summ / epochStep) - ga.allTimeMin()) * yScale);
				}
				else {
					m0 = height - (int)((mf.get(i - epochStep) - ga.allTimeMin()) * yScale);
					m1 = height - (int)((mf.get(i) - ga.allTimeMin()) * yScale);
				}
				
				// plot mean fitness
				if(i < mf.size()) {
					g.setColor(Color.blue);
					g.drawLine((int)((i - epochStep) * tstep), m0, (int)(i * tstep), m1);
				}
				*/
				if(i < mf.size()) {
					g.setColor(Color.blue);
					g.drawLine((int)((i - epochStep) * tstep), 
							height - (int)((bf.get(i - epochStep) - ga.allTimeMin()) * yScale), 
							(int)(i * tstep), 
							height - (int)((bf.get(i) - ga.allTimeMin()) * yScale));
				}
				
				// plot worst fitness
				if(i < wf.size()) {
					g.setColor(Color.black);
					g.drawLine((int)((i - epochStep) * tstep), 
							height - (int)((wf.get(i - epochStep) - ga.allTimeMin()) * yScale), 
							(int)(i * tstep), 
							height - (int)((wf.get(i) - ga.allTimeMin()) * yScale));
				}
				
				// plot best fitness
				if(i < bf.size()) {
					g.setColor(Color.white);
					g.drawLine((int)((i - epochStep) * tstep), 
							height - (int)((bf.get(i - epochStep) - ga.allTimeMin()) * yScale), 
							(int)(i * tstep), 
							height - (int)((bf.get(i) - ga.allTimeMin()) * yScale));
				}
				//m1 = m0;
			}
		}
	}
	// TODO: scaling checkbox
}

public class ContourFitnessPanel extends FitnessPanel {
	
	public ContourFitnessPanel(String title) {
		super(title);
		contour = new ContourFitnessGraph();
		add(contour, BorderLayout.CENTER);
		
		scaleContourCB = new JCheckBox("auto-scale", true);
		add(scaleContourCB, BorderLayout.SOUTH);
	}
	
	public class ContourFitnessGraph extends JPanel {
		
		public void paint(Graphics g) {
			g.setColor(Color.white);
			g.fillRect(0, 0, profile.getWidth(), profile.getHeight());
			g.setColor(Color.black);
			
			// store min/max fitnesses, since they require computation to find..
			double min = ga.minFitness();
			double max = ga.maxFitness();
			ValueMap fm = ga.getFitnessMap();
			
			double xStep = profile.getWidth() / 100.0;
			int yStep = (int)(contour.getHeight()/pop.size());
			float col;
			
			for(int i = 1; i < pop.size(); i++) {
				double f = fm.get(pop.getIndividual(i)); // fitness of individual i
				// scale colour range if specified
				// TODO: increase range by going across hues
				if(scaleContourCB.isSelected() && max != min) 
					col = (float)((f - min) / (max - min));
				else col = (float)f;
				
				// set color and plot bar
				g.setColor(new Color((float)0, (float)0, col));
				int xPos = (int)(counter * xStep) % contour.getWidth();
				// plot ahead -- overwriting previous contour
				g.fillRect(xPos, (int)((i - 1) * yStep), 
						contour.getWidth(), (int)(i * yStep));
			}	
		counter++;
	    }
	}
    }
}

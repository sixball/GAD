/*
 * Created on 15-Jul-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package gad;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

/**
 * @author Simon
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class GeneSetView extends ModelView {

	GeneSetModel model;
	TransGeneticAlgorithm tga;

	/**
	 * @param config
	 */
	public GeneSetView(GeneSetModel model, TransGeneticAlgorithm tga) {
		super();
		this.model = model;
		this.tga = tga;
		// TODO Auto-generated constructor stub

		setTitle("GeneSet View");
		setSize(new Dimension(400, 600));

		JTabbedPane tabbedPane = new JTabbedPane();
		
		MatrixGraph mg = new MatrixGraph(model.getGeneSet());
		CreditProfile cp = new CreditProfile();
		MatrixGraph eg = new MatrixGraph(tga.getEvalSet());
		
		//getContentPane().add(new MatrixGraph(), "Center");

		//getContentPane().add(new CreditProfile(), "East");
		tabbedPane.add("EvalSet", eg);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mg, cp);
		tabbedPane.add("Gene Pool", splitPane);
		getContentPane().add(tabbedPane);
	}

	public SearchModel getModel() {
		return model;
	}

	public void modelChanged() {
		model.sortForView();
		//log.debug("pool:\n" + model.toString());
		repaint();
	}

	public class MatrixGraph extends JPanel {
		GeneSet gs;
		
		public MatrixGraph(GeneSet gs) {
			this.gs = gs;
			setMinimumSize(new Dimension(100, 100));
			setPreferredSize(new Dimension(300, 400));
		}

		public void paint(Graphics g) {
			if(gs == null) return;
			int size = gs.size();
			int length = Config.getInt("GenomeLength");
			if (size == 0) {
				log.debug("Empty pool in GeneSetView.MatrixGraph.paint()");
			//if(pop.length() == 0) log.debug("zero length population in
			// MatrixGraph.paint()");
				return;
			}
			double yScale = getHeight() / size;
			double xScale = getWidth() / (double) length;

			for (int i = 0; i < size; i++) {

				PartialIntGenome indy = gs.get(i).flatten();
				for (int j = 0; j < length; j++) {

					int val = indy.get(j);

					//if(!pop.isSampling(i)) col = col.darker();
					//col = col.brighter();

					// allele
					if (val < 0) { // interpreted as unspecified
						g.clearRect((int) (j * xScale), (int) (i * yScale),
								(int) Math.ceil(xScale), (int) Math
										.ceil(yScale));
					} else {
						Color col = getColor(val);
						g.setColor(col);
						g.fillRect((int) (j * xScale), (int) (i * yScale),
								(int) Math.ceil(xScale), (int) Math
										.ceil(yScale));
					}
				}
			}
			/*
			 * g.setColor(Color.black); g.drawRect(0, (int)(pop.getHighlight() *
			 * yScale) - 1, getWidth() - 1, (int)(yScale));
			 */
		}
	}

	private Color getColor(int c) {
		// calculate color and return it
		// uses range of hue AND intensity for colour-blindness
		int range = IntGenome.MAX - IntGenome.MIN; // ignoring negative values

		if (range < 5) {

			Color[] col = new Color[] { Color.black, Color.white, Color.red,
					Color.blue, Color.green };
			return col[c];
		}

		return Color.getHSBColor((float) ((c / (double) range) * 0.75),
				(float) 0.8, (float) 1.0);//(float)((c / (double)range)));
	}
	
	protected class CreditProfile extends JPanel {

		public boolean isScaled;
		
		public CreditProfile() {
		    isScaled = true;
		    //setMinimumSize(new Dimension(50, pop.getSize()));
			int pixelsPerRow = Math.round(500 / model.size());
		    setSize(new Dimension(300, pixelsPerRow * model.size()));
			setPreferredSize(new Dimension(100, 400));
		}
		
		public void setScaled(boolean v) { isScaled = v; }
		    
		    public void paint(Graphics g) {
		    // store min/max fitnesses, since they require computation to find.
			double min, max;
			
			CreditMap cm = tga.credit;
			if(cm == null) return;
			//else log.debug("plotting fitnesses in pool:\n" + cm);

			// should be sorted or no
			//tga.sort(fm);

			if(isScaled) {
			    min = tga.minFitness();
			    max = tga.maxFitness();
			}
			else {
			    min = 0;
			    max = 1;
			}
			
			// NOTE setting min to 0 and max to, well max, to see progress
			min = 0;
			max = Config.getInt("GenomeLength") * 2 - 1;
			
			double xScale = getWidth() / (max - min);
			double yScale = getHeight() / model.size();

			// clear display
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());
			//ga.checkFitnessMap("pre plot fitnesses in matrixview paint");
			// plot fitnesses of current population -- careful!
			for(int i = 0; i < model.size(); i++) {
				if(!cm.credits(model.get(i))) break;
				log.debug("plotting fitness (" + cm.get(model.get(i)) + ") for " + model.get(i));
				double f = cm.get(model.get(i)); // fitness of individual i
			  //if(model.isSampling(i)) g.setColor(Color.gray);
			  //else 
				g.setColor(Color.lightGray);
			  if(yScale >= 2) g.drawRect((int)(0),//(fp[i] - min) * xScale),
						     (int)(i * yScale), 
						     (int)((f - min) * xScale),
						     (int)(yScale) - 1);
			  else g.drawLine((int)(0),//(fp[i] - min) * xScale), 
					  (int)(i * yScale),
					  (int)((f - min) * xScale),
					  (int)(i * yScale));
			}

			//ga.checkFitnessMap("pre get selectionprob in matrixview paint");
			
			/* plot selection probabilities
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
			*/
		    }
	    }

}
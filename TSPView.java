package gad;


import java.awt.*;
import javax.swing.*;

public class TSPView extends PopulationView implements ModelListener {

    JLabel cost;
    JTextField source;

    TSProblem prob;

    public TSPView(PopulationModel pop, Problem problem) {
	super(pop);
	this.prob = (TSProblem)problem;
	setTitle("TSP View");
	setResizable(true);
	setSize(new Dimension(200, 200));
	getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
	getContentPane().add(new RouteGraph());
	JPanel bottomPanel = new JPanel();
	bottomPanel.setMaximumSize(new Dimension(1000, 20));
	source = new JTextField(prob.getSource());
	source.setEditable(false);
	    /*
	      addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	      ga.loadCities(source.getText(), 100);
	      log.makeEntry("Source is " + ga.getSource());
	      }
	      });
	    */
	bottomPanel.add(source);
	cost = new JLabel("");
	bottomPanel.add(cost);
	getContentPane().add(bottomPanel);
    }

    public void modelChanged() {
     	repaint();
    }

    public class RouteGraph extends JPanel {

	public RouteGraph() {
	    setPreferredSize(new Dimension(300,300));
 	}

	public void update(Graphics g) {
	    paint(g); // no need to clear
	}

	public void paint(Graphics g) {
	    g.setColor(Color.white);
	    g.fillRect(0, 0, getWidth(), getHeight());

	    double x[] = prob.getX();
	    double y[] = prob.getY();

	    if(pop == null || x == null || y == null) return;
	    
	    // find max and min for scaling
	    double minX = x[0];
	    double maxX = x[0];
	    double minY = y[0];
	    double maxY = y[0];
	    
	    for(int i = 0; i < x.length; i++) {
		if(minX > x[i]) minX = x[i];
		if(maxX < x[i]) maxX = x[i];
		if(minY > y[i]) minY = y[i];
		if(maxX < y[i]) maxY = y[i];
	    }	    

	    double xscale = getWidth() / (maxX - minX);
	    double yscale = getHeight() / (maxY - minY);

	    // paint fittest
	    TSPGenome route = (TSPGenome)pop.getIndividual(pop.getHighlight());
	
	    //log.debug("route length = " + route.length());

	    for(int c = 1; c < route.length(); c++) {
		int i = route.get(c-1);
		int j = route.get(c);
		int x1 = (int)Math.floor(x[i] * xscale);
		int x2 = (int)Math.floor(x[j] * xscale);
		int y1 = (int)Math.floor(y[i] * yscale);
		int y2 = (int)Math.floor(y[j] * yscale);

		g.setColor(Color.blue);
		g.fillOval((int)(x[c] * xscale) - 2, (int)(y[c] * yscale) - 2, 5, 5);
	    
		g.setColor(Color.black);
		g.drawLine(x1, y1, x2, y2);
	    }
	    
	    int i = route.get(route.length() - 1);
	    int j = route.get(0);
	    int x1 = (int)(x[i] * xscale);
	    int x2 = (int)(x[j] * xscale);
	    int y1 = (int)(y[i] * yscale);
	    int y2 = (int)(y[j] * yscale);
	    
	    g.drawLine(x1, y1, x2, y2);

	    cost.setText(String.valueOf(prob.cost(route)).substring(0, 7));
  	}
    }
}

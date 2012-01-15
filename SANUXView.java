package gad;


import java.awt.*;
import javax.swing.*;


public class SANUXView extends AlgorithmView implements AlgorithmListener
{
	SANUXGeneticAlgorithm sga; // this shadows al in SearchAlgorithm
	
	JComboBox swapCombo;
	int swapFunction;
	
	public SANUXView(GeneticAlgorithm ga) 
	{
		super(ga);
		al = ga; // it's an Algorithm but is it a SANUXGenetic Algorithm?
		referGA(ga); 
		setTitle("SANUX View");
		//screws up width() : getContentPane().setLayout(new FlowLayout());
		//getContentPane().setLayout(new BorderLayout());
		setPreferredSize(new Dimension(300, 100));      
		setResizable(true);
		setClosable(true);
		getContentPane().add(new SANUXGraph());
		
		swapCombo = new JComboBox(new String[]{"Triangular", "Trapezoid", "Exponential"});
		getContentPane().add(swapCombo, BorderLayout.SOUTH);
		
	}
	
	public boolean referGA(GeneticAlgorithm ga) 
	{
		try {
			if(Class.forName("GAD.SANUXGeneticAlgorithm").isInstance(ga)) {
				sga = (SANUXGeneticAlgorithm)ga;
				return true;
			}
		} catch(ClassNotFoundException e) { log.error("class not found");}
		
		return false;
	}
	
	public void algorithmChanged() 
	{
		repaint();
	}
	
	
	public void update(Graphics g) 
	{
		paint(g);
	}
	
	private class SANUXGraph extends JPanel 
	{
		public SANUXGraph() 
		{
			setMinimumSize(new Dimension(100, 20));
			setPreferredSize(new Dimension(300, 100));
		}
		
		public void paint(Graphics g) 
		{
			g.clearRect(0, 0, getWidth(), getHeight());
			
			if(sga == null) {
				g.drawString("No SANUX GA assigned.", 0, (int)(getHeight() / 2));
				return;
			}
			
			// TODO: have drop-down list of different functions
			ProbArray pd = sga.swappingProbability(SANUXGeneticAlgorithm.TRIANGULAR_FUNCTION);
			
			double xScale = getWidth() / (double)pd.length();
			
			g.setColor(Color.white);
			g.fillRect(0, 0, getWidth(), getHeight());
			
			g.setColor(Color.lightGray);
			g.drawLine(0, (int)getHeight()/2, getWidth(), (int)getHeight()/2); 
			
			g.setColor(Color.black);
			for(int i = 0; i < pd.length() - 1; i++) 
			{
				g.drawLine((int)(i * xScale),
						(int)(pd.get(i) * getHeight()),
						(int)((i + 1) * xScale),
						(int)(pd.get(i + 1) * getHeight()));
			}
		}
	}
}



package gad;


import java.awt.*;
import javax.swing.*;



public class NeuroView extends PopulationView implements ModelListener {
  Organism grid;
  int gridSize;

  public NeuroView(PopulationModel pop) {
    super(pop);
    gridSize = Config.getInt("GridSize");
    modelChanged();
    
    setTitle("Neuro View");
    setPreferredSize(new Dimension(450, 250));

    getContentPane().add(new NeuroPanel());
  }

  class NeuroPanel extends JPanel 
  {
    
    // lil helper method
    private int com(int i, int j, int p) 
    {
      return (int)(255*((grid.getCell(i,j)).getProtein(p)));
    }
    
    public void paint(Graphics g) {

      int screenSize = Math.min(getHeight(), getWidth());
      int scale =  screenSize / gridSize;

      for(int i=0; i<gridSize; i++) {
	for(int j=0; j<gridSize; j++) {

	  // what do these protein level represent? anything?

	  g.setColor(new Color(com(i, j, 0), com(i, j, 1), com(i, j, 2)));
	  g.fillOval(i*scale, (j*scale), scale, scale);

	  g.setColor(new Color(com(i, j, 5), com(i, j, 6), com(i, j, 7)));
	  g.fillOval((i*scale) + screenSize, (j*scale), scale, scale);

	  g.setColor(new Color(com(i, j, 3), com(i, j, 4), 0));
	  g.fillOval((i*scale) +(2*screenSize), (j*scale), scale, scale);

	  //show living cells
	  g.setColor(Color.black);
	  if(grid.getCell(i,j).CellAlive()) 
	    g.drawOval(i*scale, (j*scale), scale, scale);
			
				
	  //draw axons
	  //g.setColor(Color.black);
	  //if(grid.getCell(i,j).CellAlive() && grid.getCell(i,j).AxonProduced()){
	  //	g.drawLine(i*scale + screenSize/(2*gridSize), (j*scale) + 50 + screenSize/(2*gridSize) , grid.getCell(i,j).getAxonX()*scale + screenSize/(2*gridSize) , grid.getCell(i,j).getAxonY()*scale + screenSize/(2*gridSize) );
	  //}
	}
      }			
    }
  }
  
  public void modelChanged() {
    grid = new Organism((NeuroGenome)pop.getIndividual(pop.getHighlight()));
    repaint();
  }
}

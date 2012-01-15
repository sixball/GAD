package gad;


import javax.swing.*;

public class ViewPane extends JDesktopPane 
{
	Logger log;
	int x, y;
	
	public ViewPane() {
		log = new Logger();
	}
	
	public void add(View view) {
		super.add(view);
		view.setVisible(true);
		view.pack();
		
		// position view
		view.setLocation(x, y);
		 
		if(x+view.getWidth() > y+view.getHeight()) 
		{
			y += view.getHeight();
		}
		else x += view.getWidth();
		
		//log.debug("parent is " + view.getClass().getSuperclass().getName());
		/*
		if(view.getClass().getSuperclass().getName() == "GAD.PopulationView") {
			PopulationView pView = (PopulationView)view;
			if(pView.getPopulation() != null) {
				pView.getPopulation().addListener(pView);
			}
			else log.debug("pop is null");
		}*/
		if(view instanceof ModelView) {
			ModelView mView = (ModelView)view;
			if(mView.getModel() != null) {
				mView.getModel().addListener(mView);
				log.debug("adding "+ view.getClass().getName());
			}
			else log.debug("model is null");
		}
		
		//if(view.getClass().getSuperclass().getName() == "AlgorithmListener") {
		if(AlgorithmView.class.isAssignableFrom(view.getClass())) {
			//log.debug("noting that " + view.getClass().getName() + " is sub-class of AlgorithmView");
			AlgorithmView av = (AlgorithmView)view;
			if(av.getAlgorithm() != null)
				av.getAlgorithm().addListener(av);
			else log.debug("algorithm is null");
		}
	}
  }

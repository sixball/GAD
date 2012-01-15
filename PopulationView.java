package gad;


public abstract class PopulationView extends ModelView implements ModelListener{
	
	PopulationModel pop;
	//Logger log;
	
	protected PopulationView() {
		super();
		// deprecated, but needs to be called by sub-classes (apparently!)
	}
	
	public PopulationView(PopulationModel pop) {
		super();
		this.pop = pop;
		//log = new Logger();
	}
	
	public PopulationModel getPopulation() 
	{  
		return pop;
	}
}

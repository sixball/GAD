/*
 * Created on 26-Jul-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gad;


/**
 * @author Simon
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class ModelView extends View {

	SearchModel model;
	
	/**
	 * 
	 */
	public ModelView() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ModelView(SearchModel model) {
		super();
		this.model = model;
		//log = new Logger();
	}
	
	public SearchModel getModel() {
		return model;
	}
	
	public abstract void modelChanged();
}

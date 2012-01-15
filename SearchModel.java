/*
 * Created on 18-Jul-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gad;


import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Random;


/**
 * @author Simon
 *
 * This represents the <i>state</i> of the search, 
 * e.g. the population for the Genetic Algorithm.
 */

public class SearchModel {
	
	ArrayList views;
	
	static Logger log = new Logger();
	static Random r = new Random();

	//public SearchModel() {}
	
	public SearchModel() {
		views = new ArrayList();
	}
	
	public void initialise() {}

	public void addListener(ModelView listener) {
		views.add(listener);
	}

	public void notifyListeners() {
		for (ListIterator i = views.listIterator(); i.hasNext();) {
			((ModelView) i.next()).modelChanged();
			//log.debug("notifying view");
		}
	};
}

package gad;

/**
   helpful wrapper of ArrayList especially for doubles.
 */

import java.util.*;

public class DoubleList {

    ArrayList list;

    public DoubleList() {
	list = new ArrayList();
    }
    
    public double get(int i) {
	return ((Double)list.get(i)).doubleValue();
    }

    public double getLast() {
	if(size() < 1) return 0;
	return get(size() - 1);
    }

    public void set(int i, double d) {
	list.set(i, new Double(d));
    }
		
    public void add(double d) {
	list.add(new Double(d));
    }

    public int size() {
	return list.size();
    }

    public void clear() {
	if(list != null)
	    list.clear();
    }
}

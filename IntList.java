package gad;

/**
   helpful wrapper of ArrayList especially for doubles.
 */

import java.util.*;

public class IntList {

    ArrayList list;

    public IntList() {
	list = new ArrayList();
    }
    
    public int get(int i) {
	return ((Integer)list.get(i)).intValue();
    }

    public double getLast() {
	if(size() < 1) return 0;
	return get(size() - 1);
    }

    public void set(int i, int v) {
	list.set(i, new Integer(v));
    }
		
    public void add(int v) {
	list.add(new Integer(v));
    }

    public int size() {
	return list.size();
    }

    public void clear() {
	if(list != null)
	    list.clear();
    }
}

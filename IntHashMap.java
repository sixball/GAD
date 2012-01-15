package gad;

import java.util.*;


public class IntHashMap extends HashMap {

    HashMap data;

    public IntHashMap() {
	data = new HashMap();
    }

    public void put(int key, int value) {
	data.put(new Integer(key), new Integer(value));
    }

    public int get(int key) {
	return ((Integer)data.get(new Integer(key))).intValue();
    }
}

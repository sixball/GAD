/*
 * Created on 22-Jun-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package gad;

import java.util.ArrayList;
/**
 * @author Simon
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
class GenomeList {
	ArrayList list;
	
	public GenomeList() {
		list = new ArrayList();
	}
	
	public GenomeList(int size) {
		list = new ArrayList(size);
	}
	
	// what the hell is this?
	//private Object get(Object o) { return null;}
	
	public Genome get(int i) {
		if(i >= getSize()) {
			System.out.println("you're asking for individual index "+i+"? But there's only " + list.size() +" in the population!");
			return null;
		}
		return (Genome)list.get(i);
	}
	
	public void set(Genome g, int i) {
		list.set(i, g);
	}
	
	public boolean add(Genome g) {
		return list.add(g);
	}
	
	public void remove(Genome g) {
		list.remove(g);
	}
	
	public void remove(int i) {
		list.remove(i);
	}
	
	public int getSize() {
		return list.size();
	}
	
	public void clear() {
		list.clear();
	}
}
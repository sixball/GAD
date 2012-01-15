package gad;

/**
   Displays Population Matrix
*/


import java.awt.*;
import javax.swing.*;

class DataMatrixView extends PopulationView implements ModelListener {
    
    JTable matrix;

    public DataMatrixView(PopulationModel pop) {
	super(pop);

	setTitle("Data Matrix View");

	setResizable(true);
	setClosable(true);
	
	matrix = new JTable(pop.size(), length);
	//	matrix.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	matrix.setColumnSelectionAllowed(true);
	
	JScrollPane matrixPane = new JScrollPane(matrix);
	matrixPane.setPreferredSize(new Dimension(200,100));
	
	matrixPane.setHorizontalScrollBarPolicy(
			   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	getContentPane().add(matrixPane);
 
	modelChanged();
    }

    public void modelChanged() {
	for(int i = 0; i < pop.size(); i++) {
	    Genome indy = pop.getIndividual(i);

	    for(int locus = 0; locus < length; locus++) {
		try {
		    if(Class.forName("IntGenome").isInstance(indy)){
			matrix.setValueAt(new Integer(((IntGenome)indy).get(locus)), i, locus);
		    } 
		} catch (ClassNotFoundException e) {}
	    }
	}
    }
}

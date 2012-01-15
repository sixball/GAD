package gad;


import java.awt.event.*;
import javax.swing.*;

public class RoyalRoadGAPanel extends JPanel {

    RoyalRoadGA ga;
    JTextField kTF;
    JButton initialiseB;

    LogView log;

    public RoyalRoadGAPanel(RoyalRoadGA theGA, LogView log) {
	ga = theGA;
	this.log = log;

	/*
	  Set up the GUI
	*/
	//initialiseB = new JButton("Initialise Royal Road");

	//add(initialiseB);

	add(new JLabel("sections"));
	
	kTF = new JTextField(String.valueOf(ga.numSections()), 3);
		kTF.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    //ga.initialise(Integer.parseInt(kTF.getText()));
		    //		    log.makeEntry("Using royal road with " + ga.numSections() + " sections.");
		    //    notifyListeners();
		}
	    });
		//add(kTF);
    }

    public void setLog(LogView log) {
	this.log = log;
    }
  /*
    public static void main(String[] arg) {
	JFrame f = new JFrame("NKGAPanel Test");
	//	NKGA ga = new NKGA();
	NKGAPanel obj = new NKGAPanel(new NKGA(new Population(10)));
	f.getContentPane().add(obj);
	f.pack();
	f.show();
    }
  */
}

package gad;


import java.awt.event.*;
import javax.swing.*;

public class NKGAPanel extends JPanel {

    NKGA ga;
    JTextField kTF;
    JButton initialiseB;

    LogView log;

    Config config;

    public NKGAPanel(NKGA theGA, Config config) {
	ga = theGA;
	this.config = config;

	/*
	  Set up the GUI
	*/
	initialiseB = new JButton("Initialise NK");
	initialiseB.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    updateK();
		    ga.initialise();
		    //log.makeEntry("New NK landscape generated.");
		    //    notifyListeners();
		}
	    });
	add(initialiseB);

	add(new JLabel("k"));
	
	kTF = new JTextField(String.valueOf(ga.getK()), 3);
	
	add(kTF);
    }

    public void updateK() {
	config.set("K", new Integer(Integer.parseInt(kTF.getText())));
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

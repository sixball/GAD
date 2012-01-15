package gad;

/*
  NOTE: This is distinct form the debugging log (Logger)
*/

import java.awt.*;
import javax.swing.*;

// TODO: proper LogEntries (with formatting)
public class LogView extends View {

    StringBuffer logfile;

    JTextField textField;

    public LogView() {
      super();
      
      logfile = new StringBuffer();
      textField = new JTextField();
      
      setTitle("Log");
      setResizable(true);
      setPreferredSize(new Dimension(300, 200));
      
      getContentPane().add(new JScrollPane(textField, 
					   ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					   ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
    }

    public void makeEntry(String entry) {
	logfile.append(entry + "\n");
	textField.setText(logfile.toString());
	// scroll to bottom
    }
}
    

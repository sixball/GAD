package gad;


import javax.swing.*;

public class View extends JInternalFrame {
	Logger log;

	static int x, y;

	int length;

	public View() {
		length = Config.getInt("GenomeLength");
		log = new Logger();
		setLocation(x += 30, y += 40);
		setClosable(true);
		setResizable(true);
	}
}
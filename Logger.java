package gad;

public class Logger {

	private static StringBuffer logfile;

	public static int loglevel = 1; // 0 is log nowt

	public Logger() {
		if (logfile == null) {
			logfile = new StringBuffer();
			// log("Started Logging with log-level " + loglevel + ":", 0);
		}
	}

	private void log(String mesg, int level) {
		if (loglevel > level) {
			logfile.append(mesg + "\n");
			System.out.println(mesg);
		}
	}

	public void report(String mesg) {
		log(mesg, 10);
	}

	public void error(String mesg) {
		error(mesg, 0);
	}

	public void error(String mesg, int level) {
		log("ERROR: " + mesg, level);
	}

	public void debug(String mesg) {
		debug(mesg, 0);
	}

	public void debug(String mesg, int level) {
		log("DEBUG: " + mesg, level);
	}

	public void ifNull(Object obj, String name) {

		if (obj == null)
			error(name + " is null"); //.. more info would be good
	}

	public String toString() {
		return logfile.toString();
	}
}
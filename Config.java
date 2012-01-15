package gad;


import java.io.*;
import java.util.*;

public class Config {

	static HashMap param = new HashMap();
	static Logger log = new Logger();

	public Config() {
		param = new HashMap();
	}

	public static void readFrom(String fileName) {
		//param = new HashMap(); allow for incremental setting

		// open file
		StreamTokenizer st;
		try {
			st = new StreamTokenizer(new FileReader(fileName));
		} catch (IOException e) {
			log.debug(e.getMessage());
			return;
		}

		st.commentChar('#');
		//st.eollsSignificant(true); compiler does not recognise!
		//st.lowerCaseMode(true);

		// load into hashmap
		try {
			int t = st.nextToken();
			while (t != StreamTokenizer.TT_EOF) {
				String name = st.sval;
				t = st.nextToken();
				// double value;
				if (t == StreamTokenizer.TT_NUMBER) {
					log.debug("attribute " + name + " has value " + st.nval);
					param.put(name, new Double(st.nval));
				}
				// string value
				if (t == StreamTokenizer.TT_WORD) {
					log.debug("attribute " + name + " has value " + st.sval);
					param.put(name, st.sval);
				}
				t = st.nextToken();
			}
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
	}

	public static void set(String name, Object value) {
		param.put(name, value);
	}

	public static void set(String name, int value) {
		set(name, new Double(value));
	}

	public static Object get(String name) {
		Object obj = param.get(name);
		if (obj == null)
			log.error("No configuration information for " + name);
		return obj;
	}

	public static int getInt(String name) {
		//log.debug("getting config for " + name);
		//log.debug(get(name).getClass().toString());
		//log.debug(((Integer)get(name)).toString());
		return (int) ((Double) get(name)).doubleValue();
		//return ((Integer)get(name)).intValue();
	}

	public static double getDouble(String name) {
		return ((Double) get(name)).doubleValue();
	}

	public static String getString(String name) {
		return (String) get(name);
	}

	public static boolean isSet(String name) {
		return get(name) != null;
	}
	
	public static void writeTo(PrintWriter writer) {
		Set paramSet = param.entrySet();
		for (Iterator it = paramSet.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			writer.println("#" + entry.getKey() + "\t" + entry.getValue());
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Set paramSet = param.entrySet();
		for (Iterator it = paramSet.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sb.append(entry.getKey() + "\t" + entry.getValue() + "\n");
		}
		return sb.toString();
	}
}

package gad;


import java.lang.reflect.*;

public class Problem 
{
  protected Logger log;
  private String name;

  public Problem() 
  {
     // this.config = config;
    log = new Logger();
  }

  public String getName() 
  {
    return name;
  }

    public void initialise(Class problemClass) {
	
	//Constructor c = null;
	Method m = null;
	

	// get and invoke initialiser method
	try {
	    m = problemClass.getMethod("initialise", null);
	} catch(NoSuchMethodException e) {
	    log.error("no initialise(Config) for " + problemClass);
	}
	
	try {
	    m.invoke(this, null);
	} catch(InvocationTargetException e) {
	    log.error("cannot invoke " + m);
	} catch(IllegalAccessException e) {
	    log.error("illegal access");
	}
    }

    public double evaluate(Genome g) {
	log.error("Problem unspecified");
	return 0;
    }
}

package EDU.cmu.cs.coral.util;

import java.util.Hashtable;

/**
  * This is like a hashtable but we can specify the type
  * right from the table (instead of Object like Hashtable does)
  *<P>
  * <A HREF="../COPYRIGHT.html">Copyright</A>
  * (c)1999 John Sweeney and Carnegie Mellon University
  */
public class TBDictionary extends Hashtable 
{

  /**
    * get the int referenced by key
    */
  public int getInt(String key) {
    return (int) getDouble(key);
  }

  /** 
    * get the double referenced by key
    */
  public double getDouble(String key) {
    String val = (String) get(key);
    if (val == null) {
      System.out.println("key \""+key+"\" is not in this TBDictionary!");
    }
    Double d = new Double(val);
    return d.doubleValue();
  }

  /**
    *get the string referenced by key
    */
  public String getString(String key) {
    return (String)get(key);
  }

}
  

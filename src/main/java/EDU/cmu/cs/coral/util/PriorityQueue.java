package EDU.cmu.cs.coral.util;

// uncomment next line if you are using JDK 1.1
//import com.sun.java.util.collections.*;

// uncomment next line if you are using JDK 1.2 or later
import java.util.*;

/**
 * This is an abstract definition of a Priority Queue.
 * <P>
 * Copyright (c)2000 William Uther
 *
 * @author William Uther (will@cs.cmu.edu)
 * @version $Revision: 1.2 $
 */

public interface PriorityQueue extends Collection {
	Comparator comparator();
	Object peekMin();
	Object removeMin();
	boolean alteredKey(Object o);
}

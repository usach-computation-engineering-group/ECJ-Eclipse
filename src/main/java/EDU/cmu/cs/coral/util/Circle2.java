/*
 * Circle2.java
 */

package EDU.cmu.cs.coral.util;

import java.io.*;
import java.lang.*;
import java.util.*;
import EDU.gatech.cc.is.util.*;

/**
 * A class for manipulating Circle2.  
 * 
  * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch and Carnegie Mellon University
 *
 * @author Rosemary Emery
 * @version $Revision: 1.1 $

 */

public class Circle2 implements Cloneable, Serializable
	{
	public Vec2 centre; 
	public double radius;

	public Circle2()
		{
		centre = new Vec2(0.0,0.0);
		radius = 0;
		}

	public Circle2(Vec2 origin, double rad)
		{
		centre = new Vec2(origin);
		radius = rad;
		}

	public Circle2(double x, double y, double rad)
		{
		centre = new Vec2(x,y);
		radius = rad;
		}

	public Circle2(Circle2 c)
		{
		centre = new Vec2(c.centre);
		radius = c.radius;
		}

	/**
	 * Create a new Circle2 by cloning.
	 * @param c Circle2, the Circle2 to copy.
	 */
	public 	Object clone()
		{
		return(new Circle2(this));
		}

	/**
	 * translate the Circle2 to a new origin 
	 * @param offset, the new origin of the translated Circle2
       */
	public void offsetSimCircle(Vec2 offset)
		{
			centre.add(offset);
		}
	}

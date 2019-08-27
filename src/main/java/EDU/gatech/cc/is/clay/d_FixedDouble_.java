/*
 *  d_FixedDouble.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Always reports the same double.
 * <P>
 * For detailed information on how to configure behaviors, see the
 * <A HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */


public class d_FixedDouble_ extends NodeDouble
	{
	/** 
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private final double point;

	/**
	 * Instantiate a d_FixedDouble_ schema.
	 * @param x double, the x value to report;
	 */
	public d_FixedDouble_(double x)
		{
		if (DEBUG) System.out.println("d_FixedDouble_: instantiated");
		point = x;
		}

	/**
	 * Return a constant Vec2.
	 * @param timestamp long, not used but retained for compatibility.
	 * @return the double.
	 */
	public double Value(long timestamp)
		{
		if (DEBUG) System.out.println("d_FixedDouble_: Value()");
		return(point);
		}
        }

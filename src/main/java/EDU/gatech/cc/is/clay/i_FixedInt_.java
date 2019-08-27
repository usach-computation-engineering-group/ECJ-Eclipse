/*
 *  i_FixedInt_.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Always reports the same int.
 * <P>
 * For detailed information on how to configure behaviors, see the
 * <A HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */


public class i_FixedInt_ extends NodeInt
	{
	/** 
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private final int point;

	/**
	 * Instantiate a i_FixedInt_ schema.
	 * @param x int, the int to report;
	 */
	public i_FixedInt_(int x)
		{
		if (DEBUG) System.out.println("i_FixedInt_: instantiated");
		point = x;
		}

	/**
	 * Return a constant Vec2.
	 * @param timestamp long, not used but retained for compatibility.
	 * @return the int.
	 */
	public int Value(long timestamp)
		{
		if (DEBUG) System.out.println("i_FixedInt_: Value()");
		return(point);
		}
        }

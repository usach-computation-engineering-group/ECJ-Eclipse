/*
 *  v_FixedPoint_.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Always reports the same Vec2.  This
 * is useful for moving to a known location.
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


public class v_FixedPoint_ extends NodeVec2
	{
	/** 
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private final Vec2 point;

	/**
	Instantiate a v_FixedPoint_ schema.
	@param x double, the x coordinate of the point to return.
	@param y double, the y coordinate of the point to return.
	*/
	public v_FixedPoint_(double x, double y)
		{
		if (DEBUG) System.out.println("v_FixedPoint_: instantiated");
		point = new Vec2(x,y);
		}

	/**
	Return a constant Vec2.
	@param timestamp long, not used but retained for compatibility.
	@return the point.
	*/
	public Vec2 Value(long timestamp)
		{
		if (DEBUG) System.out.println("v_FixedPoint_: Value()");
		return(new Vec2(point.x, point.y));
		}
        }

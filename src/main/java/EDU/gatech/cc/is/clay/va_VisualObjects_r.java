/*
 * va_VisualObjects_r.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.abstractrobot.VisualObjectSensor;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Reports a list of Vec2s pointing to
 * the type of attractor requested and detected by a vision-equipped robot.
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


public class va_VisualObjects_r extends NodeVec2Array
	{
	/** 
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private VisualObjectSensor	abstract_robot;
	private int attractor_type;

	/**
	Instantiate a va_VisualObjects_r node.
	@param ar VisualObjectSensor, abstract_robot object that provides hardware support.
	*/
	public va_VisualObjects_r(int t, VisualObjectSensor ar)
		{
		if (DEBUG) System.out.println("va_VisualObjects_r: instantiated");
		abstract_robot = ar;
		attractor_type = t;
		}

	/**
	Return an array of Vec2s pointing from the
	center of the robot to the detected attractors.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the sensed obstacles
	*/
	public Vec2[] Value(long timestamp)
		{
		if (DEBUG) System.out.println("va_VisualObjects_r: Value()");
		return(abstract_robot.getVisualObjects(timestamp,attractor_type));
		}
        }

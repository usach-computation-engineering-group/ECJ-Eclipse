/*
 * va_Teammates_r.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.abstractrobot.KinSensor;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Report a list of Vec2s pointing to the teammates detected by a KinSensor.
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


public class va_Teammates_r extends NodeVec2Array
	{
	/** 
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private KinSensor abstract_robot;

	/**
	Instantiate a va_Teammates_r schema.
	@param ar KinSensor, the abstract_robot object that provides hardware support.
	*/
	public va_Teammates_r(KinSensor ar)
		{
		if (DEBUG) System.out.println("va_Teammates_r: instantiated");
		abstract_robot = ar;
		}

	/**
	Return an array of Vec2s pointing from the
	center of the robot to the detected teammates
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the sensed teammates
	*/
	public Vec2[] Value(long timestamp)
		{
		if (DEBUG) System.out.println("va_Teammates_r: Value()");
		return(abstract_robot.getTeammates(timestamp));
		}
        }

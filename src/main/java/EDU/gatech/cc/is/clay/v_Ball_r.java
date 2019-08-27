/*
 * v_Ball_r.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.abstractrobot.BallSensor;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Report the egocentric position of a soccer ball.
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


public class v_Ball_r extends NodeVec2
	{
	/** 
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private BallSensor abstract_robot;

	/**
	Instantiate a v_Ball_r schema.
	@param ar BallSensor, the abstract_robot object that provides hardware support.
	*/
	public v_Ball_r(BallSensor ar)
		{
		if (DEBUG) System.out.println("v_Ball_r: instantiated");
		abstract_robot = ar;
		}

	/**
	Return a Vec2 pointing from the
	center of the robot to the ball.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the sensed ball
	*/
	public Vec2 Value(long timestamp)
		{
		if (DEBUG) System.out.println("v_Ball_r: Value()");
		return(abstract_robot.getBall(timestamp));
		}
        }

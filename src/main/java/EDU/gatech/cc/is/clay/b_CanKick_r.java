/*
 * b_CanKick_r.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.abstractrobot.KickActuator;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Report whether we are in a position to kick the ball.
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


public class b_CanKick_r extends NodeBoolean
	{
	/** 
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private KickActuator abstract_robot;

	/**
	 * Instantiate a b_CanKick_r schema.
	 * @param ar KickActuator, the abstract_robot object 
	 * that provides hardware support.
	 */
	public b_CanKick_r(KickActuator ar)
		{
		if (DEBUG) System.out.println("b_CanKick_r: instantiated");
		abstract_robot = ar;
		}

	/**
	 * Return true if we can kick the ball.
	 * @param timestamp long, only get new 
	 *	information if timestamp > than last call or timestamp == -1.
	 * @return true if we can kick the ball.
	 */
	public boolean Value(long timestamp)
		{
		if (DEBUG) System.out.println("b_CanKick_r: Value()");
		return(abstract_robot.canKick(timestamp));
		}
        }

/*
 *  v_SteerHeading_r.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.abstractrobot.SimpleInterface;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Report a Vec2 pointing in the direction of a robot's heading.
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


public class v_SteerHeading_r extends NodeVec2
	{
	/** 
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private SimpleInterface	abstract_robot;

	/**
	 * Instantiate a psHeadingS schema.
	 * @param ar the abstract_robot object that provides hardware support.
	 */
	public v_SteerHeading_r(SimpleInterface ar)
		{
		if (DEBUG) System.out.println("v_SteerHeading_r: instantiated");
		abstract_robot = ar;
		}

        Vec2    last_val = new Vec2();
        long    lasttime = 0;
	/**
	 * Return a Vec2 pointing from the
	 * center of the robot in the direction of it's heading.
	 * @param timestamp long, only get new information 
	 *        if timestamp > than last call or timestamp == -1.
	 * @return the heading
	 */
	public Vec2 Value(long timestamp)
		{
		if (DEBUG) System.out.println("v_SteerHeading_r: Value()");

                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;

			/*--- get the heading ---*/
			last_val = new Vec2(1,0);
			last_val.sett(abstract_robot.getSteerHeading(timestamp));
			}

		return(new Vec2(last_val.x, last_val.y));
		}
        }

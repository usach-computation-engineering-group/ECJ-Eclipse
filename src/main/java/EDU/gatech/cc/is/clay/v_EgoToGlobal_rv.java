/*
 * v_EgoToGlobal_rv.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.abstractrobot.SimpleInterface;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Convert an egocentric Vec2 to global coordinates
 * based on the position information proved by the robot.
 * <P>
 * For detailed information on how to configure behaviors, see the
 * <A HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */


public class v_EgoToGlobal_rv extends NodeVec2
	{
	/** 
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private SimpleInterface	abstract_robot;
	private NodeVec2 embedded1;

	/**
	 * Instantiate a v_EgoToGlobal_rv schema.
	 * @param ar SimpleInterface, the abstract_robot object 
	 *           that provides hardware support.
	 * @param im1 NodeVec2, the embedded egocentric schema.
	 */
	public v_EgoToGlobal_rv(SimpleInterface ar, NodeVec2 im1)
		{
		if (DEBUG) System.out.println("v_EgoToGlobal_rv: instantiated");
		abstract_robot = ar;
		embedded1 = im1;
		}

        Vec2    last_val = new Vec2();
        long    lasttime = 0;
	/**
	 * Return a Vec2 representing the global coordinate of
	 * the embedded egocentric schema.
	 * @param timestamp long, only get new information 
         *        if timestamp > than last call or timestamp == -1.
	 * @return the global coordinate.
	 */
	public Vec2 Value(long timestamp)
		{
		if (DEBUG) System.out.println("v_EgoToGlobal_rv: Value()");

                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;

			/*--- get the position ---*/
			last_val = abstract_robot.getPosition(timestamp);
			last_val.add(embedded1.Value(timestamp));
			}

		return(new Vec2(last_val.x, last_val.y));
		}
        }

/*
 * v_GlobalToEgo_rv.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.abstractrobot.SimpleInterface;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Convert a global Vec2 to egocentric coordinates
 * based on the positional information provided by a SimpleInterface robot.
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


public class v_GlobalToEgo_rv extends NodeVec2
	{
	/** 
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private SimpleInterface	abstract_robot;
	private NodeVec2 embedded1;

	/**
	 * Instantiate a v_GlobalToEgo_rv schema.
	 * @param ar SimpleInterface, the abstract_robot object 
	 *           that provides hardware support.
	 * @param im1 NodeVec2, the embedded node.
	 */
	public v_GlobalToEgo_rv(SimpleInterface ar, NodeVec2 im1)
		{
		if (DEBUG) System.out.println("v_GlobalToEgo_rv: instantiated");
		abstract_robot = ar;
		embedded1 = im1;
		}

        Vec2    last_val = new Vec2();
        long    lasttime = 0;
	/**
	 * Return a Vec2 representing the egocentric coordinate of
	 * the embedded global schema.
	 * @param timestamp long, only get new information 
	 *        if timestamp > than last call or timestamp == -1.
	 * @return the egocentric coordinate.
	 */
	public Vec2 Value(long timestamp)
		{
		if (DEBUG) System.out.println("v_GlobalToEgo_rv: Value()");

                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;

			/*--- get the position ---*/
			last_val = embedded1.Value(timestamp);
			last_val.sub(abstract_robot.getPosition(timestamp));
			}

		return(new Vec2(last_val.x, last_val.y));
		}
        }

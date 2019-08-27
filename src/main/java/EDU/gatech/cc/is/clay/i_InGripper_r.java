/*
 * i_InGripper_r.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.abstractrobot.GripperActuator;
import EDU.gatech.cc.is.util.Vec2;

/**
 * Report the type of object in the gripper.  If nothing is there, return 0.
 * <P>
 * For detailed information on how to configure behaviors, see the
 * <A HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */


public class i_InGripper_r extends NodeInt
	{
	/** 
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private GripperActuator	abstract_robot;

	/**
	Instantiate an i_InGripper_r schema.
	@param ar GripperActuator, the abstract_robot object that provides hardware support.
	*/
	public i_InGripper_r(GripperActuator ar)
		{
		if (DEBUG) System.out.println("i_InGripper_r: instantiated");
		abstract_robot = ar;
		}

        int     last_val = -1;
        long    lasttime = 0;
	/**
	Return an int representing the type of object in the
	robot's gripper, -1 if empty.  
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the type of the object, 0 if empty.
	*/
	public int Value(long timestamp)
		{
		if (DEBUG) System.out.println("i_InGripper_r: Value()");

                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;

			/*--- get the info ---*/
			last_val = abstract_robot.getObjectInGripper(timestamp);
			}

		return(last_val);
		}
        }

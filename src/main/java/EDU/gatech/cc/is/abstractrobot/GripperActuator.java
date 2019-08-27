/*
 * GripperActuator.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.util.*;


/**
 * The GripperActuator class provides an abstract interface to the 
 * hardware of a robot that can grip things.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public interface GripperActuator
	{
        /**
         * Get the kind of object in the gripper.
         * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1.
         * @return channel (1-6) which type/color of object 
	 *        in the gripper, 0 otherwise.
         */
        public abstract int getObjectInGripper(long timestamp);

        /**
         * Set the gripper "finger" position from 0 to 1, with
         * 0 being closed and 1 being open.  A value of -1 puts the
         * gripper in a special "trigger" mode where it will close whenever
         * vision detects an attractor in the gripper.
         * In simulation, any setting other than 1 means closed.
         * @param position the desired position from 0 to 1.
         */
        public abstract void setGripperFingers(long timestamp, double position);

        /**
         * Set the gripper height from 0 to 1, with
         * 0 being down and 1 being up.
         * In simulation this has no effect.
         * @param position the desired position from 0 to 1.
         */
        public abstract void setGripperHeight(long timestamp, double position);
	}

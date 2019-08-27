/*
 * SimpleCye.java
 */

package EDU.cmu.cs.coral.abstractrobot;

import EDU.gatech.cc.is.util.*;
import EDU.gatech.cc.is.abstractrobot.*;


/**
 * Provides an abstract interface to the hardware of
 * a basic Cye robot (no vision, gripper
 * or communication).
 
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1999,2000 CMU
 *
 * @author Rosemary Emery
 * @version $Revision: 1.7 $
 */

public interface SimpleCye extends SimpleInterface,
				   InternalSensor, VisualObjectSensor
	{
        public final double MAX_TRANSLATION = 0.9144; // maximum speed is 3ft/sec
	public final double MAX_STEER = 0.7854;
	public final double RADIUS = 0.445; // over exaggerate
	public final double WIDTH = 2*0.115; // width is 23 cm
	public final double LENGTH = 0.4127; // length is 40.6 cm
	public final double WHEEL_RADIUS = 0.1285/2.0; // in actual fact is smaller than this
	public final double TRAILER_LENGTH = 0.445; // this is from hitch to end
	public final double TRAILER_WIDTH = 0.386; // this is a guesstimation
	public final double TRAILER_FRONT = 0.115; // from hitch to front
	public final double HITCH_TO_TRAILER_WHEEL = 0.445 - 0.115; //estimation for now

	// some useful numbers
	public final double SPORT_HITCH_TO_TRAILER_WHEEL = 0.29;
        public  static final double  VISION_RANGE = 3.0;
        public  static final int     VISION_FOV_DEG = 100;
        public  static final double  VISION_FOV_RAD = Units.DegToRad(100);


	/**
	 * How far sonar ring is from center of robot.
	 */
	public final double SONAR_RADIUS = 0.23;

	
	}

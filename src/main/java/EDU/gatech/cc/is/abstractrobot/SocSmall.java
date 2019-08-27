/*
 * SocSmall.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.util.*;
import EDU.gatech.cc.is.communication.*;


/**
 * Provides an abstract interface to the simulated
 * hardware of a small RoboCup robot.  It's simulated size and perceptions match
 * the specifications of RoboCup regulations for small robots.
 * If you write a control system using this interface to the hardware,
 * you can test it in simulation and (maybe someday) on mobile robots.
 * <P>
 * <B>Sensors and Actuators</B><BR>
 * SocSmall robots can sense their position on the field,
 * the locations of their teammates, opponents, the goals and
 * the ball.
 * The robots can turn and drive, as well as kick the ball, if they
 * are close to it.
 * <P>
 * <B>Frames of reference</B><BR>
 * We use a standard cartesian coordinate system in
 * meters and radians.  Pretend you are looking down
 * on a robot: +x goes out to your right (East), +y goes up (North).  
 * The center of the soccer field is (0,0).
 * When the robot is initialized, it is facing in the direction
 * of the opponents goal.
 * Headings are given in radians, with East=0, North=PI/2 and so on 
 * counter-clockwise around to 2*PI.
 * Some methods return "egocentric" vectors.
 * An egocentric vector is given relative to the center of
 * the robot in the same heading reference frame as global coordinates.
 * An object one meter east of the robot is at (1,0) egocentrically.
 * <P>
 * <B>Implementations</B><BR>
 * This class is extended by a simulation class (SocSmallSim)
 * The subclass handles details of interaction with the simulated
 * world.  Potentially, in the future, another implementation will
 * extend this class for actual physical soccer robots.
 * <P>
 * <B>Timestamps</B><BR>
 * Many of the sensor and motor command methods (e.g. get* and set*) 
 * require a timestamp as a parameter.  This is to help reduce redundant 
 * computations for the same movement step.  When real robots are used, 
 * this becomes especially important because I/O to sensors is expensive
 * in time.
 * <P>
 * If the timestamp is less than or equal to the value sent on the
 * last call to one of these methods, old data is returned.
 * If the timestamp is greater than the last timestamp (or -1), the
 * robot (simulated or real) is queried, and new data is returned.  
 * The idea is that during each control cycle the higher level software will
 * increment the timestamp and use it for all calls to these methods.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public interface SocSmall extends SimpleInterface, KinSensor, 
	KickActuator, GoalSensor, BallSensor, Transceiver
	{
	// some useful numbers

	/**
	 * How close the ball must be to the kicking zone for a kick to work.
	 */
        public  static final double  KICKER_SPOT_RADIUS = 0.02; //2cm

	/**
	 * How fast the ball is kicked.
	 */ public  static final double  KICKER_SPEED = 0.50; 	//40cm/sec

	/**
	 * Max speed of a SocSmall robot.
	 */
        public  static final double  MAX_TRANSLATION = 0.30; 	//20cm/sec

	/**
	 * Max turning rate of a SocSmall robot.
	 */
        public  static final double  MAX_STEER = Math.PI*2; 	//360/sec

	/**
	 * Radius of a SocSmall robot.
	 */
        public  static final double  RADIUS = 0.06;		//5 cm

	}

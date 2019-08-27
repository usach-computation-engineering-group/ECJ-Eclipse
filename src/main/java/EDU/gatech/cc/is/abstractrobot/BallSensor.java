/*
 * BallSensor.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.util.*;


/**
 * Provides an interface to a soccer robot's ball sensor.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch 
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public interface BallSensor
	{
	/**
	 * Get a Vec2 that points to the ball.
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1.
	 * @return the sensed location of the ball
	 * @see EDU.gatech.cc.is.util.Vec2
	 */
	public abstract Vec2 getBall(long timestamp);


	/**
	 * Get an integer that indicates whether a scoring event
	 * just occured.
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1.
	 * @return 1 if team just scored, -1 if scored against,
	 *        0 otherwise.
	 */
	public abstract int getJustScored(long timestamp);
	}

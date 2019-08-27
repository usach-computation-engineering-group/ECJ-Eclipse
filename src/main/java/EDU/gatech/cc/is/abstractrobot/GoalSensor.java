/*
 * GoalSensor.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.util.*;


/**
 * Provides an abstract interface to the goal sensing hardware of
 * a soccer robot.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public interface GoalSensor
	{
	/**
	 * Get a Vec2 that points to the opponent's goal. This is the one
	 * to get the ball across to score.
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1.
	 * @return the sensed location of the goal
	 * @see EDU.gatech.cc.is.util.Vec2
	 */
	public abstract Vec2 getOpponentsGoal(long timestamp);

	/**
	 * Get a Vec2 that points to the team's goal. This is the one
	 * to defend.
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1.
	 * @return the sensed location of the goal
	 * @see EDU.gatech.cc.is.util.Vec2
	 */
	public abstract Vec2 getOurGoal(long timestamp);
	}

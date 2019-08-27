/*
 * KinSensor.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.util.*;


/**
 * The KinSensor class provides an abstract interface to the simulated
 * hardware of a robot that can sense it's kin.  In simulation this 
 * is implemented by searching for objects the same color as the
 * robot.  Do not expect these methods to provide useful information
 * before the simulation begins running (i.e. at initiation time).
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public interface KinSensor
	{
	/**
	 * Get an array of Vec2s that represent the locations of 
	 * teammates (Kin).
	 * @param timestamp only get new information if 
	 * timestamp > than last call or timestamp == -1.
	 * @return the sensed teammates.
	 * @see EDU.gatech.cc.is.util.Vec2
	 */
	public abstract Vec2[] getTeammates(long timestamp);

	/**
	 * Get an array of Vec2s that represent the
	 * locations of opponents.
	 * @param timestamp only get new information if 
	 * 	timestamp > than last call or timestamp == -1.
	 * @return the sensed opponents.
	 * @see EDU.gatech.cc.is.util.Vec2
	 */
	public abstract Vec2[] getOpponents(long timestamp);

	/**
	 * Get the robot's player number, between 0
	 * and the number of robots on the team.
	 * Don't confuse this with getID which returns a unique number
	 * for the object in the simulation as a whole, not on its individual
	 * team.
	 * @param timestamp only get new information if 
	 *	timestamp > than last call or timestamp == -1.
	 * @return the player number.
	 */
	public abstract int getPlayerNumber(long timestamp);

	/**
	 * Set the maximum range at which kin may be sensed.  Primarily
	 * for use in simulation.
	 * @param r double, the maximum range.
	 */
	public abstract void setKinMaxRange(double r);
	}

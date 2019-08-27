/*
 * VisualObjectSensor.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.util.*;


/**
 * Provides an abstract interface to the simulated
 * hardware of a robot that equipped with vision.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.3 $
 */

public interface VisualObjectSensor
	{
        /**
         * Get an array of Vec2s that represent the
         * locations of visually sensed objects egocentrically
         * from center of the robot to the objects currently sensed by the
         * vision system.
         * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1 .
         * @param channel (1-6) which type/color of object to retrieve.
         * @return the sensed objects.
         */
        public Vec2[] getVisualObjects(long timestamp, int channel);


	  /**
	    * This sets the amount of noise that will affect the sensor. the noise
	    * is a normal distribution with mean of mean.
	    * @param mean this is the mean of the distribution.  most cases this will be 0
	    * @param stddev this is the standard deviation of the noise (>= 0.0)
	    *                 if equal to 0, then noise will not affect the sensor
	    * @param seed this is the value used to seed the number generator, for
	    *             repeatable pseudorandom noise.
	    */
	  public void setVisionNoise(double mean, double stddev, long seed);
	}

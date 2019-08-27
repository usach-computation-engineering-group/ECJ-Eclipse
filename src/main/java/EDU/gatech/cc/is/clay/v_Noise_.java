/*
 * v_Noise_.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import java.util.Random;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Generates a vector in a random direction for a specified time.
 * This software module is based on the motor schema formulation developed 
 * by Ronald C. Arkin
 * <P>
 * Arkin's original formulation is described in
 * "Motor Schema Based Mobile Robot
 * Navigation," <I>International Journal of Robotics Research</I>,
 * vol. 8, no 4, pp 92-112.
 * <P>
 * The source code in this module is based on "first principles"
 * (e.g. published papers) and is not derived from any previously
 * existing software.
 * <P>
 * For detailed information on how to configure behaviors, see the
 * <A HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997 Georgia Tech Research Corporation
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class v_Noise_ extends NodeVec2
	{
	/**
	 * Turns debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private double TIMEOUT = 5.0;
	private	double accum = Math.random()*TIMEOUT;
	private Random r = new Random(0); // constant seed

	/**
	 * Instantiate an v_Noise_ schema.
	 *
	 * @param t double, how long the random direction 
	 *	should persist in seconds.
	 */
	public v_Noise_(double t)
		{
		if (DEBUG) System.out.println("v_Noise_: instantiated.");
		last_val.sett(Math.random()*2*Math.PI);
		TIMEOUT = t;
		r.setSeed(System.currentTimeMillis());
		}


	/**
	 * Instantiate an v_Noise_ schema.
	 *
	 * @param t double, how long the random direction 
	 *	should persist in seconds.
	 * @param s long, the random number seed.
	 */
	public v_Noise_(double t, long s)
		{
		if (DEBUG) System.out.println("v_Noise_: instantiated.");
		last_val.sett(Math.random()*2*Math.PI);
		TIMEOUT = t;
		r.setSeed(s);
		}


	Vec2	last_val = new Vec2();
	long	lasttime = 0;
	/**
	Return a Vec2 representing a random direction to go
	for a period of time.
	@param timestamp only get new information if timestamp > than last call
                or timestamp == -1.
	@return the movement vector.
	*/
	public Vec2 Value(long timestamp)
		{
		double	tempmag;

		if ((timestamp > lasttime)||(timestamp == -1))
			{
			/*--- reset the timestamp ---*/
			double time_incd = (double)(timestamp-lasttime)/1000;
			if (timestamp > 0) lasttime = timestamp;
			else
				timestamp = lasttime + 1;
			accum += time_incd;

			/*--- reset output ---*/
			if (accum > TIMEOUT)
				{
				accum = 0;
				last_val.sett(r.nextDouble()*2*Math.PI);
				}
			}
		return (new Vec2(last_val.x, last_val.y));
		}
        }

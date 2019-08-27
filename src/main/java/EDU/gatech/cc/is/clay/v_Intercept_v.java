/*
 * v_Intercept_v.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Generates a vector towards an intercept with a
 * moving attractor.  It infers the velocity of the attractor through
 * observation.  Thanks to Rob Orr for the idea.
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


public class v_Intercept_v extends NodeVec2
	{
	/**
	Turns debug printing on or off.
	*/
	public static final boolean DEBUG = /*true;*/ Node.DEBUG;
	private NodeVec2	global_robot_pos;
	private NodeVec2	ego_attractor_pos;
	private Vec2		global_attractor_pos;
	private Vec2		attractor_vel;
	private double		robot_max = 0.5;

	/**
	Instantiate a v_Intercept_v node.
	@param rm double, robot's max speed to compute the intercept.
	@param im1 NodeVec2, the embedded node that provides 
			robot global position.
	@param im2 NodeVec2, the embedded node that provides an
		egocentric vector to the attractor.
	*/
	public v_Intercept_v(double rm, NodeVec2 im1,
		NodeVec2 im2)
		{
		if (DEBUG) System.out.println("v_Intercept_v: instantiated.");
		global_robot_pos = im1;
		ego_attractor_pos = im2;
		robot_max = rm;
		}


	Vec2	last_val = new Vec2(0,0);
	Vec2	last_A = new Vec2(0,0);
	Vec2	last_Ag = new Vec2(0,0);
	long	lasttime = 0;
	/**
	Return a Vec2 representing the direction to go to intercept the
	attractor.  Return (0,0) if intercept is impossible, or
	attractor position is (0,0).
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the movement vector.
	*/
	public Vec2 Value(long timestamp)
		{
		double sol1, sol2, sol, a, b, c;

		if ((timestamp > lasttime)||(timestamp == -1))
			{
			if (DEBUG) System.out.println("v_Intercept_v:");

			/*--- compute time inc ---*/
			double time_incd = (double)(timestamp - lasttime)/1000;

			/*--- reset the timestamp ---*/
			if (timestamp > 0) lasttime = timestamp;

			/*--- get the data ---*/
			Vec2 R  = global_robot_pos.Value(timestamp);
			Vec2 A  = ego_attractor_pos.Value(timestamp);
			Vec2 Ag = new Vec2(R.x + A.x, R.y + A.y); // global pos
			Vec2 Av = new Vec2(Ag.x, Ag.y); 
			Av.sub(last_Ag); // velocity of object
			Av.setr(Av.r/time_incd);
			boolean good_solution = false;
			if ((A.r > 0)&&(last_A.r > 0)&&(Av.r < 0.5))
				// we see something now
				// we saw something last time
				// and velocity is less than 0.5m/sec
				// 0.5 is a sanity check, it may be changed
				{
				/*--- compute time to intercept ---*/
				a = - robot_max * robot_max
					+ Av.x * Av.x
					+ Av.y * Av.y;

				b = 2 * Av.x * Ag.x 
					+ 2 * Av.y * Ag.y
					- 2 * R.x * Av.x
					- 2 * R.y *Av.y;

				c = Ag.x * Ag.x + Ag.y * Ag.y
					- 2 * R.x * Ag.x
					- 2 * R.y * Ag.y
					+ R.x * R.x
					+ R.y * R.y;
				sol = 0;
				if ((b*b > 4*a*c))
					{
					// we have real solutions
					sol1 = -b + Math.sqrt(b*b - 4*a*c)/2*a;
					sol2 = -b - Math.sqrt(b*b - 4*a*c)/2*a;
					if ((sol1 < 0) && (sol2 < 0))
						good_solution = false;
					else if ((sol1 < sol2) && (sol1 > 0))
						{
						good_solution = true;
						sol = sol1;
						}
					else
						{
						good_solution = true;
						sol = sol2;
						}
					}
				if (good_solution)
					{
					// the solution is a time, so
					// compute the intercept point
					last_val = new Vec2(
						A.x + sol*Av.x,
						A.y + sol*Av.y);
					}
				else
					{
					// point to what we see if
					// no solution
					//last_val = new Vec2(0,0);
					last_val = A;
					}
				}
			else
				{
				// just point to what we see if no
				// good track.
				last_val = A;
				}
			last_A = A;
			last_Ag = Ag;
			}
		if (DEBUG) System.out.println(last_val.r);
		return (new Vec2(last_val.x, last_val.y));
		}
        }

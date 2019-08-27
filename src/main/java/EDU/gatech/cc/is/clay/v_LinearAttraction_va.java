/*
 * v_LinearAttraction_va.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Generates a vector towards a multiple goal locations
 * that varies with distance from the goals.  The attraction is
 * increased linearly at greater distances.  Based on Arkin's
 * original formulation.
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
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class v_LinearAttraction_va extends NodeVec2
	{
	/**
	Turns debug printing on or off.
	*/
	public static final boolean DEBUG = /*true;*/ Node.DEBUG;
	private NodeVec2Array	embedded1;
	private double		controlled_zone = 1.0;
	private double	        dead_zone = 0.0;

	/**
	Instantiate a v_LinearAttraction_va schema.
	@param czr double, controlled zone radius.
	@param dzr double, dead zone radius.
	@param im1 double, the node that generates a list of
		egocentric vectors to the goals.
	*/
	public v_LinearAttraction_va(double czr, double dzr,
		NodeVec2Array im1)
		{
		if (DEBUG) System.out.println("v_LinearAttraction_va: instantiated.");
		embedded1 = im1;
		if ((czr < dzr) || (czr<0) || (dzr<0))
			{
			System.out.println("v_LinearAttraction_va: illegal parameters");
			return;
			}
		controlled_zone = czr;
		dead_zone = dzr;
		}


	Vec2	last_val = new Vec2();
	long	lasttime = 0;
	/**
	Return a Vec2 representing the direction to go towards the
	goal.  Magnitude varies with distance.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the movement vector.
	*/
	public Vec2 Value(long timestamp)
		{
		double	mag;
		double	tempmag;
		double	max_mag=0;
		Vec2[] goals;

		if ((timestamp > lasttime)||(timestamp == -1))
			{
			if (DEBUG) System.out.println("v_LinearAttraction_va:");

			/*--- reset the timestamp ---*/
			if (timestamp > 0) lasttime = timestamp;

			/*--- reset output ---*/
			last_val.setr(0);

			/*--- get the goals ---*/
			goals = embedded1.Value(timestamp);

			/*--- consider each goal ---*/
			for (int i = 0; i< goals.length; i++)
				{
				Vec2 goal = goals[i];

				/*--- consider the magnitude ---*/
				// inside dead zone?
				if (goal.r < dead_zone)
					{
					mag = 0;
					}
				// inside control zone?
				else if (goal.r < controlled_zone)
					mag = (goal.r - dead_zone)/
						(controlled_zone - dead_zone);
				// outside control zone
				else mag = 1.0;
				if (DEBUG) System.out.println(mag+" "+goal.r);
			 	if (Math.abs(mag)>max_mag)
					max_mag = mag;
				
				/*--- set the vector ---*/
				goal.setr(mag);
				last_val.add(goal);
				}
			if (last_val.r>1.0)
				last_val.setr(max_mag);
			}
		if (DEBUG) System.out.println(last_val);
		return (new Vec2(last_val.x, last_val.y));
		}
        }

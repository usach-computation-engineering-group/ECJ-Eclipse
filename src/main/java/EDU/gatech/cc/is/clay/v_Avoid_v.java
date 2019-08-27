/*
 * v_Avoid_v.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * This node (motor schema) generates a vector away from a single hazard.
 * Magnitude varies from 0 to 1.  Based on Arkin's formulation.
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

public class v_Avoid_v extends NodeVec2
	{
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2	embedded1;
	private double		sphere = 1.0;
	private double	        safety = 0.0;

	/**
	Instantiate a v_Avoid_v schema.
	@param soe double, the sphere of influence beyond which the hazards
		are not considered.
	@param s double, the safety zone, inside of which a maximum repulsion
		from the object is generated.
	@param im1 NodeVec2, the embedded node that generates an item
	*/
	public v_Avoid_v(double soe, double s,
		NodeVec2 im1)
		{
		if (DEBUG) System.out.println("v_Avoid_v: instantiated.");
		embedded1 = im1;
		if ((soe < s) || (soe<0) || (s<0))
			{
			System.out.println("v_Avoid_v: illegal parameters");
			return;
			}
		sphere = soe;
		safety = s;
		}


	Vec2	last_val = new Vec2();
	long	lasttime = 0;
	/**
	Return a Vec2 representing the direction to go away from
	the detected hazards.
	@param timestamp long, only get new information 
		if timestamp > than last call or timestamp == -1.
	@return the movement vector.
	*/
	public Vec2 Value(long timestamp)
		{
		double	tempmag;
		double	max_mag=0;

		if ((timestamp > lasttime)||(timestamp == -1))
			{
			/*--- reset the timestamp ---*/
			if (timestamp > 0) lasttime = timestamp;

			/*--- get the obstacles ---*/
			Vec2 obs = embedded1.Value(timestamp);

			if (obs.r < safety)
				{
				tempmag = -1*Units.HUGE;
				}
			else if (obs.r < sphere)
				tempmag = -1*(sphere - obs.r)/(sphere - safety);
			else tempmag = 0;

			obs.setr(tempmag);
			last_val = obs;
			}
		return (new Vec2(last_val.x, last_val.y));
		}
        }

/*
 * v_Attract_va.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * This node (motor schema) generates a vector away from the
 * items detected by its embedded perceptual schema.
 * Magnitude varies from 0 to 1.
 * <P>
 * This version is slightly different from Arkin's original
 * formulation.  In the original, a repulsion vector is computed
 * for each detected attractor with the result being the sum of
 * these vectors.  The result is that several hazards grouped closely
 * are more repulsive than a single hazard.  This causes problems
 * when each sonar return is treated as a separate hazard --- walls
 * for instance are more repulsive than a small hazard.
 * <P>
 * This version computes the direction of the repulsive vector
 * as in the original, but the returned magnitude is the largest
 * of the vectors, not the sum.
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

public class v_Attract_va extends NodeVec2
	{
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array	embedded1;
	private double		sphere = 1.0;
	private double	        safety = 0.0;

	/**
	Instantiate a v_Attract_va schema.
	@param soe double, the sphere of influence beyond which the hazards
		are not considered.
	@param s double, the safety zone, inside of which a maximum repulsion
		from the object is generated.
	@param im1 NodeVec2Array, the embedded node that generates a list
		of items to avoid.
	*/
	public v_Attract_va(double soe, double s,
		NodeVec2Array im1)
		{
		if (DEBUG) System.out.println("v_Attract_va: instantiated.");
		embedded1 = im1;
		if ((soe < s) || (soe<0) || (s<0))
			{
			System.out.println("v_Attract_va: illegal parameters");
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
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
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

			/*--- reset output ---*/
			last_val.setr(0);

			/*--- get the list of attractors ---*/
			Vec2[] obs = embedded1.Value(timestamp);

			/*--- consider each attractor ---*/
			for(int i = 0; i<obs.length; i++)
				{
				/*--- too close ? ---*/
				if (obs[i].r < safety)
					{
					tempmag = 1;
					}
				/*--- controlled zone ---*/
				else if (obs[i].r < sphere)
					tempmag = 
						1*(sphere - obs[i].r)/
						(sphere - safety);
				/*--- outside sphere ---*/
				else tempmag = 0;
				
				/*--- set the repulsive vector ---*/
				obs[i].setr(tempmag);

				/*--- check if maximum value ---*/
				if(Math.abs(tempmag)>max_mag) 
					max_mag = Math.abs(tempmag);

				/*--- add it to the sum ---*/
				if (DEBUG) System.out.println(obs[i]);
				last_val.add(obs[i]);
				}
			if (last_val.r>1.0)
				last_val.setr(max_mag);
			if (DEBUG) System.out.println("v_Attract_va.Value: "+
				obs.length+" attractor "+
				"output "+
				last_val);
			}
		return (new Vec2(last_val.x, last_val.y));
		}
        }

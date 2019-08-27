/*
 * v_Swirl_vav.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Generate a
 * vector that swirls to one side or the other of detected hazards.
 * One embedded node provides a list of hazards, the other points 
 * in the reference direction (typically a goal location).
 * <P>
 * The "swirl" behavior was originally developed by 
 * Andy Henshaw and Tom Collins at the Georgia Tech Research Institute.
 * It is also similar to Marc Slack's NATs.
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


public class v_Swirl_vav extends NodeVec2
	{
	/**
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array	embedded1;
	private NodeVec2	embedded2;
	private double		sphere = 1.0;
	private double	        safety = 0.0;

	/**
	Instantiate a v_Swirl_vav schema.
	@param soe double, the sphere of influence beyond which the hazards
		are not considered.
	@param s double, the safety zone, inside of which a maximum repulsion
		from the object is generated.
	@param im1 NodeVec2Array, the embedded perceptual schema that generates a list
		of items to avoid.
	@param im2 NodeVec2, the embedded perceptual schema that generates a pointer
		to the goal (must be egocentric).
	*/
	public v_Swirl_vav(double soe, double s,
		NodeVec2Array im1, NodeVec2 im2)
		{
		if (DEBUG) System.out.println("v_Swirl_vav: instantiated.");
		embedded1 = im1;
		embedded2 = im2;
		if ((soe < s) || (soe<0) || (s<0))
			{
			System.out.println("v_Swirl_vav: illegal parameters");
			return;
			}
		sphere = soe;
		safety = s;
		}


	Vec2	last_val = new Vec2();
	long	lasttime = 0;
	/**
	Return a Vec2 representing the direction to go.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the movement vector.
	*/
	public Vec2 Value(long timestamp)
		{
		double	tempmag;
		double	max_mag = 0;
		double	tempdir;
		double	refheading;

		if ((timestamp > lasttime)||(timestamp == -1))
			{
			/*--- reset the timestamp ---*/
			if (timestamp > 0) lasttime = timestamp;

			/*--- reset output ---*/
			last_val.setr(0);

			/*--- get the list of obstacles and ref dir ---*/
			Vec2[] obs = embedded1.Value(timestamp);
			refheading = embedded2.Value(timestamp).t;

			/*--- consider each obstacle ---*/
			for(int i = 0; i<obs.length; i++)
				{
				// only swirl around the obs if it is
				// in front of us.
				tempdir = -Units.BestTurnRad(refheading,
					obs[i].t);
				if (Math.abs(tempdir)<Math.PI/2)
					{
					/*--- compute direction of swirl ---*/
					// first decide left or right, negative 
					// is right
					if (tempdir < 0)
						obs[i].sett(obs[i].t - Math.PI/2);
					else
						obs[i].sett(obs[i].t + Math.PI/2);
				
					/*--- compute magnitude of swirl ---*/
					// inside saftey zone, set full magnitude
					if (obs[i].r < safety)
						{
						tempmag = 1;
						}
					// in controlled zone
					else if (obs[i].r < sphere)
						tempmag = (sphere - obs[i].r)/
							(sphere - safety);
					// outside sphere of influence, ignore
					else tempmag = 0;
					// set the magnitude
					obs[i].setr(tempmag);

					/*--- check if it is the biggest one ---*/
					if (Math.abs(tempmag)>max_mag) 
						max_mag = Math.abs(tempmag);
	
					/*--- add it to the sum ---*/
					if (DEBUG) System.out.println(obs[i]);
					last_val.add(obs[i]);
					}
				}
			/*--- normalize ---*/
			//NOT!
			//if (last_val.r > 0)
			//	last_val.setr(0.0);
			last_val.setr(max_mag);
			if (DEBUG) System.out.println("v_Swirl_vav.Value: "+
				obs.length+" obstacles "+
				"output "+
				last_val);
			}
		return (new Vec2(last_val.x, last_val.y));
		}
        }

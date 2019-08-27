/*
 * v_SwirlLeft_va.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Generate a vector that aways swirls to the left of detected hazards.
 * The embedded perceptual schema provides a list of hazards
 * This is useful for "always turn left" type behaviors.
 * The "swirl" behavior was originally developed by Andy Henshaw
 * and Tom Collins.
 * <P>
 * For detailed information on how to configure behaviors, see the
 * <A HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */


public class v_SwirlLeft_va extends NodeVec2
	{
	/**
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array	embedded1;
	private double		sphere = 1.0;
	private double	        safety = 0.0;

	/**
	Instantiate a v_SwirlLeft_va schema.
	@param soe double, the sphere of influence beyond which the hazards
		are not considered.
	@param s double, the safety zone, inside of which a maximum repulsion
		from the object is generated.
	@param im1 double, the embedded perceptual schema that generates a list
		of items to avoid.
	*/
	public v_SwirlLeft_va(double soe, double s,
		NodeVec2Array im1)
		{
		if (DEBUG) System.out.println("v_SwirlLeft_va: instantiated.");
		embedded1 = im1;
		if ((soe < s) || (soe<0) || (s<0))
			{
			System.out.println("v_SwirlLeft_va: illegal parameters");
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

		if ((timestamp > lasttime)||(timestamp == -1))
			{
			/*--- reset the timestamp ---*/
			if (timestamp > 0) lasttime = timestamp;

			/*--- reset output ---*/
			last_val.setr(0);

			/*--- get the list of obstacles and ref dir ---*/
			Vec2[] obs = embedded1.Value(timestamp);

			/*--- consider each obstacle ---*/
			for(int i = 0; i<obs.length; i++)
				{
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
				obs[i].sett(obs[i].t 
					+ Units.DegToRad(90));

				/*--- check if it is the biggest one ---*/
				if (Math.abs(tempmag)>max_mag) 
					max_mag = Math.abs(tempmag);
	
				/*--- add it to the sum ---*/
				if (DEBUG) System.out.println(obs[i]);
					last_val.add(obs[i]);
				}
			/*--- normalize ---*/
			//NOT!
			//if (last_val.r > 0)
			//	last_val.setr(0.0);
			last_val.setr(max_mag);
			if (DEBUG) System.out.println("v_SwirlLeft_va.Value: "+
				obs.length+" obstacles "+
				"output "+
				last_val);
			}
		return (new Vec2(last_val.x, last_val.y));
		}
        }

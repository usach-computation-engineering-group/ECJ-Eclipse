/*
 * v_GeoField_vav.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Generates a linear attractive field from detected objects,
 * along a given axis.  Several of these fields can be combined
 * to generate robot formations.
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


public class v_GeoField_vav extends NodeVec2
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
	Instantiate a v_GeoField_vav schema.
	@param soe double, the sphere of influence beyond which the objects
		are not considered.
	@param s double, the safety zone, inside of which a maximum attraction
		is generated.
	@param im1 NodeVec2Array, the embedded perceptual schema that generates a list
		of items to avoid.
	@param im2 NodeVec2, the embedded perceptual schema that generates 
		to the axis of attraction.
	*/
	public v_GeoField_vav(double soe, double s,
		NodeVec2Array im1, NodeVec2 im2)
		{
		if (DEBUG) System.out.println("v_GeoField_vav: instantiated.");
		embedded1 = im1;
		embedded2 = im2;
		if ((soe < s) || (soe<0) || (s<0))
			{
			System.out.println("v_GeoField_vav: illegal parameters");
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

			/*--- consider each object ---*/
			for(int i = 0; i<obs.length; i++)
				{
				tempdir = Units.BestTurnRad(refheading,
					obs[i].t);

				/*--- compute direction of swirl ---*/
				// first decide left or right, negative 
				// is right
				Vec2 axis = new Vec2(obs[i].x, obs[i].y);
				if (tempdir < 0)
					obs[i].sett(obs[i].t - Math.PI/2);
				else
					obs[i].sett(obs[i].t + Math.PI/2);
			
				/*--- compute magnitude of swirl ---*/
				// first compute with respect to object
				// range...
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

				// now adjust for off-axis
				//System.out.println("t "+axis.t+" "+refheading);
				axis.sett(axis.t - refheading);
				double off = Math.abs(axis.y);
				if (off > 0.1)
					off = (0.4 - off)/0.3;
				else
					off = off/0.1;
				if ((off<0)||(axis.x<0)) off = 0;
				tempmag = tempmag * off;
				//System.out.println("y "+axis.y+" "+off+" "
				//	+tempmag);

				// set the magnitude
				obs[i].setr(tempmag);
					
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
			if (DEBUG) System.out.println("v_GeoField_vav.Value: "+
				obs.length+" obstacles "+
				"output "+
				last_val);
			}
		return (new Vec2(last_val.x, last_val.y));
		}
        }

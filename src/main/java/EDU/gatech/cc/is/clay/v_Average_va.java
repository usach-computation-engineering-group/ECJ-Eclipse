/*
 * v_Average_va.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * This node generates a vector that is the average of the
 * incoming list.
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

public class v_Average_va extends NodeVec2
	{
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array	embedded1;
	private double		sphere = 1.0;
	private double	        safety = 0.0;

	/**
	 * Instantiate a v_Average_va schema.
	 * @param im1 NodeVec2Array, the embedded node that generates a list
	 *	of items to average.
	 */
	public v_Average_va(NodeVec2Array im1)
		{
		if (DEBUG) System.out.println("v_Average_va: instantiated.");
		embedded1 = im1;
		}


	Vec2	last_val = new Vec2();
	long	lasttime = 0;
	/**
	 * Return a Vec2 representing the direction to go away from
	 * the detected hazards.
	 * @param timestamp long, only get new 
	 * information if timestamp > than last call or timestamp == -1.
	 * @return the average vector.
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
				last_val.add(obs[i]);
				}

			/*--- normalize ---*/
			if (obs.length>0)
				{
				last_val.setx(last_val.x/((double)obs.length));
				last_val.sety(last_val.y/((double)obs.length));
				}

			if (DEBUG) System.out.println("v_Average_va.Value: "+
				obs.length+" attractor "+
				"output "+
				last_val);
			}
		return (new Vec2(last_val.x, last_val.y));
		}
        }

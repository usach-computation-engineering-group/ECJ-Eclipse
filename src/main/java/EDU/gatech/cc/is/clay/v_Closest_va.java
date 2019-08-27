/*
 * v_Closest_va.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Finds the closest in a list of Vec2s.  Assumes the
 * vectors point egocentrically to the objects, so that
 * the closest one has the shortest r value.
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


public class v_Closest_va extends NodeVec2
	{
	/**
	Turns debugging on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array	embedded1;

	/**
	Instantiate a v_Closest_va node.
	@param im1 NodeVec2, the embedded node that generates a list
		of items to scan.
	*/
	public v_Closest_va(NodeVec2Array im1)
		{
		if (DEBUG) System.out.println("v_Closest_va: instantiated.");
		embedded1 = im1;
		}


	Vec2	last_val = new Vec2();
	long	lasttime = 0;

	/**
	Return a Vec2 representing the closest object, or 0,0 if
	none are visible.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the vector.
	*/
	public Vec2 Value(long timestamp)
		{
		double	tempmag;

		if ((timestamp > lasttime)||(timestamp == -1))
			{
			/*--- reset the timestamp ---*/
			if (timestamp > 0) lasttime = timestamp;

			/*--- reset output ---*/
			last_val.setr(0);

			/*--- get the list of obstacles ---*/
			Vec2[] objs = embedded1.Value(timestamp);

			/*--- consider each obstacle ---*/
			double closest = 99999999;
			for(int i = 0; i<objs.length; i++)
				{
				if (objs[i].r < closest)
					{
					closest = objs[i].r;
					last_val = objs[i];
					}
				}
			if (DEBUG) System.out.println("v_Closest_va.Value: "+
				objs.length+" things to see "+
				"output "+
				last_val);
			}
		return (new Vec2(last_val.x, last_val.y));
		}
        }

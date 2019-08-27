/*
 * v_Average_vv.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Average the Vec2 output of two embedded Vec2 nodes.
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


public class v_Average_vv extends NodeVec2
	{
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2	embedded1;
	private NodeVec2	embedded2;
	private double		sphere = 1.0;
	private double	        safety = 0.0;

	/**
	Instantiate a v_Average_vv schema.
	@param im1 NodeVec2, an embedded perceptual schema that generates a vector
			to be averaged.
	@param im2 NodeVec2, an embedded perceptual schema that generates a vector
			to be averaged.
	*/
	public v_Average_vv(NodeVec2 im1, NodeVec2 im2)
		{
		if (DEBUG) System.out.println("v_Average_vv: instantiated.");
		embedded1 = im1;
		embedded2 = im2;
		}


	Vec2	last_val = new Vec2();
	long	lasttime = 0;
	/**
	Return a Vec2 representing the average of the two embedded nodes.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the vector average.
	*/
	public Vec2 Value(long timestamp)
		{
                if (DEBUG) System.out.println("v_Average_vv: Value()");
 
                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;
 
                        /*--- compute the average ---*/
                        last_val = embedded1.Value(timestamp);
			last_val.add(embedded2.Value(timestamp));
			last_val.setr(last_val.r*0.5);
                        }

		return (new Vec2(last_val.x, last_val.y));
		}
        }

/*
 * b_Close_vv.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Return true if the values of 
 * two embedded nodes are close to one another.
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


public class b_Close_vv extends NodeBoolean
	{
	/** 
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2	embedded1;
	private NodeVec2	embedded2;
	private double		range;

	/**
	Instantiate a b_Close_vv schema.
	@param r   double, how close they must be.
	@param im1 NodeVec2, the embedded node that generates a vector
			to be detected.
	@param im2 NodeVec2, the embedded node that generates a vector
			to be detected.
	*/
	public b_Close_vv(double r, NodeVec2 im1, NodeVec2 im2)
		{
		if (DEBUG) System.out.println("b_Close_vv: instantiated.");
		range = r;
		embedded1 = im1;
		embedded2 = im2;
		}

	boolean	last_val = false;
	long	lasttime = 0;
	/**
	Return a boolean indicating if the two embedded schemas
	are close to each other.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return true if non-zero, false if zero.
	*/
	public boolean Value(long timestamp)
		{
                if (DEBUG) System.out.println("b_Close_vv: Value()");
 
                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;
 
                        /*--- compute the output ---*/
                        Vec2 tmp1 = embedded1.Value(timestamp);
                        Vec2 tmp2 = embedded2.Value(timestamp);
			tmp1.sub(tmp2);
			if (Math.abs(tmp1.r) < range)
				last_val = true;
			else
				last_val = false;
                        }

		return (last_val);
		}
        }

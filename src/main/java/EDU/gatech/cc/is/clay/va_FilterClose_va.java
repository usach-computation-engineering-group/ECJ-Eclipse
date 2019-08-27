/*
 * va_FilterClose_va.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Keep Vec2s that are close to 0,0.
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

public class va_FilterClose_va extends NodeVec2Array
	{
	/**
	Turns debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array	embedded2;
	private	double		range = 0.5;

	/**
	 * Instantiate a va_FilterClose_va node.
	 * @param r how close things must be to be kept.
	 * @param im2 NodeVec2Arry, the embedded node 
	 *	that generates a list of items to filter.
	 */
	public va_FilterClose_va(double r, NodeVec2Array im2)
		{
		if (DEBUG) System.out.println("va_FilterClose_va: instantiated.");
		embedded2 = im2;
		range = r;
		}


	Vec2	last_val[] = new Vec2[0];
	long	lasttime = 0;
	/**
	 * Return a filtered Vec2Array.
	 * @param timestamp long, only get new 
	 * information if timestamp > than last call or timestamp == -1.
	 * @return the filtered array of Vec2s.
	 */
	public Vec2[] Value(long timestamp)
		{
		if ((timestamp > lasttime)||(timestamp == -1))
			{
			/*--- reset the timestamp ---*/
			if (timestamp > 0) lasttime = timestamp;

			/*--- get info from imbedded schemas ---*/
			Vec2[] im2 = embedded2.Value(timestamp);

			/*--- count ---*/
			int count = 0;
			for (int i=0; i<im2.length; i++)
				{
				Vec2 tmp = new Vec2(im2[i].x, im2[i].y);
				if (tmp.r <= range)
					count++;
				}

			/*--- fill it in ---*/
			last_val = new Vec2[count];
			count = 0;
			for (int i=0; i<im2.length; i++)
				{
				Vec2 tmp = new Vec2(im2[i].x, im2[i].y);
				if (tmp.r <= range)
					last_val[count++] = im2[i];
				}
			}
                Vec2[] retval = new Vec2[last_val.length];
                for(int i = 0; i<last_val.length; i++)
                        retval[i] = new Vec2(last_val[i].x,
                                last_val[i].y);
                return(retval);
		}
        }

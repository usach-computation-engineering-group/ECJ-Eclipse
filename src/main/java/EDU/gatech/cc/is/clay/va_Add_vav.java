/*
 * va_Add_vav.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Add one vector to an array of others.  Useful for converting
 * ego to global or global to ego positions for an array of sensor readings. 
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


public class va_Add_vav extends NodeVec2Array
	{
	/**
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array	embedded1;
	private NodeVec2	embedded2;

	/**
	Instantiate a va_Add_vav node.
	@param im1 NodeVec2Array, the node that generates an
			Array of vectors to be added.
	@param im2 NodeVec2, the node that generates a vector
			to be added to the others.
	*/
	public va_Add_vav(NodeVec2Array im1, NodeVec2 im2)
		{
		if (DEBUG) System.out.println("va_Add_vav: instantiated.");
		embedded1 = im1;
		embedded2 = im2;
		}


	Vec2[]	last_val = new Vec2[0];
	long	lasttime = -1;
	/**
	Return a Vec2[] that is the sum of the original values and the
	embedded node.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the array of Vec2s.
	*/
	public Vec2[] Value(long timestamp)
		{
                if (DEBUG) System.out.println("va_Add_vav: Value()");
 
                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;

			/*--- get the embedded values ---*/
                        last_val = embedded1.Value(timestamp);
			Vec2 tmp = embedded2.Value(timestamp);

			/*--- add the values ---*/
			for (int i=0; i<last_val.length; i++)
				last_val[i].add(tmp);
                        }
                Vec2[] retval = new Vec2[last_val.length];
                for(int i = 0; i<last_val.length; i++)
                        retval[i] = new Vec2(last_val[i].x,
                                last_val[i].y);
                return(retval);
		}
        }

/*
 * va_Merge_vav.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Merge a Vec2 into a Vec2Array.
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

public class va_Merge_vav extends NodeVec2Array
	{
	public static final boolean DEBUG = Node.DEBUG;
	private NodeVec2Array	embedded1;
	private NodeVec2	embedded2;

	/**
	 * Instantiate a va_Merge_vav schema.
	 * @param im1 NodeVec2Array, embedded node that generates a list
	 *	of items to merge.
	 * @param im2 NodeVec2, the other embedded node that generates an item
	 *	of items to merge.
	 */
	public va_Merge_vav(NodeVec2Array im1, NodeVec2 im2)
		{
		if (DEBUG) System.out.println("va_Merge_vav: instantiated.");
		embedded1 = im1;
		embedded2 = im2;
		}


	Vec2	last_val[] = new Vec2[0];
	long	lasttime = 0;
	/**
	 * Return a Vec2Array that is the merge of two others.
	 * @param timestamp long, only get new information 
	 * 	if timestamp > than last call or timestamp == -1.
	 * @return the merged list.
	 */
	public Vec2[] Value(long timestamp)
		{
		if ((timestamp > lasttime)||(timestamp == -1))
			{
			/*--- reset the timestamp ---*/
			if (timestamp > 0) lasttime = timestamp;

			/*--- get info from imbedded schemas ---*/
			Vec2[] im1 = embedded1.Value(timestamp);
			Vec2[] im2 = new Vec2[1];
			im2[0] = embedded2.Value(timestamp);
			last_val = new Vec2[im1.length + im2.length];

			/*--- merge ---*/
			for (int i=0; i<im1.length; i++)
				last_val[i] = im1[i];
			for (int i=im1.length; i<(im1.length+im2.length); i++)
				last_val[i] = im2[i-im1.length];

			}
                Vec2[] retval = new Vec2[last_val.length];
                for(int i = 0; i<last_val.length; i++)
                        retval[i] = new Vec2(last_val[i].x,
                                last_val[i].y);
                return(retval);
		}
        }

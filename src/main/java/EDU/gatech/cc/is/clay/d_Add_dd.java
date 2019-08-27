/*
 * d_Add_dd.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;

/**
 * Add the output of two NodeDoubles.
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


public class d_Add_dd extends NodeDouble
	{
	/**
	 * Turn debugging on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private NodeDouble	embedded1;
	private NodeDouble	embedded2;


	/**
	 * Instantiate a d_Add_dd schema. It will 
	 * compute im1+im2.
	 * @param im1 NodeDouble, the embedded node that generates a double.
	 * @param im2 NodeDouble, an embedded node that generates a double.
	 */
	public d_Add_dd(NodeDouble im1, NodeDouble im2)
		{
		if (DEBUG) System.out.println("d_Add_dd: instantiated.");
		embedded1 = im1;
		embedded2 = im2;
		}


	double	last_val = 0;
	long	lasttime = 0;
	/**
	 * Return a double representing the sum.
	 * @param timestamp long, only get new information 
	 *	if timestamp > than last call or timestamp == -1.
	 * @return the vector difference.
	 */
	public double Value(long timestamp)
		{
                if (DEBUG) System.out.println("d_Add_dd: Value()");
 
                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;
 
                        /*--- compute the sum ---*/
                        last_val = embedded1.Value(timestamp);
			last_val += embedded2.Value(timestamp);
                        }

		return (last_val);
		}
        }

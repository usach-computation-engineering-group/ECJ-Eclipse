/*
 * b_NonNegative_s.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;

/**
 * True if embedded node is non-Negative.
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


public class b_NonNegative_s extends NodeBoolean
	{
	/** 
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private NodeScalar	embedded1;

	/**
	Instantiate a b_NonNegative_s schema.
	@param im1 NodeScalar, the embedded node that generates a scalar
			to be detected.
	*/
	public b_NonNegative_s(NodeScalar im1)
		{
		if (DEBUG) System.out.println("b_NonNegative_s: instantiated.");
		embedded1 = im1;
		}

	boolean	last_val = false;
	long	lasttime = 0;
	/**
	Return a boolean indicating if the embedded schema
	is non-Negative.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return true if non-Negative, false if otherwise.
	*/
	public boolean Value(long timestamp)
		{
                if (DEBUG) System.out.println("b_NonNegative_s: Value()");
 
                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;
 
                        /*--- compute the output ---*/
                        double tmp = embedded1.doubleValue(timestamp);
			if (tmp >= 0)
				last_val = true;
			else
				last_val = false;
			if (false) System.out.println("non Negative " +last_val);
                        }

		return (last_val);
		}
        }

/*
 * b_Persist_s.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Persist on a true value of embedded
 * NodeBoolean for the prescribed amount of time.
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


public class b_Persist_s extends NodeBoolean
	{
	/**
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private long		last_true = -1;
	private long		timeout = -1;
	private NodeScalar	embedded1;

	/**
	Instantiate an b_Persist_s operator.
	@param t   double, how long a true value should persist (seconds).
	@param im1 NodeScalar, the embedded perceptual schema that generates a value
			to persist.
	*/
	public b_Persist_s(double t, NodeScalar im1)
		{
		if (DEBUG) System.out.println("opNot: instantiated.");
		embedded1 = im1;
		timeout = Math.abs((long)(t*1000));
		last_true = -timeout;
		}


	boolean	last_val = false;
	long	lasttime = 0;
	/**
	Return a boolean representing the persistant value of
	the output of the embedded schema.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return true or false.
	*/
	public boolean Value(long timestamp)
		{
                if (DEBUG) System.out.println("b_Persist_s: Value()");
 
                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;
 
                        /*--- compute the difference ---*/
                        last_val = embedded1.booleanValue(timestamp);
			if (last_val == true)
				{
				last_true = timestamp;
				}
			if ((timestamp-last_true)<=timeout)
				last_val = true;
			else
				last_val = false;
                        }

		return (last_val);
		}
        }

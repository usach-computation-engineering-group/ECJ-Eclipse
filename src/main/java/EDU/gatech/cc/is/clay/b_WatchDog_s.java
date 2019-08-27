/*
 * b_WatchDog_s.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Monitor a boolean node for true values.  If embedded node does not
 * go true before timeout, this node goes true.
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


public class b_WatchDog_s extends NodeBoolean
	{
	/** 
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = Node.DEBUG;
	private long		last_true = -1;
	private long		trigger = 0;
	private long		last_touch = -1;
	private long		period = 0;
	private long		expiration = -1;
	private NodeScalar	embedded1;
	private	boolean		triggered = false;

	/**
	Instantiate a b_WatchDog_s operator.
	@param t   double, how long im1 can be false before a trigger (seconds).
	@param p   double, how long a true value should persist (seconds) when triggered.
	@param im1 NodeScalar, the embedded node that generates a value
			to watch.
	*/
	public b_WatchDog_s(double t, double p, NodeScalar im1)
		{
		if (DEBUG) System.out.println("opNot: instantiated.");
		embedded1 = im1;
		trigger = Math.abs((long)(t*1000));
		period = Math.abs((long)(p*1000));
		}


	boolean	last_val = false;
	long	lasttime = 0;
	/**
	Return a boolean WatchDog value.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return true or false.
	*/
	public boolean Value(long timestamp)
		{
                if (DEBUG) System.out.println("b_WatchDog_s: Value()");
 
                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;
 
                        /*--- compute the value ---*/
			// check if triggered	
			if (triggered == true)
				{
				if (timestamp > expiration)
					{
					last_touch = timestamp;
					triggered = false;
					expiration = -1;
					last_val = false;
					}
				}
			else
				{
                        	last_val = embedded1.booleanValue(timestamp);
				if ((last_val == true)||(last_touch==-1))
					{
					last_touch = timestamp;
					}
				if ((timestamp-last_touch)>trigger)
					{
					triggered = true;
					expiration = timestamp + period;
					last_val = true;
					}
				else
					last_val = false;
				}
                        }

		return (last_val);
		}
        }

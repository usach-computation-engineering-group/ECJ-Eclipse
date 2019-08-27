/*
 * NodeBoolean.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;

/**
 * A Node that returns boolean values. Since it is an extension
 * of NodeScalar, it can return double and int values also.
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


public abstract class NodeBoolean extends NodeScalar
	{
	/**
	 * Provides the value of the node.
	 * If you implement a NodeBoolean, you need to define
	 * this method.
	 *
	 * @param timestamp long indicates time of the request
	 * @return the value
	 */
        public abstract boolean Value(long timestamp);

	/**
	 * Convert boolean output value to double.
	 *
	 * @param timestamp long indicates time of the request
	 * @return the double value
	 */
        public double doubleValue(long timestamp)
                {
                if (Value(timestamp))
			return(1.0);
		else
			return(0.0);
                }
 
	/**
	 * Convert boolean output to int.
	 *
	 * @param timestamp long indicates time of the request
	 * @return the int value
	 */
        public int intValue(long timestamp)
                {
                if (Value(timestamp))
			return(1);
		else
			return(0);
                }

	/**
	 * Get the boolean value.
	 *
	 * @param timestamp long indicates time of the request
	 * @return the boolean value
	 */
	public boolean booleanValue(long timestamp)
		{
		return(Value(timestamp));
		}
        }

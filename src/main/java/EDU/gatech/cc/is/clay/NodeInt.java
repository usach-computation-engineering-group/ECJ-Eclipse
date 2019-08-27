/*
 * NodeInt.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;

/**
 * A Node that returns int values. Since it is an extension
 * of NodeScalar, it can return boolean and double values also.
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


public abstract class NodeInt extends NodeScalar
	{
	/**
         * Provides the value of the node.
         * If you implement a NodeInt, you need to define
         * this method.
	 *
         * @param timestamp long indicates time of the request
         * @return the value
         */
        public abstract int Value(long timestamp);

        /**
         * Convert int output to double.
	 *
         * @param timestamp long indicates time of the request
         * @return the double value
         */
        public double doubleValue(long timestamp)
                {
                return (double)Value(timestamp);
                }
 
        /**
         * The int value.
	 *
         * @param timestamp long indicates time of the request
         * @return the int value
         */
        public int intValue(long timestamp)
                {
                return Value(timestamp);
                }

        /**
         * Convert int output to boolean.
	 *
         * @param timestamp long indicates time of the request
         * @return the boolean value (true if non-zero).
         */
        public boolean booleanValue(long timestamp)
                {
                if (Value(timestamp) != 0)
                        return(true);
                else
                        return(false);
		}
        }

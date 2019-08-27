/*
 * NodeScalar.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;

/**
 * A Node that returns int, double and boolean values. 
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


public abstract class NodeScalar extends Node
	{
        /**
         * Get the double value.
	 *
         * @param timestamp long indicates time of the request
         * @return the double value
         */
	public abstract	double doubleValue(long timestamp);


        /**
         * Get the int value.
	 *
         * @param timestamp long indicates time of the request
         * @return the int value
         */
        public abstract int    intValue(long timestamp);

        /**
         * Get the boolean value.
	 *
         * @param timestamp long indicates time of the request
         * @return the boolean value
         */
        public abstract boolean booleanValue(long timestamp);
        }

/*
 * NodeVec2.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;

/**
 * A Node that returns Vec2 values.
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


public abstract class NodeVec2 extends Node
	{
	/**
	 * Get the Vec2 value.
	 * If you implement a NodeVec2, you need to define
	 * this method.
	 *
	 * @param timestamp long, the time of the request
	 */
        public abstract Vec2 Value(long timestamp);
        }

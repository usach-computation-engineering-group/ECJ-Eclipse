/*
 * v_Select_vai.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Selects one from an array of embedded NodeVec2s based on the output
 * of an embedded integer schema.
 * Configuration is by setting the embedded array to point to
 * desired embedded NodeVec2s.
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


public class v_Select_vai extends NodeVec2
	{
	/**
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = /*true;*/Node.DEBUG;

	/**
	Maximum number of embedded nodes.
	*/
	public static final int MAX_EMBEDDED = 20;

	/**
	The array of embedded NodeVec2s to select from.
	*/
	public NodeVec2[]	embedded = new NodeVec2[MAX_EMBEDDED];

	private NodeInt		selector;

	/**
	Instantiate a v_Select_vai node.
	The embedded selector outputs an integer which
	indicates which of the nodes to activate.

	@param s  NodeInt, the selector.
	*/
	public v_Select_vai(NodeInt s)
		{
		if (DEBUG) System.out.println("v_Select_vai: instantiated.");
		selector = s;
		}


	Vec2	last_val = new Vec2();
	long	lasttime = 0;

	/**
	Return a Vec2 representing the selectively activated embedded
	node, based on the embedded selector.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the Vec2.
	*/
	public Vec2 Value(long timestamp)
		{
                if (DEBUG) System.out.println("v_Select_vai: Value()");
 
                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;
 
			/*--- get the selection ---*/
			int choice = Math.abs(selector.Value(timestamp));

                        /*--- add in the weighted value of each schema ---*/
			if (choice < embedded.length)
				{
				if (embedded[choice] != null)
					last_val = embedded[choice].Value(timestamp);
				else
					System.out.println("v_Select_vai: illegal"
						+" selection: "
						+ choice + " null."); 
				}
			else
				{
				System.out.println("v_Select_vai: illegal"
					+" selection: "
					+ choice + " out of range."); 
				}
			}

		return (new Vec2(last_val.x, last_val.y));
		}
        }

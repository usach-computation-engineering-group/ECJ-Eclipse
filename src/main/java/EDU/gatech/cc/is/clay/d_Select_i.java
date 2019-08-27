/*
 * d_Select_i.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;

/**
 * Select one from an array of doubles based on the output of
 * an embedded integer schema.  The embedded selector outputs 
 * an integer, which indicates which of the doubles to output.
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


public class d_Select_i extends NodeDouble
	{
	/**
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = /*true;*/Node.DEBUG;

	/**
	Maximum number of choices to select from.
	*/
	public static final int MAX_EMBEDDED = 20;

	/**
	The double values that are the outputs selected from.
	*/
	public double[]	embedded = new double[MAX_EMBEDDED];

	private NodeInt		selector;

	/**
	Instantiate a d_Select_i node.
	@param s  NodeInt, the selector.
	*/
	public d_Select_i(NodeInt s)
		{
		if (DEBUG) System.out.println("d_Select_i: instantiated.");
		selector = s;
		}


	double	last_val = 0;
	long	lasttime = 0;
	/**
	Return a double selectively activated 
	based on the embedded NodeInt selector.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the double.
	*/
	public double Value(long timestamp)
		{
                if (DEBUG) System.out.println("d_Select_i: Value()");
 
                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;
 
			/*--- get the selection ---*/
			int choice = Math.abs(selector.Value(timestamp));

                        /*--- add in the weighted value of each schema ---*/
			if (choice < embedded.length)
				{
				last_val = embedded[choice];
				}
			else
				{
				System.out.println("d_Select_i: illegal"
					+" selection: "
					+ choice); 
				}
			}

		return (last_val);
		}
        }

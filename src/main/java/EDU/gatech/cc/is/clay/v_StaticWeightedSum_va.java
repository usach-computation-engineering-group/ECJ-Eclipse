/*
 * v_StaticWeightedSum_va.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;

/**
 * Combine an array of embedded schemas using
 * static weights set at configuration time.
 * Configuration is done by setting the values of the embedded[]
 * and weights[] arrays.
 * <P>
 * This software module is based on the motor schema formulation developed
 * by Ronald C. Arkin described in "Motor Schema Based Mobile Robot
 * Navigation," <I>International Journal of Robotics Research</I>,
 * vol. 8, no 4, pp 92-112.
 * <P>
 * The source code in this module is based on "first principles"
 * (e.g. published papers) and is not derived from any previously
 * existing software.
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


public class v_StaticWeightedSum_va extends NodeVec2
	{
	/**
	Turn debug printing on or off.
	*/
	public static final boolean DEBUG = /*true;*/Node.DEBUG;
	
	/**
	Maximum number of components of an v_StaticWeightedSum_va node.
	*/
	public static final int MAX_COMPONENTS = 20;

	/**
	The embedded nodes to multiply by the weights and sum.
	*/
	public NodeVec2[]	embedded = new NodeVec2[MAX_COMPONENTS];

	/**
	The weights to multiply the nodes by.
	*/
	public double[]		weights  = new double[MAX_COMPONENTS];

	/**
	Instantiate a v_StaticWeightedSum_va node.
	*/
	public v_StaticWeightedSum_va()
		{
		}


	Vec2	last_val = new Vec2();
	long	lasttime = 0;
	/**
	Return a Vec2 representing the weighted sum of the embedded
	nodes.
	@param timestamp long, only get new information if timestamp > than last call
                or timestamp == -1.
	@return the vector weighted sum.
	*/
	public Vec2 Value(long timestamp)
		{
                if (DEBUG) System.out.println("v_StaticWeightedSum_va: Value()");
 
                if ((timestamp > lasttime)||(timestamp == -1))
                        {
                        /*--- reset the timestamp ---*/
                        if (timestamp > 0) lasttime = timestamp;
 
                        /*--- add in the weighted value of each schema ---*/
                        last_val.setr(0);
			Vec2 temp;
			int i = 0;
			while(embedded[i]!=null)
				{
                		if (DEBUG) System.out.println(i+" weight "+
					weights[i]);
				temp = embedded[i].Value(timestamp);
				temp.setr(temp.r*weights[i]);
				last_val.add(temp);
				i++;
				}
			}

		return (new Vec2(last_val.x, last_val.y));
		}
        }

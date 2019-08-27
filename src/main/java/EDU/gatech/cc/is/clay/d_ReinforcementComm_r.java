/*
 *  d_ReinforcementComm_r.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import java.util.Enumeration;
import EDU.gatech.cc.is.communication.*;

/**
 * Report the sum of recently received Reinforcement Learning Messages.
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


public class d_ReinforcementComm_r extends NodeDouble
	{
	/** 
	 * Turn debug printing on or off.
	 */
	public static final boolean DEBUG = Node.DEBUG;
	private Transceiver	abstract_robot = null;
	private Enumeration	channel = null;

	/**
	 * Instantiate a d_ReinforcementComm_r schema.
	 * @param ar the abstract_robot object that provides hardware support.
	 */
	public d_ReinforcementComm_r(Transceiver ar)
		{
		if (DEBUG) System.out.println("d_ReinforcementComm_r: instantiated");
		abstract_robot = ar;
		channel = ar.getReceiveChannel();
		}

        double	last_val = 0;
        long    lasttime = 0;
	/**
	 * Return a double representing the sum of reinforcement messages 
	 * received.
	 * @param timestamp long, only get new information 
	 *	if timestamp > than last call or timestamp == -1.
	 * @return the reinforcement
	 */
	public double Value(long timestamp)
		{
		if (DEBUG) System.out.println("d_ReinforcementComm_r: Value()");

                if ((timestamp > lasttime)||(timestamp == -1))
                        {
			if (timestamp != -1) lasttime = timestamp;

			last_val = 0;

			while (channel.hasMoreElements())
				{
				Message m = (Message)channel.nextElement();
				if (m instanceof ReinforcementMessage)
					{
					last_val +=
					((ReinforcementMessage)m).val;
					}
				else
					{
					}
				}
			}
		return(last_val);
		}
        }

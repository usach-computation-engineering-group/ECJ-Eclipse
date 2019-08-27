/*
 * i_Learner_id.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.learning.*;

/**
 * A node that uses a reinforcement learning module to learn 
 * over time which output to select, given the current state 
 * and reward.
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


public class i_Learner_id extends NodeInt
	{
	private	i_ReinforcementLearner_id rl;
	private	int	lastval=0;
	private	int	laststate=-1;
	private int	firsttime=1;
	private long	lasttime=-1;

	/**
	 * Instantiate a reinforcement learning node with a specified 
	 * learning module.
	 *
	 * @param m  i_ReinforcementLearning_id, the learning module.
	 * @param e1 NodeInt, a node that provides state information.
	 * @param e2 NodeScalar, a node that provides the reinforcement
	 *                       signal.
	 */
	public i_Learner_id(i_ReinforcementLearner_id m, NodeInt e1,
		NodeScalar e2)
		{
		rl = m;
		embedded_nodes = new Node[2];
		embedded_nodes[0] = e1;
		embedded_nodes[1] = e2;
		}
	
	/**
	 * Get the value of the node.
	 * 
	 * @param timestamp long, the time of the request.
	 */
	public int Value(long timestamp)
		{
		if ((timestamp != lasttime)||(timestamp == -1))
			{
			if (timestamp != -1) lasttime = timestamp;

			/*--- get state and reward values ---*/
			int state = ((NodeInt)embedded_nodes[0]).Value(timestamp);
			double reward = ((NodeScalar)embedded_nodes[1]).doubleValue(
				timestamp);

			/*--- call initTrial if it is the first call ---*/
			if (firsttime==1)
				{
				lastval = rl.initTrial(state);
				firsttime = 0;
				}
			/*--- call query otherwise ---*/
			else
				{
				/* Only query if the state has changed */
				if (state != laststate)
					lastval = rl.query(state,reward);
				}
			laststate = state;
			}
		return lastval;
		}
	}

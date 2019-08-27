/*
 * i_StepLearner_id.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;
import EDU.gatech.cc.is.learning.*;

/**
 * A node that uses a reinforcement learning module to learn 
 * over time which output to select, given the current state 
 * and reward.
 * Differs from i_Learner_id because it calls the learner each step.
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


public class i_StepLearner_id extends NodeInt
	{
	private	i_ReinforcementLearner_id rl;
	private	int	lastval=0;
	private	int	laststate=-1;
	private	double	lastreward=-999999999;
	private int	firsttime=1;
	private long	lasttime=-1;
	private long	max_in_one_state=-1;
	private long	last_state_time=-1;

	/**
	 * Instantiate a reinforcement learning node with a specified 
	 * learning module.
	 *
	 * @param m  i_ReinforcementLearning_id, the learning module.
	 * @param ms long, maximum time to stay in one state.
	 * @param e1 NodeInt, a node that provides state information.
	 * @param e2 NodeScalar, a node that provides the reinforcement
	 *                       signal.
	 */
	public i_StepLearner_id(i_ReinforcementLearner_id m, long ms,
		NodeInt e1,
		NodeScalar e2)
		{
		rl = m;
		max_in_one_state = ms;
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
			int state = ((NodeInt)embedded_nodes[0])
				.Value(timestamp);
			double reward = ((NodeScalar)embedded_nodes[1])
				.doubleValue(timestamp);

			/*--- call initTrial if it is the first call ---*/
			if (firsttime==1)
				{
				lastval = rl.initTrial(state);
				laststate = state;
				firsttime = 0;
				}

			/*--- call query otherwise ---*/
			else
				{
				// if the state is different
				if ((state != laststate)||
					// or we've been in this state
					// too long.
					(timestamp > (last_state_time + 
						max_in_one_state))||
					// or the reward changed
					(reward != lastreward))
					{
					lastval = rl.query(state,reward);
					last_state_time = timestamp;
					}
				}
			lastreward = reward;
			laststate = state;
			}
		return lastval;
		}
	}

/**
 * i_PriQLearner_id.java 
 */

package EDU.cmu.cs.coral.learning;

import	java.io.*;
import	EDU.gatech.cc.is.learning.*;
import EDU.cmu.cs.coral.util.*;
import EDU.cmu.cs.coral.util.PriorityQueue;

// uncomment next line if you are using JDK 1.1
//import com.sun.java.util.collections.*;

// uncomment next line if you are using JDK 1.2 or later
import java.util.*;

/**
 * An object that learns to select from several actions based on
 * a reward.  Uses the Prioritized Sweeping technique of Moore.
 * <P>
 * The module will learn to select a discrete output based on 
 * state and a continuous reinforcement input.  The "i"s in front 
 * of and behind the name imply that this class takes integers as 
 * input and output.  The "d" indicates a double for the reinforcement 
 * input (i.e. a continuous value). 
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch (tucker@cc.gatech.edu)
 * @author William Uther (will@cs.cmu.edu)
 * @version $Revision: 1.4 $
 */

public class i_PriQLearner_id extends i_ReinforcementLearner_id
	implements Cloneable, Serializable
	{
	/**
	 * Used to indicate the learner uses discounted rewards.
	 */
	public static final int DISCOUNTED = 1;

	protected	int	criteria = DISCOUNTED;	// assume discounted rewards
	
	final class tcount {
		int count;
		tcount(int ct) {
			count = ct;
		}
	}
	protected final class state {
		
		final int stateID;
		
		final Map[] nextStates;
		final double[] totalReward;
		final Set incommingStates;
		final double[] qValue;
		
		double stateValue;
		int action;
		double delta;	// this is how much we've changed since notifying our incomming states
		
		final double noTransQVal = 0.01;
		
		state(int id) {
			stateID = id;
			
			nextStates = new Map[numactions];
			totalReward = new double[numactions];
			incommingStates = new HashSet();
			qValue = new double[numactions];
			
			for (int i=0; i<numactions; i++) {
				nextStates[i] = new HashMap();
			}
			delta = 0;
		}
		
		void sawTransitionTo(state r, int action, double reward) {
			if (!nextStates[action].containsKey(r)) {
				nextStates[action].put(r, new tcount(1));
			} else {
				tcount ct = (tcount)nextStates[action].get(r);
				ct.count++;
			}
			totalReward[action] += reward;
			
			r.incommingStates.add(this);
		}
		
		double reCalcValueAction() {
			double bestVal = Double.NEGATIVE_INFINITY;
			int bestAction = -1;
			
			for (int i=0; i<numactions; i++) {
				if (qValue[i] > bestVal) {
					bestVal = qValue[i];
					bestAction = i;
				}
			}
			
			double diff = Math.abs(stateValue - bestVal);
			stateValue = bestVal;
			action = bestAction;
			
			return diff;
		}
		
		void reCalcQValue(int action) {
			double thisVal = 0;
			int totalCount = 0;
			Iterator it = nextStates[action].entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry)it.next();
				state toState = (state)entry.getKey();
				tcount count = (tcount)entry.getValue();
				
				thisVal += count.count*toState.stateValue;
				totalCount += count.count;
			}
			if (totalCount != 0) {
				thisVal *= gamma;
				thisVal += totalReward[action];
				thisVal /= totalCount;
			} else {
				thisVal = noTransQVal;
			}
			
			qValue[action] = thisVal;
		}
	}
	
	private final class stateComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			if (!(o1 instanceof state))
				return 0;
			if (!(o2 instanceof state))
				return 0;
			
			state e1 = (state)o1;
			state e2 = (state)o2;
			
			if (e1.delta == e1.delta)
				return 0;
			
			if (e1.delta > e2.delta)	// note this is reversed from normal order
				return -1;
			
			return 1;
		}
	}
	
	protected state[] states;
	protected PriorityQueue changeQueue;
	
	protected int numactions;
	
        private double  profile[][];            // count of times in each 
                                                // state/action for this trial
        private int     last_policy[];          // used to count changes in 
                                                // policy
        private int     changes = 0;            // used to count changes 
                                                // in policy per trial
	private	int	queries = 0;		// queries per trial
	private	double	total_reward = 0;	// reward over trial
        private boolean     first_of_trial = true;     // indicates if first time
        private double  gamma=0.99;              // discount rate
        private double  randomrate=0.1;         // frequency of random actions
        private double  randomratedecay=0.99;   // decay rate of random actions
        private Random  rgen;                   // the random number generator
        private int     xn;                     // last state
        private int     an;                     // last action
	private	long	seed=0;			// random number seed
	private static final boolean DEBUG=false;
	private int updateCount = 50;
	private double minUpdate = 1e-4;

	/**
	 * Instantiate a Prioritized Sweeping learner using default parameters.
         * Parameters may be adjusted using accessor methods.
	 *
	 * @param numstates  int, the number of states the system could be in.
	 * @param numactions int, the number of actions or outputs to 
         *                        select from.
	 * @param criteria   int, should be DISCOUNTED or AVERAGE.
	 * @param seed       long, the seed. 
	 */
	public i_PriQLearner_id(int numstatesin, int numactionsin, int criteriain,
		long seedin) {
		super(numstatesin, numactionsin);
		this.numactions = numactionsin;
		if (criteriain != DISCOUNTED) {
			System.out.println("i_PriQLearner_id: invalid criteria");
			criteria = DISCOUNTED;
		} else
			criteria = criteriain;
		seed = seedin;
		rgen = new Random(seed);
		changeQueue = new Heap(new stateComparator());
		states = new state[numstates];
		profile = new double[numstates][numactions];
		last_policy = new int[numstates];
		for(int i=0; i<numstates; i++) {
			for(int j=0; j<numactions; j++) {
				profile[i][j] = 0;
			}
			last_policy[i] = 0;
		}
		xn = an = 0;
	}

	/**
	 * Instantiate a Q learner using default parameters.
	 * This version assumes you will use a seed of 0.
         * Parameters may be adjusted using accessor methods.
	 *
	 * @param numstates  int, the number of states the system could be in.
	 * @param numactions int, the number of actions or outputs to 
         *                        select from.
	 * @param criteria   int, should be DISCOUNTED or AVERAGE.
	 */
	public i_PriQLearner_id(int numstatesin, int numactionsin, int criteriain) {
		this(numstatesin, numactionsin, criteriain, 0);
		this.numactions = numactionsin;
	}


	/**
	 * Instantiate a Q learner using default parameters.
	 * This version assumes you will use discounted rewards.
         * Parameters may be adjusted using accessor methods.
	 *
	 * @param numstates  int, the number of states the system could be in.
	 * @param numactions int, the number of actions or outputs to 
         *                        select from.
	 */
	public i_PriQLearner_id(int numstatesin, int numactionsin) {
		this(numstatesin, numactionsin, DISCOUNTED);
		this.numactions = numactionsin;
	}


	/**
	 * Set gamma for the Q-learner.
	 * This is the discount rate, 0.8 is typical value.
	 * It should be between 0 and 1.
	 *
	 * @param g double, the new value for gamma (0 < g < 1).
	 */
	public void setGamma(double g) {
		if ((g<0)||(g>1)) {
			System.out.println("i_PriQLearner_id.setGamma: illegal value");
			return;
		}
		gamma = g;
	}


	/**
	 * Set the random rate for the Q-learner.
	 * This reflects how frequently it picks a random action.
	 * Should be between 0 and 1.
	 *
	 * @param r double, the new value for random rate (0 < r < 1).
	 */
	public void setRandomRate(double r) {
		randomrate = r;
	}

	/**
	 * Set the random decay for the Q-learner.
	 * This reflects how quickly the rate of chosing random actions
	 * decays. 1 would never decay, 0 would cause it to immediately
	 * quit chosing random values.
	 * Should be between 0 and 1.
	 *
	 * @param r double, the new value for randomdecay (0 < r < 1).
	 */
	public void setRandomRateDecay(double r) {
		randomratedecay = r;
	}


	/**
	 * Generate a String that describes the current state of the
	 * learner.
	 *
	 * @return a String describing the learner.
	 */

	public String toString() {
		StringBuffer retval = new StringBuffer(super.toString());

		retval.append("type = i_PriQLearner_id gamma = " + gamma + "\n");
		
		for (int i=0; i<numstates; i++) {
			if (states[i] == null) {
				// retval.append("State " + i + " not seen.\n");
			} else {
				retval.append("State: " + i + ":");
				for (int j=0; j<numactions; j++) {
					retval.append(" ");
					retval.append(states[i].qValue[j]);
				}
				retval.append("\n");
			}
		}
		
		return retval.toString();
	}

	protected void updateState(state st) {
		st.delta = 0;
		
		for (int action = 0; action< numactions; action++) {
			st.reCalcQValue(action);
		}
		
		double diff = st.reCalcValueAction();
		
		if (diff > 1) {
			System.out.println("State " + st.stateID + " changed by " + diff + " to " + st.stateValue);
			System.out.println("X: " + (st.stateID%10) + " Y: " + ((st.stateID/10)%10));
			System.out.println("QueueSize: " + changeQueue.size());
		}
		
		if (diff > 0) {
			Iterator it = st.incommingStates.iterator();
			
			while (it.hasNext()) {
				state s = (state)it.next();
				
				s.delta += diff;
				if (changeQueue.contains(s))
					changeQueue.alteredKey(s);
				else
					changeQueue.add(s);
			}
		}
	}

	/**
	 * Select an output based on the state and reward.
	 *
	 * @param statein  int,    the current state.
	 * @param rewardin double, reward for the last output, positive
	 *                         numbers are "good."
	 */
	public int query(int yn, double rn) {
		//System.out.println("state "+yn+" reward "+rn);
		total_reward += rn;
		queries++;

		if ((yn < 0) || (yn>(numstates - 1))) {
			System.out.println("i_PriQLearner_id.query: state "+yn
				+" is out of range.");
			return 0;
		}

		// add this new transition
		
		if (states[yn] == null) {
			states[yn] = new state(yn);
		}

		if (!first_of_trial) {
			if (states[xn] == null)
				throw new UnexpectedException();
			states[xn].sawTransitionTo(states[yn], an, rn);
			profile[xn][an]++;

			// update our value table and policy

			// System.out.print("current state: ");

			updateState(states[xn]);	// must always update the state we just left
			
			// System.out.println("QueueSize: " + changeQueue.size());
			
			// run the updates till the max delta is minUpdate * (largest delta when we started)
			double limit = minUpdate;
			if (!changeQueue.isEmpty()) {
				state minState = (state)changeQueue.peekMin();
				limit *= minState.delta;
			}
			
			for (int i=0; i<updateCount-1; i++) {
				if (changeQueue.isEmpty())
					break;
				state st = (state)changeQueue.removeMin();
				double delta = st.delta;
				updateState(st);
				if (delta < limit)
					break;
			}
		} else
			first_of_trial = false;

		// choose the action

		int action;

		randomrate *= randomratedecay;
		if (rgen.nextDouble() <= randomrate) {
            action = Math.abs(rgen.nextInt() % numactions);
			if (true)
				System.out.println("random action, rate is " + randomrate);
		} else {
			action = states[yn].action;
/*
			if (states[yn].stateValue > 0.01)
				System.out.println("State value: " + states[yn].stateValue);
*/
		}

		 /*
		 * Remember for next time
		 */
		xn = yn;
		an = action;

		if (logging)
			CheckForChanges();

		return action;
	}


	/**
	 * Local method to see how much the policy has changed.
	 */
	private void CheckForChanges() {
		int i,j;

		for(i = 0; i<numstates; i++) {
			int     action = 0;
			if (states[i] != null) {
				action = states[i].action;
				if (last_policy[i] != action) {
					changes++;
					last_policy[i] = action;
				}
			}
		}
		if (logging)
			log(String.valueOf(changes));
	}


	/**
	 * Called when the current trial ends.
	 *
	 * @param Vn     double, the value of the absorbing state.
	 * @param reward double, the reward for the last output.
	 */
	public void endTrial(double Vn, double rn) {
		total_reward += rn;

		if (DEBUG)
			System.out.println("xn ="+xn+" an ="+an+" rn="+rn);

		state resultingState = new state(-1);
		
		resultingState.stateValue = Vn;

		// add this new transition
		
		if (!first_of_trial) {
			if (states[xn] == null)
				throw new UnexpectedException();
			states[xn].sawTransitionTo(resultingState, an, rn);
			profile[xn][an]++;

			// update our value table and policy

			updateState(states[xn]);	// must always update the state we just left
			
			for (int i=0; i<updateCount-1; i++) {
				state st = (state)changeQueue.removeMin();
				double delta = st.delta;
				updateState(st);
				if (delta < minUpdate)
					break;
			}
		}

		// record the policy

		if (logging) {
			CheckForChanges();
			try {
				savePolicy();
			} catch (IOException e) {
			}
		}
	}


	/**
	 * Called to initialize for a new trial.
	 */
	public	int initTrial(int s) {
		first_of_trial = true;
		changes = 0;
		queries = 0;
		total_reward = 0;
		profile = new double[numstates][numactions];
		System.out.println("Prioritized Sweeping init");
		return(query(s, 0));
	}


	/**
	 * Report the average reward per step in the trial.
	 * @return the average.
	 */
	public	double getAvgReward() {
		return(total_reward/(double)queries);
	}

	
	/**
	 * Report the number of queries in the trial.
	 * @return the total.
	 */
	public	int getQueries() {
		return(queries);
	}


	/**
	 * Report the number of policy changes in the trial.
	 * @return the total.
	 */
	public	int getPolicyChanges() {
		return(changes);
	}

	
	/**
	 * Read the policy from a file.
	 *
	 * @param filename String, the name of the file to read from.
	 */
	public void readPolicy() throws IOException {
		System.err.println(getClass().getName() + " readPolicy() not implemented!");
		return;
	}


	/**
	 * Write the policy to a file.
	 *
	 * @param filename String, the name of the file to write to.
	 */
	public void savePolicy() throws IOException {
		System.err.println(getClass().getName() + " readPolicy() not implemented!");
		return;
	}


	/**
	 * Write the policy profile to a file.
	 *
	 * @param filename String, the name of the file to write to.
	 */
	public void saveProfile(String profile_filename) throws IOException {
		int i, j;
		String  lineout;
		double	total_hits = 0;

		PrintWriter p = new PrintWriter(
			new BufferedWriter(
				new FileWriter(profile_filename)));

		p.println("// Policy profile:");
		p.println("// Q-learning Parameters:");
		p.println("// "+gamma + " // gamma");
		p.println("// "+randomrate + " // random rate");
		p.println("// "+randomratedecay + " // random rate decay");
		p.println("// Number of states ");
		p.println(numstates);
		p.println("// The profile. ");
		p.println("// proportion of hits, action, number of hits");

		/*--- total up all the state/action hits ---*/
		for(i=0; i<numstates; i++) {
			for(j=0; j<numactions; j++) {
				total_hits += (double) this.profile[i][j];
			}
		}

		for(i=0; i<numstates; i++) {
			double  Vn = Double.NEGATIVE_INFINITY;  //very bad
			int action = 0;
			int hits = 0;
			state s = states[i];
			for(j=0; j<numactions; j++) {
				hits += this.profile[i][j];
			}
			if (s != null) {
				action = s.action;
			}
			p.println((double)hits/total_hits + " " +
				action + " " + hits);
		}

		p.flush();
		p.close();

		return;
	}
}

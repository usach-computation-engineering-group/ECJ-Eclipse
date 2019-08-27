/**
 * i_QLearner_id.java 
 */

package EDU.gatech.cc.is.learning;

import	java.io.*;
import	java.util.*;

/**
 * An object that learns to select from several actions based on
 * a reward.  Uses the Q-learning method as defined by Watkins.
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
 * @version $Revision: 1.1 $
 */

public class i_QLearner_id extends i_ReinforcementLearner_id
	implements Cloneable, Serializable
	{
	/**
	 * Used to indicate the learner uses average rewards.
	 */
	public static final int AVERAGE = 0;

	/**
	 * Used to indicate the learner uses discounted rewards.
	 */
	public static final int DISCOUNTED = 1;

	private	int	criteria = DISCOUNTED;	// assume discounted rewards
        private double  q[][];                  // the q-values
        private double  p[][];                  // count of times in each 
                                                // state/action
        private double  profile[][];            // count of times in each 
                                                // state/action for this trial
        private int     last_policy[];          // used to count changes in 
                                                // policy
        private int     changes = 0;            // used to count changes 
                                                // in policy per trial
	private	int	queries = 0;		// queries per trial
	private	double	total_reward = 0;	// reward over trial
        private int     first_of_trial = 1;     // indicates if first time
        private double  gamma=0.8;              // discount rate
        private double  alpha=0.2;              // learning rate
        private double  randomrate=0.1;         // frequency of random actions
        private double  randomratedecay=0.99;   // decay rate of random actions
        private Random  rgen;                   // the random number generator
        private int     xn;                     // last state
        private int     an;                     // last action
	private	long	seed=0;			// random number seed
	private static final boolean DEBUG=false;


	/**
	 * Instantiate a Q learner using default parameters.
         * Parameters may be adjusted using accessor methods.
	 *
	 * @param numstates  int, the number of states the system could be in.
	 * @param numactions int, the number of actions or outputs to 
         *                        select from.
	 * @param criteria   int, should be DISCOUNTED or AVERAGE.
	 * @param seed       long, the seed. 
	 */
	public i_QLearner_id(int numstatesin, int numactionsin, int criteriain,
		long seedin)
		{
		super(numstatesin, numactionsin);
		if ((criteriain != DISCOUNTED)&&(criteriain != AVERAGE))
			{
			System.out.println("i_QLearner_id: invalid criteria");
			criteria = DISCOUNTED;
			}
		else
			criteria = criteriain;
		if (criteria == DISCOUNTED)
			System.out.println("i_QLearner_id: DISCOUNTED");
		else
			System.out.println("i_QLearner_id: AVERAGE");
		seed = seedin;
                rgen = new Random(seed);
                q = new double[numstates][numactions];
                profile = new double[numstates][numactions];
                p = new double[numstates][numactions];
                last_policy = new int[numstates];
                for(int i=0; i<numstates; i++)
                        {
                        for(int j=0; j<numactions; j++)
				{
                                q[i][j] = rgen.nextDouble()*2 - 1;
				p[i][j] = 0;
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
	public i_QLearner_id(int numstatesin, int numactionsin, int criteriain)
		{
		super(numstatesin, numactionsin);
		if ((criteriain != DISCOUNTED)&&(criteriain != AVERAGE))
			{
			System.out.println("i_QLearner_id: invalid criteria");
			criteria = DISCOUNTED;
			}
		else
			criteria = criteriain;
		if (criteria == DISCOUNTED)
			System.out.println("i_QLearner_id: DISCOUNTED");
		else
			System.out.println("i_QLearner_id: AVERAGE");
                rgen = new Random(seed);
                q = new double[numstates][numactions];
                profile = new double[numstates][numactions];
                p = new double[numstates][numactions];
                last_policy = new int[numstates];
                for(int i=0; i<numstates; i++)
                        {
                        for(int j=0; j<numactions; j++)
				{
                                q[i][j] = rgen.nextDouble()*2 - 1;
				p[i][j] = 0;
				profile[i][j] = 0;
				}
                        last_policy[i] = 0;
                        }
                xn = an = 0;
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
	public i_QLearner_id(int numstatesin, int numactionsin)
		{
		super(numstatesin, numactionsin);
		System.out.println("i_QLearner_id: DISCOUNTED");
		criteria = DISCOUNTED;
                rgen = new Random(seed);
                q = new double[numstates][numactions];
                profile = new double[numstates][numactions];
                p = new double[numstates][numactions];
                last_policy = new int[numstates];
                for(int i=0; i<numstates; i++)
                        {
                        for(int j=0; j<numactions; j++)
				{
                                q[i][j] = rgen.nextDouble()*2 - 1;
				p[i][j] = 0;
				profile[i][j] = 0;
				}
                        last_policy[i] = 0;
                        }
                xn = an = 0;
                }


	/**
	 * Set gamma for the Q-learner.
	 * This is the discount rate, 0.8 is typical value.
	 * It should be between 0 and 1.
	 *
	 * @param g double, the new value for gamma (0 < g < 1).
	 */
	public void setGamma(double g)
		{
		if ((g<0)||(g>1))
			{
			System.out.println("id_QLearner_i.setGamma: illegal value");
			return;
			}
		gamma = g;
		}


	/**
	 * Set alpha for the Q-learner.
	 * This reflects how quickly it should learn.
	 * Alpha should be between 0 and 1.
	 *
	 * @param a double, the new value for alpha (0 < a < 1).
	 */
	public void setAlpha(double a)
		{
		alpha = a;
		}


	/**
	 * Set the random rate for the Q-learner.
	 * This reflects how frequently it picks a random action.
	 * Should be between 0 and 1.
	 *
	 * @param r double, the new value for random rate (0 < r < 1).
	 */
	public void setRandomRate(double r)
		{
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
	public void setRandomRateDecay(double r)
		{
		randomratedecay = r;
		}


	/**
	 * Generate a String that describes the current state of the
	 * learner.
	 *
	 * @return a String describing the learner.
	 */
        public String toString()
                {
                int i, j;

		String retval = super.toString();
                retval = retval + "type = id_QLearner_i alpha = "
			+alpha+" gamma = "
                        +gamma+"\n";
                for (i=0; i<numstates;i++)
                        {
                        for (j=0; j<numactions;j++)
                                {
                                retval = retval + q[i][j] + "   ";
                                }
                        if (i<(numstates - 1)) retval += "\n";
                        }
                return retval;
                }

	/**
	 * Select an output based on the state and reward.
	 *
	 * @param statein  int,    the current state.
	 * @param rewardin double, reward for the last output, positive
	 *                         numbers are "good."
	 */
        public int query(int yn, double rn)
                {
		//System.out.println("state "+yn+" reward "+rn);
		total_reward += rn;
		queries++;

                // yn is present state, rn is present reward
                double  pick;
                int     action;

                if (yn>(numstates -1)) // very bad
                        {
                        System.out.println("id_QLearner_i.query: state "+yn
                                +" is out of range.");
                        return 0;
                        }

                /*
                 * Find approximate value of present state, and best action.
                 *
                 * ie:  max q[yn][i] over all i, i is the best action.
                 */
                double  Vn = -9999999999f;  //very bad
                action = 0;
                for (int i = 0; i < numactions; i++)
                        {
                        if (q[yn][i] > Vn)
                                {
                                Vn = q[yn][i];
                                action = i;
                                }
                        }

                /*
                 * Now update according to Watkin's iteration:
                 */
                if (first_of_trial != 1)
                        {
                        if (DEBUG) System.out.println(
				"xn ="+xn+" an ="+an+" rn="+rn);

			if (criteria == DISCOUNTED)
				{
                        	// Watkins update rule:
                        	q[xn][an] = (1 - alpha)*q[xn][an] +
                                	alpha*(rn + gamma*Vn);
				}
			else // criteria == AVERAGE
				{
                        	// Average update rule
                        	q[xn][an] = (p[xn][an] * q[xn][an] + rn + Vn)/
                        		(p[xn][an] + 2);
				// denominator: number of times did the last
				// action in the last state times the previous
				// average reward plus the current reward
				// plus the projected reward
				// numerator: number times did the last 
				// state/action plus this state plus next
				}

                        p[xn][an]++; //count times in the last state/action
                        profile[xn][an]++; //count times for this trial
                        }
                else
                        first_of_trial = 0;


                /*
                 * Select random action, possibly
                 */
                if (rgen.nextDouble() <= randomrate)
                        {
                        action = rgen.nextInt() % numactions;
                        if (action < 1) action = -1 * action;
                        if (true) System.out.println("random action, rate is " + randomrate);
                        }
                randomrate *= randomratedecay;

                /*
                 * Remember for next time
                 */
                xn = yn;
                an = action;

                if (logging) CheckForChanges();

                return action;
                }


	/**
	 * Local method to see how much the policy has changed.
	 */
        private void CheckForChanges()
                {
                int i,j;

                for(i = 0; i<numstates; i++)
                        {
                        double  val = -9999999999f;
                        int     action = 0;
                        for(j=0; j<numactions; j++)
                                {
                                if (q[i][j] > val)
                                        {
                                        action = j;
                                        val = q[i][j];
                                        }
                                }
                        if (last_policy[i] != action)
                                {
                                changes++;
                                last_policy[i] = action;
				}
			}
		if (logging) log(String.valueOf(changes));
		}


	/**
	 * Called when the current trial ends.
	 *
	 * @param Vn     double, the value of the absorbing state.
	 * @param reward double, the reward for the last output.
	 */
        public void endTrial(double Vn, double rn)
                {
		total_reward += rn;

                if (DEBUG) System.out.println(
			"xn ="+xn+" an ="+an+" rn="+rn);

		if (criteria == DISCOUNTED)
			{
                	// Watkins update rule:
                	q[xn][an] = (1 - alpha)*q[xn][an] +
                		alpha*(rn + gamma*Vn);
			}
		else // criteria == AVERAGE
			{
                	// average update rule
                	q[xn][an] = (p[xn][an] * q[xn][an] + rn)/
                		(p[xn][an] + 2);
			// see update above in query() for explanation 
			// of this rule
			}

                p[xn][an] += 1;
                profile[xn][an] += 1;

                if (logging)
                        {
                        CheckForChanges();
                        try
                                {
                                savePolicy();
                                }
                        catch (IOException e)
                                {
                                }
			}
		}


	/**
	 * Called to initialize for a new trial.
	 */
	public	int initTrial(int s)
		{
		first_of_trial = 1;
        	changes = 0;
		queries = 0;
		total_reward = 0;
                profile = new double[numstates][numactions];
		System.out.println("Q init");
		return(query(s, 0));
		}


	/**
	 * Report the average reward per step in the trial.
	 * @return the average.
	 */
	public	double getAvgReward()
		{
		return(total_reward/(double)queries);
		}

	
	/**
	 * Report the number of queries in the trial.
	 * @return the total.
	 */
	public	int getQueries()
		{
		return(queries);
		}


	/**
	 * Report the number of policy changes in the trial.
	 * @return the total.
	 */
	public	int getPolicyChanges()
		{
		return(changes);
		}

	
	/**
	 * Read the policy from a file.
	 *
	 * @param filename String, the name of the file to read from.
	 */
        public void readPolicy() throws IOException
                {
                int i, j, k;
                String  linein;

                FileInputStream f;
		InputStreamReader isr;
                StreamTokenizer p;

                try
                        {
                        f = new FileInputStream(policyfilename);
			isr = new InputStreamReader(f);
                        p = new StreamTokenizer(isr);
                        }
                catch (SecurityException e)
                        {
                        return;
                        }

                // configure the tokenizer
                p.parseNumbers();
                p.slashSlashComments(true);
                p.slashStarComments(true);

                k = p.nextToken();
                alpha = p.nval;

                k = p.nextToken();
                gamma = p.nval;

                k = p.nextToken();
                randomrate = p.nval;

                // to get around java bug that can't read e-xxx nums
                if (randomrate > 1.0)
                        {
                        k = p.nextToken();
                        randomrate = 0;
                        }

                k = p.nextToken();
                randomratedecay = p.nval;

                for(i=0; i<numstates; i++)
                        {
                        for(j=0; j<numactions; j++)
                                {
                                k = p.nextToken();
                                this.p[i][j] = p.nval;
                                k = p.nextToken();
                                q[i][j] = p.nval;
                                }
                        }

                f.close();

                return;
                }


	/**
	 * Write the policy to a file.
	 *
	 * @param filename String, the name of the file to write to.
	 */
        public void savePolicy() throws IOException
                {
                int i, j;
                String  lineout;

                FileOutputStream f = new FileOutputStream(policyfilename);
                PrintWriter p = new PrintWriter(f);

                p.println("// Q-learning Parameters:");
                p.println(alpha + " // alpha");
                p.println(gamma + " // gamma");
                p.println(randomrate + " // random rate");
                p.println(randomratedecay + " // random rate decay");
                p.println("// The policy. ");
                p.println("// The first number is the hits in that ");
                p.println("// state/action, the following num is the s/a ");
                p.println("// Q-value. ");

                for(i=0; i<numstates; i++)
                        {
                        for(j=0; j<numactions; j++)
                                {
                                p.print(this.p[i][j] + " ");
                                p.print(q[i][j] + " ");
                                }
                        p.println();
                        }

                f.close();

                return;
                }


	/**
	 * Write the policy profile to a file.
	 *
	 * @param filename String, the name of the file to write to.
	 */
        public void saveProfile(String profile_filename) throws IOException
                {
                int i, j;
                String  lineout;
		double	total_hits = 0;

                PrintWriter p = new PrintWriter(
				new BufferedWriter(
				new FileWriter(profile_filename)));

                p.println("// Policy profile:");
                p.println("// Q-learning Parameters:");
                p.println("// "+alpha + " // alpha");
                p.println("// "+gamma + " // gamma");
                p.println("// "+randomrate + " // random rate");
                p.println("// "+randomratedecay + " // random rate decay");
                p.println("// Number of states ");
		p.println(numstates);
                p.println("// The profile. ");
                p.println("// proportion of hits, action, number of hits");

		/*--- total up all the state/action hits ---*/
                for(i=0; i<numstates; i++)
                        {
                        for(j=0; j<numactions; j++)
                                {
				total_hits += (double) this.profile[i][j];
                                }
                        }

                for(i=0; i<numstates; i++)
                        {
                	double  Vn = -9999999999f;  //very bad
                	int action = 0;
			int hits = 0;
                        for(j=0; j<numactions; j++)
                                {
				hits += this.profile[i][j];
                        	if (q[i][j] > Vn)
                                	{
                                	Vn = q[i][j];
                                	action = j;
                                	}
                                }
			p.println((double)hits/total_hits + " " +
				action + " " + hits);
                        }

		p.flush();
		p.close();

                return;
                }
	}


/**
 * i_SLearner_id.java 
 */

package EDU.gatech.cc.is.learning;

import	java.io.*;
import	java.util.*;

/**
 * An object that learns to select from several actions based on
 * a reward.  Uses the S-learning method as defined by Mataric.
 * "S" is for "single-step."
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

public class i_SLearner_id extends i_ReinforcementLearner_id
	implements Cloneable, Serializable
	{
        private double  a[][];                  // the q-values
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
        private int     xn;                     // last state
        private int     an;                     // last action
	private static final boolean DEBUG=false;


	/**
	 * Instantiate an S learner.
	 *
	 * @param numstates  int, the number of states the system could be in.
	 * @param numactions int, the number of actions or outputs to 
         *                        select from.
	 * @param criteria   int, IGNORED, but retained for compatibility.
	 */
	public i_SLearner_id(int numstatesin, int numactionsin, int criteriain)
		{
		super(numstatesin, numactionsin);
		System.out.println("i_SLearner_id");
                a = new double[numstates][numactions];
                profile = new double[numstates][numactions];
                p = new double[numstates][numactions];
                last_policy = new int[numstates];
                for(int i=0; i<numstates; i++)
                        {
                        for(int j=0; j<numactions; j++)
				{
                                a[i][j] = 0;
				p[i][j] = 0;
				profile[i][j] = 0;
				}
                        last_policy[i] = 0;
                        }
                xn = an = 0;
		}


	/**
	 * Instantiate an S learner using default parameters.
	 *
	 * @param numstates  int, the number of states the system could be in.
	 * @param numactions int, the number of actions or outputs to 
         *                        select from.
	 */
	public i_SLearner_id(int numstatesin, int numactionsin)
		{
		super(numstatesin, numactionsin);
		System.out.println("i_SLearner_id: DISCOUNTED");
                a = new double[numstates][numactions];
                profile = new double[numstates][numactions];
                p = new double[numstates][numactions];
                last_policy = new int[numstates];
                for(int i=0; i<numstates; i++)
                        {
                        for(int j=0; j<numactions; j++)
				{
                                a[i][j] = 0;
				p[i][j] = 0;
				profile[i][j] = 0;
				}
                        last_policy[i] = 0;
                        }
                xn = an = 0;
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
		total_reward += rn;
		queries++;

                // yn is present state, rn is present reward
                double  pick;
                int     action;

                if (yn>(numstates -1)) // very bad
                        {
                        System.out.println("id_SLearner_i.query: state "+yn
                                +" is out of range.");
                        return 0;
                        }

                /*
                 * Find approximate value of present state, and best action.
                 *
                 * ie:  max a[yn][i] over all i, i is the best action.
                 */
                double  Vn = -9999999999f;  //very bad
                action = 0;
                for (int i = 0; i < numactions; i++)
                        {
			// choose untried if available
                        if (p[yn][i] == 0)
				{
				Vn = 0;
				action = i;
				break;
				}
			// else choose "best" one
			else if ((a[yn][i]/p[yn][i]) > Vn)
                                {
                                Vn = a[yn][i]/p[yn][i];
                                action = i;
                                }
                        }

                /*
                 * Now update according to Mataric's iteration:
                 */
                if (first_of_trial != 1)
                        {
                        if (DEBUG) System.out.println(
				"xn ="+xn+" an ="+an+" rn="+rn);
			a[xn][an] += rn; // reward last state/action
                        p[xn][an]++; //count times in last state/action
                        profile[xn][an]++; //count times for this trial
                        }
                else
                        first_of_trial = 0;

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
				// choose untried if available
                        	if (p[i][j] == 0)
					{
					val = 0;
					action = j;
					break;
					}
                                else if ((a[i][j]/p[i][j]) > val)
                                        {
                                        action = j;
                                        val = a[i][j]/p[i][j];
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
                if (DEBUG) System.out.println(
			"xn ="+xn+" an ="+an+" rn="+rn);

		a[xn][an] += rn;
                p[xn][an] += 1;
                profile[xn][an] += 1;
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
		System.out.println("A init");
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
                p.println("// S-learning");
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
                        	if (a[i][j] > Vn)
                                	{
                                	Vn = a[i][j];
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


	public void readPolicy()
		{
		}

	public void savePolicy()
		{
		}
	}



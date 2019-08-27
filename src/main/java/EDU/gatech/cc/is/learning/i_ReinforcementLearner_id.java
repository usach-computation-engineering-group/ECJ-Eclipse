/**
 * i_ReinforcementLearner_id.java 
 */

package EDU.gatech.cc.is.learning;

import	java.io.*;

/**
 * Reinforcement learning class with discrete inputs and outputs.  
 * The module will learn to select a discrete output based on 
 * state and a continuous reinforcement input.  The "i"s in front 
 * of and behind the name imply that this class takes integers as 
 * input and output.  The "d" indicates a double for the reinforcement 
 * input (i.e. a continuous value).  No committment is made to 
 * model-free or model-based learning, the class may be extended to either. 
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch (tucker@cc.gatech.edu)
 * @version $Revision: 1.1 $
 */

public abstract class i_ReinforcementLearner_id
	implements Cloneable, Serializable
	{
	/**
	 * Indicates whether logging is turned on or not.
	 */
	protected boolean	logging = false;

	/**
	 * The number of situations or states the object may be in.
	 */
	protected int		numstates;

	/**
	 * The number of actions to select from.
	 */
	protected int		numactions;

	/**
	 * The name of the file to save or read the policy in/from.
	 */
	protected String	policyfilename="default.policy";

	private String	logfilename = "";
	private FileOutputStream logfile;
	private PrintWriter	logstream;


	/**
	 * Instantiates a reinforcement learner using default parameters.
         * Individual implementations may allow additional
	 * parameters to be adjusted using accessor methods.
	 *
	 * @param numstates  int, the number of states the system could be in.
	 * @param numactions int, the number of actions or outputs to select from.
	 */
	public i_ReinforcementLearner_id(int numstatesin, int numactionsin)
		{
		numstates = numstatesin;
		numactions = numactionsin;
		}

        /**
         * Generate a String that describes the learner.
         *
         * @return a String describing the learner.
         */
        public String toString()
                {
                int i, j;

                String retval = "Reinforcement learner with "+numstates+
			" states and "+numactions+" actions \n";
		return(retval);
		}

	/**
	 * Select an output based on the state and reward.
	 *
	 * @param statein  int,    the current state.
	 * @param rewardin double, reward for the last output, positive
	 *                         numbers are "good."
	 */
	public	abstract int query(int statein, double rewardin);


	/**
	 * Called when the current trial ends.
	 * This is used for tasks that have "absorbing states."
	 *
	 * @param V      double, the value of the absorbing state. Usually
         *                       a large positive number if the final state
	 *                       is desirable, or negative otherwise.
	 * @param reward double, the single-step reward for the last 
	 *		         state/action.
	 */
	public	abstract void endTrial(double V, double reward);


	/**
	 * Called to initialize for a new trial and get the
	 * first action.
	 *
	 * @param statein  int,    the current state.
	 */
	public	abstract int initTrial(int statein);


	/**
	 * return statistical info about the learner.
	 * Logging must be on for this to work.
	 *
	 * @return the average reward per query for the trial.
	 */
	public	double getAvgReward()
		{
		return(0);
		}


	/**
	 * return statistical info about the learner.
	 * Logging must be on for this to work.
	 *
	 * @return the number of policy changes for the trial.
	 */
	public	int getPolicyChanges()
		{
		return(0);
		}


	/**
	 * return statistical info about the learner.
	 * Logging must be on for this to work.
	 *
	 * @return the number of queries for the trial.
	 */
	public	int getQueries()
		{
		return(0);
		}


	/**
	 * Set the filename for policy reading and writing.
	 *
	 * @param filename String, the directory and filename to use.
	 */
	public	void	setPolicyFileName(String filename)
		{
		policyfilename = filename;
		}

	/**
	 * Turn on logging for data gathering.  It is up to
	 * the extended classes to actually do the writing to the file.
	 *
	 * @param filename String, the directory and filename to use.
	 */
	public	void	loggingOn(String filename)
		{
		logfilename = filename;
		logging = true;
		try
			{
                	logfile = 
				new FileOutputStream(logfilename);
                	logstream = new PrintWriter(
				logfile);
			}
		catch (IOException e)
			{
			logging = false;
			System.out.println("id_ReinforcementLearner_i: can't open logfile "
				+logfile);
			}
		}


	/**
	 * Print something to the logfile. Called by the classes that 
	 * extend this one.
	 *
	 * @param out String, the string to print.
	 */
	public	void	log(String out)
		{
		if ((logging)&&(logstream!=null))
			{
			logstream.println(out);
			}
		}
			

	/**
	 * Turn logging off.
	 */
	public	void	loggingOff()
		{
		logging = false;
		try
			{
			logfile.close();
			}
		catch (IOException e)
			{
			}
		}

	
	/**
	 * Turn logging on, but without opening a file.
	 * This indicates we should keep track of some items, but not
	 * save them to a file.
	 */
	public	void	loggingOn()
		{
		logging = true;
		}

	
	/**
	 * Read the policy from a file.
	 * Use setPolicyFileName() to determine where the policy is saved.
	 *
	 * @param filename String, the name of the file to read from.
	 */
	public abstract void readPolicy() throws IOException;


	/**
	 * Write the policy to a file.
	 * Use setPolicyFileName() to determine where the policy is read from.
	 *
	 * @param filename String, the name of the file to write to.
	 */
	public abstract void savePolicy() throws IOException;
	}

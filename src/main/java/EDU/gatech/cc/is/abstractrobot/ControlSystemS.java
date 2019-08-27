/*
 * ControlSystemS.java
 */

package EDU.gatech.cc.is.abstractrobot;

/**
 * This is the superclass for all robot Control Systems.
 * When you create a contol system by extending this class,
 * it can run within JavaBotHard to control a real robot
 * or JavaBotSim in simulation.
 * <P>
 * Ordinarily this sort of class would be declared abstract.
 * But because robots and control systems must be instantiated on
 * the fly, this class had to be fully implemented.
 *
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class ControlSystemS
	{
	/**
	 * Return value from TakeStep(), indicates everything is OK.
	 */
	public static final int CSSTAT_OK=0;


	/**
	 * Return value from TakeStep(), indicates some sort of error condition
	 * has occured.
	 */
	public static final int CSSTAT_ERROR=1;


	/**
	 * Return value from TakeStep(), indicates the mission is complete.
	 */
	public static final int CSSTAT_DONE=2;


	/**
	 * The robot to which the control system is attached.
	 */
	public Simple abstract_robot;


	/**
	 * The random number seed to use in configuration or whatever.
	 */
	public long seed;


	/**
	 * Constructor. Don't override this method, use Configure instead.
	 */
	public ControlSystemS()
		{
		}


	/**
	 * DEPRECATED, use init() instead.
         * Initialize the object. Don't override this method, use 
	 * Configure instead.
	 * @deprecated to conform with Java naming conventions, use init().
	 */
	public	void Init(Simple r)
		{
		abstract_robot = r;
		}


	/**
         * Initialize the object. Don't override this method, use 
	 * Configure instead.
	 * @param r Simple, the robot hardware.
	 * @param s long, random number seed.
	 */
	public	void init(Simple r, long s)
		{
		seed = s;
		Init(r);
		}


        /**
	 * DEPRECATED, Use configure() instead.
	 * @deprecated to conform with Java naming conventions, use configure().
         */
        public  void Configure()
                {
                }


        /**
         * Override this method if you like,
         * to configure your control system.
         */
        public  void configure()
                {
		Configure();
                }


	/**
	 * Get a copy of the abstract robot object.
	 * @return the abstract robot object.
	 */
	public Simple getAbstractRobot()
		{
		return(abstract_robot);
		}


	/**
	 * Called every timestep to allow the control system to
	 * run.  You should override this method for your control
	 * system.  Note: this is the proper name ("TakeStep" does
	 * not comform to Java naming conventions).
	 */
	public int takeStep()
		{
		return(TakeStep());
		}


	/**
	 * DEPRECATED, Use takestep() instead.
	 * Called every timestep to allow the control system to
	 * run.  You should override this method for your control
	 * system.  
	 * @deprecated to conform with Java naming conventions,
	 *	use takestep() instead.
	 */
	public int TakeStep()
		{
		return(CSSTAT_OK);
		}
	

	/**
	 * DEPRECATED, Use trialInit() instead.
	 * Called at the beginning of every trial.  You can
	 * override this method to read configuration information from
	 * a file or to reset variables.
	 * @deprecated to conform with Java naming conventions,
	 *	use trialInit() instead.
	 */
	public void TrialInit()
		{
		}
	

	/**
	 * Called at the beginning of every trial.  You can
	 * override this method to read configuration information from
	 * a file or to reset variables.
	 */
	public void trialInit()
		{
		}


	/**
	 * DEPRECATED, Use trialEnd() instead.
	 * Called at the end of every trial.  You can
	 * override this method to save configuration information to a
	 * file or to reset variables.
	 * @deprecated to conform with Java naming conventions,
	 *	use trialEnd() instead.
	 */
	public void TrialEnd()
		{
		}
	

	/**
	 * Called at the end of every trial.  You can
	 * override this method to save configuration information to a
	 * file or to reset variables.
	 */
	public void trialEnd()
		{
		}
	

	/**
	 * Called only once, at the end of the simulation or hard run.  
	 * Override it to save log data to a file.
	 */
	public void quit()
		{
		}
	}

/*
 * ControlSystemSS.java
 */

package EDU.cmu.cs.coral.abstractrobot;
import EDU.gatech.cc.is.abstractrobot.*;

/**
 * This is the superclass for a Cye robot Control System.
 * When you create a contol system by extending this class,
 * it can run within JavaBotHard to control a real robot (maybe someday)
 * or JavaBotSim in simulation.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1999, 2000 CMU
 *
 * @see Simple
 * @author Rosemary Emery
 * @version $Revision: 1.2 $
 */

public class ControlSystemCye extends ControlSystemS
	{
	protected SimpleCye abstract_robot;

	/**
	 * Initialize the object.  Don't override this method, use
	 * Configure instead.
	 */
	public 	final void init(Simple ar, long s)
		{
                super.init(ar, s);
                abstract_robot = ((SimpleCye) ar);
		}
	}

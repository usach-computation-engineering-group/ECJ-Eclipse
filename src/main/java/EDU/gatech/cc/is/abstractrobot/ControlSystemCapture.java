/*
 * ControlSystemCapture.java
 */

package EDU.gatech.cc.is.abstractrobot;

/**
 * This is the superclass for a MultiForageN150 robot Control System.
 * When you create a contol system by extending this class,
 * it can run within JavaBotHard to control a real robot
 * or JavaBotSim in simulation.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @see Simple
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class ControlSystemCapture extends ControlSystemS
	{
	protected Capture abstract_robot;

	/**
	 * Initialize the object. Don't override this method,
	 * use Configure instead.
	 */
	public final void init(Simple ar, long s)
		{
		super.init(ar,s);
		abstract_robot = ((Capture) ar);
		}
	}

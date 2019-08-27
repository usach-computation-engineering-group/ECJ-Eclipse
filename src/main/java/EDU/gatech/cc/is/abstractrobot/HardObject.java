/*
 * HardObject.java
 */

package EDU.gatech.cc.is.abstractrobot;

/**
 * If you want to control a real robot you must implement  this interface.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public interface HardObject
	{
        /**
         * Take a hardware step.  This method should execute commands
	 * issued to the control API by the control system and return
	 * immediately.
         */
	public abstract void takeStep();
	}


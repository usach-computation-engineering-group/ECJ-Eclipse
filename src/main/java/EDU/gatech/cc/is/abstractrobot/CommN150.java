/*
 * CommN150.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.communication.Transceiver;
import EDU.gatech.cc.is.util.*;


/**
 * Provides an abstract interface to the hardware of
 * a communicating Nomad 150 robot.  In addition to the 
 * basic capabilities of a SimpleN150 robot a CommN150 robot
 * can communicate.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 * @see CommN150Hard
 * @see CommN150Sim
 */

public interface CommN150 extends SimpleN150, Transceiver, KinSensor
	{
	}

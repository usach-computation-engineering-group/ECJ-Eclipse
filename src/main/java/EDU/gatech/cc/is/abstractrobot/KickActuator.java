/*
 * KickActuator.java
 */

package EDU.gatech.cc.is.abstractrobot;

/**
 * Interface to a kicking actuator for a soccer robot.
 * <P>
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker R. Balch
 *
 * @see SocSmall
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public interface KickActuator
	{
        /**
         * Reveals whether or not the ball is in a position to be kicked.
         * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1.
         * @return true if the ball can be kicked, false otherwise.
         */
	public boolean canKick(long timestamp);

	/**
	 * If the ball can be kicked, kick it.
         * @param timestamp not used, but retained for compatibility.
	 */
	public void kick(long timestamp);
	}

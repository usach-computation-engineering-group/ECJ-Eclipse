/*
 * SocRef.java
 */

package EDU.gatech.cc.is.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;


/**
 * methods of a Soccer Referee.
 * For this simulation system they are implemented by the ball.
 * That's right, the ball is the referee.
 * <P>
 * Copyriht (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public interface SocRef
	{
	/**
	 * True if the game is underway.  False means
	 * the players should return to their starting
	 * positions.
	 */
	public	boolean	playBall();

	/**
	 * True if the east team gets to kick off this time.
	 */
	public	boolean	eastKickOff();

	/**
	 * True if the west team gets to kick off this time.
	 */
	public	boolean	westKickOff();

	/**
	 * True if the east team scored during the last timestep.
	 */
	public	boolean	eastJustScored();

	/**
	 * True if the west team scored during the last timestep.
	 */
	public	boolean	westJustScored();
	}


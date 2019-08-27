/*
 * GolfBallNoiseSim.java
 */

package EDU.gatech.cc.is.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;


/**
 * A noisy golfball for RoboCup Soccer.
 * <B>Introduction</B><BR>
 * GolfBallNoiseSim implements a golf ball for RoboCup
 * soccer simulations.  The ball is also the scorekeeper and 
 * the referee; after all who would know better whether a 
 * scoring event occured?
 * <P>
 * A "shot clock" keeps track of how long since a scoring
 * event occured.  If it times-out, the ball is reset to the
 * center of the field.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class GolfBallNoiseSim extends GolfBallSim 
	{
	private final static double NOISE_MAG = 0.1;

	/**
	 * Handle a push.  This is how to kick or push the ball.
	 */
        public void push(Vec2 d, Vec2 v)
                {
                /*--- move according to the push ---*/
                position.add(d);
		velocity = new Vec2(v.x, v.y);
		velocity.sett(velocity.t + (rando.nextDouble()*NOISE_MAG));
                }
	}

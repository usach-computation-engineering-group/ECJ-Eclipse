/*
 * AttractorPermSim.java
 */

package EDU.gatech.cc.is.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.communication.Message;


/**
 * A simple attractor for simulation.  This attractor differs from
 * AttractorSim in that it remains once it is putDown.
 *
 * <P>
 * Copyright (c)2000 by Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.3 $
 */

public class AttractorPermSim extends AttractorSim implements SimulatedObject
	{
	public void putDown(Vec2 p)
		{
		picked_up = false;
		deposited = true;
		position = p;

		/*--- make invisible if in bin ---*/
		if (p.r<=1.0) setVisionClass(-1);// only diff from AttractorSim
		}
	}

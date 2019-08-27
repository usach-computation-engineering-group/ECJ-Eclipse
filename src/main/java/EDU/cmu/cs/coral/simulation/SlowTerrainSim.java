/*
 * SlowTerrainSim.java
 */

package EDU.cmu.cs.coral.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.simulation.*;
import EDU.gatech.cc.is.communication.Message;


/**
 * a road for simulation.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch and Carnegie Mellon University
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class SlowTerrainSim extends TerrainSim
	{
	private	double TRAVERSABILITY = 0.5;

        public double getTraversability()
                {
                return(TRAVERSABILITY);
                }
	}

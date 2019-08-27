/*
 * SimulatedTerrainObject.java
 */

package EDU.cmu.cs.coral.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.simulation.*;


/**
 * If you want to include a new terrain object for TeamBots simulation, 
 * you must implement  this interface.
 * <P>
 * These methods are used by other simulated objects to either
 * generate simulated sensor values or reproduce accurate dynamic results.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch and Carnegie Mellon University
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public interface SimulatedTerrainObject extends SimulatedObject
	{
        /**
       	 * Return the traversability of this terrain. Values greater
	 * than 1.0 indicate faster than normal speeds (e.g. highways)
	 * values less than 1.0 indicate slow terrain (e.g. forest, etc).
	 * @returns the benefit of this terrain.
         */
	public abstract double getTraversability();
	}

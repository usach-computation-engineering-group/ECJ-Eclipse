/*
 * SimulatedLinearObject.java
 */

package EDU.cmu.cs.coral.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.communication.Message;
import EDU.gatech.cc.is.simulation.*;


/**
 * If you want to include a new linear object for TB simulation, 
 * you must implement  this interface.
 * <P>
 * Most of these methods are used by other simulated objects to either
 * generate simulated sensor values or reproduce accurate dynamic results.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch and Carnegie Mellon University
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public interface SimulatedLinearObject extends SimulatedObject
	{
        /**
       	 * Initialize a simulated object.  Called automatically by
	 * TBSim.
	 * @param x1 x coord of first point
	 * @param y1 y coord of first point
	 * @param x2 x coord of second point
	 * @param y2 y coord of second point
	 * @param r radius.
	 * @param fg the foreground color of the object when drawn.
	 * @param bg the background color of the object when drawn.
	 * @param vc the vision class of the object - for use 
	 *	by simulated vision.
	 * @param id a unique ID number fore the object.
	 * @param s  random number seed.
         */
	public abstract void init(double x1, double y1, 
			double x2, double y2, double r,
			Color fg, Color bg, int vc, int id, long s);
	}


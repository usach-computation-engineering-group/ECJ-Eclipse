/* This code is part of the simulation package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */

package EDU.cmu.cs.coral.simulation;

import java.awt.*;
import EDU.cmu.cs.coral.simulation.LineSim;
import EDU.gatech.cc.is.simulation.ObstacleSim;
import EDU.cmu.cs.coral.util.Circle2;
import EDU.cmu.cs.coral.util.Polygon2;
import EDU.gatech.cc.is.util.Vec2;

public class ColorTransitionSim extends LandmarkSim {

 
  public boolean checkCollision(Circle2 c) { return false; }
  public boolean checkCollision(Polygon2 p) { return false;}

  /**
    * Draw the object.
    */
  public void draw(Graphics g, int w, int h,
		   double t, double b, double l, double r)
    {
      double meterspp = (r - l) / (double)w;
      //     if (DEBUG) System.out.println("meterspp "+meterspp);
      int radius = (int)(RADIUS / meterspp);
      int xpix = (int)((position.x - l) / meterspp);
      int ypix = (int)((double)h - ((position.y - b) / meterspp));
      // if (DEBUG) System.out.println("robot at"+
      //			    " at "+xpix+","+ypix);
      
      /*--- draw the main body ---*/
   
      g.setColor(foreground);
      g.fillArc(xpix - radius, ypix - radius,
		 radius+radius , radius+radius, 90,180 );

      g.setColor(background);
      g.fillArc(xpix - radius, ypix - radius,
		radius+radius , radius+radius, 90,-180 );
      
    }

}

    

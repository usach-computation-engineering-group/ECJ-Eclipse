/*
 * AttractorHurtPersonSim.java
 */

package EDU.cmu.cs.coral.simulation;

import java.awt.*;
import java.util.Random;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.simulation.*;


/**
 * A person.
 *
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997 Georgia Tech Research Corporation
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class AttractorHurtPersonSim extends AttractorPermSim
	{
	public final static double RADIUS = 1.0;

	/**
	 * Draw the person as an icon.
	 */
	public void drawIcon(Graphics g, int w, int h,
		double t, double b, double l, double r)
		{
		top =t; bottom =b; left =l; right = r;
		
		if(picked_up != true)
			{
			double meterspp = (r - l) / (double)w;
			if (DEBUG) System.out.println("meterspp "+meterspp);
			int radius = 8;
			int xpix = (int)((position.x - l) / meterspp);
			int ypix = (int)((double)h - ((position.y - b) / meterspp));
			if (DEBUG) System.out.println("person at"+
				" at "+xpix+","+ypix);
	
			/*--- draw the main body ---*/
			g.setColor(foreground);
			//body
			g.fillRect(xpix - (radius/4), ypix - (radius/2), (radius/2), 
				radius);
			//head
			g.fillOval(xpix - (radius/4), ypix - (radius), (radius/2)-1,
				(radius/2)-1);
			//legs
			g.fillRect(xpix - (radius/6), ypix + (radius/2) + 1, 
				(radius/3), radius);

			/*--- draw the cross ---*/
			g.setColor(background);
			//offset the cross
			xpix = xpix + radius/2;
			ypix = ypix - radius/2;

			g.fillRect(xpix - (radius/8), ypix - (radius/2), (radius/4), 
				radius);
			g.fillRect(xpix - (radius/2), ypix - (radius/8), radius, 
				radius/4);
			}
		}
	/**
	 * Draw the person.
	 */
	public void draw(Graphics g, int w, int h,
		double t, double b, double l, double r)
		{
		top =t; bottom =b; left =l; right = r;
		
		if(picked_up != true)
			{
			double meterspp = (r - l) / (double)w;
			if (DEBUG) System.out.println("meterspp "+meterspp);
			int radius = (int)(RADIUS / meterspp);
			int xpix = (int)((position.x - l) / meterspp);
			int ypix = (int)((double)h - ((position.y - b) / meterspp));
			if (DEBUG) System.out.println("person at"+
				" at "+xpix+","+ypix);
		
			/*--- draw the main body ---*/
			g.setColor(foreground);
			//body
			g.fillRect(xpix - (radius/4), ypix - (radius/2), (radius/2), 
				radius);
			//head
			g.fillOval(xpix - (radius/4), ypix - (radius), (radius/2)-1,
				(radius/2)-1);
			//legs
			g.fillRect(xpix - (radius/6), ypix + (radius/2) + 1, 
				(radius/3), radius);

			/*--- draw the cross ---*/
			g.setColor(background);
			//offset the cross
			xpix = xpix + radius/2;
			ypix = ypix - radius/2;

			g.fillRect(xpix - (radius/8), ypix - (radius/2), (radius/4), 
				radius);
			g.fillRect(xpix - (radius/2), ypix - (radius/8), radius, 
				radius/4);
			}
		}
	}

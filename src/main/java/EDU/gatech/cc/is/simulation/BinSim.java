/*
 * BinSim.java
 */

package EDU.gatech.cc.is.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.communication.Message;


/**
 * A simple bin for depositing attractors.
 *
 * <P>
 * Copyright (c)2000 by Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class BinSim extends AttractorSim implements SimulatedObject
	{
	protected	Color	background = Color.red;
	public		static final boolean DEBUG = false;


	/**
	 * Instantiate a <B>BinSin</B> object.  Be sure
	 * to also call init with proper values.
	 * @see BinSim#init
	 */
        public BinSim()
		{
		if (DEBUG) System.out.println("BinSim: instantiated.");
		}	


        /**
         * Initialize an <B>BinSim</B> object.
	 * Called automatically by JavaBotSim.
	 * @param xp	the x coordinate.
	 * @param yp	the y coordinate.
	 * @param t	ingored.
	 * @param r	the radius.
	 * @param f	the foreground color.
	 * @param b	ignored.
	 * @param v	the vision class.
	 * @param i	the unique id.
	 * @param s	random number seed.
         */
	public void init(double xp, double yp, double t, double r,
		Color f, Color b, int v, int i, long s)
		{
		super.init(xp,yp,t,r,f,b,v,i,s);
		background = b;
		if (DEBUG) System.out.println("BinSim: initialized"
			+" at "+xp+","+yp);
		}

	public boolean isPushable()
		{
		return(false);
		}
	
	public boolean isPickupable()
		{
		return(false);
		}

	public Vec2 getClosestPoint(Vec2 from)
		{
		// otherwise go from the center of the attractor.
		Vec2 tmp = new Vec2(position.x, position.y);
		tmp.sub(from);
		return(tmp);
		}

	public Vec2 getCenter(Vec2 from)
		{
		Vec2 tmp = new Vec2(position.x, position.y);
		tmp.sub(from);
		return(tmp);
		}

	public void push(Vec2 d, Vec2 v)
		{
		// not pushable
		}

	public void quit()
		{
		}


	/**
	 * Draw the bin.
	 */
	public void draw(Graphics g, int w, int h,
		double t, double b, double l, double r)
		{
		top =t; bottom =b; left =l; right = r;
		
		double meterspp = (r - l) / (double)w;
		if (DEBUG) System.out.println("meterspp "+meterspp);
		int radius = (int)(RADIUS / meterspp);
		int xpix = (int)((position.x - l) / meterspp);
		int ypix = (int)((double)h - ((position.y - b) / meterspp));
		if (DEBUG) System.out.println("robot at"+
				" at "+xpix+","+ypix);
	
		/*--- draw the main body ---*/
		g.setColor(foreground);
		for(int i=0; i<3; i++)
			g.drawRect(xpix - radius+i, ypix - radius+i,
				2*radius-2*i, 2*radius-2*i);
		}


        /**
         * Draw the object in a specific spot.
         */
        public void draw(Vec2 pos, Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
		boolean old_pu = picked_up;
		picked_up = false;
                Vec2 old_pos = position;
                position = pos;
                draw(g,w,h,t,b,l,r);
                position = old_pos;
		picked_up = old_pu;
                }
	}

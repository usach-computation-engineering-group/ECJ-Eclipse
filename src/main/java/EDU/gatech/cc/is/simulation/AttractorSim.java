/*
 * AttractorSim.java
 */

package EDU.gatech.cc.is.simulation;

import java.awt.*;
import java.util.Random;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.communication.Message;

import EDU.cmu.cs.coral.util.Polygon2;
import EDU.cmu.cs.coral.util.Circle2;


/**
 * A simple attractor for simulation.
 *
 * <P>
 * Copyright (c)2000 by Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.3 $
 */

public class AttractorSim extends Object implements SimulatedObject
	{
	protected 	Vec2	position = new Vec2(0,0);
	protected	Color	foreground = Color.red;
	protected	Color	background = Color.red;
	protected	SimulatedObject[] all_objects = new SimulatedObject[0];
	protected	int	visionclass = 0;
	protected	int	unique_id = 0;
	protected	double	RADIUS = 0.1;
	protected	double	top = 10;
	protected	double	bottom = -10;
	protected	double	left = -10;
	protected	double	right = 10;
	protected	boolean deposited = false;
	protected	long seed = 0;
	protected	Random rando = null;
	public		static final boolean DEBUG = false;


	/**
	 * Instantiate an <B>AttractorSim</B> object.  Be sure
	 * to also call Init with proper values.
	 * @see AttractorSim#init
	 */
        public AttractorSim()
		{
		if (DEBUG) System.out.println("AttractorSim: instantiated.");
		}	


        /**
         * Initialize an <B>AttractorSim</B> object.
	 * Called automatically by JavaBotSim.
	 * @param xp	the x coordinate.
	 * @param yp	the y coordinate.
	 * @param t	ingored.
	 * @param r	the radius.
	 * @param f	the foreground color.
	 * @param b	the background color.
	 * @param v	the vision class.
	 * @param i	the unique id.
	 * @param s	random number seed.
         */
	public void init(double xp, double yp, double t, double r,
		Color f, Color b, int v, int i, long s)
		{
		position = new Vec2(xp,yp);
		RADIUS = r;
		foreground = f;
		background = b;
		visionclass = v;
		setID(i);
		rando = new Random(s);
		if (DEBUG) System.out.println("AttractorSim: initialized"
			+" at "+xp+","+yp);
		}


	/**
	 * Take a simulated step;
	 */
	public void takeStep(long time_increment, SimulatedObject[] all_objs)
		{
                /*--- keep pointer to the other objects ---*/
                all_objects = all_objs;

                // that's all that's really necessary
		}

	
	public boolean isObstacle()
		{
		return(false);
		}
	
	public boolean isPushable()
		{
		return(true);
		}
	
	public boolean isPickupable()
		{
		return(true);
		}

	public void receive(Message m)
		{
		// ignore messages.
		}
	
	public Vec2 getClosestPoint(Vec2 from)
		{
		// say we're far away if picked up
		if (picked_up)
			return(new Vec2(99999999,0));
		else
			// otherwise go from the center of the attractor.
			{
			Vec2 tmp = new Vec2(position.x, position.y);
			tmp.sub(from);
			return(tmp);
			}
		}
        /**
	 * determine if the object is intersecting with a specified circle.
	 * This is useful for obstacle avoidance and so on.
	 * @param c the circle which may be intersecting the current object.
	 * @return true if collision detected.
         */
	public boolean checkCollision(Circle2 c)
	    {
	    return false;
	    }

        /**
	 * determine if the object is intersecting with a specified polygon.
	 * This is useful for obstacle avoidance and so on.
	 * @param p the polygon which may be intersecting the current object.
	 * @return true if collision detected.
         */
	public boolean checkCollision(Polygon2 p)
		{
		return false;		
		}

	public Vec2 getPosition()
		{
		return(new Vec2(position.x, position.y));
		}

	public Vec2 getCenter(Vec2 from)
		{
		if (picked_up)
			return(new Vec2(99999999,0));
		else
			{
			Vec2 tmp = new Vec2(position.x, position.y);
			tmp.sub(from);
			return(tmp);
			}
		}

	public void push(Vec2 d, Vec2 v)
		{
		/*--- move according to the push ---*/
		position.add(d);
		}

	protected boolean picked_up = false;
	public void pickUp(SimulatedObject o)
		{
		picked_up = true;
		System.out.println("picked up");
		}

	public void putDown(Vec2 p)
		{
		picked_up = false;
		deposited = true;
		setVisionClass(-1); // make invisible
		position = p;
		System.out.println("put down");
		}

	public void setVisionClass(int v)
		{
		visionclass = v;
		}

	public int getVisionClass()
		{
		return(visionclass);
		}

	public void setID(int i)
		{
		unique_id = i;
		}

	public int getID()
		{
		return(unique_id);
		}

	public void quit()
		{
		}


        /**
         * Draw the attractor's ID.
         */
        public void drawID(Graphics g, int w, int h,
                double t, double b, double l, double r)
		{
		//skip for attractors
		}


        /**
         * Draw the attractor's State.
         */
        public void drawState(Graphics g, int w, int h,
                double t, double b, double l, double r)
		{
		//skip for attractors
		}


        /**
         * Set the length of the trail (in movement steps).
         * Non-robots can ignore this. 
         * @param l int, the length of the trail.
         */
        public void setTrailLength(int l)
		{
		//ignore
		}


        /**
         * Clear the trail.
         * Non-robots can ignore this.
         */
        public void clearTrail()
		{
		//ignore
		}


        /**
         * Draw the attractor's Trail.
         */
        public void drawTrail(Graphics g, int w, int h,
                double t, double b, double l, double r)
		{
		//skip for attractors
		}


	/**
	 * Draw the attractor as an icon.
	 * Default is just to do a regular draw.
	 */
	public void drawIcon(Graphics g, int w, int h,
		double t, double b, double l, double r)
		{
		draw(g, w, h, t, b, l, r);
		}


	/**
	 * Draw the attractor.
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
			if (DEBUG) System.out.println("robot at"+
				" at "+xpix+","+ypix);
	
			/*--- draw the main body ---*/
			g.setColor(foreground);
			g.fillOval(xpix - radius, ypix - radius,
				radius + radius, radius + radius);
			}
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

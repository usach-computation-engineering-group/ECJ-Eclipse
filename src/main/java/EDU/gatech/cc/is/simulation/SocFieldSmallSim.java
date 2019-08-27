/*
 * SocFieldSmallSim.java
 */

package EDU.gatech.cc.is.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.communication.Message;

import EDU.cmu.cs.coral.util.Polygon2;
import EDU.cmu.cs.coral.util.Circle2;



/**
 * Draw an official RoboCup soccer field.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.3 $
 */

public class SocFieldSmallSim extends Object implements SimulatedObject
	{
	protected 	Vec2	position = new Vec2(0,0);
	protected	Color	foreground = Color.green;
	protected	SimulatedObject[] all_objects = new SimulatedObject[0];
	protected	int	visionclass = 0;
	protected	int	unique_id = 0;
	protected	double	RADIUS = 0.1;
	protected	double	top = 10;
	protected	double	bottom = -10;
	protected	double	left = -10;
	protected	double	right = 10;
	protected	double	width = 500;//pixels
	protected	double	height = 500;//pixels
	protected	double	meterspp = (right-left)/width;
	public		static final boolean DEBUG = false;


	/**
	 * Instantiate a <B>SocFieldSmallSim</B> object.  Be sure
	 * to also call init with proper values.
	 * @see SocFieldSmallSim#init
	 */
        public SocFieldSmallSim()
		{
		if (DEBUG) System.out.println("SocFieldSmallSim: instantiated.");
		}	


        /**
         * Initialize an <B>SocFieldSmallSim</B> object.
	 * Called automatically by JavaBotSim.
	 * None of the arguments are used except unique id.
	 * @param xp	ignored.
	 * @param yp	ignored.
	 * @param t	ingored.
	 * @param r	ignored.
	 * @param f	ignored.
	 * @param b	ignored.
	 * @param v	vision class (should be 0 for invisible).
	 * @param i	the unique id.
	 * @param s	random number seed.
         */
	public void init(double xp, double yp, double t, double r,
		Color f, Color b, int v, int i, long s)
		{
		foreground = f;
		visionclass = v;
		setID(i);
		if (DEBUG) System.out.println("SocFieldSmallSim: initialized");
		}


	/**
	 * Take a simulated step;
	 */
	public void takeStep(long time_increment, SimulatedObject[] all_objs)
		{
		// do nothink!
		}
	
	public boolean isObstacle()
		{
		return(false);
		}
	
	public boolean isPushable()
		{
		return(false);
		}
	
	public void receive(Message m)
		{
		// ignore messages.
		}
	
	public boolean isPickupable()
		{
		return(false);
		}
	
        public Vec2 getPosition()
                {
		// say we are far away to avoid interactions.
		return(new Vec2(99999999,0));
                }

	public Vec2 getClosestPoint(Vec2 from)
		{
		// say we are far away to avoid interactions.
		return(new Vec2(99999999,0));
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

	public Vec2 getCenter(Vec2 from)
		{
		// say we are far away to avoid interactions.
		return(new Vec2(99999999,0));
		}

	public void push(Vec2 d, Vec2 v)
		{
		// ignore.
		}

	public void pickUp(SimulatedObject o)
		{
		// ignore.
		}

	public void putDown(Vec2 p)
		{
		// ignore.
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
	 * Convert from size in meters to pixels.
	 */
	public	int	size(double m)
		{
		return((int)((m / meterspp)));
		}

	/**
	 * Convert y in field coordinates to Y in pixel coordinates.
	 */
	public	int	Y(double y)
		{
		return((int)(height-((y - bottom) / meterspp)));
		}

		
	/**
	 * Convert x in field coordinates to X in pixel coordinates.
	 */
	public	int	X(double x)
		{
		return((int)(((x - left) / meterspp)));
		}
		

        /**
         * Draw the objects's State.
         */
        public void drawState(Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                //skip for soccer fields
                }


        /**
         * Draw the objects's ID.
         */
        public void drawID(Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                //skip for soccer fields
                }


        /**
         * Draw the object as an icon.
         * Default is just to do a regular draw.
         */
        public void drawIcon(Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                draw(g, w, h, t, b, l, r);
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
         * Draw the objects's Trail.
         */
        public void drawTrail(Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                //skip for soccer fields
                }


	/**
	 * Draw the field.
	 */
	public void draw(Graphics g, int w, int h,
		double t, double b, double l, double r)
		{
		top =t; bottom =b; left =l; right = r;
		width =(double)w; height =(double)h;
		meterspp  = (right - left) / width;

		if (DEBUG) System.out.println("SocFieldSmallSim.draw: "+
			top+" "+
			bottom+" "+
			left+" "+
			right+" "+
			width+" "+
			height+" "+
			meterspp);
		if (DEBUG) System.out.println("SocFieldSmallSim.draw: "+
			X(0)+ " "+
			Y(0)+ " ");
		
		g.setColor(Color.white);

		// This is rather convoluted looking, but it is like that
		// to ensure that the lines always match up

		// Draw outer boundary
		g.drawRect(X(-1.39),Y(.7825),size(2.78),size(1.565));
		g.drawRect(X(-1.39)+1,Y(.7825)+1,size(2.78)-2,size(1.565)-2);
		g.drawRect(X(-1.39)+2,Y(.7825)+2,size(2.78)-4,size(1.565)-4);
		g.drawRect(X(-1.39)+3,Y(.7825)+3,size(2.78)-6,size(1.565)-6);
	
		// Draw center line and center circle
		g.drawLine(X(0),Y(0.7825),X(0),Y(-0.7825));
		g.drawLine(X(0)+1,Y(0.7825),X(0)+1,Y(-0.7825));
		g.drawOval(X(-0.150),Y(0.150),size(0.300),size(0.300));
		g.drawOval(X(-0.150)-1,Y(0.150)-1,size(0.300)+2,size(0.300)+2);
	
		// Draw defense zones
		g.drawRect(X(-1.39),                        Y(.5),  
				size(.225),  size(1));
		g.drawRect(X(-1.39)+size(2.78)-size(.225),  Y(.5),
				size(.225),  size(1));
		g.drawRect(X(-1.39)+1,                      Y(.5)+1,  
				size(.225)-2,  size(1)-2);
		g.drawRect(X(-1.39)+size(2.78)-size(.225)+1,Y(.5)+1,
				size(.225)-2,  size(1)-2);

		// Draw corners
		// lower left
		g.drawLine(X(-1.37),Y(-.7325),X(-1.34),Y(-.7625));
		g.drawLine(X(-1.37),Y(-.7325)-1,X(-1.34)+1,Y(-.7625));
		// lower right
		g.drawLine(X(1.37),Y(-.7325),X(1.34),Y(-.7625));
		g.drawLine(X(1.37),Y(-.7325)-1,X(1.34)-1,Y(-.7625));
		// upper right
		g.drawLine(X(1.37),Y(.7325),X(1.34),Y(.7625));
		g.drawLine(X(1.37),Y(.7325)+1,X(1.34)-1,Y(.7625));
		// upper left
		g.drawLine(X(-1.37),Y(.7325),X(-1.34),Y(.7625));
		g.drawLine(X(-1.37),Y(.7325)+1,X(-1.34)+1,Y(.7625));

		// Draw goals
		g.setColor(Color.blue);
		g.fillRect(X(-1.39)+4-size(0.1),Y(0.25),size(0.1),size(0.5));
		g.fillRect(X(-1.39)+3+size(2.78)-6,Y(0.25),size(0.1),size(0.5));
		}

        /**
         * Draw the soccer field in a specific spot.
	 * This doesn't really make sense for the soccer field, but
	 * we need to handle it just in case someone calls it.
         */
        public void draw(Vec2 pos, Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                draw(g,w,h,t,b,l,r);
                }


	public static void main(String args[])
		{
		System.out.println((int)(-1.9)+" "+(int)(1.9));
		}
	}

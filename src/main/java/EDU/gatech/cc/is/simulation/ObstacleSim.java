/*
 * ObstacleSim.java
 */

package EDU.gatech.cc.is.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.communication.Message;

import EDU.cmu.cs.coral.util.Polygon2;
import EDU.cmu.cs.coral.util.Circle2;


/**
 * an obstacle for simulation.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.4 $
 */

public class ObstacleSim extends Object implements SimulatedObject
	{
	protected Vec2	position;
	protected	Color	foreground, background;
	protected	SimulatedObject[] all_objects;
	protected	int	visionclass;
	protected	int	unique_id;
	protected	double	RADIUS;
	protected	double	lastx, lasty;
	public	static final boolean DEBUG = false;


	/**
	 * Instantiate an <B>ObstacleSim</B> object.  Be sure
	 * to also call init with proper values.
	 * @see ObstacleSim#init
	 */
        public ObstacleSim()
		{
		position = new Vec2(0,0);
		foreground = Color.black;
		if (DEBUG) System.out.println("ObstacleSim: instantiated.");
		}	


        /**
         * Initialize a <B>ObstacleSim</B> object.
	 * This is called automatically by JavaBotSim.
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
		position = new Vec2(xp,yp);
		RADIUS = r;
		foreground = f;
		background = b;
		visionclass = v;
		setID(i);
		if (DEBUG) System.out.println("ObstacleSim: initialized"
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
		return(true);
		}
	
	public boolean isPushable()
		{
		return(false);
		}
	
	public boolean isPickupable()
		{
		return(false);
		}
	
        public Vec2 getPosition()
                {
                return(new Vec2(position.x, position.y));
                }

	public Vec2 getClosestPoint(Vec2 from)
		{
		Vec2 tmp = new Vec2(position.x, position.y);
		tmp.sub(from);
		if (tmp.r < RADIUS)
			tmp.setr(0);
		else
			tmp.setr(tmp.r-RADIUS);

                Vec2 last = new Vec2(tmp.x, tmp.y);
                last.add(from);
                lastx = last.x;
                lasty = last.y;

		return(tmp);
		}

        /**
	 * determine if the object is intersecting with a specified circle.
	 * This is useful for obstacle avoidance and so on.
	 * @param c the circle which may be intersecting the current object.
	 * @return true if collision detected.
         */
	public boolean checkCollision(Circle2 c)
	    {
	    Vec2 closest = getClosestPoint(c.centre); // closest is a vector with origin at centre that leads to closest point on current object
	    if (closest.r <= c.radius) // closest point is within c.radius of c.centre
			{
			return true;
			}
	    else 
			{
			return false;
			}
	    }

        /**
	 * determine if the object is intersecting with a specified polygon.
	 * This is useful for obstacle avoidance and so on.
	 * @param p the polygon which may be intersecting the current object.
	 * @return true if collision detected.
         */
	public boolean checkCollision(Polygon2 p)
		{
		Vec2 vertex1, vertex2, vec1, vector2, closestPt;
		int numberEdges = p.vertices.size(); // n edges if n vertices (as vertex n+1 wraps round to vertex 0)
		double scale;

		for (int i=0;i<numberEdges;i++)
			{
			vertex1 = (Vec2)p.vertices.elementAt(i);
			vertex2 = (Vec2)p.vertices.elementAt((i+1)%numberEdges);
			vertex1.sub(position);
			vertex2.sub(position);
			// if either vertex is within the circles radius you are colliding
			if ((vertex1.r < RADIUS) || (vertex2.r < RADIUS))
				{
				return true;
				} 
			vertex1.add(position);
			vertex2.add(position);
			vec1 = new Vec2(vertex2);
			vec1.sub(vertex1);
			vector2 = new Vec2(position);
			vector2.sub(vertex1);
			scale = ((vec1.x*vector2.x)+(vec1.y*vector2.y))/((vec1.x*vec1.x)+(vec1.y*vec1.y));
			closestPt = new Vec2(scale*vec1.x, scale*vec1.y);
			closestPt.add(vertex1); // absolute position of closest point
			closestPt.sub(position); // position of closest point relative to centre of current object
			if (closestPt.r < RADIUS)
				{
				// now need to check if closestPt lies between vertex1 and vertex2
				// i.e. it could lie on vector between them but outside of them
				if ( (scale > 0.0) && (scale < 1.0) )
					{
					return true;
					}
				}
			}
		return false; // closest point to object on each edge of polygon not within object			
		}


	public Vec2 getCenter(Vec2 from)
		{
		Vec2 tmp = new Vec2(position.x, position.y);
		tmp.sub(from);
		return(tmp);
		}

	public void push(Vec2 d, Vec2 v)
		{
		// sorry no pushee obstacles!
		}

	public void pickUp(SimulatedObject o)
		{
		// sorry no pickupee obstacles!
		}

	public void receive(Message m)
		{
		// default is to ignore messages.
		}

	public void putDown(Vec2 p)
		{
		// sorry no put downee obstacles!
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
         * Draw the objects's ID.
         */
        public void drawID(Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                //skip for obstacles
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
         * Draw the objects's State.
         */
        public void drawState(Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                double meterspp = (r - l) / (double)w;
                if (DEBUG) System.out.println("meterspp "+meterspp);
                int x1pix = (int)((lastx - l) / meterspp);
                int y1pix = (int)((double)h - ((lasty - b) / meterspp));
                if (DEBUG) System.out.println("line at"+
                        " at "+x1pix+","+y1pix);

                /*--- draw the oval ---*/
                g.setColor(background);
                g.fillOval(x1pix-2, y1pix-2, 4, 4);
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
		//ignore for obstacles
                }


	/**
	 * Draw the object.
	 */
	public void draw(Graphics g, int w, int h,
		double t, double b, double l, double r)
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

	/**
	 * Draw the object in a specific spot.
	 */
	public void draw(Vec2 pos, Graphics g, int w, int h,
		double t, double b, double l, double r)
		{
		Vec2 old_pos = position;
		position = pos;
		draw(g,w,h,t,b,l,r);
		position = old_pos;
		}
	}

/*
 * TerrainSim.java
 */

package EDU.cmu.cs.coral.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.simulation.*;
import EDU.gatech.cc.is.communication.Message;

import EDU.cmu.cs.coral.util.Polygon2;
import EDU.cmu.cs.coral.util.Circle2;


/**
 * Standard terrain.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch and Carnegie Mellon University
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class TerrainSim extends Object implements SimulatedLinearObject, 
	SimulatedTerrainObject
	{
	private double	lastx, lasty;
	private Vec2	start;
	private Vec2	end;
	private Vec2	center;
	private	double	m, b; // eqn of line between start and end
	private	double	mrot; // m rotated 90 degrees
	private	Color	foreground,background;
	private	SimulatedObject[] all_objects;
	private	int	visionclass;
	private	int	unique_id;
	private	double	RADIUS;
	private	Vec2[]	corners = new Vec2[4];
	public	static final boolean DEBUG = false;
	private	double TRAVERSABILITY = 1.0;

	/**
	 * Instantiate a <B>RoadSim</B> object.  Be sure
	 * to also call init with proper values.
	 * @see RoadSim#init
	 */
        public TerrainSim()
		{
		start = new Vec2(0,0);
		end = new Vec2(0,0);
		center = new Vec2(0,0);
		foreground = Color.black;
		background = Color.black;
		if (DEBUG) System.out.println("RoadSim: instantiated.");
		}	


        /**
         * Initialize a <B>RoadSim</B> object.
	 * This is called automatically by JavaBotSim.
	 * @param x	x coordinate of first point.
	 * @param y	y coordinate of first point.
	 * @param t	direction to second point.
	 * @param r	distance to second point.
	 * @param f	the foreground color.
	 * @param b	ignored.
	 * @param v	the vision class.
	 * @param i	the unique id.
	 * @param s	random number seed.
         */
	public void init(double x, double y, double t,
		double r, Color f, Color back, int v, int i, long s)
		{
		System.out.println("RoadSim: initialized with wrong arguments.\n"
			+"use `linearobject' declaration in description \n"
			+"file instead of `object.'");
		start = new Vec2(x,y);
		Vec2 tmp = new Vec2();
		tmp.setr(r);
		tmp.sett(t);
		end = new Vec2(x,y);
		end.add(tmp);
		center = new Vec2((start.x+end.x)/2,(start.y+end.y)/2);
		RADIUS = r;
		foreground = f;
		background = back;
		visionclass = v;
		setID(i);
		if (DEBUG) System.out.println("RoadSim: initialized"
			+" at "+x+","+y);
		}

        /**
         * Initialize a <B>RoadSim</B> object.
	 * This is called automatically by JavaBotSim.
	 * @param x1	x coordinate of first point.
	 * @param y1	y coordinate of first point.
	 * @param x2	x coordinate of second point.
	 * @param y2	y coordinate of second point.
	 * @param r	the radius (width).
	 * @param f	the foreground color.
	 * @param b	ignored.
	 * @param v	the vision class.
	 * @param i	the unique id.
	 * @param s	random number seed.
         */
	public void init(double x1, double y1, double x2, double y2,
		double r, Color f, Color back, int v, int i, long s)
		{
		start = new Vec2(x1,y1);
		end = new Vec2(x2,y2);

		/*--- compute eqn of the line between start end ---*/
		m = (x2-x1);
		if (m==1)
			m = 9999999999999f; //huge
		else
			m = (y2-y1)/m;
		mrot = Math.tan(Math.atan(m) + (Math.PI/2));//rotate m 90 deg
		b = y1 - m*x1;
		//System.out.println("init m mrot b "+m+" "+mrot+" "+b);

		/*--- compute endpoints of polygon ---*/
		Vec2 tmp = new Vec2(start);
		tmp.sub(end);
		tmp.setr(r);
		tmp.sett(tmp.t + Math.PI/2);
		Vec2 tmp2 = new Vec2(tmp);
		tmp2.add(start);
		corners[0] = tmp2;
		tmp2 = new Vec2(tmp);
		tmp2.add(end);
		corners[1] = tmp2;
		tmp.sett(tmp.t + Math.PI);
		tmp2 = new Vec2(tmp);
		tmp2.add(end);
		corners[2] = tmp2;
		tmp2 = new Vec2(tmp);
		tmp2.add(start);
		corners[3] = tmp2;

		center = new Vec2((start.x+end.x)/2,(start.y+end.y)/2);
		RADIUS = r;
		foreground = f;
		background = back;
		visionclass = v;
		setID(i);
		if (DEBUG) System.out.println("RoadSim: initialized"
			+" at "+x1+","+y1
			+" to "+x2+","+y2);
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

	public double getTraversability()
		{
		return(TRAVERSABILITY);
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
                return(new Vec2(center.x, center.y));
                }

	public Vec2 getClosestPoint(Vec2 from)
		{
		double b3, x3, y3;
		b3 = x3 = y3 = 0;

		/*--- special cases ---*/
		if ((m>999999999f)||(m<-999999999f))
			// vertical 
			{
			x3 = start.x;
			y3 = from.y;
			}
		else if (Math.abs(m)<0.0000001)
			// horizontal 
			{
			x3 = from.x;
			y3 = start.y;
			}

		/*--- normal case ---*/
		else
			{
			b3 = from.y - mrot*from.x;
			x3 = m - mrot;
			if (x3 == 0)
				x3 = 9999999999999f; // huge
			else
				x3 = (b3 - b)/x3;
			y3 = mrot*x3 + b3;

			/*--- adjust to be relative ---*/
			//x3 = x3 - from.x;
			//y3 = y3 - from.y;
			}
	
		// we now have the point on the line closest to the
		// robot

		/*--- handle points outside the bounds ---*/
		if ((x3<start.x)&&(x3<end.x))
			// to the left of both points
			{
			if (start.x<end.x)
				{
				x3 = start.x;
				y3 = start.y;
				}
			else
				{
				x3 = end.x;
				y3 = end.y;
				}
			}
		else if ((x3>start.x)&&(x3>end.x))
			{
			if (start.x>end.x)
				{
				x3 = start.x;
				y3 = start.y;
				}
			else
				{
				x3 = end.x;
				y3 = end.y;
				}
			}
		if ((y3<start.y)&&(y3<end.y))
			// to the left of both points
			{
			if (start.y<end.y)
				{
				x3 = start.x;
				y3 = start.y;
				}
			else
				{
				x3 = end.x;
				y3 = end.y;
				}
			}
		else if ((y3>start.y)&&(y3>end.y))
			{
			if (start.y>end.y)
				{
				x3 = start.x;
				y3 = start.y;
				}
			else
				{
				x3 = end.x;
				y3 = end.y;
				}
			}

		/*--- adjust to be relative ---*/
		x3 = x3 - from.x;
		y3 = y3 - from.y;

		//System.out.println("m mrot b b3: "+m+", "+mrot+", "+b+", "+b3);
		//System.out.println("closest pt : "+x3+", "+y3);

		Vec2 tmp = new Vec2(x3, y3);
		if ((tmp.r - RADIUS)<0)
			tmp.setr(0);
		else
			tmp.setr(tmp.r - RADIUS);

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
		Vec2 tmp = new Vec2(center.x, center.y);
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
		// skip
                }


	/**
	 * Draw the object.
	 */
	public void draw(Graphics g, int w, int h,
		double t, double b, double l, double r)
		{
		double meterspp = (r - l) / (double)w;
		if (DEBUG) System.out.println("meterspp "+meterspp);

		/*--- draw circles on each end ---*/
		int rint  = (int)(RADIUS/meterspp);
		int x1pix = (int)((start.x - l) / meterspp);
		int y1pix = (int)((double)h - ((start.y - b) / meterspp));
		g.setColor(foreground);
		g.fillOval(x1pix-rint, y1pix-rint, rint*2, rint*2);
		g.setColor(background);
		g.drawOval(x1pix-rint, y1pix-rint, rint*2, rint*2);

		int x2pix = (int)((end.x - l) / meterspp);
		int y2pix = (int)((double)h - ((end.y - b) / meterspp));
		g.setColor(foreground);
		g.fillOval(x2pix-rint, y2pix-rint, rint*2, rint*2);
		g.setColor(background);
		g.drawOval(x2pix-rint, y2pix-rint, rint*2, rint*2);

		if (DEBUG) System.out.println("line at"+
			" at "+x1pix+","+y1pix);

		/*--- draw the polygon ---*/
		int[] outlinex = new int[4];
		int[] outliney = new int[4];
		for (int i=0; i<4; i++)
			{
			outlinex[i] = (int)((corners[i].x -l) / meterspp);
			outliney[i] = (int)(h - ((corners[i].y -b)/meterspp));
			}
		g.setColor(foreground);
		g.fillPolygon(outlinex, outliney, 4);
		g.setColor(background);
		g.drawLine(outlinex[0], outliney[0],
			outlinex[1], outliney[1]);
		g.drawLine(outlinex[2], outliney[2],
			outlinex[3], outliney[3]);

		}

	/**
	 * Draw the object in a specific spot.
	 */
	public void draw(Vec2 pos, Graphics g, int w, int h,
		double t, double b, double l, double r)
		{
		Vec2 old_pos = center;
		center = pos;
		draw(g,w,h,t,b,l,r);
		center = old_pos;
		}
	}

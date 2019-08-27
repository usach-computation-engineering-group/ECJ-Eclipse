/*
 * PolygonObstacleSim.java
 */

package EDU.cmu.cs.coral.simulation;

import java.awt.*;
import java.util.*;
import java.lang.Math;
import EDU.gatech.cc.is.util.*;
import EDU.gatech.cc.is.simulation.*;
import EDU.gatech.cc.is.communication.Message;
import EDU.cmu.cs.coral.util.Polygon2;
import EDU.cmu.cs.coral.util.Circle2;


/**
 * an obstacle for simulation.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch and Carnegie Mellon University
 *
 * @author Rosemary Emery
 * @version $Revision: 1.2 $
 */

public class PolygonObstacleSim extends Object implements SimulatedObject
	{
	private Vec2	position; // this is the x,y coordinate of its centre
	private     int	numberSides;
	private	Color	foreground, background;
	private	SimulatedObject[] all_objects;
	private	int	visionclass;
	private	int	unique_id;
	private	double	RADIUS;
	private	double	lastx, lasty;
	public	static final boolean DEBUG = false;
	private 	Polygon2	body;
	private	Polygon2	drawBody;
	private 	double	offset;


	/**
	 * Instantiate an <B>PolygonObstacleSim</B> object.  Be sure
	 * to also call init with proper values.
	 * @see PolygonObstacleSim#init
	 */
        public PolygonObstacleSim()
		{
		position = new Vec2(0,0);
		foreground = Color.black;
		body = new Polygon2();
		if (DEBUG) System.out.println("PolygonObstacleSim: instantiated.");
		}	


        /**
         * Initialize a <B>PolygonObstacleSim</B> object.
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

	// defaults to square with diagonal 2*RADIUS
	public void init(double xp, double yp, double t, double r,
		Color f, Color b, int v, int i, long s)
		{
		position = new Vec2(xp,yp);
		RADIUS = r;
		foreground = f;
		background = b;
		visionclass = v;
		offset = t;
		setID(i);
		// now create polygonal body with diagonal of length 2r
		numberSides = 4;
		Vector temp = new Vector();
		Vector drawTemp = new Vector();
		double hyp = Math.sqrt(r*r + r*r);
		double newx = Math.abs(hyp*Math.cos((Math.PI/4)));
		double newy = Math.abs(hyp*Math.sin((Math.PI/4)));

		Vec2[] tempBody = new Vec2[4];
		double[] tempBodyx = new double[4];
		double[] tempBodyy = new double[4];
		Vec2[] tempBody2 = new Vec2[4];
		Vec2 temp2;

		tempBody2[0] = new Vec2(newx, newy);
		tempBody2[1] = new Vec2(newx, -1*newy);
		tempBody2[2] = new Vec2(-1*newx, -1*newy);
		tempBody2[3] = new Vec2(-1*newx, newy);

		for(int j =0;j<numberSides;j++)
			{
			drawTemp.addElement(tempBody2[j]);
			}

		drawBody = new Polygon2(drawTemp);

		double newAng;

		tempBody[0] = new Vec2(newx, newy);
		tempBody[1] = new Vec2(newx, -1*newy);
		tempBody[2] = new Vec2(-1*newx, -1*newy);
		tempBody[3] = new Vec2(-1*newx, newy);
		for(int j = 0; j<numberSides; j++) // scale and rotate
			{
			newAng = tempBody[j].t + offset;
			if (newAng >= 2*Math.PI)
				{
				newAng-= 2*Math.PI;
				}
			tempBody[j].sett(newAng);
			tempBodyx[j] = tempBody[j].x + position.x;
			tempBodyy[j] = tempBody[j].y + position.y;
			temp2 = new Vec2(tempBodyx[j], tempBodyy[j]);
			temp.addElement(temp2);
			}

		body = new Polygon2(temp);

		if (DEBUG) System.out.println("PolygonObstacleSim: initialized"
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
		int numberEdges = body.vertices.size(); // n edges if n vertices (as vertex n+1 wraps round to vertex 0)
		double scale;
		int i;
		Vector closestPts = new Vector(); 
		Vec2 closest, vertex1, vertex2, vec1, vector2;
		for (i=0;i<numberEdges;i++)
			{
			vertex1 = (Vec2)body.vertices.elementAt(i);
			vertex2 = (Vec2)body.vertices.elementAt((i+1)%numberEdges);
			vec1 = new Vec2(vertex2.x-vertex1.x, vertex2.y-vertex1.y);
			vector2 = new Vec2(from.x-vertex1.x, from.y-vertex1.y);
			scale = ((vec1.x*vector2.x)+(vec1.y*vector2.y))/((vec1.x*vec1.x)+(vec1.y*vec1.y));
			// if closest pt lies within vertices add, if scale is negative, set closest point to vertex 1, if it is > 1.0 then point not in line, set equal to vertex2
			if (scale <= 0.0) {
				closest = new Vec2(0,0); // set to vertex1
			} else if (scale >= 1.0) {
				closest = new Vec2(vec1); // set to vertex2 relative to vertex1
			} else {
				closest = new Vec2(scale*vec1.x, scale*vec1.y);
			}
			closest.add(vertex1); // absolute position of closest point
			closestPts.addElement(closest);
		}

		Vec2 tempDistance = new Vec2(position);
		tempDistance.sub(from);
		double distance = tempDistance.r;
		closest = new Vec2(position);
		closest.sub(from);
		Vec2 temp;
		for(i=0;i<closestPts.size();i++)
			{
			temp = (Vec2)closestPts.elementAt(i);
			temp.sub(from);
			if(temp.r <= distance) 
				{
				distance = temp.r;
				closest = new Vec2(temp); 
				}
			}
		Vec2 last = new Vec2(closest.x, closest.y);
		last.add(from);
		lastx = last.x;
		lasty = last.y;
		return closest;
	
	}	

        /**
	 * determine if the object is intersecting with a specified circle.
	 * This is useful for obstacle avoidance and so on.
	 * @param c the circle which may be intersecting the current object.
	 * @return true if collision detected.
         */
	public boolean checkCollision(Circle2 c)
	    {
		// set up p to be correct vertices
		Vec2 vertex1, vertex2, vec1, vector2, closestPt;
		int numberEdges = body.vertices.size(); // n edges if n vertices (as vertex n+1 wraps round to vertex 0)
		double scale;
		int i;

		for (i=0;i<numberEdges;i++)
			{
			vertex1 = (Vec2)body.vertices.elementAt(i);
			vertex2 = (Vec2)body.vertices.elementAt((i+1)%numberEdges);
			vertex1.sub(c.centre);
			vertex2.sub(c.centre);
			// if either vertex is within the circles radius you are colliding
			if ((vertex1.r < c.radius) || (vertex2.r < c.radius))
				{
				return true;
				} 
			vertex1.add(c.centre);
			vertex2.add(c.centre);
			vec1 = new Vec2(vertex2);
			vec1.sub(vertex1);
			vector2 = new Vec2(c.centre);
			vector2.sub(vertex1);
			scale = ((vec1.x*vector2.x)+(vec1.y*vector2.y))/((vec1.x*vec1.x)+(vec1.y*vec1.y));
			closestPt = new Vec2(scale*vec1.x, scale*vec1.y);
			closestPt.add(vertex1); // absolute position of closest point
			closestPt.sub(c.centre); // position of closest point relative to centre of current object
			if (closestPt.r < c.radius)
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


        /**
	 * determine if the object is intersecting with a specified polygon.
	 * This is useful for obstacle avoidance and so on.
	 * @param p the polygon which may be intersecting the current object.
	 * @return true if collision detected.
         */
	public boolean checkCollision(Polygon2 p)
		{
		int i = 0;
		Vec2 vertex1, vertex2;
		while(i<p.vertices.size())
			{
			vertex1 = (Vec2)p.vertices.elementAt(i);
			vertex2 = (Vec2)p.vertices.elementAt((i+1)%(p.vertices.size()));
			if(body.lineIntersectsWithPolygon(vertex1, vertex2))
				{
				return true;
				}
			i++;
			}
		return false;
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
		Vec2[] pbody = new Vec2[drawBody.vertices.size()];     // outline of body
		int[] bodyx = new int[drawBody.vertices.size()];     
		int[] bodyy = new int[drawBody.vertices.size()];     
		for(int z=0;z<drawBody.vertices.size();z++)
			{
			pbody[z] = new Vec2((Vec2)drawBody.vertices.elementAt(z));
			}
		for(int j = 0; j<drawBody.vertices.size();j++) // scale and rotate
			{
			pbody[j].setr(pbody[j].r / meterspp);
			pbody[j].sett(pbody[j].t - offset);
			bodyx[j] = (int)pbody[j].x + xpix;
			bodyy[j] = (int)pbody[j].y + ypix;
			}
		g.fillPolygon(bodyx, bodyy, body.vertices.size());
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

/*
 * LandmarkSim.java
 */

package EDU.cmu.cs.coral.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.communication.Message;
import EDU.gatech.cc.is.simulation.SimulatedObject;
import EDU.gatech.cc.is.simulation.ObstacleSim;
/**
 * this is a simulation of a landmark.  It is stationary and 
 * gives no clues to its presence other than its existence.
 */

public class LandmarkSim extends ObstacleSim
{
    protected Vec2 position;
    protected Color foreground;
  protected Color background;
  protected SimulatedObject [] all_objects;
    protected int visionclass;
    protected int unique_id;
    protected double RADIUS;
    /**
     * Instantiate a LandmarkSim object.
     */
    public LandmarkSim() {
	super();
	
	position = new Vec2(0,0);
	foreground = Color.black;
	background = Color.green;
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
	System.out.println("LandmarkSim: initialized"
				      +" at "+xp+","+yp+" "+
			   position.toString());
    }
    
    public Vec2 getPosition() { return new Vec2(position.x, position.y); }

    /**
     * Take a simulated step;
     */
    public void takeStep(long time_increment, SimulatedObject[] all_objs)
    {
	/*--- keep pointer to the other objects ---*/
	all_objects = all_objs;

	// that's all that's really necessary
    }

    /**
     * Draw the object.
     */
    public void draw(Graphics g, int w, int h,
		     double t, double b, double l, double r) {
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
    
    public Vec2 getClosestPoint(Vec2 from)
    {
	Vec2 tmp = new Vec2(position.x, position.y);
	tmp.sub(from);
	if (tmp.r < RADIUS)
	    tmp.setr(0);
	else
	    tmp.setr(tmp.r-RADIUS);
	return(tmp);
    }
    
    public Vec2 getCenter(Vec2 from)
    {
	Vec2 tmp = new Vec2(position.x, position.y);
	tmp.sub(from);
	return(tmp);
    }

    public int getVisionClass()
    {
	return(visionclass);
    }

    public void setVisionClass(int v)
    {
	visionclass = v;
    }


    public void setID(int i)
    {
	unique_id = i;
    }
    
    public int getID()
    {
	return(unique_id);
    }

    public void draw(Vec2 pos, Graphics g, int w, int h,
		     double t, double b, double l, double r)
    {
	Vec2 old_pos = position;
	position = pos;
	draw(g,w,h,t,b,l,r);
	position = old_pos;
    }

    public double getRadius() { return RADIUS;}
}


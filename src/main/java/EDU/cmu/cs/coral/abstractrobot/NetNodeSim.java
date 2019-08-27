/*
 * NetNodeSim.java
 */

package EDU.cmu.cs.coral.abstractrobot;

import java.awt.*;
import java.util.Enumeration;
import EDU.gatech.cc.is.abstractrobot.*;
import EDU.gatech.cc.is.communication.*;
import EDU.gatech.cc.is.simulation.*;
import EDU.gatech.cc.is.util.*;
import EDU.cmu.cs.coral.abstractrobot.*;
import EDU.cmu.cs.coral.util.*;

/**
 * NetNodeSim implements NetNode for simulation.  Also includes 
 * code implementing communication.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)2000 Tucker Balch
 *
 * @see NetNodeSim
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class NetNodeSim extends Simple
	implements NetNode, SimulatedObject
	{
	private TransceiverSim	transceiver;	// comm to other robots
	protected Vec2	position;
	protected Color	foreground, background;
	protected KinSensorSim kin_sensor;
	private long	time;
	private double	timed;
	private CircularBuffer	trail;
	protected double left, right, top, bottom;
	private	SimulatedObject[] all_objects = new SimulatedObject[0];
	private	int	visionclass;
	private	double	RADIUS=0.5;
	public	static final boolean DEBUG = false;
	 
	/**
	 * Instantiate a <B>NetNodeSim</B> object.  Be sure
	 * to also call init with proper values.
	 * @see NetNodeSim#init
	 */
        public NetNodeSim()
		{
		/*--- set parameters ---*/
                super(1);

		position = new Vec2(0,0);
		foreground = Color.black;
		background = Color.black;
		if (DEBUG) System.out.println("NetNodeSim: instantiated.");

		/*--- set default bounds ---*/
		top = 1000;
		bottom = -1000;
		left = -1000;
		right = 1000;
		}	


        /**
         * Initialize a <B>NetNodeSim</B> object.
         */
	public void init(double xp, double yp, double tp, double ignore,
		Color f, Color b, int v, int i, long s)
		{
		trail = new CircularBuffer(1000);
		setID(i);
		transceiver = new TransceiverSim(this, this);
		kin_sensor = new KinSensorSim(this);
		position = new Vec2(xp,yp);
		foreground = f;
		background = b;
		time = 0;
		timed = 0;
		visionclass = v;
		if (DEBUG) System.out.println("NetNodeSim: initialized"
			+" at "+xp+","+yp);	
		}


	/**
	 * Take a simulated step;
	 */
	public void takeStep(long time_increment, SimulatedObject[] all_objs)
		{
		if (DEBUG) System.out.println("NetNodeSim.TakeStep()");

		/*--- keep pointer to the other objects ---*/
		all_objects = all_objs;

		/*--- update the time ---*/
		time += time_increment;
		double time_incd = ((double)time_increment)/1000;
		timed += time_incd;

		Vec2[] start = new Vec2[1];
		Vec2[] dir   = new Vec2[1];
		start[0] = new Vec2(position);
		dir[0]   = new Vec2(0,10);
		displayVectors.set(start,dir);
		}


	/*--- From SimulatedObject ---*/

	public boolean isObstacle()
		{
		return(false);
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

	public boolean checkCollision(Polygon2 p)
		{
		return false; // closest point to object on each 
				//edge of polygon not within object
		}

	public void push(Vec2 d, Vec2 v)
		{
		// sorry no pushee NetNodes!
		}

	public void pickUp(SimulatedObject o)
		{
		// sorry no pickupee NetNodes!
		}

	public void putDown(Vec2 p)
		{
		// sorry no put downee NetNodes!
		}

	public void setVisionClass(int v)
		{
		visionclass = v;
		}

	public int getVisionClass()
		{
		return(visionclass);
		}


	/**
         * Draw the robot's ID.
         */
        public void drawID(Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                top =t; bottom =b; left =l; right =r;
                if (DEBUG) System.out.println("draw "+
                        w + " " +
                        h + " " +
                        t + " " +
                        b + " " +
                        l + " " +
                        r + " ");
                double meterspp = (r - l) / (double)w;
                int radius = (int)(RADIUS / meterspp);
                int xpix = (int)((position.x - l) / meterspp);
                int ypix = (int)((double)h - ((position.y - b) / meterspp));

                /*--- draw ID ---*/
                g.setColor(background);
                g.drawString(String.valueOf(getPlayerNumber(0))
			,xpix-radius,ypix-radius);
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


        private Point last = new Point(0,0);
        /**
         * Draw the robot's Trail.
         */
        public void drawTrail(Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                //top =t; bottom =b; left =l; right =r;
                //if (DEBUG) System.out.println("draw "+
                        //w + " " +
                        //h + " " +
                        //t + " " +
                        //b + " " +
                        //l + " " +
                        //r + " ");
                //double meterspp = (r - l) / (double)w;
                //int xpix = (int)((position.x - l) / meterspp);
                //int ypix = (int)((double)h - ((position.y - b) / meterspp));

                ///*--- record the point ---*/
                //Point p = new Point(xpix,ypix);
                //if ((last.x!=xpix)||(last.y!=ypix))
                        //trail.put(p);
                //last = p;

                ///*--- get the list of all points ---*/
                //Enumeration point_list = trail.elements();

                ///*--- draw the trail ---*/
                //g.setColor(background);
                //Point from = (Point)point_list.nextElement();
                //while (point_list.hasMoreElements())
                        //{
                        //Point next = (Point)point_list.nextElement();
                        //g.drawLine(from.x,from.y,next.x,next.y);
                        //from = next;
                        //}
                }


	private String display_string = "blank";
	/**
         * Set the String that is printed on the robot's display.
         * For simulated robots, this appears printed below the agent
         * when view "Robot State" is selected.
         * @param s String, the text to display.
         */
        public void setDisplayString(String s)
		{
		display_string = s;
		}


        /**
         * Draw the robot's state.
         */
        public void drawState(Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                top =t; bottom =b; left =l; right =r;
                if (DEBUG) System.out.println("draw "+
                        w + " " +
                        h + " " +
                        t + " " +
                        b + " " +
                        l + " " +
                        r + " ");
                double meterspp = (r - l) / (double)w;
                int radius = (int)(RADIUS / meterspp);
                int xpix = (int)((position.x - l) / meterspp);
                int ypix = (int)((double)h - ((position.y - b) / meterspp));

                /*--- draw State ---*/
                g.setColor(background);
                g.drawString(display_string,xpix+radius+3,ypix-radius);
		
		/*--- draw Vectors ---*/
		displayVectors.draw(g, w, h, t, b, l, r);
                }


	/**
         * Set the length of the trail (in movement steps).
         * @param l int, the length of the trail.
         */
        public void setTrailLength(int l)
		{
		trail = new CircularBuffer(l);
		}


        /**
         * Clear the trail.
         */
        public void clearTrail()
		{
		trail.clear();
		}


        /**
         * Draw the robot in a specific spot.
         */
        public void draw(Vec2 pos, Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                Vec2 old_pos = position;
                position = pos;
                draw(g,w,h,t,b,l,r);
                position = old_pos;
                }


	/**
	 * Draw the robot.
	 */
	public void draw(Graphics g, int w, int h,
		double t, double b, double l, double r)
		{
		top =t; bottom =b; left =l; right =r;
		if (DEBUG) System.out.println("draw "+
			w + " " +
			h + " " +
			t + " " +
			b + " " +
			l + " " +
			r + " ");
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
	 * Clean up.
	 */
	public void quit()
		{
		}


	/**
	 * Gets time elapsed since the robot was instantiated. 
	 * Since this is simulation, it may not match real elapsed time.
	 */
	public long getTime()
		{
		return(time);
		}


	private	long	last_Obstaclest = 0;
	private	Vec2	last_Obstacles[];
	private	int	num_Obstacles;
	/**
	 * Get an array of Vec2s that point egocentrically from the
	 * center of the robot to the obstacles currently sensed by the 
	 * bumpers and sonars.
	 * @param timestamp only get new information 
	 *	if timestamp > than last call or timestamp == -1 .
	 * @return the sensed obstacles.
	 */
	public Vec2[] getObstacles(long timestamp)
		{
		return(new Vec2[0]);
		}


	private	double	obstacle_rangeM = 1.0;
	/**
	 * Set the maximum range at which a sensor reading should be considered
	 * an obstacle.  Beyond this range, the readings are ignored.
	 * The default range on startup is 1 meter.
	 * @param range the range in meters.
	 */
	public void setObstacleMaxRange(double range)
		{
		obstacle_rangeM = range;
		}


	private	long	last_VisualObjectst = 0;
	private Vec2[]	last_VisualObjects;
	private	int	num_VisualObjects = 0;
	private	int	last_channel = 0;
	/**
	 * Get an array of Vec2s that represent the
	 * locations of visually sensed objects egocentrically
 	 * from center of the robot to the objects currently sensed by the 
	 * vision system.	
	 * @param timestamp only get new information 
	 *	if timestamp > than last call or timestamp == -1 .
	 * @param channel (1-6) which type/color of object to retrieve.
	 * @return the sensed objects.
	 */
	public Vec2[] getVisualObjects(long timestamp, int channel)
		{
		return(new Vec2[0]);
		}


	private	long	last_VisualSizest = 0;
	/**
	 * NOT IMPLEMENTED:
	 * Get an array of doubles that represent an estimate of the
	 * size in square meters of the visually sensed objects.
	 * @param timestamp only get new information if 
	 * 	timestamp > than last call or timestamp == -1 .
	 * @param channel (1-6) which type/color of object to retrieve.
	 * @return the sizes of the sensed objects.
	 */
	public double[] getVisualSizes(long timestamp, int channel)
		{
		/* todo */
		return(new double[0]);
		}


	/**
	 * Get the position of the robot in global coordinates.
	 * @param timestamp only get new information 
	 *	if timestamp > than last call or timestamp == -1.
	 * @return the position.
	 */
	public Vec2 getPosition(long timestamp)
		{
		return(new Vec2(position.x, position.y));
		}
		

	/**
	 * Get the position of the robot in global coordinates.
	 * @return the position.
	 */
	public Vec2 getPosition()
		{
		return(new Vec2(position.x, position.y));
		}
		

	/**
	 * Reset the odometry of the robot in global coordinates.
	 * This might be done when reliable sensor information provides
	 * a very good estimate of the robot's location, or if you
	 * are starting the robot in a known location other than (0,0).
	 * Do this only if you are certain you're right!
	 * @param position the new position.
	 */
	public void resetPosition(Vec2 posit)
		{
		position.setx(posit.x);
		position.sety(posit.y);
		}


	private boolean	in_reverse = false;
	/**
	*/
	public double getSteerHeading(long timestamp)
		{
		return(0);
		}

	
	/**
	*/
	public void resetSteerHeading(double heading)
		{
		}


	private double desired_heading;
	/**
	*/
	public void setSteerHeading(long timestamp, double heading)
		{
		}
	

	/**
	*/ 
	public void setSpeed(long timestamp, double speed)
		{
		}


	/**
	*/
	public void setBaseSpeed(double speed)
		{
		}


        /**
         * Return an int represting the player's ID on the team.
         * This value may not be valid if the simulation has not
         * been "set up" yet.  Do not use it during initialization.
         * @param timestamp only get new information if
         *      timestamp > than last call or timestamp == -1 .
         * @return the number.
         */
        public int getPlayerNumber(long timestamp)
                {
                return(kin_sensor.getPlayerNumber(all_objects));
                }


        /**
	 * NOT IMPLEMENTED
         * Get an array of Vec2s that point egocentrically from the
         * center of the robot to the teammates currently sensed by the
         * robot.
         * @param timestamp only get new information if
         *      timestamp > than last call or timestamp == -1 .
         * @return the sensed teammates.
         */
        public Vec2[] getTeammates(long timestamp)
                {
                return(new Vec2[0]);
                }


        /**
	 * NOT IMPLEMENTED
         * Get an array of Vec2s that point egocentrically from the
         * center of the robot to the opponents currently sensed by the
         * robot.
         * @param timestamp only get new information if
         *      timestamp > than last call or timestamp == -1 .
         * @return the sensed teammates.
         */
        public Vec2[] getOpponents(long timestamp)
                {
                return(new Vec2[0]);
                }


        private double  kin_rangeM = 4.0;
        /**
         * Set the maximum range at which a sensor reading should be considered
         * kin.  Beyond this range, the readings are ignored.
         * Also used by opponent sensor.
         * The default range on startup is 1 meter.
         * @param range the range in meters.
         */
        public void setKinMaxRange(double range)
                {
                kin_rangeM = range;
                kin_sensor.setKinMaxRange(range);
                }

	  public Color getForegroundColor() { return foreground; }

	  public Color getBackgroundColor() { return background; }


        /*--- Transceiver methods ---*/


        public void multicast(int[] ids, Message m)
                throws CommunicationException
                {
                transceiver.multicast(ids, m, all_objects);
                }

        public void broadcast(Message m)
                {
                transceiver.broadcast(m, all_objects);
                }

        public void unicast(int id, Message m)
                throws CommunicationException
                {
                transceiver.unicast(id, m, all_objects);
                }

        public CircularBufferEnumeration getReceiveChannel()
                {
                return(transceiver.getReceiveChannel());
                }

        public void setCommunicationMaxRange(double m)
                {
                transceiver.setCommunicationMaxRange(m);
                }

        public void receive(Message m)
                {
                transceiver.receive(m);
                }

        public boolean connected()
                {
                return(true);
                }
	}

/*
 * SimpleN150Sim.java
 */

package EDU.gatech.cc.is.abstractrobot;

import java.awt.*;
import java.util.Enumeration;
import EDU.gatech.cc.is.util.*;
import EDU.gatech.cc.is.simulation.*;
import EDU.gatech.cc.is.communication.*;
import EDU.cmu.cs.coral.simulation.*;
import EDU.cmu.cs.coral.abstractrobot.*;

import EDU.cmu.cs.coral.util.Polygon2;
import EDU.cmu.cs.coral.util.Circle2;


/**
 * SimpleN150Sim implements SimpleN150 for simulation.
 * Also includes code implementing communication, gripper and
 * vision.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @see SimpleN150
 * @author Tucker Balch
 * @version $Revision: 1.6 $
 */

public class SimpleN150Sim extends Simple
	implements SimpleN150, SimulatedObject
	{
	private	CircularBuffer	trail;	// robot's trail
	private KinSensorSim	kin_sensor;	// senses our kin
	private TransceiverSim	transceiver;	// comm to other robots
	protected Vec2	position;
	protected Vec2	steer;
	private	double	turret;
	private	double	speed;
	protected Color	foreground, background;
	private long	time;
	private double	timed;
	protected double left, right, top, bottom;
	private	SimulatedObject[] all_objects = new SimulatedObject[0];
	private	int	visionclass;
	public	static final boolean DEBUG = false;
	 
	/**
	 * Instantiate a <B>SimpleN150Sim</B> object.  Be sure
	 * to also call init with proper values.
	 * @see SimpleN150Sim#init
	 */
        public SimpleN150Sim()
		{
		/*--- set parameters ---*/
                super(1);

		position = new Vec2(0,0);
		steer = new Vec2(1,0);
		turret = 0;
		foreground = Color.black;
		background = Color.black;
		if (DEBUG) System.out.println("SimpleN150Sim: instantiated.");

		/*--- set default bounds ---*/
		top = 1000;
		bottom = -1000;
		left = -1000;
		right = 1000;
		}	


        /**
         * Initialize a <B>SimpleN150Sim</B> object.
         */
	public void init(double xp, double yp, double tp, double ignore,
		Color f, Color b, int v, int i, long s)
		{
		trail = new CircularBuffer(1000);
		setID(i);
                kin_sensor = new KinSensorSim(this);
		transceiver = new TransceiverSim(this, this);
		position = new Vec2(xp,yp);
		steer = new Vec2(1,0);
		steer.sett(tp);
		turret = tp;
		foreground = f;
		background = b;
		time = 0;
		timed = 0;
		visionclass = v;
		if (DEBUG) System.out.println("SimpleN150Sim: initialized"
			+" at "+xp+","+yp);	
		}


	/**
	 * Take a simulated step;
	 */
	private double last_traversability = 1.0;
	public void takeStep(long time_increment, SimulatedObject[] all_objs)
		{
		if (DEBUG) System.out.println("SimpleN150Sim.TakeStep()");

		/*--- keep pointer to the other objects ---*/
		all_objects = all_objs;

		/*--- update the time ---*/
		time += time_increment;
		double time_incd = ((double)time_increment)/1000;
		timed += time_incd;

		/*--- update the steering ---*/
		double sturn = Units.BestTurnRad(steer.t, desired_heading);
		if (Math.abs(sturn) > (MAX_STEER*time_incd))
			{
			if (sturn<0)
				sturn = -MAX_STEER*time_incd;
			else sturn = MAX_STEER*time_incd;
			}
		steer.sett(steer.t + sturn);

		/*--- update the turret ---*/
		double tturn = Units.BestTurnRad(turret, 
			desired_turret_heading);
		if (Math.abs(tturn) > (MAX_TURRET*time_incd))
			{
			if (tturn<0)
				tturn = -MAX_TURRET*time_incd;
			else tturn = MAX_TURRET*time_incd;
			}
		turret = Units.ClipRad(turret + tturn);

		/*--- compute velocity ---*/
		Vec2 velocity = new Vec2(steer.x, steer.y);
		if (in_reverse)
			velocity.setr(-base_speed * last_traversability
				* desired_speed);
		else
			velocity.setr(base_speed * last_traversability
				* desired_speed);
		//System.out.println(velocity.r);
		// don't drive unless close turret aligned.
		//if (Math.abs(Units.BestTurnRad(turret,
                        //desired_turret_heading))>Math.PI/2.0)
			//velocity.setr(0.00001);

		/*--- compute a movement step ---*/
		Vec2 mvstep = new Vec2(velocity.x, velocity.y);
		mvstep.setr(mvstep.r * time_incd);

		/*--- test the new position to see if in bounds ---*/
		Vec2 pp = new Vec2(position.x, position.y);
		pp.add(mvstep);
		if (pp.x+RADIUS > right)
			{
			position.setx(right-RADIUS);
			velocity.setx(0);
			mvstep.setx(0);
			}
		else if (pp.x-RADIUS < left)
			{
			position.setx(left+RADIUS);
			velocity.setx(0);
			mvstep.setx(0);
			}

		if (pp.y+RADIUS > top)
			{
			position.sety(top-RADIUS);
			velocity.sety(0);
			mvstep.sety(0);
			}
		else if (pp.y-RADIUS < bottom)
			{
			position.sety(bottom+RADIUS);
			velocity.sety(0);
			mvstep.sety(0);
			}

		/*--- test the new position to see if on top of obstacle ---*/
		pp = new Vec2(position.x, position.y);
		boolean moveok = true;
		last_traversability = 1.0;
		pp.add(mvstep);
		for (int i=0; i<all_objects.length; i++)
			{
			if (all_objects[i].isObstacle() && 
				(all_objects[i].getID() != unique_id))
				{
				Vec2 tmp = all_objects[i].getClosestPoint(pp);
				if (tmp.r < RADIUS)
					{
					moveok = false;
					break;
					}
				}
			else if (all_objects[i] instanceof 
				SimulatedTerrainObject)
				{
				Vec2 tmp = all_objects[i].getClosestPoint(pp);
				if (tmp.r == 0) // on/in object
					last_traversability =
				((SimulatedTerrainObject)all_objects[i]).getTraversability();
				}
			}
		if (moveok) position.add(mvstep);

		/*--- test the new position to see if on top of pushable ---*/
		for (int i=0; i<all_objects.length; i++)
			{
			if (all_objects[i].isPushable() && 
				(all_objects[i].getID() != unique_id))
				{
				Vec2 tmp = all_objects[i].getClosestPoint(pp);
				if (tmp.r < RADIUS)
					{
					tmp.setr(RADIUS - tmp.r);
					all_objects[i].push(tmp, velocity);
					}
				}
			}

		/*--- check for object in gripper ---*/
		if (trigger_mode)
			setGripperFingers(-1,-1);
		}


	/*--- From SimulatedObject ---*/

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


	public void push(Vec2 d, Vec2 v)
		{
		// sorry no pushee robots!
		}

	public void pickUp(SimulatedObject o)
		{
		// sorry no pickupee robots!
		}

	public void putDown(Vec2 p)
		{
		// sorry no put downee robots!
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
                top =t; bottom =b; left =l; right =r;
                if (DEBUG) System.out.println("draw "+
                        w + " " +
                        h + " " +
                        t + " " +
                        b + " " +
                        l + " " +
                        r + " ");
                double meterspp = (r - l) / (double)w;
                int xpix = (int)((position.x - l) / meterspp);
                int ypix = (int)((double)h - ((position.y - b) / meterspp));

                /*--- record the point ---*/
                Point p = new Point(xpix,ypix);
                if ((last.x!=xpix)||(last.y!=ypix))
                        trail.put(p);
                last = p;

                /*--- get the list of all points ---*/
                Enumeration point_list = trail.elements();

                /*--- draw the trail ---*/
                g.setColor(background);
                Point from = (Point)point_list.nextElement();
                while (point_list.hasMoreElements())
                        {
                        Point next = (Point)point_list.nextElement();
                        g.drawLine(from.x,from.y,next.x,next.y);
                        from = next;
                        }
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
		int turretd = (int)Units.RadToDeg(turret);
		int visionr = (int)(MultiForageN150.VISION_RANGE / meterspp);
		int xpix = (int)((position.x - l) / meterspp);
		int ypix = (int)((double)h - ((position.y - b) / meterspp));
		if (DEBUG) System.out.println("robot at"+
			" at "+xpix+","+ypix);

		/*--- draw the main body ---*/
		g.setColor(foreground);
		g.fillOval(xpix - radius, ypix - radius,
			radius + radius, radius + radius);
		//g.drawOval(xpix - radius, ypix - radius,
		//	radius + radius, radius + radius);

		/*--- draw the turret ---*/
		g.setColor(background);
		int dirx = xpix + (int)((double)radius * Math.cos(turret));
		int diry = ypix + -(int)((double)radius * Math.sin(turret));
		g.drawLine(xpix, ypix, dirx, diry);
		g.drawArc(xpix - visionr, ypix - visionr,
				visionr + visionr, visionr + visionr,
				turretd - (MultiForageN150.VISION_FOV_DEG/2), 
					MultiForageN150.VISION_FOV_DEG);
				

		/*--- draw steer      ---*/
		dirx = xpix + (int)((double)radius * Math.cos(steer.t)*0.5);
		diry = ypix + -(int)((double)radius * Math.sin(steer.t)*0.5);
		g.drawLine(xpix, ypix, dirx, diry);

		/*--- draw what we are carrying ---*/
		if (in_gripper != null)
			{
			Vec2 gpos = new Vec2(RADIUS,0);
			gpos.sett(turret);
			gpos.add(position);
			in_gripper.draw(gpos,g,w,h,t,b,l,r);
			}
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
		if((timestamp > last_Obstaclest)||(timestamp == -1))
			{
			if (timestamp != -1) last_Obstaclest = timestamp;
			Vec2 tmp_objs[] = new Vec2[all_objects.length];
			num_Obstacles = 0;
			/*--- check all objects ---*/
			for(int i = 0; i<all_objects.length; i++)
				{
				/*--- check if it's an obstacle and not self ---*/
				if (all_objects[i].isObstacle() &&
					(all_objects[i].getID() != unique_id))
					{
					Vec2 tmp = all_objects[i].getClosestPoint(
							position);
					if (tmp.r<obstacle_rangeM)
					tmp_objs[num_Obstacles++] = tmp;
					}
				}
			last_Obstacles = new Vec2[num_Obstacles];
			for(int i = 0; i<num_Obstacles; i++)
			last_Obstacles[i] = new Vec2(tmp_objs[i].x,
				tmp_objs[i].y);
			}
		Vec2[] retval = new Vec2[num_Obstacles];
		for(int i = 0; i<num_Obstacles; i++)
			retval[i] = new Vec2(last_Obstacles[i].x,
				last_Obstacles[i].y);
		return(retval);
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
		if(((timestamp > last_VisualObjectst)||
			(channel != last_channel))||(timestamp == -1))
			{
			if (timestamp != -1) last_VisualObjectst = timestamp;
			last_channel = channel;
			num_VisualObjects = 0;
			Vec2 tmp_objs[] = new Vec2[all_objects.length];
			/*--- check all objects ---*/
			for(int i = 0; i<all_objects.length; i++)
				{
				/*--- check if it's of the right code and not self ---*/
				if (all_objects[i].getVisionClass()==channel &&
					(all_objects[i].getID() != unique_id))
					{
					Vec2 tmp = all_objects[i].getCenter(
							position);
					if ((tmp.r<MultiForageN150.VISION_RANGE)&&
						(Math.abs(
						Units.BestTurnRad(turret,tmp.t))
						< (MultiForageN150.VISION_FOV_RAD/2)))
						tmp_objs[num_VisualObjects++] = tmp;
					}
				}
			last_VisualObjects = new Vec2[num_VisualObjects];
			for(int i = 0; i<num_VisualObjects; i++)
			last_VisualObjects[i] = new Vec2(tmp_objs[i].x,
				tmp_objs[i].y);
			}
		Vec2[] retval = new Vec2[num_VisualObjects];
		for(int i = 0; i<num_VisualObjects; i++)
			retval[i] = new Vec2(last_VisualObjects[i].x,
				last_VisualObjects[i].y);
		return(retval);
		}
	  /**
	    * this is a dummy implementation to keep compatibility with VisualSensorObject.
	    * at this point, vision noise is not built into the class. for an example,
	    * see SimpleCyeSim.
	    */
	  public void setVisionNoise(double mean, double stddev, long seed) { }

	  

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


	private	long	last_VisualAxest = 0;
	/**
	 * NOT IMPLEMENTED:
	 * Get an array of doubles that represent the
	 * major axis orientation of the visually sensed objects.
	 * 0 and PI are horizontal, PI/2 is vertical.
	 * @param timestamp only get new information 
	 *	if timestamp > than last call or timestamp == -1 .
	 * @param channel (1-6) which type/color of object to retrieve.
	 * @return the major axes of the sensed objects.
	 */
	public double[] getVisualAxes(long timestamp, int channel)
		{
		/* todo */
		return(new double[0]);
		}


	private	long	last_ObjectInGrippert = -1;
	private	int	last_ObjectInGripper = -1;
	/**
	 * Get the kind of object in the gripper.
	 * @param timestamp only get new information 
	 *	if timestamp > than last call or timestamp == -1 .
	 * @return channel (1-6) which type/color of 
	 *	 object in the gripper, 0 otherwise.
	 */
	public int getObjectInGripper(long timestamp)
		{
		if((timestamp > last_ObjectInGrippert)||(timestamp == -1))
			{
			if (timestamp != -1) last_ObjectInGrippert = timestamp;
			last_ObjectInGripper = -1;

			/*--- check if we are holding something ---*/
			if (in_gripper != null)
				last_ObjectInGripper = 
					in_gripper.getVisionClass();
			else
				{
				/*--- find gripper position in global coord ---*/
				Vec2 gpos = new Vec2(RADIUS,0);
				gpos.sett(turret);
				gpos.add(position);
	
				/*--- check all objects ---*/
				for(int i = 0; i<all_objects.length; i++)
					{
					/*--- check if it's not self ---*/
					if (all_objects[i].getID() != unique_id)
						{
						Vec2 tmp = all_objects[i].getCenter(gpos);
						if ((tmp.r<MultiForageN150.GRIPPER_CAPTURE_RADIUS)
							&&(all_objects[i].getVisionClass()>=0))
							{
							last_ObjectInGripper = 
							all_objects[i].getVisionClass();
							break;
							}
						}
					}
				}
			}
		return(last_ObjectInGripper);
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
		return(steer.t);
		}

	
	/**
	*/
	public void resetSteerHeading(double heading)
		{
		/* ensure in legal range */
		heading = Units.ClipRad(heading); 

		/* if we're in reverse, the steer heading is PI out */
		if (in_reverse) heading = Units.ClipRad(heading + Math.PI);

		/* set the angle */
		steer.sett(heading);
		}


	private double desired_heading;
	/**
	*/
	public void setSteerHeading(long timestamp, double heading)
		{
		/* ensure in legal range */
		desired_heading = Units.ClipRad(heading);

		/* check if we should go in reverse */
		double turn = Units.BestTurnRad(steer.t, desired_heading);
		if (Math.abs(turn)>(Math.PI/2))
			{
			in_reverse = true;
			desired_heading = 
				Units.ClipRad(desired_heading+Math.PI);
			}
		else in_reverse = false;
		}
	

	/**
	*/
	public double getTurretHeading(long timestamp)
		{
		return(turret);
		}

	
	/**
	*/
	public void resetTurretHeading(double heading)
		{
		/* ensure in legal range */
		heading = Units.ClipRad(heading); 

		turret = heading;
		}

	
	double	desired_turret_heading = 0;
	/**
	*/
	public void setTurretHeading(long timestamp, double heading)
		{
		/* ensure in legal range */
		desired_turret_heading = Units.ClipRad(heading);
		}

	
	private double desired_speed = 0;
	/**
	*/ 
	public void setSpeed(long timestamp, double speed)
		{
		/* ensure legal range */
		if (speed > 1.0) speed = 1.0;
		else if (speed < 0) speed = 0;
		desired_speed = speed;
		}


	protected double base_speed = MAX_TRANSLATION;
	/**
	*/
	public void setBaseSpeed(double speed)
		{
		if (speed > MAX_TRANSLATION) speed = MAX_TRANSLATION;
		else if (speed < 0) speed = 0;
		base_speed = speed;
		}


	private	boolean	gripper_closed = false;
	private boolean trigger_mode = false;
	private SimulatedObject in_gripper = null;
	/**
	 * 0 closed 1 open.  -1 trigger mode.
	 */
	public void setGripperFingers(long timestamp, double grip)
		{
		if (grip>=1)
			{
			grip = 1;
			gripper_closed = false;
			trigger_mode = false;
			}
		if (grip==0)
			{
			trigger_mode = false;
			}
		if (grip < 0)
			{
			grip = 1; //open
			trigger_mode = true;
			gripper_closed = false;
			}

		//gripper_closed = true;
		/*--- if closing the gripper ---*/
		// see if there is something to pick up and gripper not already
		// closed
		if (((grip == 0)||trigger_mode)
			&&(gripper_closed==false)
			&&(in_gripper == null))
			{
			/*--- find gripper position in global coord ---*/
			Vec2 gpos = new Vec2(RADIUS,0);
			gpos.sett(turret);
			gpos.add(position);

			/*--- check all objects ---*/
			for(int i = 0; i<all_objects.length; i++)
				{
				/*--- check if it's not self ---*/
				if (all_objects[i].getID() != unique_id)
					{
					Vec2 tmp = all_objects[i].getCenter(gpos);

					// check if can pick it up
					if ((tmp.r< 
					   MultiForageN150.GRIPPER_CAPTURE_RADIUS)
						&&(all_objects[i].isPickupable())
						&&(all_objects[i].getVisionClass()>=0))
						{
						in_gripper = all_objects[i];
						all_objects[i].pickUp(
							(SimulatedObject) this);
						trigger_mode = false;
						gripper_closed = true;
						break;
						}
					}
				}
			}
		else if (grip == 1)
		/*--- if opening the gripper ---*/
			{
			// see if we should put something down
			if (in_gripper != null)
				{
				//System.out.println("putdown "+trigger_mode
					//+gripper_closed);
				Vec2 gpos = new Vec2(RADIUS,0);
				gpos.sett(turret);
				gpos.add(position);
				in_gripper.putDown(gpos);
				in_gripper = null;
				}
			gripper_closed = false;
			trigger_mode = false;
			}
		if (grip == 0)
			{
			trigger_mode = false;
			gripper_closed = true;
			}
		}


        private long    last_Opponentst = 0;
        private Vec2    last_Opponents[];
        /**
         * Get an array of Vec2s that point egocentrically from the
         * center of the robot to the opponents currently sensed by the
         * robot
         * @param timestamp only get new information if 
	 *		timestamp > than last call or timestamp == -1 .
         * @return the sensed Opponents.
         */
        public Vec2[] getOpponents(long timestamp)
                {
                if((timestamp > last_Opponentst)||(timestamp == -1))
                        {
                        if (timestamp != -1) last_Opponentst = timestamp;
                        last_Opponents = kin_sensor.getOpponents(all_objects);
                        }
                return(last_Opponents);
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


        private long    last_Teammatest = 0;
        private Vec2    last_Teammates[] = new Vec2[0];
        /**
         * Get an array of Vec2s that point egocentrically from the
         * center of the robot to the teammates currently sensed by the
         * robot.
         * @param timestamp only get new information if
         *      timestamp > than last call or timestamp == -1 .
         * @return the sensed teammates.
         */
        public Vec2[] getTeammates(long timestamp)
                {
                if((timestamp > last_Teammatest)||(timestamp == -1))
                        {
                        if (timestamp != -1) last_Teammatest = timestamp;
                        last_Teammates = kin_sensor.getTeammates(all_objects);
                        }
                return(last_Teammates);
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


	/**
	 * NOT IMPLEMENTED
	 */
	public void setGripperHeight(long timestamp, double position)
		{
		/* todo */
		}


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

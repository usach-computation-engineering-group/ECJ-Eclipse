/*
 * SocSmallSim.java
 */

package EDU.gatech.cc.is.abstractrobot;

import java.awt.*;
import java.util.Enumeration;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.CircularBuffer;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.simulation.*;
import EDU.gatech.cc.is.communication.*;
import EDU.gatech.cc.is.util.*;

import EDU.cmu.cs.coral.util.Polygon2;
import EDU.cmu.cs.coral.util.Circle2;


/**
 * Implements SocSmall for simulation.
 * You should see the specifications in SocSmall for details on 
 * how to use these methods.
 * <P>
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @see SocSmall
 * @author Tucker Balch
 * @version $Revision: 1.4 $
 */

public class SocSmallSim extends Simple 
	implements SocSmall, SimulatedObject
	{
	private CircularBuffer	trail;		// the robot's trail
	private int[]		opponent_ids;	// pointers to opponents
	private KinSensorSim	kin_sensor;	// senses our kin
	private TransceiverSim	transceiver;	// communicates to robots
	private Vec2		position;	// location of the robot
	private GolfBallSim	theball;	// ball object 
	private Vec2		steer;		// robot heading
	private	double		speed;		// current speed
	private	Color		foreground, 	// colors for drawing
				background;
	private long		time;		// elapsed time
	private double		timed;		// double version of time
	private double		left, 		// used for drawing purposes
				right, 
				top, 
				bottom;
	private	SimulatedObject[] all_objects 	// to keep track of other
		= new SimulatedObject[0];	// objects in the simulation
	private	int		visionclass;	// how other robots see us
	
	public	static final boolean DEBUG = false;// set true for debug
						// messages

	private	boolean	on_east_team = false;	// which team we are on
	private	boolean	on_west_team = false;

	private	Vec2	kick_off_pos = new Vec2();// initial position 
						// for kick offs

	private	Vec2	receive_pos = new Vec2();// initial position when
						// not kicking off
	private	Vec2	team_goal;		// location of goal to defend
	private	Vec2	opponent_goal;		// location of other goal


	/**
	 * Instantiate a <B>SocSmallSim</B> object.  Be sure
	 * to also call init with proper values.
	 * @see SocSmallSim#init
	 */
        public SocSmallSim()
		{
		if (DEBUG) System.out.println("SocSmall: instantiated.");
		// defaults
		position = new Vec2(0,0);
		steer = new Vec2(1,0);
		foreground = Color.black;
		background = Color.black;
		top = 10;
		bottom = -10;
		left = -10;
		right = 10;
		}	


        /**
         * Initialize a <B>SocSmallSim</B> object.  Some of the parameters
	 * are ignored because soccer robots have specific starting
	 * places and headings.
	 * @param xp	if negative, it means this robot is on the west team,
	 *		east team otherwise.
	 * @param yp	ignored.
	 * @param tp	ignored.
	 * @param ignore ignored.
	 * @param f	color 1 for the robot.
	 * @param b	color 2 for the robot.
	 * @param v	vision class of the robot (usually 1 or 2 depending
	 *	 	on whether on west team or east team).
	 * @param i	unique simulation id (NOT the same as player number!).
         */
	public void init(double xp, double yp, double tp, double ignore,
		Color f, Color b, int v, int i, long s)
		{
		trail = new CircularBuffer(100);
		if (DEBUG) System.out.println("MultiForageN150Sim: initialized"
			+" at "+xp+","+yp);

		/*--- set global parameters ---*/
		setID(i);
		kin_sensor = new KinSensorSim(this);
		transceiver = new TransceiverSim(this, this);
		foreground = f;
		background = b;
		time = 0;
		timed = 0;
		visionclass = v;

		if (xp<0)
			// on the west team
			{
			on_west_team = true;
			on_east_team = false;
			team_goal = new Vec2(-1.37,0);
			opponent_goal = new Vec2(1.37,0);
			}
		else
			// on east team
			{
			on_west_team = false;
			on_east_team = true;
			team_goal = new Vec2(1.37,0);
			opponent_goal = new Vec2(-1.37,0);
			}
		int num = getID()%5;
		if (num == 0)
			{
			kick_off_pos = new Vec2(1.2,0);
			receive_pos  = new Vec2(1.2,0);
			}
		else if (num == 1)
			{
			kick_off_pos = new Vec2(0.5,0);
			receive_pos  = new Vec2(0.5,0.25);
			}
		else if (num == 2)
			{
			kick_off_pos = new Vec2(0.15,0.5);
			receive_pos  = new Vec2(0.15,0.5);
			}
		else if (num == 3)
			{
			kick_off_pos = new Vec2(0.15,0.0);
			receive_pos  = new Vec2(0.50,-0.25);
			}
		else if (num == 4)
			{
			kick_off_pos = new Vec2(0.15,-0.5);
			receive_pos  = new Vec2(0.15,-0.5);
			}
		if (on_west_team)
			// west team kicks off first
			{
			kick_off_pos.setx(-kick_off_pos.x);
			receive_pos.setx(-receive_pos.x);
			position = new Vec2(kick_off_pos.x, kick_off_pos.y);
			}
		else
			// east receives
			position = new Vec2(receive_pos.x, receive_pos.y);
		steer = getOpponentsGoal(-1);
		}


	/**
	 * Take a simulated step;
	 */
	public void takeStep(long time_increment, SimulatedObject[] all_objs)
		{
		if (DEBUG) System.out.println("SocSmallSim.TakeStep()");

		/*--- keep pointer to the other objects ---*/
		all_objects = all_objs;

		/*--- locate the ball, if we haven't already ---*/
		if (theball == null)
			{
			for (int i=0; i<all_objects.length; i++)
				// the ball is always vision class 3!
				if (all_objects[i].getVisionClass()==3)
					{
					theball = (GolfBallSim)all_objects[i];
					break;
					}
			if (theball == null) //still!
				System.out.println("SocSmallSim: there is "+
					"apparently no GolfBallSim object "+
					"declared in the decription file "+
					"or it's vision class is not 3. ");
			}

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

		/*--- compute velocity ---*/
		Vec2 velocity = new Vec2(steer.x, steer.y);
		velocity.setr(base_speed * desired_speed);

		/*--- compute a movement step ---*/
		Vec2 mvstep = new Vec2(velocity.x, velocity.y);
		mvstep.setr(mvstep.r * time_incd);

		/*--- test the new position to see if in bounds ---*/
		// use bounds of official RoboCup soccer field
		Vec2 pp = new Vec2(position.x, position.y);
		pp.add(mvstep);
		if ((pp.x+RADIUS > 1.37)||(pp.x-RADIUS < -1.37))
			{
			velocity.setx(0);
			mvstep.setx(0);
			}
		if ((pp.y+RADIUS > 0.7625)||(pp.y-RADIUS < -0.7625))
			{
			velocity.sety(0);
			mvstep.sety(0);
			}

		/*--- test the new position to see if on top of obstacle ---*/
		pp = new Vec2(position.x, position.y);
		boolean moveok = true;
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
					Vec2 tmp2 = new Vec2(velocity);
					tmp2.sett(tmp.t);
					all_objects[i].push(tmp, tmp2);
					}
				}
			}

		/*--- finally, check to see if we need to reset position ---*/
		if (theball.playBall() != true)
			{
			trail.clear();
			if (on_east_team)
				{
				if (theball.eastKickOff())
					position = new Vec2(kick_off_pos.x, 
						kick_off_pos.y);
				else
					position = new Vec2(receive_pos.x, 
						receive_pos.y);
				}
			else if (theball.westKickOff())
				position = new Vec2(kick_off_pos.x, 
					kick_off_pos.y);
			else
				position = new Vec2(receive_pos.x, 
						receive_pos.y);
					
			steer = getOpponentsGoal(-1);
			}
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

	  public Color getForegroundColor() { return foreground; }

	  public Color getBackgroundColor() { return background; }


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


	private	Point last = new Point(0,0);
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
                displayVectors.draw(g,w,h,t,b,l,r);
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
                g.drawString(String.valueOf(getID()%5),xpix-radius,ypix-radius);
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
		int steerd = (int)Units.RadToDeg(steer.t);
		int xpix = (int)((position.x - l) / meterspp);
		int ypix = (int)((double)h - ((position.y - b) / meterspp));
		if (DEBUG) System.out.println("robot at"+
			" at "+xpix+","+ypix);

		/*--- draw the main body ---*/
		g.setColor(foreground);
		g.fillArc(xpix - radius, ypix - radius,
				radius + radius, radius + radius,
				steerd, 90);
		g.fillArc(xpix - radius, ypix - radius,
				radius + radius, radius + radius,
				steerd+180, 90);
		g.setColor(background);
		g.fillArc(xpix - radius, ypix - radius,
				radius + radius, radius + radius,
				steerd+90, 90);
		g.fillArc(xpix - radius, ypix - radius,
				radius + radius, radius + radius,
				steerd+270, 90);

		/*--- draw steer ---*/
		g.setColor(Color.black);
		int dirx = xpix + (int)((double)radius * Math.cos(steer.t));
		int diry = ypix + -(int)((double)radius * Math.sin(steer.t));
		g.drawLine(xpix, ypix, dirx, diry);
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


        private long    last_Opponentst = 0;
        private Vec2    last_Opponents[] = new Vec2[0];
        /**
         * Get an array of Vec2s that point egocentrically from the
         * center of the robot to the Opponents currently sensed by the
         * robot.
         * @param timestamp only get new information if 
	 * 	timestamp > than last call or timestamp == -1 .
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


	private	long	last_Teammatest = 0;
	private	Vec2	last_Teammates[] = new Vec2[0];
	/**
	 * Get an array of Vec2s that point egocentrically from the
	 * center of the robot to the teammates currently sensed by the 
	 * robot.
	 * @param timestamp only get new information if 
	 *	timestamp > than last call or timestamp == -1 .
	 * @return the sensed teammates.
	 */
	public Vec2[] getTeammates(long timestamp)
		{
		if((timestamp > last_Teammatest)||(timestamp == -1))
			{
			if (timestamp != -1) last_Teammatest = timestamp;
			last_Teammates = kin_sensor.getTeammates(all_objects);
			}
		Vec2[] retval = new Vec2[last_Teammates.length];
		for (int i = 0; i<last_Teammates.length; i++)
			retval[i] = new Vec2(last_Teammates[i].x,
				last_Teammates[i].y);
		return(retval);
		}

	public Vec2 getBall(long timestamp)
		{
		if (theball!=null) return(
			new Vec2(theball.getCenter(position)));
		else return(new Vec2());
		}


	long last_JustScoredt = 0;
	int last_JustScored  = 0;
        /**
         * Get an integer that indicates whether a scoring event
         * just occured.
         * @param timestamp only get new information
         *        if timestamp > than last call or timestamp == -1.
         * @return 1 if team just scored, -1 if scored against,
         *        0 otherwise.
         */
	public int getJustScored(long timestamp)
		{
		if((timestamp > last_JustScoredt)||(timestamp == -1))
			{
			if (timestamp != -1) last_JustScoredt = timestamp;

			last_JustScored = 0;

			if (theball==null) 
				return(0);
			else
				{
				if (on_east_team)
					{
					if (theball.eastKickOff())
						last_JustScored = -1;
					if (theball.westKickOff())
						last_JustScored = 1;
					}
				else 
					{
					if (theball.eastKickOff())
						last_JustScored = 1;
					if (theball.westKickOff())
						last_JustScored = -1;
					}
				}
			}
		return(last_JustScored);
		}


	public Vec2 getOurGoal(long timestamp)
		{
		Vec2 retval = new Vec2(team_goal.x, team_goal.y);
		retval.sub(position);
		return(retval);
		}
	public Vec2 getOpponentsGoal(long timestamp)
		{
		Vec2 retval = new Vec2(opponent_goal.x, opponent_goal.y);
		retval.sub(position);
		return(retval);
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
	public boolean canKick(long timestamp)
		{
		boolean retval = false;
		Vec2 kickpos = new Vec2(steer.x, steer.y);
		kickpos.setr(RADIUS);
		kickpos.add(position);
		if (theball!=null)
			{
			Vec2 tmp = theball.getCenter(kickpos);
			if (tmp.r<=KICKER_SPOT_RADIUS)
				retval = true;
			}
		return(retval);
		}
	public void kick(long timestamp)
		{
		Vec2 d = new Vec2(0,0);
		Vec2 v = new Vec2(KICKER_SPEED,0);
		v.sett(steer.t);
		if (theball != null)
			theball.push(d,v);
		}


	private	long	last_Obstaclest = 0;
	private	Vec2	last_Obstacles[];
	private	int	num_Obstacles;
	/**
	 * Get an array of Vec2s that point egocentrically from the
	 * center of the robot to the obstacles currently sensed by the 
	 * bumpers and sonars.
	 * @param timestamp only get new information if 
	 *	timestamp > than last call or timestamp == -1 .
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


	private	double	kin_rangeM = 4.0;
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


	private	double	obstacle_rangeM = 4.0;
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


	/**
	 * Get the position of the robot in global coordinates.
	 * @param timestamp only get new information 
	 * 	if timestamp > than last call or timestamp == -1.
	 * @return the position.
	 * @see Simple#resetPosition
	 */
	public Vec2 getPosition(long timestamp)
		{
		return(new Vec2(position.x, position.y));
		}
		

	/**
	 * Get the position of the robot in global coordinates.
	 * @return the position.
	 * @see Simple#resetPosition
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
	 * @see Simple#getPosition
	 */
	public void resetPosition(Vec2 posit)
		{
		position.setx(posit.x);
		position.setx(posit.y);
		}


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
		}
	
	
	private double desired_speed = 0;
	/**
	 * @see SocSmallSim#setSpeed
	 */ 
	public void setSpeed(long timestamp, double speed)
		{
		/* ensure legal range */
		if (speed > 1.0) speed = 1.0;
		else if (speed < 0) speed = 0;
		desired_speed = speed;
		}


	private double base_speed = MAX_TRANSLATION;
	/**
	 * @see SocSmallSim#setBaseSpeed
	 */
	public void setBaseSpeed(double speed)
		{
		if (speed > MAX_TRANSLATION) speed = MAX_TRANSLATION;
		else if (speed < 0) speed = 0;
		base_speed = speed;
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

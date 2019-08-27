/*
 * SimpleCyeSim.java
 */

package EDU.cmu.cs.coral.abstractrobot;

import java.awt.*;
import java.lang.Math;
import java.util.Enumeration;
import java.util.*;
import EDU.gatech.cc.is.util.*;
import EDU.gatech.cc.is.simulation.*;
import EDU.gatech.cc.is.communication.*;
import EDU.cmu.cs.coral.simulation.*;
import EDU.cmu.cs.coral.abstractrobot.*;
import EDU.gatech.cc.is.abstractrobot.*;
import EDU.cmu.cs.coral.util.Polygon2;
import EDU.cmu.cs.coral.util.Circle2;
import EDU.cmu.cs.coral.localize.GaussianSampler;

/**
 * SimpleCyeSim implements SimpleCye for simulation.
 * Also includes code implementing communication and
 * vision.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1999,2000 CMU
 *
 * @author Rosemary Emery
 * @version $Revision: 1.10 $
 */

public class SimpleCyeSim extends Simple
    implements SimpleCye, SimulatedObject
	{
	private	CircularBuffer	trail;	// robot's trail
	private KinSensorSim	kin_sensor;	// senses our kin
	private TransceiverSim	transceiver;	// comm to other robots
	protected Vec2	position;
	protected Vec2	steer;
	private	double	speed;

	protected Vec2 trailer_steer;
	protected Vec2 trailer_position;

	  protected Vec2 mvstep; //the amount the front moves in 1 timestep

	protected Color	foreground, background;
	private long	time;
	private double	timed;
	protected double left, right, top, bottom;
	protected	SimulatedObject[] all_objects = new SimulatedObject[0];
	private	int	visionclass;
	public	static final boolean DEBUG = false;
	protected Polygon2 cyeBody;
	protected Polygon2 trailer;
	protected boolean CanTurn = true;

	  protected GaussianSampler visionNoiseGen; //this generates noise for us...
	  protected double visionNoiseStddev;// standard deviation of the noise....
	  protected double visionNoiseMean; //mean of thenoise (usually 0)

	/**
	 * Instantiate a <B>SimpleCyeSim</B> object.  Be sure
	 * to also call init with proper values.
	 * @see SimpleCyeSim#init
	 */
        public SimpleCyeSim()
		{
		/*--- set parameters ---*/
                super(1);

		position = new Vec2(0,0);
		steer = new Vec2(1,0);
		trailer_steer = new Vec2(1,0);
		trailer_position = new Vec2(1,0);
		updatePolys(position, trailer_position);
		foreground = Color.black;
		background = Color.black;
		if (DEBUG) System.out.println("SimpleCyeSim: instantiated.");

		//set the noise maker to initially be no noise
		visionNoiseStddev = 0.0;
		visionNoiseMean = 0.0;
		visionNoiseGen = new GaussianSampler(1,31337); //one variable and seed value
		

		/*--- set default bounds ---*/
		top = 1000;
		bottom = -1000;
		left = -1000;
		right = 1000;
		}	


        /**
         * Initialize a <B>SimpleCySim</B> object.
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
		trailer_steer = new Vec2(1,0);
		trailer_steer.sett(tp); // initial same heading as front of cye robot
		trailer_position = new Vec2(xp,yp); // initial same position as robot
		mvstep = new Vec2(0,0);
		
		updatePolys(position, trailer_position);
		foreground = f;
		background = b;
		time = 0;
		timed = 0;
		visionclass = v;
		if (DEBUG) System.out.println("SimpleCyeSim: initialized"
			+" at "+xp+","+yp);
		}


	/**
	 * Take a simulated step;
	 */
	private double last_traversability = 1.0;
	public void takeStep(long time_increment, SimulatedObject[] all_objs)
		{
		if (DEBUG) System.out.println("SimpleCyeSim.TakeStep()");

		/*--- keep pointer to the other objects ---*/
		all_objects = all_objs;

		/*--- update the time ---*/
		time += time_increment;
		double time_incd = ((double)time_increment)/1000;
		timed += time_incd;

		/*--- compute left and right wheel speeds ---*/

		double right_wheel_vel = 0.0;
		double left_wheel_vel = 0.0;		

		/*--- figuring out position relative to where what to be heading ---*/
		/*--- i.e. controller ---*/

		double temp_heading = desired_heading;
		double temp_steer_t = steer.t;

		double heading_difference = temp_heading - temp_steer_t;

		/*--- fix heading difference so that lies between +/- PI ---*/

		if (heading_difference > Math.PI)
			{
			heading_difference -= 2*Math.PI;
			}
		else if (heading_difference < -1*Math.PI)
			{
			heading_difference += 2*Math.PI;
			}

		double angle_between_trailer_and_front = steer.t - trailer_steer.t;

		if (angle_between_trailer_and_front > Math.PI)
			{
			angle_between_trailer_and_front -= 2*Math.PI;
			}
		else if (angle_between_trailer_and_front < -1*Math.PI)
			{
			angle_between_trailer_and_front += 2*Math.PI;
			}

		// now for controller

		// proportional control
		double minNonWrap = 0.0*desired_speed; // minimum for non cord wrapping case
		double minWrap = 0.15*desired_speed; // minimum for cord wrapping case
		double proportion = 0.0; // what will use to determine speed
		
		if (desired_speed < 0.0)
		  {
                        if (canTurn(angle_between_trailer_and_front, heading_difference))
                            {
                                right_wheel_vel = base_speed*desired_speed;
				left_wheel_vel = base_speed*desired_speed;
                            }
                        else
			  {
			    right_wheel_vel = 0.0;
			    left_wheel_vel = 0.0;
			  }

                    }
		
		else if(steer.t == desired_heading) 
			{
			// if heading in direction want to be in keep on going
			right_wheel_vel = desired_speed*base_speed;
			left_wheel_vel = desired_speed*base_speed;
			}
		else if(desired_speed == 0.0) 
			{
			
			// call after above so will stop turning if headed in the right direction
			if (canTurn(angle_between_trailer_and_front, heading_difference))
				{
				right_wheel_vel = dsignum(heading_difference)*0.25*base_speed;
				left_wheel_vel = -1.0*right_wheel_vel;
				}
			else 
				{
				right_wheel_vel = 0.0;
				left_wheel_vel = 0.0;
				}
			}
		else if(canTurn(angle_between_trailer_and_front, heading_difference)) 
			{
			if (Math.abs(heading_difference) >= (Math.PI)/2 )
			{
				if (Math.abs(heading_difference) < Math.PI)
					{
					right_wheel_vel = dsignum(heading_difference)*desired_speed*base_speed;
					}
				else
					{
					right_wheel_vel = dsignum(heading_difference)*desired_speed*base_speed*-1;	
					}
				left_wheel_vel = -1*right_wheel_vel;
				}
			else 
				{
//				proportion = ((minNonWrap-desired_speed)/(Math.PI/2))*Math.abs(heading_difference) + desired_speed;
				proportion = (-4*(minNonWrap-desired_speed)/Math.pow(Math.PI,2))*Math.pow(Math.abs(heading_difference),2) + (4*(minNonWrap-desired_speed)/Math.PI)*Math.abs(heading_difference)+ desired_speed;
//				proportion = 0.0;
				if (dsignum(heading_difference) < 0.0)
					{
					right_wheel_vel = base_speed*proportion;
					left_wheel_vel = base_speed*desired_speed;
					}
				else 
					{
					right_wheel_vel = base_speed*desired_speed;
					left_wheel_vel = base_speed*proportion;
					}
				}
			}
		else // cannot turn in direction i want to go or i will wrap cord
			{
//			proportion = ((minWrap-desired_speed)/(Math.PI/2))*Math.abs(heading_difference) + desired_speed;
			proportion = (-4*(minWrap-desired_speed)/Math.pow(Math.PI,2))*Math.pow(Math.abs(heading_difference),2) + (4*(minWrap-desired_speed)/Math.PI)*Math.abs(heading_difference)+ desired_speed;
//			proportion = 0.175*desired_speed;
			if (dsignum(heading_difference) < 0.0)
				{
				right_wheel_vel = proportion*base_speed;
				left_wheel_vel = base_speed*desired_speed;
				}
			else 
				{
				right_wheel_vel = base_speed*desired_speed;
				left_wheel_vel = proportion*base_speed;
				}
			}


		/*--- set distance each wheel traveled in this time step ---*/

		double delta_distance_right = (right_wheel_vel*time_incd);
		double delta_distance_left = (left_wheel_vel*time_incd);

		/*--- set heading and velocity based on right and left wheel velocities ---*/
		double delta_phi = (delta_distance_right-delta_distance_left)/LENGTH; // sign taken into account
		steer.sett(steer.t + delta_phi);

		Vec2 delta_displacement = new Vec2(steer.x, steer.y);
		delta_displacement.setx(((delta_distance_right+delta_distance_left)/2)*Math.cos(steer.t));
		delta_displacement.sety(((delta_distance_right+delta_distance_left)/2)*Math.sin(steer.t));
		
		/*--- now compute the location of the trailer ---*/
		/*--- first the new heading of the trailer ---*/

		double trailer_delta_phi = -1*sgn(desired_speed)*delta_displacement.r*Math.sin(-steer.t + trailer_steer.t)/HITCH_TO_TRAILER_WHEEL;
		trailer_steer.sett(trailer_steer.t + trailer_delta_phi);

		/*--- now the new position of the trailer ---*/

		Vec2 delta_trailer_displacement = new Vec2(trailer_steer.x, trailer_steer.y);
		delta_trailer_displacement.setx(sgn(desired_speed)*delta_displacement.r*Math.cos(steer.t));
		delta_trailer_displacement.sety(sgn(desired_speed)*delta_displacement.r*Math.sin(steer.t));

		// note: position of trailer is relative to its hitch		

		Vec2 trailer_mvstep = new Vec2(delta_trailer_displacement.x, delta_trailer_displacement.y);

		/*--- compute a movement step ---*/
		mvstep = new Vec2(delta_displacement.x, delta_displacement.y);
		Vec2 velocity = new Vec2(steer.x, steer.y);

		if (time_incd == 0.0) 
			{
			velocity.setr(0);
			} 
		else 
			{
			velocity.setr(delta_displacement.r/time_incd);
			}

		/*--- test the new position to see if in bounds ---*/
		// note: this test must be completely rewritten in order to account for
		// configuration space of cye robot - it is _not_ a sphere!!!!
		// note:  add in check to see if trailer in bounds

		Vec2 pp = new Vec2(position.x, position.y);
		Vec2 trailer_pp = new Vec2(trailer_position.x, trailer_position.y);

// ensure not colliding with edge of boundary

		pp.add(mvstep);

//		System.out.println("position " + pp.x + " " + pp.y + " " + time);
		if ((pp.x+RADIUS > right)||(pp.x-RADIUS < left))
			{
			velocity.setr(0);
			mvstep.setr(0);
			trailer_mvstep.setr(0);
			}
		if ((pp.y+RADIUS > top)||(pp.y-RADIUS < bottom))
			{
			velocity.setr(0);
			mvstep.setr(0);
			trailer_mvstep.setr(0);
			}

		/*--- test the new position to see if on top of obstacle ---*/
		// first check main body

		boolean moveok = true;
		last_traversability = 1.0;
		trailer_pp.add(trailer_mvstep);


		updatePolys(pp, trailer_pp);

		for (int i=0; i<all_objects.length; i++)
			{
			if (all_objects[i].isObstacle() && 
				(all_objects[i].getID() != unique_id))
				{
				Vec2 tmp = all_objects[i].getClosestPoint(pp);
				if (tmp.r < RADIUS)
					// only perform more time consuming check is object within RADIUS of centre of robot
					{
					if(all_objects[i].checkCollision(cyeBody))
						{
						moveok = false;
						break;
						}
					else if(all_objects[i].checkCollision(trailer))
						{
						moveok = false;
						break;
						}
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

		if (moveok) 
			{
			position.add(mvstep);
			trailer_position.add(trailer_mvstep);
			}

		else  // move is not okay - take back orientation change
			{
			steer.sett(steer.t - delta_phi);
			trailer_steer.sett(trailer_steer.t - trailer_delta_phi);
			updatePolys(position, trailer_position);
			}
	
		/*--- test the new position to see if on top of pushable ---*/
		for (int i=0; i<all_objects.length; i++)
			{
			if (all_objects[i].isPushable() && 
				(all_objects[i].getID() != unique_id))
				{
				Vec2 tmp = all_objects[i].getClosestPoint(position);
				if (tmp.r < RADIUS)
					// only perform more time consuming check is object within RADIUS of centre of robot
					{
					if(all_objects[i].checkCollision(cyeBody))
						{
						  // push based on closest point
						  all_objects[i].push(tmp, velocity);
						}
					else if(all_objects[i].checkCollision(trailer))
						{
						  tmp = new Vec2(all_objects[i].getClosestPoint(trailer_position));
						  all_objects[i].push(tmp, velocity);
						}
					}
				}
			}
	}

	boolean canTurn(double angle_between_trailer_and_front, double heading_difference)
		{
		if ((Math.abs(angle_between_trailer_and_front ) <= (Math.PI)/2) || 
			(angle_between_trailer_and_front <= -1*Math.PI/2 && heading_difference > 0.0)
			|| (angle_between_trailer_and_front >= Math.PI/2 && heading_difference < 0.0))
			{
			  CanTurn = true;
				return true;
			}
		else 
			{
			  CanTurn = false;
			return false;
			}
		}			

	public boolean getCommandError(long timestamp)
           {
	     if (CanTurn)
	       return false;
	     else
	       return true;
	   }

        public double getVoltage(long timestamp)
           {
	     return 12.0;
	   }

	void updatePolys(Vec2 pp, Vec2 trailer_pp)
		{		
		Vec2[] body = new Vec2[4];     // outline of body
		Vec2 vertex;
		double[] bodyx = new double[4];
		double[] bodyy = new double[4];
		double newAng;
		double xpix = pp.x;
		double ypix = pp.y;
		body[0] = new Vec2(WIDTH/2, LENGTH/2); 
		body[3] = new Vec2(-1*WIDTH/2, LENGTH/2);
		body[2] = new Vec2(-1*WIDTH/2, -1*LENGTH/2);
		body[1] = new Vec2(WIDTH/2,  -1*LENGTH/2);
		
		int j,k;
		for(j = 0; j<4; j++) // scale and rotate
			{
			newAng = body[j].t + steer.t;
			if (newAng >= 2*Math.PI)
				{
				newAng-= 2*Math.PI;
				}
			body[j].sett(newAng);
			bodyx[j] = body[j].x + xpix;
			bodyy[j] = body[j].y + ypix;
			}
		Vector bodyVector = new Vector();
		for(j=0;j<4;j++)
			{
			vertex = new Vec2(bodyx[j], bodyy[j]);
			bodyVector.addElement(vertex);
			}
		cyeBody = new Polygon2(bodyVector);

		double txpix = trailer_pp.x;
		double typix = trailer_pp.y;


		Vec2[] tbody = new Vec2[5];     // outline of trailer
		double[] tbodyx = new double[5];
		double[] tbodyy = new double[5];
		tbody[0] = new Vec2(TRAILER_FRONT, 0); 
		tbody[4] = new Vec2(-1*TRAILER_FRONT, TRAILER_WIDTH/2);
		tbody[3] = new Vec2(-1*TRAILER_LENGTH, TRAILER_WIDTH/2);
		tbody[2] = new Vec2(-1*TRAILER_LENGTH, -1*TRAILER_WIDTH/2);
		tbody[1] = new Vec2(-1*TRAILER_FRONT,  -1*TRAILER_WIDTH/2);
		for(k = 0; k<5; k++) // scale and rotate
			{
			newAng = tbody[k].t + trailer_steer.t;
			if (newAng >= 2*Math.PI)
				{
				newAng-= 2*Math.PI;
				}
			tbody[k].sett(newAng);
			tbodyx[k] = tbody[k].x + txpix;
			tbodyy[k] = tbody[k].y + typix;
			}
		Vector trailerVector = new Vector();
		for(k=0;k<5;k++)
			{
			vertex = new Vec2(tbodyx[k], tbodyy[k]);
			trailerVector.addElement(vertex);
			}
		trailer = new Polygon2(trailerVector);
		}


	/*--- From SimulatedObject ---*/

	double dsignum(double a)
		{
		if (a < 0.0)
			{
			return -1.0;
			}
		else
			{
			return 1.0;
			}
		}

	double sgn(double a)
           {
	     if (a < 0.0)
	       return -1.0;
	     if (a > 0.0)
	       return 1.0;
	     else
	       return 0.0;
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
		int numberEdges = cyeBody.vertices.size(); // n edges if n vertices (as vertex n+1 wraps round to vertex 0)
		double scale;
		int i;
		Vector closestPts = new Vector(); 
		Vec2 closest, vertex1, vertex2, vec1, vector2;
		for (i=0;i<numberEdges;i++)
			{
			vertex1 = (Vec2)cyeBody.vertices.elementAt(i);
			vertex2 = (Vec2)cyeBody.vertices.elementAt((i+1)%numberEdges);
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
		numberEdges = trailer.vertices.size(); // n edges if n vertices (as vertex n+1 wraps round to vertex 0)
		for (i=0;i<numberEdges;i++)
			{
			vertex1 = (Vec2)trailer.vertices.elementAt(i);
			vertex2 = (Vec2)trailer.vertices.elementAt((i+1)%numberEdges);
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
		int numberEdges = cyeBody.vertices.size(); // n edges if n vertices (as vertex n+1 wraps round to vertex 0)
		double scale;
		int i;

		for (i=0;i<numberEdges;i++)
			{
			vertex1 = (Vec2)cyeBody.vertices.elementAt(i);
			vertex2 = (Vec2)cyeBody.vertices.elementAt((i+1)%numberEdges);
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

		numberEdges = trailer.vertices.size(); // n edges if n vertices (as vertex n+1 wraps round to vertex 0)
		for (i=0;i<numberEdges;i++)
			{
			vertex1 = (Vec2)trailer.vertices.elementAt(i);
			vertex2 = (Vec2)trailer.vertices.elementAt((i+1)%numberEdges);
			vec1 = new Vec2(vertex2);
			vec1.sub(vertex1);
			vector2 = new Vec2(c.centre);
			vector2.sub(vertex1);
			scale = ((vec1.x*vector2.x)+(vec1.y*vector2.y))/((vec1.x*vec1.x)+(vec1.y*vec1.y));
			closestPt = new Vec2(scale*vec1.x, scale*vec1.y);
			closestPt.add(vertex1); // absolute position of closest point
			closestPt.sub(c.centre); // position of closest point relative to centre of current object
			if (closestPt.r <= c.radius)
				{
				// now need to check if closestPt lies between vertex1 and vertex2
				// i.e. it could lie on vector between them but outside of them
				closestPt.add(c.centre);
				vertex1.sub(c.centre);
				vertex2.sub(c.centre);
				if ( (vertex1.r <= c.radius) || (vertex2.r <= c.radius) || (trailer.pointWithinPolygon(closestPt)))
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
			if(cyeBody.lineIntersectsWithPolygon(vertex1, vertex2))
				{
				return true;
				}
			if(trailer.lineIntersectsWithPolygon(vertex1, vertex2))
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
                
		/*--- draw the vectors if any ---*/
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
		int radius = (int)(0.203 / meterspp);
		int visionr = (int)(SimpleCye.VISION_RANGE/meterspp);
		int xpix = (int)((position.x - l) / meterspp);
		int ypix = (int)((double)h - ((position.y - b) / meterspp));
		if (DEBUG) System.out.println("robot at"+
			" at "+xpix+","+ypix);

		/*--- draw the main body ---*/
		g.setColor(foreground);
		Vec2[] body = new Vec2[4];     // outline of body
		int[] bodyx = new int[4];
		int[] bodyy = new int[4];
		body[0] = new Vec2(WIDTH/2, LENGTH/2); 
		body[1] = new Vec2(-1*WIDTH/2, LENGTH/2);
		body[2] = new Vec2(-1*WIDTH/2, -1*LENGTH/2);
		body[3] = new Vec2(WIDTH/2,  -1*LENGTH/2);
		for(int j = 0; j<4; j++) // scale and rotate
			{
			body[j].setr(body[j].r / meterspp);
			body[j].sett(body[j].t - steer.t); 
			bodyx[j] = (int)body[j].x + xpix;
			bodyy[j] = (int)body[j].y + ypix;
			}
		g.fillPolygon(bodyx, bodyy, 4);
		int dirx = xpix + (int)((double)radius * Math.cos(steer.t));
		int diry = ypix + -(int)((double)radius * Math.sin(steer.t));
		g.setColor(background);
		/*--- draw steer      ---*/
		g.drawArc(xpix - visionr, ypix - visionr,
				visionr + visionr, visionr + visionr,
				(int)Units.RadToDeg(steer.t) 
				- (SimpleCye.VISION_FOV_DEG/2), SimpleCye.VISION_FOV_DEG);

		dirx = xpix + (int)((double)radius * Math.cos(steer.t)*0.5);
		diry = ypix + -(int)((double)radius * Math.sin(steer.t)*0.5);
		g.drawLine(xpix, ypix, dirx, diry);

		/*--- draw trailer ---*/

		int txpix = (int)((trailer_position.x - l) / meterspp);
		int typix = (int)((double)h - ((trailer_position.y - b) / meterspp));


		Vec2[] tbody = new Vec2[5];     // outline of trailer
		int[] tbodyx = new int[5];
		int[] tbodyy = new int[5];
		tbody[0] = new Vec2(TRAILER_FRONT, 0); 
		tbody[1] = new Vec2(-1*TRAILER_FRONT, TRAILER_WIDTH/2);
		tbody[2] = new Vec2(-1*TRAILER_LENGTH, TRAILER_WIDTH/2);
		tbody[3] = new Vec2(-1*TRAILER_LENGTH, -1*TRAILER_WIDTH/2);
		tbody[4] = new Vec2(-1*TRAILER_FRONT,  -1*TRAILER_WIDTH/2);
		for(int k = 0; k<5; k++) // scale and rotate
			{
			tbody[k].setr(tbody[k].r / meterspp);
			tbody[k].sett(tbody[k].t - trailer_steer.t);
			tbodyx[k] = (int)tbody[k].x + txpix;
			tbodyy[k] = (int)tbody[k].y + typix;
			}
		g.fillPolygon(tbodyx, tbodyy, 5);

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
					Vec2 tmp = all_objects[i].getClosestPoint(
							position);
					//add some noise to our vision.  in this case, 
					//change the value of tmp by a noisy amount
					//this is a 0-meaned distribution
					double noise = visionNoiseGetNext();
					
					//the new value is x% of the old one
					tmp.setr( tmp.r * (1.0 - noise));

					//get new random part for theta
					noise = visionNoiseGetNext();
					tmp.sett( tmp.t* (1.0 - noise));
					

					if ((tmp.r<SimpleCye.VISION_RANGE)&&
						(Math.abs(
						Units.BestTurnRad(steer.t,tmp.t))
						< (SimpleCye.VISION_FOV_RAD/2)))
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
	    * This sets the variance of the normal distribution used to
	    * simulate noise in the vision sensor.  It also gives a seed
	    * value, which allows for repeatable pseudo-noise.
	    * @param mean the mean of the distribution.  
	    * @param stddev this is the standard deviation.  It is defined for values >= 0.
	    * a vlaue of 0 means noise does not affect the sensor.
	    * @param seed this is the seed value for the random number generator.
	    */
	  public void setVisionNoise(double mean, double stddev, long seed) {
	    visionNoiseGen = new GaussianSampler(1, seed);
	    visionNoiseStddev = stddev;
	    visionNoiseMean = mean;
	  }
	    
	  protected double visionNoiseGetNext() {
	    //this is a mean =0 gaussian
	    return visionNoiseGen.generateDist(visionNoiseMean, visionNoiseStddev); 
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
/*
		double turn = Units.BestTurnRad(steer.t, desired_heading);
		if (Math.abs(turn)>(Math.PI/2))
			{
			in_reverse = true;
			desired_heading = 
				Units.ClipRad(desired_heading+Math.PI);
			}
		else in_reverse = false;
*/
		}
	

	/**
	*/
	
	private double desired_speed = 0;
	/**
	*/ 
	public void setSpeed(long timestamp, double speed)
		{
		/* ensure legal range */
		if (speed > 1.0) speed = 1.0;
		else if (speed < -1.0) speed = -1.0; // allowing backing up
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

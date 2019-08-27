/*
 * SimpleCyeHard.java
 */

package EDU.cmu.cs.coral.abstractrobot;

import java.util.Enumeration;
import java.awt.Color;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.cmu.cs.coral.cye.*;
import EDU.gatech.cc.is.abstractrobot.*;
import CMVision.*;
import java.util.*;
import java.io.*;

/**
 * SimpleCyeHard implements SimpleCye for
 * Cye  hardware using the  class.

 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @see SimpleCye
 * @author Rosemary Emery
 * @version $Revision: 1.6 $
 */

public class SimpleCyeHard extends Simple implements SimpleCye, HardObject
	{
	protected JCyeSrv Srv;
	protected static final boolean DEBUG = false;	
	protected double	cycles = 0;
	protected double	run_time_sum = 0;
	protected double	time_sum = 0;
	protected boolean	keep_running = true;
	protected int	hard_command = 0;
	protected int	old_hard_command = 0;
	protected Vec2 last_Position = new Vec2(0,0);
	protected Vec2 steer;
	protected Vec2 trailer_steer;
	protected Vec2 position = new Vec2(0,0);
	protected double CYEUNITSTOMETERS = 0.0254; // 0.03048;
        protected double SPEED_CONVERSION = 549.45; // converts metres/s to cye units/s (based on fact max speed is 0.91 m/s (3'/s) and in cye units is 500

	Vec2 origin = new Vec2(0,0);
	private	long	start_time = 0;
	protected	int	num_Obstacles;
	    protected boolean CanTurn = false;

          // replace this later with visual object class that contains CMVision myVision
	
	    private int XWIDTH = 80;
	    private double FOV = Math.PI*80.0/180;
	    private int YWIDTH = 60;
	    private double PIXEL_METER_CONVERSION = 0.01; // start with one pixel is one centimeter - highly unlikely and might need lookup

	    private int MAX_BALL_PIXEL = 70;
	    private double[] DistanceLookUp = { 10.275, 3.163, 3.163, 3.163, 2.4518, 2.147, 1.9438, 1.7406, 1.639, 1.5374,
						1.4358, 1.3342, 1.2834, 1.2326, 1.1818, 1.131, 1.0802, 1.0294, 0.9278, 0.8262,
						0.8008, 0.7754, 0.75, 0.7246, 0.6992, 0.6738, 0.6484, 0.623, 0.605982, 0.588964,
						0.5722, 0.555182, 0.538164, 0.5214, 0.504382, 0.487364, 0.4706, 0.453582, 0.436564,
						0.4198, 0.4071, 0.3944, 0.3817, 0.369, 0.36265, 0.3563, 0.34995, 0.3436, 0.3309,
						0.3182, 0.31185, 0.3055, 0.29915, 0.2928, 0.28645, 0.2801, 0.27375, 0.2674, 0.2547,
						0.242, 0.23565, 0.2293, 0.22295, 0.2166, 0.2166, 0.2166, 0.2166, 0.2166, 0.2166,
						0.2166, 0.2166 };

	    private double[][] PixelToDistance = new double[160][120];

	  // replace this later with a visual object class that contains one
	  JCMVision myVision;

        /**
         * Instantiate a <B>SimpleCyeHard</B> object.  You should only
         * instantiate one of these per robot connected to your
         * computer.  Standard call is SimpleCyeHard("/dev/ttySO", 9600, "W", 0));
         * @param commDevice "/dev/ttyS0", "COM1" ...
         * @param baud baud rate for communication.
	 * @param linkType W for WIRED, NR for NEW_RADIO and OR for OLD_RADIO
	 * @param colorID - robot id and depends on color of robot
         * @exception Exception If unable to configure the hardware.
         */
	public SimpleCyeHard(String commDevice, int baud, String linkType, int colorID) throws Exception
		{
		/*--- set default parameters ---*/
                super(1);
		// now read in PixelToDistance information


		/*--- open up the robot hardware ---*/
		if (linkType.equalsIgnoreCase("NR"))
		    {
		    System.out.println("initializing new radio cye");
		    Srv = new JCyeSrv(commDevice, baud, JCyeComm.NEW_RADIO, (byte)colorID);
		    }
		else if (linkType.equalsIgnoreCase("OR"))
		    {
		    System.out.println("intializing old-radio cye");
		    Srv = new JCyeSrv(commDevice, baud, JCyeComm.OLD_RADIO, (byte)colorID);
		    }
		else
		    {
		    System.out.println("initializing wired cye");
		    Srv = new JCyeSrv(commDevice, baud, JCyeComm.WIRED, (byte)colorID);
		    }


	
	    
		       		Srv.SendStopMotors();
			Srv.Wait(500);
	  
		/*--- make robot beep ---*/
        	Srv.SendBuzzerOn(true);
        	Srv.Wait(100); // 100 ms
        	Srv.SendBuzzerOn(false);

		/*--- reset robot ---*/
        	Srv.SendHeading(0);
		Srv.Wait(500);
		Srv.SendPosition(0,0);
		Srv.Wait(500);
		Srv.SendHandleLength(20);
		Srv.SendHandleLength(0); // try this, 0 mostly  works
		Srv.Wait(500);

		steer = new Vec2(0,0); // initialize to having a orientation of zero degrees
		trailer_steer = new Vec2(0,0); //initialize trailer to having an orientation of zero degrees
		/*--- start the clock ---*/
		start_time = System.currentTimeMillis();

		// vision stuff - again replace later with Visual object
		myVision = new JCMVision();
		boolean visionSuccess = myVision.init(0, 2*XWIDTH, 2*YWIDTH); // CHECK PARAMETERS
		if (!visionSuccess)
		  System.out.println("error initializing video capture device");
		// now read in PixelToDistance information

	       	FileReader file = new FileReader("PixelToDistance.dat");
		BufferedReader br = new BufferedReader(file);
		
		for(int y=0;y<120;y++)
		    {
			
			StringTokenizer st = new StringTokenizer(br.readLine());
			String buf;

			if (st.countTokens()!=160)
			    System.err.println("parsed PixelToDistance.txt wrong");
			for (int x=0;x<160;x++)
			    {
				buf = st.nextToken().trim();
				PixelToDistance[x][y]= (Double.valueOf(buf)).doubleValue();
			    }
		    }
		file.close();
		br.close();
		
	
		}


	/**
	 * Conducts periodic I/O with
	 * the robot.  It runs at most every SimpleCyeHard.MIN_CYCLE_TIME 
	 * milliseconds to gather sensor data from the robot, and issue
	 * movement commands.
	 */
	public void takeStep()
		{
		double	uncorrected_steering = 0;
		double	turn = 0;
		double	speed_command = 0;
		int	result = 0;
		long	current_time,  this_cycle,sleep_time;
		double currCycle;
		boolean turnOnly = false;
		double velocity;
		Vec2 new_Position = new Vec2(position);
		double right_wheel_vel=0;
		double left_wheel_vel=0;
		boolean collision = false;
		Vec2 displacement = new Vec2(position);
		double amount_displaced;
		if(keep_running)
		  {
			/*--- mark current time ---*/
			current_time = System.currentTimeMillis();
			/*--- get current position and battery info ---*/
			double x= (double)Srv.GetLastX()*CYEUNITSTOMETERS; // conversion from 1/10 of foot to meters
			double y= (double)Srv.GetLastY()*CYEUNITSTOMETERS;

			// position - first update last position and then set position to new position
			last_Position.setx(position.x);
			last_Position.sety(position.y);

			position = new Vec2(x,y);
			position.add(origin);


		System.out.println("value of keep running " + keep_running + " " + position.x + " " + position.y);

			if (desired_speed == 0.0)
			  {
			    displacement = new Vec2(0,0); // as when turning on spot there is a slight displacement
			  }

			else
			  {
			    displacement.sub(position); // displacement represents distance travelled last time step
			  }


			collision = Srv.GetObstacle();
			Srv.ClearObstacle();
			if (collision)
			    {
				num_Obstacles = 1;
			    }
			else
				num_Obstacles = 0;

			//--- compute steering command
			/*--- i.e. controller ---*/
		/*---  update  heading of the trailer ---*/
			amount_displaced = displacement.r*sgn(desired_speed);
			double trailer_delta_phi = -1*amount_displaced*Math.sin(-1*steer.t + trailer_steer.t)/HITCH_TO_TRAILER_WHEEL; // even use this length for sport trailer - might need to make a bit longer for regular trailer ?
		trailer_steer.sett(trailer_steer.t + trailer_delta_phi);
		System.out.println("trailer angle " + trailer_steer.t);
		System.out.println("displacement " + amount_displaced);
			// get new heading of robot
			// i.e. UPDATE STEER.T
			steer.sett(Srv.GetLastH()); // sets value of steer to what Cye reckons it is

			//System.out.println("current heading " + steer.t);

		// we now know the current heading and position of the drive unit and the current heading of the trailer
		// let's determine the new position of the drive unit based on the current desired_heading 
		// and the desired_speed - we will send the Cye these commands and then at the next take_step
		// update the position and heading of the drive unit and trailer as appropriate

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

		// update trailer_steer.t based on distance just travelled in last time step

		double angle_between_trailer_and_front = steer.t - trailer_steer.t;

		if (angle_between_trailer_and_front > Math.PI)
			{ 
			angle_between_trailer_and_front -= 2*Math.PI;
			}
		else if (angle_between_trailer_and_front < -1*Math.PI)
			{
			angle_between_trailer_and_front += 2*Math.PI;
			}

		System.out.println("angle between two " + angle_between_trailer_and_front);
		System.out.println("heading difference " + heading_difference);

		// now for controller

		// proportional control
		double minNonWrap = 0.0*desired_speed; // minimum for non cord wrapping case
		double minWrap = 0.15*desired_speed; // minimum for cord wrapping case
		double proportion = 0.0; // what will use to determine speed
		double distLength = 2.0; // this represents how many meters away from the current position of the robot
		// it will be told to go - this will always be farther away than the robot will really need to go given
		// it's velocity but this ensures that the robot doesn't stop early

		/*(if (collision)
		  {
		    new_Position = new Vec2(position);
		    velocity = 0.0;
		  }
		*/
		if (desired_speed < 0.0)
		    {
			if (canTurn(angle_between_trailer_and_front, heading_difference))
			    {
				velocity = base_speed*desired_speed;
				new_Position = new Vec2(position.x - Math.cos(desired_heading)*distLength, 
							position.y - Math.sin(desired_heading)*distLength);

			    }
			else
			    {
				velocity = 0.0;
				Srv.SendStopMotors();
		 		Srv.SendBuzzerOn(true);
				Srv.Wait(100); // 100 ms
				Srv.SendBuzzerOn(false);
				new_Position = new Vec2(position.x, position.y);
			    }
		    }
		else if(Math.abs(steer.t - desired_heading) <= 0.05) // tolerance of 1.6%
			{
			// if heading in direction want to be in keep on going
			velocity = base_speed*desired_speed;
			new_Position = new Vec2(position.x + Math.cos(desired_heading)*distLength, 
						position.y + Math.sin(desired_heading)*distLength);
			}
		else if(desired_speed == 0.0) 
			{
			
			// call after above so will stop turning if headed in the right direction
			if (canTurn(angle_between_trailer_and_front, heading_difference))
				{
				 turnOnly = true;
				 // set heading difference back to between 0 and 2pi
				 if (heading_difference < 0.0)
				   heading_difference = heading_difference + 2*Math.PI;
				 velocity = 0.4*base_speed; // was turning too far at 0.5
				}
			else 
				{
				  new_Position = new Vec2(position); // therefore won't move
				  velocity = 0.0;
				}
			}
		else if(canTurn(angle_between_trailer_and_front, heading_difference)) 
			{
			if (Math.abs(heading_difference) >= (Math.PI)/2 )
			{

			  // set heading_difference back to between 0 and 2pi
			  turnOnly = true;
			  if (heading_difference < 0.0)
			    heading_difference = heading_difference + 2*Math.PI;
			  velocity = desired_speed*base_speed;
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
				
			// now need to determine the position to go to and the velocity
			velocity = (right_wheel_vel+left_wheel_vel)/2.0;
			Vec2 tempSteer = new Vec2(steer);
			currCycle = 0.001;
			/*		tempSteer.sett((right_wheel_vel-left_wheel_vel)*currCycle/LENGTH + tempSteer.t);
			double new_x = velocity*distLength*Math.cos(tempSteer.r);
			double new_y = velocity*distLength*Math.sin(tempSteer.r);
			new_Position = new Vec2(position.x+new_x, position.y+new_y);
			*/

		double delta_distance_right = (right_wheel_vel*currCycle);
		double delta_distance_left = (left_wheel_vel*currCycle);
		System.out.println("this cycle " + currCycle);
		System.out.println("distance " + delta_distance_right + " " + delta_distance_left);
		System.out.println("velocity " + velocity);
		/*--- set heading and velocity based on right and left wheel velocities ---*/
		
		double delta_phi = (delta_distance_right-delta_distance_left)/LENGTH;
		tempSteer.sett(tempSteer.t + delta_phi);
		System.out.println("tempsteer " + tempSteer.t);
		Vec2 delta_displacement = new Vec2(tempSteer.x, tempSteer.y);
		delta_displacement.setx(((delta_distance_right+delta_distance_left)/2)*Math.cos(tempSteer.t));
		delta_displacement.sety(((delta_distance_right+delta_distance_left)/2)*Math.sin(tempSteer.t));

		System.out.println("delta x, y" + delta_displacement.x + " " + delta_displacement.y);
		double new_x = 1.0*delta_displacement.x;
		double new_y = 1.0*delta_displacement.y;
		new_Position = new Vec2(position.x +new_x, position.y+new_y);
		velocity = desired_speed*base_speed;

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
			velocity = (right_wheel_vel+left_wheel_vel)/2.0;
			Vec2 tempSteer = new Vec2(steer);
			currCycle = 0.001; // (double)(System.currentTimeMillis() - current_time)/1000;
			/*		tempSteer.sett((right_wheel_vel-left_wheel_vel)*currCycle/LENGTH + tempSteer.t);
			double new_x = velocity*distLength*Math.cos(tempSteer.r);
			double new_y = velocity*distLength*Math.sin(tempSteer.r);
			new_Position = new Vec2(position.x+new_x, position.y+new_y);
			*/

		double delta_distance_right = (right_wheel_vel*currCycle);
		double delta_distance_left = (left_wheel_vel*currCycle);
		System.out.println("this cycle " + currCycle);
		System.out.println("distance " + delta_distance_right + " " + delta_distance_left);

		/*--- set heading and velocity based on right and left wheel velocities ---*/
		System.out.println("velocity " + velocity);
		
		double delta_phi = (delta_distance_right-delta_distance_left)/LENGTH;
		tempSteer.sett(tempSteer.t + delta_phi);
		System.out.println("tempsteer " + tempSteer.t);

		Vec2 delta_displacement = new Vec2(tempSteer.x, tempSteer.y);
		delta_displacement.setx(distLength*((delta_distance_right+delta_distance_left)/2)*Math.cos(tempSteer.t));
		delta_displacement.sety(distLength*((delta_distance_right+delta_distance_left)/2)*Math.sin(tempSteer.t));
		System.out.println("delta x, y" + delta_displacement.x + " " + delta_displacement.y);
		double new_x = 1.0*delta_displacement.x;
		double new_y = 1.0*delta_displacement.y;
		new_Position = new Vec2(position.x +new_x, position.y+new_y);
		velocity = desired_speed*base_speed;

			}
		/*	if (collision) // used to be if (velocity = 0.0)
			{
			    System.out.println("stopping robot");
			  			  Srv.SendStopMotors();
			  			  Srv.SendBuzzerOn(true);
			  Srv.Wait(100); // 100 ms
			  Srv.SendBuzzerOn(false);
			  collision = Srv.GetObstacle();
			}
		*/
		      if (turnOnly)
			{
			  System.out.println("Turning to " + desired_heading + " velocity " + velocity);
			  Srv.SendHeadingDestination(desired_heading, (int)velocity);
			}
		      else
			{
                         System.out.println("new position, velocity  " + new_Position.x + " " + new_Position.y + " " + velocity);
			 Srv.SendPositionVelocityDestination(new_Position.x/CYEUNITSTOMETERS, new_Position.y/CYEUNITSTOMETERS, (int)velocity);
			}
	


			/*--- sleep an appropriate time ---*/
			//since it runs so slowly already, we skip sleeping
			this_cycle = System.currentTimeMillis() - current_time;
			run_time_sum += this_cycle;
			//sleep_time = 50;
			//sleep_time = MIN_CYCLE_TIME - this_cycle;
			//if (sleep_time<50) sleep_time = 50;
			//try
				//{
				//Thread.sleep(sleep_time);
				//}
			//catch (InterruptedException e){};
			cycles++;
		  }
		else
		  {
		    Srv.SendStopMotors();
		  }

		}

	boolean canTurn(double angle_between_trailer_and_front, double heading_difference)
		{
		if ((Math.abs(angle_between_trailer_and_front ) <= (Math.PI)/2) || 
			(angle_between_trailer_and_front <= -1*Math.PI/2 && heading_difference > 0.0)
			|| (angle_between_trailer_and_front >= Math.PI/2 && heading_difference < 0.0))
			{
			    CanTurn = true;;
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
	     return Srv.GetLastB();
	   }

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

	/**
	 * Quit the I/O thread.  Do this only when you are done with the
	 * robot! 
	 */
	public void quit()
		{
		time_sum = (double)(System.currentTimeMillis() - start_time);
		System.out.println("SimpleCyeHard.stop: stopping the robot");
		Srv.SendStopMotors(); // stops the robot
		keep_running = false; // stop the thread
		System.out.println("SimpleCyeHard.stop: avg robot I/O cycle" +
			" took " + run_time_sum/cycles + " ms");

		// vision stuff - again, take out etc. when Visual Object
		myVision.quit();
		}


	/**
	 * Gets time elapsed since the robot was instantiated.  Unlike 
	 * simulation, this is real elapsed time.
	 */
	public long getTime()
		{
		return(System.currentTimeMillis() - start_time);
		}


	/**
	 * Get an array of Vec2s that point egocentrically from the
	 * center of the robot to the obstacles currently sensed by the 
	 * bumpers and sonars.
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1 .
	 * @return the sensed obstacles.
	 */
	public Vec2[] getObstacles(long timestamp)
		{
		Vec2[] retval = null;
		retval = new Vec2[num_Obstacles];
		if (num_Obstacles == 1)
		  {
		   Vec2 tmp = new Vec2(WIDTH/2.0,0);
		   tmp.sett(steer.t);
		   retval[0] = new Vec2(tmp);
		  }
		return(retval);
		}

	/**
	 * Get an array of Vec2s that represent the
	 * locations of visually sensed objects egocentrically
 	 * from center of the robot to the objects currently sensed by the 
	 * vision system.	
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1 .
	 * @param channel (1-6) which type/color of object to retrieve.
	 * @return the sensed objects.
	 */

	private	long	last_VisualObjectst = 0;
	    
	    // take out once remove out of VisualObjectSensor
	public void setVisionNoise(double mean, double stddev, long seed)
	    {
	    }
	
	public Vec2[] getVisualObjects(long timestamp, int channel)
		{
		  int colour_id = 0; // default
		  // convert channel into appropriate colour id
		  if (channel == 3) // in simulator this is soccer ball
		    colour_id = 0; // 
		  else if (channel == 2) // in simulator this is obstacle
		      colour_id = 1;
		  else if (channel == 4) // orange objects
		      colour_id = 4;
		  else if (channel == 5) // yellow objects
		      colour_id = 2;
		  else if (channel == 6) // blue objects
		      colour_id = 3;
		  else if (channel == 7) // green objects
		      colour_id = 4;
		  boolean visionSuccess = myVision.processFrame();
		  while(!visionSuccess)
		    {
		      System.out.println("problems processing Frame");
		      visionSuccess = myVision.processFrame();
		    }
		  int numberRegions = myVision.getNumRegions(colour_id);
		  JCMVision.Region myRegions[] = myVision.getRegions(colour_id, numberRegions);

		  // THIS IS A HACK FOR NOW - ensures only one ball returned but get all the obstacles
		  if ((channel == 3 || channel == 4) && numberRegions !=0 )
		      numberRegions = 1;
		  // THIS IS A HACK FOR NOW

		  // now to convert to coordinates
		  double xVal,yVal;
		  int x1,y1,x2,y2,xwidth,ywidth, ballSize;

		  double newTheta, equivX,equivY, distance;
		    if (numberRegions == 0 && channel == 3)
		      {
			  Srv.SendBuzzerOn(true);
			  Srv.Wait(100); // 100 ms
			  Srv.SendBuzzerOn(false);
		      }
		  
		    Vector tempObjs = new Vector();
		    int numberValidRegions = 0;
		    boolean addObject = true;

		  for(int i=0;i<numberRegions;i++)
		    {
			addObject = true;
			equivX = 0.0;
			equivY = 0.0;
		      x1 = myRegions[i].x1;
		      x2 = myRegions[i].x2;
		      y1 = myRegions[i].y1;
		      y2 = myRegions[i].y2;

		      xVal = ((double)x1+(double)x2)/2.0;
		      yVal = ((double)y1+(double)y2)/2.0;
		      

		      xwidth = Math.abs(x1-x2);
		      ywidth = Math.abs(y1-y2);
		      
		      

	       	      // CONVERT FROM PIXELS TO X,Y LOCATION - based on size
	       	      newTheta = Math.atan(1.0/(XWIDTH/Math.tan(FOV/2.0)))*(XWIDTH-xVal);
	       	      if (newTheta < 0.0)
	       		  newTheta += 2*Math.PI; // get between 0 and 2pi
	       	      System.out.println("new theta " + newTheta);

		       if (channel == 3 || channel == 4)
		       	  {
			      // ball 

			      ballSize = Math.min(MAX_BALL_PIXEL,  Math.max(xwidth,ywidth));
		      
			      distance = DistanceLookUp[ballSize];
			      equivY = distance*Math.cos(newTheta);
			      equivX = distance*Math.sin(newTheta);
			  }
		       else if (channel == 2 || channel == 5 || channel == 6 || channel == 7)
			   {
			       // obstacles
			       
			       /*			       if (y2 >= 70)
				   distance = -0.0039*y2+0.5688;
			       else
				   distance = 38.86*Math.pow(y2, -1.124);
			       */
			       try {
				      
			       distance = Math.min(Math.min(PixelToDistance[Math.min(159, (int)xVal)][y2],PixelToDistance[Math.min(159,x1)][y2]),PixelToDistance[Math.min(159, x2)][y2]);
			       if (distance == PixelToDistance[Math.min(159, x1)][y2])
				   	newTheta = Math.atan(1.0/(XWIDTH/Math.tan(FOV/2.0)))*(XWIDTH-x1);
			       if (distance == PixelToDistance[Math.min(159, x2)][y2])
				   	newTheta = Math.atan(1.0/(XWIDTH/Math.tan(FOV/2.0)))*(XWIDTH-x2);

			       } catch (Exception e) {
				   System.err.println(e);
				   System.err.println("x1 = "+x1);
				   System.err.println("x2 = "+x1);
				   System.err.println("y2 = "+y2);
				   System.err.println("xVal = "+xVal);
				   distance=1;
			       }
			       System.err.println("x, y " + xVal + " " + y2 + " " + distance);
			       equivY = distance*Math.cos(newTheta);
			       equivX = distance*Math.cos(newTheta);

			       if (Math.min(xwidth, ywidth) <= 2)
				   addObject = false; // too small to worry about - noise
			       if (y2 >= 119 && y1 >= 110 && xVal > 77 && xVal < 83)
				   addObject = false; // is itself so don't add

			   }
		       if (addObject)
			   {
		       Vec2 tObj = new Vec2(equivX, equivY);
		      tObj.sett(newTheta+steer.t); // now returns heading independent of robot's heading
		      tempObjs.addElement(tObj);
		      numberValidRegions++;
			   }
		    }

		 Vec2[] objs = new Vec2[numberValidRegions];
		 for(int r=0;r<numberValidRegions;r++)
		     {
			 objs[r] = new Vec2((Vec2)tempObjs.elementAt(r));
		     }
	  
		return(objs);
		}

	protected int	obstacle_rangeInch = (int)(Units.MeterToInch(1.0)+0.5);
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
		obstacle_rangeInch = (int)(Units.MeterToInch(range)+0.5);
		}


	/**
	 * Get the position of the robot in global coordinates.
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1.
	 * @return the position.
	 * @see SimpleCyeHard#resetPosition
	 */
	public Vec2 getPosition(long timestamp)
		{
		Vec2 retval = null;
		retval = new Vec2(last_Position.x, last_Position.y);
		return(retval);
		}
		

	/**
	 * Reset the odometry of the robot in global coordinates.
	 * This might be done when reliable sensor information provides
	 * a very good estimate of the robot's location, or if you
	 * are starting the robot in a known location other than (0,0).
	 * Do this only if you are certain you're right!
	 * @param position the new position.
	 * @see SimpleCyeHard#getPosition
	 */
	public void resetPosition(Vec2 p)
		{
		origin = new Vec2(p.x - last_Position.x, 
			p.y - last_Position.y);
		}


	/**
	 * Get the current heading of the steering motor (radians).
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1 .
	 * @return the heading in radians.
	 * @see SimpleCyeHard#resetSteerHeading
	 * @see SimpleCyeHard#setSteerHeading
	 */
	public double getSteerHeading(long timestamp)
		{
		double retval = 0;
		retval = steer.t;
		return(retval);
		}

	
	/**
	 * Reset the steering odometry of the robot in 
	 * global coordinates.
	 * This might be done when reliable sensor information provides
	 * a very good estimate of the robot's heading, or you are starting
	 * the robot at a known heading other than 0.
	 * Do this only if you are certain you're right!
	 * It is also a good idea not to be moving when you do this.
	 * @param heading the new heading in radians.
	 * @see SimpleCyeHard#getSteerHeading
	 * @see SimpleCyeHard#setSteerHeading
	 */
	public void resetSteerHeading(double heading)
		{
		  // this function currently does nothing
		}


	protected double desired_heading;
	/**
	 * Set the desired heading for the steering motor.
	 * If the turn is greater than 90 degrees we set the turn
	 * to be less than that and move in reverse.
	 * @param heading the heading in radians.
	 * @see SimpleCyeHard#getSteerHeading
	 * @see SimpleCyeHard#resetSteerHeading
	 */
	public void setSteerHeading(long timestamp, double heading)
		{
		/* ensure in legal range */
		desired_heading = Units.ClipRad(heading);
		}
	


	
	protected double desired_speed = 0;
	/**
	 * Set the desired speed for the robot (translation).  
	 * The speed must be between 0.0 and 1.0; where 0.0 is stopped
	 * and 1.0 is "full blast".  It will be clipped
	 * to whichever limit it exceeds.  
	 * The actual commanded speed is zero until the 
	 * steering motor is close to the desired heading.
	 * Use setBaseSpeed to adjust the top speed.
	 * @param timestamp only get new information 
	 *        if timestamp > than last call 
	 * @param speed the desired speed from 0 to 1.0, where 1.0 is the 
	 *	base speed.
	 * @see SimpleCyeHard#setSteerHeading
	 * @see SimpleCyeHard#setBaseSpeed 
	 */ 
	public void setSpeed(long timestamp, double speed)
		{
		/* ensure legal range */
		if (speed > 1.0) speed = 1.0;
		if (speed < -1.0) speed = -1.0; // changed this to allow negative speeds
		// else if (speed < 0) speed = 0;
		desired_speed = speed;
		}


	protected double base_speed = SimpleCye.MAX_TRANSLATION*SPEED_CONVERSION;
	/**
	 * Set the base speed for the robot (translation) in
	 * meters per second.
         * Base speed is how fast the robot will move when
         * setSpeed(1.0) is called.
	 * The speed must be between 0 and MAX_TRANSLATION.  
	 * It will be clipped to whichever limit it exceeds.
	 * @param speed the desired speed from 0 to 1.0, where 1.0 is the 
	 *	base speed.
	 * @see SimpleCyeHard#setSpeed
	 */
	public void setBaseSpeed(double speed)
		{
		if (speed > SimpleCye.MAX_TRANSLATION) speed = SimpleCye.MAX_TRANSLATION;
		else if (speed < 0) speed = 0;
		base_speed = speed*SPEED_CONVERSION;
		}


	private String display_string = "blank";
	/**
         * Set the String that is printed on the robot's display.
         * For real robots, this appears printed on the console.
         * @param s String, the text to display.
         */
        public void setDisplayString(String s)
		{
		// only print it if it is different than last time
		if (display_string != s)
			{
			display_string = s;
			System.out.println(display_string);
			}
		}

	  public Color getForegroundColor() { return Color.black;}
	  public Color getBackgroundColor() { return Color.black;}

	    public double sgn(double val)
	    {
		if(val < 0.0)
		    return -1.0;
		else if(val > 0.0)
		    return 1.0;
		else
		    return 0;
	    }
}











































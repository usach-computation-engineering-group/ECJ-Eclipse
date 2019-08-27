/*
 * SimpleN150Hard.java
 */

package EDU.gatech.cc.is.abstractrobot;

import java.util.Enumeration;
import java.awt.Color;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.nomad150.Ndirect;


/**
 * SimpleN150Hard implements SimpleN150 for
 * Nomad 150 hardware using the Ndirect class.
 * You should see the specifications in SimpleN150
 * and Ndirect class documentation for details.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @see SimpleN150
 * @see EDU.gatech.cc.is.nomad150.Ndirect
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class SimpleN150Hard extends Simple implements SimpleN150, HardObject
	{
	protected Ndirect nomad150_hardware;
	protected static final boolean DEBUG = false;	

        /**
         * Instantiate a <B>SimpleN150Hard</B> object.  You should only
         * instantiate one of these per robot connected to your
         * computer.  Standard call is SimpleN150Hard(1,38400);
         * @param serial_port 1 = ttys0 (COM1), 2 = ttys1 (COM2) ...
         * @param baud baud rate for communication.
         * @exception Exception If unable to configure the hardware.
         */
	public SimpleN150Hard(int serial_port, int baud) throws Exception
		{
		/*--- set default parameters ---*/
                super(1);

		/*--- open up the robot hardware ---*/
		nomad150_hardware = new Ndirect(serial_port, baud);

		/*--- sonars on! ---*/
		nomad150_hardware.sn_on();

		/*--- start the clock ---*/
		start_time = System.currentTimeMillis();
		}


	protected double	cycles = 0;
	protected double	run_time_sum = 0;
	protected double	time_sum = 0;
	protected boolean	keep_running = true;
	protected double	old_desired_heading = 0;
	protected double	old_desired_turret_heading = 0;
	protected int	hard_command = 0;
	protected int	old_hard_command = 0;
	protected int	sonar_raw_data[] = new int[16];
	/**
	 * Conducts periodic I/O with
	 * the robot.  It runs at most every SimpleN150Hard.MIN_CYCLE_TIME 
	 * milliseconds to gather sensor data from the robot, and issue
	 * movement commands.
	 */
	public void takeStep()
		{
		double	uncorrected_steering = 0;
		double	turn = 0;
		double	turret_turn = 0;
		double	speed_command = 0;
		int	result = 0;
		long	current_time, this_cycle, sleep_time;

		if(keep_running)
			{
			/*--- mark current time ---*/
			current_time = System.currentTimeMillis();

			/*--- fill in the sensor data ---*/
			// ping robot to get the sensor data
			nomad150_hardware.gs();

			// position
			double x = Units.Inch10ToMeter(
				nomad150_hardware.get_x());
                        double y = Units.Inch10ToMeter(
				nomad150_hardware.get_y());
                        last_Position.setx(x);
                        last_Position.sety(y);
			last_Position.add(origin);

			// steer heading
                        uncorrected_steering = Units.Deg10ToRad(
				nomad150_hardware.get_steering());
                        if (in_reverse)
                        	{
                               	// rotate by 180
                               	last_SteerHeading = Units.ClipRad(
                                       	last_SteerHeading + Math.PI);
                               	}
			else
				last_SteerHeading = uncorrected_steering;
			// turret heading
			last_TurretHeading = Units.Deg10ToRad(
				nomad150_hardware.get_turret());

			// sonar and bump data
			// get the raw bump data
			nomad150_hardware.get_sn(sonar_raw_data);
			long bumps = nomad150_hardware.get_bp();
			if (bumps != 240)
				num_Obstacles = 1;
			else
				num_Obstacles = 0;
			// hack so as to ignore sonar from sensor 0
			sonar_raw_data[0] = obstacle_rangeInch+100;
			for(int i = 0; i<16; i++) 
				if (sonar_raw_data[i] + 
					(int)Units.MeterToInch(SONAR_RADIUS) <
					obstacle_rangeInch)
					num_Obstacles++;
			last_Obstacles = new Vec2[num_Obstacles];
			int j = 0;
			for(int i = 0; i<16; i++) 
				if (sonar_raw_data[i] +
					(int)Units.MeterToInch(SONAR_RADIUS) <
					obstacle_rangeInch)
					{
					last_Obstacles[j] = new Vec2();
					last_Obstacles[j].setr(SONAR_RADIUS +
						Units.InchToMeter(
							sonar_raw_data[i]));
					last_Obstacles[j].sett(
						last_TurretHeading +
						(i * Units.PI2/16));
					j++;
					}
			if (bumps != 240)
				{
				Vec2 tmpvec = new Vec2(SimpleN150.RADIUS,0);
				// top ring
				if (bumps == 65776) 
					tmpvec.sett(0);
				else if (bumps == 262384) 
					tmpvec.sett(Units.DegToRad(1*36));
				else if (bumps == 1048816) 
					tmpvec.sett(Units.DegToRad(2*36));
				else if (bumps == 4194544) 
					tmpvec.sett(Units.DegToRad(3*36));
				else if (bumps == 496) 
					tmpvec.sett(Units.DegToRad(4*36));
				else if (bumps == 1264) 
					tmpvec.sett(Units.DegToRad(5*36));
				else if (bumps == 4336) 
					tmpvec.sett(Units.DegToRad(6*36));
				else if (bumps == 16624) 
					tmpvec.sett(Units.DegToRad(7*36));
				else if (bumps == 241) 
					tmpvec.sett(Units.DegToRad(8*36));
				else if (bumps == 244) 
					tmpvec.sett(Units.DegToRad(9*36));
				// bottom ring
				else if (bumps == 131312) 
					tmpvec.sett(Units.DegToRad(18+0*36));
				else if (bumps == 524528) 
					tmpvec.sett(Units.DegToRad(18+1*36));
				else if (bumps == 2097392) 
					tmpvec.sett(Units.DegToRad(18+2*36));
				else if (bumps == 8388848) 
					tmpvec.sett(Units.DegToRad(18+3*36));
				else if (bumps == 752) 
					tmpvec.sett(Units.DegToRad(18+4*36));
				else if (bumps == 2288) 
					tmpvec.sett(Units.DegToRad(18+5*36));
				else if (bumps == 8432) 
					tmpvec.sett(Units.DegToRad(18+6*36));
				else if (bumps == 33008) 
					tmpvec.sett(Units.DegToRad(18+7*36));
				else if (bumps == 242) 
					tmpvec.sett(Units.DegToRad(18+8*36));
				else if (bumps == 248) 
					tmpvec.sett(Units.DegToRad(18+9*36));
				else
					{
					tmpvec.setr(999);
					}
				last_Obstacles[num_Obstacles-1] = tmpvec;
				}

			//--- compute steering command
			// figure out best turn direction
			turn = Units.BestTurnRad(uncorrected_steering, 
				desired_heading);

			// now decide whether to go in reverse or forward 
			if (turn>(Math.PI/2))
                        	{
                        	in_reverse = true;
                        	turn = turn - Math.PI;
                        	}
                	else if (turn<-(Math.PI/2))
                        	{
                        	in_reverse = true;
                        	turn = turn + Math.PI;
                        	}
                	else in_reverse = false;

			//--- compute turret steering command
			turret_turn = Units.BestTurnRad(
				last_TurretHeading,
				desired_turret_heading);

			//--- compute speed command
			// only go if turret within 90 deg of proper heading 
			// if (Math.abs(turn)<(Math.PI/3.0))
			if (Math.abs(turret_turn)<(Math.PI/2.0))
				speed_command = desired_speed;
			else speed_command = 0;
			// go backwards if in reverse 
			if (in_reverse) speed_command *= -1;
			hard_command = Units.MeterToInch10(
				speed_command*base_speed);
		
			/*--- send movement command to the robot ---*/
			// only if new commands 
			if ((old_hard_command != hard_command) ||
				(old_desired_heading != desired_heading) ||
				(old_desired_turret_heading != 
					desired_turret_heading))
	
				// this should return sensor data, but fails to 
				nomad150_hardware.mv(
					Ndirect.MV_VM,
					hard_command,
					Ndirect.MV_PR,
					Units.RadToDeg10(turn),
					Ndirect.MV_PR,
					Units.RadToDeg10(turret_turn));

			/*--- set the old variables ---*/
			old_hard_command = hard_command;
			old_desired_heading = desired_heading;
			old_desired_turret_heading = desired_turret_heading;

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
		}



	/**
	 * Quit the I/O thread.  Do this only when you are done with the
	 * robot! 
	 */
	public void quit()
		{
		time_sum = (double)(System.currentTimeMillis() - start_time);
		System.out.println("SimpleN150Hard.stop: stopping the robot");
		setSpeed(-1,0.0); // stop the robot
		keep_running = false; // stop the thread
		nomad150_hardware.st(); // really stop the robot just in case
		nomad150_hardware.sn_off(); // sonars off
		System.out.println("SimpleN150Hard.stop: avg robot I/O cycle" +
			" took " + run_time_sum/cycles + " ms");
		}


	private	long	start_time = 0;
	/**
	 * Gets time elapsed since the robot was instantiated.  Unlike 
	 * simulation, this is real elapsed time.
	 */
	public long getTime()
		{
		return(System.currentTimeMillis() - start_time);
		}


	private	long	last_Obstaclest = 0;
	protected	Vec2	last_Obstacles[] = new Vec2[0];
	protected	int	num_Obstacles;
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
		for(int i = 0; i<num_Obstacles; i++)
			retval[i] = new Vec2(last_Obstacles[i].x,
				last_Obstacles[i].y);
		return(retval);
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


	private	long	last_Positiont = 0;
	protected Vec2 last_Position = new Vec2(0,0);
	/**
	 * Get the position of the robot in global coordinates.
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1.
	 * @return the position.
	 * @see SimpleN150Hard#resetPosition
	 */
	public Vec2 getPosition(long timestamp)
		{
		Vec2 retval = null;
		retval = new Vec2(last_Position.x, last_Position.y);
		return(retval);
		}
		

	Vec2 origin = new Vec2(0,0);
	/**
	 * Reset the odometry of the robot in global coordinates.
	 * This might be done when reliable sensor information provides
	 * a very good estimate of the robot's location, or if you
	 * are starting the robot in a known location other than (0,0).
	 * Do this only if you are certain you're right!
	 * @param position the new position.
	 * @see SimpleN150Hard#getPosition
	 */
	public void resetPosition(Vec2 p)
		{
		origin = new Vec2(p.x - last_Position.x, 
			p.y - last_Position.y);
		}


	private long	last_SteerHeadingt = 0;
	protected double last_SteerHeading = 0;
	protected boolean in_reverse = false;
	/**
	 * Get the current heading of the steering motor (radians).
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1 .
	 * @return the heading in radians.
	 * @see SimpleN150Hard#resetSteerHeading
	 * @see SimpleN150Hard#setSteerHeading
	 */
	public double getSteerHeading(long timestamp)
		{
		double retval = 0;
		retval = last_SteerHeading;
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
	 * @see SimpleN150Hard#getSteerHeading
	 * @see SimpleN150Hard#setSteerHeading
	 */
	public void resetSteerHeading(double heading)
		{
		/* get the current turret heading */
		nomad150_hardware.gs(); // must do this first
		int turret = nomad150_hardware.get_turret();
		
		/* ensure in legal range */
		heading = Units.ClipRad(heading); 
		
		/* if we're in reverse, the steer heading is PI out */
		if (in_reverse) heading = Units.ClipRad(heading + Math.PI);

		/* set the angles, remember to convert to 10ths of degrees */
		nomad150_hardware.da(Units.RadToDeg10(heading),turret);
		}


	protected double desired_heading;
	/**
	 * Set the desired heading for the steering motor.
	 * If the turn is greater than 90 degrees we set the turn
	 * to be less than that and move in reverse.
	 * @param heading the heading in radians.
	 * @see SimpleN150Hard#getSteerHeading
	 * @see SimpleN150Hard#resetSteerHeading
	 */
	public void setSteerHeading(long timestamp, double heading)
		{
		/* ensure in legal range */
		desired_heading = Units.ClipRad(heading);
		}
	

	private	long	last_TurretHeadingt = 0;
	protected double last_TurretHeading = 0;
	/**
	 * Get the current heading of the turret motor.
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1 .
	 * @return the turret heading in radians.
	 * @see SimpleN150Hard#setTurretHeading
	 * @see SimpleN150Hard#resetTurretHeading
	 */
	public double getTurretHeading(long timestamp)
		{
		double retval = 0;
		retval = last_TurretHeading;
		return(retval);
		}

	
	/**
	 * Reset the turret odometry of the robot in global coordinates.
	 * This might be done when reliable sensor information provides
	 * a very good estimate of the robot's turret heading.
	 * Do this only if you are certain you're right!
	 * It is also a good idea not to be moving when you do this.
	 * @param heading the new turret heading in radians.
	 * @see SimpleN150Hard#getTurretHeading
	 * @see SimpleN150Hard#setTurretHeading
	 */
	public void resetTurretHeading(double heading)
		{
		/* get the current steer heading */
		nomad150_hardware.gs();  // must do this first
		int steering = nomad150_hardware.get_steering();

		/* ensure in legal range */
		heading = Units.ClipRad(heading); 

		/* set the angles, remember to convet to 10ths of degrees */
		nomad150_hardware.da(steering, Units.RadToDeg10(heading));

		/* in_reverse doesn't matter for the turret */
		}

	
	double	desired_turret_heading = 0;
	/**
	 * Set the desired heading for the turret motor.
	 * We assume the turret is not symmetric, so there is no
	 * reverse trick as in the steering motor.
	 * @param heading the heading in radians.
	 * @see SimpleN150Hard#getTurretHeading
	 * @see SimpleN150Hard#resetTurretHeading
	 */
	public void setTurretHeading(long timestamp, double heading)
		{
		/* ensure in legal range */
		desired_turret_heading = Units.ClipRad(heading);
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
	 * @see SimpleN150Hard#setSteerHeading
	 * @see SimpleN150Hard#setBaseSpeed 
	 */ 
	public void setSpeed(long timestamp, double speed)
		{
		/* ensure legal range */
		if (speed > 1.0) speed = 1.0;
		else if (speed < 0) speed = 0;
		desired_speed = speed;
		}


	protected double base_speed = SimpleN150.MAX_TRANSLATION;
	/**
	 * Set the base speed for the robot (translation) in
	 * meters per second.
         * Base speed is how fast the robot will move when
         * setSpeed(1.0) is called.
	 * The speed must be between 0 and MAX_TRANSLATION.  
	 * It will be clipped to whichever limit it exceeds.
	 * @param speed the desired speed from 0 to 1.0, where 1.0 is the 
	 *	base speed.
	 * @see SimpleN150Hard#setSpeed
	 */
	public void setBaseSpeed(double speed)
		{
		if (speed > SimpleN150.MAX_TRANSLATION) speed = SimpleN150.MAX_TRANSLATION;
		else if (speed < 0) speed = 0;
		base_speed = speed;
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

	}

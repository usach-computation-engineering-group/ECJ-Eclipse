/*
 * MultiForageN150Hard.java
 */

package EDU.gatech.cc.is.abstractrobot;

import java.util.Enumeration;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.nomad150.Ndirect;
import EDU.gatech.cc.is.communication.*;
import EDU.gatech.cc.is.util.*;
import EDU.gatech.cc.is.newton.*;


/**
 * MultiForageN150Hard implements MultiForageN150 for
 * Nomad 150 hardware using the Ndirect class.
 * You should see the specifications in MultiForageN150
 * and Ndirect class documentation for details.
 * <P>
 * To reduce I/O between the controller and the robot, a thread is
 * set up to perform periodic I/O.  The sensor data and motor commands
 * are exchanged through MultiForageN150Hard class variables (globals).
 *
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @see MultiForageN150
 * @see EDU.gatech.cc.is.nomad150.Ndirect
 * @see EDU.gatech.cc.is.Newton
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class MultiForageN150Hard extends SimpleN150Hard 
	implements MultiForageN150
	{
	protected Newton	newt = null;
	protected NewtonTrans nt;	
	private static final boolean DEBUG = false;	
	private static final int GRIPPER_F_RANGE = -1050;  //microseconds
	private static final int GRIPPER_F_BASE  = 1800;   //microseconds
	private static final int GRIPPER_H_RANGE = -700;   //microseconds
	private static final int GRIPPER_H_BASE  = 2000;   //microseconds

        /**
         * Instantiate a <B>MultiForageN150Hard</B> object.  You should only
         * instantiate one of these per robot connected to your
         * computer.  Standard call is MultiForageN150Hard(1,38400);
         * @param serial_port 1 = ttys0 (COM1), 2 = ttys1 (COM2) ...
         * @param baud baud rate for communication.
         * @exception Exception If unable to configure the hardware.
         */
	public MultiForageN150Hard(int serial_port, int baud) throws Exception
		{
		super(serial_port, baud);

		/*--- open up the newton hardware ---*/
		try 
			{
			newt = new Newton(3, 38400);
			}
		catch (Exception e)
			{
			System.out.println("MultiForageN150Hard: "+e);
			System.out.println("MultiForageN150Hard: run will "+
				"continue without vision support.");
			newt = null;
			}
		if (newt != null) nt = new NewtonTrans(newt,"newton.cfg");
		}


	protected double	old_gripper_finger = -99;
	protected double old_gripper_height = -99;
	protected int	hard_command = 0;
	protected int	old_hard_command = 0;

	/**
	 * Body of the thread that conducts periodic I/O with
	 * the robot.  It runs at most every MultiForageN150Hard.MIN_CYCLE_TIME 
	 * milliseconds to gather sensor data from the robot, and issue
	 * movement commands.
	 */
	public void run()
		{
		double	uncorrected_steering = 0;
		double	turn = 0;
		double	turret_turn = 0;
		double	speed_command = 0;
		int	result = 0;
		long	current_time, this_cycle, sleep_time;
		int	grip_obj = 0;

		if(keep_running)
			{
			/*--- mark current time ---*/
			current_time = System.currentTimeMillis();

			/*--- check vision first ---*/
			// read in the data
			if (newt!=null)
				{
				//for(int k=0; k<10; k++)
					nt.read_frame();
				}
			// check to see if something is in the gripper
			grip_obj = getObjectInGripper(-1);

			// close gripper if something there
			if (trigger_mode && (grip_obj == 1 || grip_obj == 0))
				gripper_finger = 0;

			/*--- move the gripper fingers ---*/
			if (old_gripper_finger != gripper_finger)
				{
				// 0 is closed, 1 is open
				// System.out.println("gripper finger "+gripper_finger);
				old_gripper_finger = gripper_finger;
				int gripper_finger_hard = (int)((double)
					GRIPPER_F_RANGE * gripper_finger
					+ (double)GRIPPER_F_BASE);
				nomad150_hardware.mv(Ndirect.MV_PWM_LOW_0, 
					15000-gripper_finger_hard,
					Ndirect.MV_IGNORE,0,
					Ndirect.MV_IGNORE,0);
				nomad150_hardware.mv(Ndirect.MV_PWM_HIGH_0, 
					gripper_finger_hard,
					Ndirect.MV_IGNORE,0,
					Ndirect.MV_IGNORE,0);
				}

			/*--- move the gripper height next ---*/
			if (old_gripper_height != gripper_height)
				{
				int gripper_height_hard = 0;
				// 0 is down 1 is up
				// System.out.println("gripper height "+gripper_height);
				old_gripper_height = gripper_height;
				gripper_height_hard = (int)((double)
					GRIPPER_H_RANGE * gripper_height
					+ (double)GRIPPER_H_BASE);
				nomad150_hardware.mv(Ndirect.MV_PWM_LOW_1, 
					15000-gripper_height_hard,
					Ndirect.MV_IGNORE,0,
					Ndirect.MV_IGNORE,0);
				nomad150_hardware.mv(Ndirect.MV_PWM_HIGH_1, 
					gripper_height_hard,
					Ndirect.MV_IGNORE,0,
					Ndirect.MV_IGNORE,0);
				}

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

			// sonar data
			// get the raw data
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
				Vec2 tmpvec = new Vec2(RADIUS,0);
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
			// only go if within 90 deg of proper heading 
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


	private	long	last_VisualObjectst = 0;
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
	public Vec2[] getVisualObjects(long timestamp, int channel)
		{
		Vec2[] objs = new Vec2[0];
		if (newt!=null)
			{
			objs = nt.getVisualObjects(channel);
			for (int i = 0; i < objs.length; i++)
				{
				objs[i].sett(last_TurretHeading + objs[i].t);
				}
			if (DEBUG/*true*/) 
				System.out.println(objs.length+
				" turret "+last_TurretHeading);
			}
		return(objs);
		}

	   /**
	    * this is a dummy implementation to keep compatibility with VisualSensorObject.
	    * at this point, vision noise is not built into the class. for an example,
	    * see SimpleCyeSim.
	    */
	  public void setVisionNoise(double mean, double stddev, long seed) { }


	private	long	last_ObjectInGrippert = 0;
	private	int	last_ObjectInGripper = -1;
	private	long	last_ObjectInGrippermem = -1;
	/**
	 * Get the kind of object in the gripper.
	 * @param timestamp only get new information 
	 *        if timestamp > than last call or timestamp == -1 .
	 * @return channel (0-5) which type/color of object 
	 *         `in the gripper, -1 otherwise.
	 * @see MultiForageN150Hard#getVisualObjects
	 */
	public int getObjectInGripper(long timestamp)
		{
		long delaytime = 500; // 0.5 sec memory
		int retval = -1;
		Vec2 gpos = new Vec2(GRIPPER_POSITION,0);
		gpos.sett(last_TurretHeading);
		for(int i = 0; i < 2; i++)
			{
			Vec2[] objs = getVisualObjects(timestamp, i);
			for(int j=0; j<objs.length; j++)
				{
				objs[j].sub(gpos);
				if (objs[j].r < GRIPPER_CAPTURE_RADIUS)
					{
					retval = i;
					break;
					}
				}
			if (retval != -1)
				{
				last_ObjectInGrippermem = timestamp;
				last_ObjectInGripper = retval;
				break;
				}
			}
		long timesince = timestamp - last_ObjectInGrippermem;
		if ((retval == -1)&&(timesince <= delaytime))
			retval = last_ObjectInGripper;
		return(retval);
		}


        protected double  gripper_finger = 1.0;
        protected boolean trigger_mode = false;
        /**
         * Set the gripper "finger" position from 0 to 1, with
         * 0 being closed and 1 being open.
         * In simulation, any setting less than 1 means closed.
         * @param position the desired position from 0 to 1.
         */
        public void setGripperFingers(long time_stamp, double position)
                {
                if (position>1) position = 1;
                if (position>=0)
                        {
                        trigger_mode = false;
                        }
                else if (position==-1)
                        {
                        position = 1;
                        trigger_mode = true;
                        }
                else if (position < 0) position = 0;
                gripper_finger = position;
                }


        protected double  gripper_height = 0;
        /**
         * Set the gripper height from 0 to 1, with
         * 0 being down and 1 being up.
         * In simulation this has no effect.
         * @param position the desired position from 0 to 1.
         */
        public void setGripperHeight(long time_stamp, double position)
                {
                if (position>1) position = 1;
                else if (position<0) position = 0;
                gripper_height = position;
                }


	/**
	 * NOT IMPLEMENTED.
	 * Get an array of Vec2s that represent the locations of 
	 * teammates (Kin).
	 * @param timestamp only get new information if 
	 * timestamp > than last call or timestamp == -1.
	 * @return the sensed teammates.
	 * @see EDU.gatech.cc.is.util.Vec2
	 */
	public Vec2[] getTeammates(long timestamp)
		{
		return(new Vec2[0]);
		}


	/**
	 * NOT IMPLEMENTED.
	 * Get an array of Vec2s that represent the
	 * locations of opponents.
	 * @param timestamp only get new information if 
	 * 	timestamp > than last call or timestamp == -1.
	 * @return the sensed opponents.
	 * @see EDU.gatech.cc.is.util.Vec2
	 */
	public Vec2[] getOpponents(long timestamp)
		{
		return(new Vec2[0]);
		}


	/**
	 * NOT IMPLEMENTED.
	 * Get the robot's player number, between 0
	 * and the number of robots on the team.
	 * Don't confuse this with getID which returns a unique number
	 * for the object in the simulation as a whole, not on its individual
	 * team.
	 * @param timestamp only get new information if 
	 *	timestamp > than last call or timestamp == -1.
	 * @return the player number.
	 */
	public int getPlayerNumber(long timestamp)
		{
		return(0);
		}


	/**
	 * NOT IMPLEMENTED.
	 * Set the maximum range at which kin may be sensed.  Primarily
	 * for use in simulation.
	 * @param r double, the maximum range.
	 */
	public void setKinMaxRange(double r)
		{
		}


        /**
	 * NOT IMPLEMENTED.
         * Broadcast a message to all teammates, except self.
         * @param m Message, the message to be broadcast.
         */
        public void broadcast(Message m)
		{
		}


        /**
	 * NOT IMPLEMENTED.
         * Transmit a message to just one teammate.  Transmission to
         * self is allowed.
         * @param id int, the ID of the agent to receive the message.
         * @param m Message, the message to transmit.
         * @exception CommunicationException if the receiving agent does not
         *              exist.
         */
        public void unicast(int id, Message m) 
                throws CommunicationException
		{
		}


        /**
	 * NOT IMPLEMENTED.
         * Transmit a message to specific teammates.  Transmission to
         * self is allowed.
         * @param ids int[], the IDs of the agents to receive the message.
         * @param m Message, the message to transmit.
         * @exception CommunicationException if one of the receiving agents 
         *              does not exist.
         */
        public void multicast(int[] ids, Message m)
                throws CommunicationException
		{
		}


        /**
	 * NOT IMPLEMENTED.
         * Get an enumeration of the incoming messages.  The messages
         * are automatically buffered by the implementation.
         * Unless the implementation guarantees it, you cannot
         * count on all messages being delivered.
         * Example, to print all incoming messages:
         * <PRE>
         * Transceiver c = new RobotComm();
         * CircularBufferEnumeration r = c.getReceiveChannel();
         * while (r.hasMoreElements())
         *      System.out.println(r.nextElement());
         * </PRE>
         * @return the CircularBufferEnumeration.
         */
        public CircularBufferEnumeration getReceiveChannel()
		{
		return(null);
		}

        /**
	 * NOT IMPLEMENTED.
         * Set the maximum range at which communication can occur.
         * In simulation, this corresponds to a simulation of physical limits,
         * on mobile robots it corresponds to a signal strength setting.
         * @param r double, the maximum range.
         */
        public void setCommunicationMaxRange(double r)
		{
		}

        /**
	 * NOT IMPLEMENTED.
         */
        public boolean connected()
		{
		return(false);
		}

	}

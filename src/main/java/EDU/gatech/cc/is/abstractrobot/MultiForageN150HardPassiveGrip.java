/*
 * MultiForageN150HardPassiveGrip.java
 */

package EDU.gatech.cc.is.abstractrobot;

import java.util.Enumeration;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.nomad150.Ndirect;
import EDU.gatech.cc.is.communication.*;
import EDU.gatech.cc.is.newton.*;


/**
 * MultiForageN150HardPassiveGrip implements MultiForageN150 for
 * Nomad 150 hardware using the Ndirect class.
 * Assumes a passive gripper.
 * You should see the specifications in MultiForageN150
 * and Ndirect class documentation for details.
 * <P>
 * To reduce I/O between the controller and the robot, a thread is
 * set up to perform periodic I/O.  The sensor data and motor commands
 * are exchanged through MultiForageN150HardPassiveGrip 
 * class variables (globals).
 *
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @see MultiForageN150
 * @see EDU.gatech.cc.is.nomad150.Ndirect
 * @see EDU.gatech.cc.is.Newton
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class MultiForageN150HardPassiveGrip extends MultiForageN150Hard
	implements MultiForageN150, HardObject
	{
	// keep track if recently had something in gripper
	private int grip_counter = 0;

        /**
         * Instantiate a <B>MultiForageN150HardPasiveGrip</B> object.  
	 * You should only
         * instantiate one of these per robot connected to your
         * computer.  Standard call is MultiForageN150Hard(1,38400);
         * @param serial_port 1 = ttys0 (COM1), 2 = ttys1 (COM2) ...
         * @param baud baud rate for communication.
         * @exception Exception If unable to configure the hardware.
         */
        public MultiForageN150HardPassiveGrip(int serial_port, int baud) 
		throws Exception
		{
		super(serial_port, baud);
		}


	/**
	 * Conducts periodic I/O with
	 * the robot.  It runs at most every 
	 * MultiForageN150HardPassiveGrip.MIN_CYCLE_TIME 
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
		int	grip_obj = 0;
		boolean	open_the_gripper = false;

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
			grip_obj = getObjectInGripper(getTime());
			if (grip_obj != -1) grip_counter = 4;
			grip_counter--;

			//grip_obj = getObjectInGripper(-1);

			// close gripper if something there
			if (trigger_mode && (grip_obj == 1 || grip_obj == 0))
				gripper_finger = 0;

			/*--- move the gripper fingers ---*/
			if (old_gripper_finger != gripper_finger)
				{
				// 0 is closed, 1 is open
				// System.out.println("gripper finger "+gripper_finger);
				// IGNORE ACTUAL MOVEMENT FOR PASSIVE GRIP
				old_gripper_finger = gripper_finger;

				//Signal for code later on to backup
				//if ((gripper_finger == 1)&&(grip_obj!=-1))
				if ((gripper_finger == 1)&&(grip_counter>=0))
					open_the_gripper = true;  
				}

			/*--- move the gripper height next ---*/
			if (old_gripper_height != gripper_height)
				{
				int gripper_height_hard = 0;
				// 0 is down 1 is up
				// System.out.println("gripper height "+gripper_height);
				// IGNORE for passive gripper
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
			if (open_the_gripper) // execute a backup 
				{
				open_the_gripper = false;

				// stop movement and turn to align 
				// steering and turret
				turn = Units.BestTurnRad(uncorrected_steering, 
					last_TurretHeading);
				// send command to robot
				nomad150_hardware.mv(
					Ndirect.MV_VM,
					0, // stop drive motor
					Ndirect.MV_PR,
					Units.RadToDeg10(turn), //turn
					Ndirect.MV_PR,
					0); // stop turret

				// wait for turn
                        	int vel = 100;
                        	while (vel != 0)
                                	{
                                	nomad150_hardware.get_rv();
                                	vel = nomad150_hardware.get_vsteering();
                                	}

				// backup
                                nomad150_hardware.mv(Ndirect.MV_PR ,
					Units.MeterToInch10(-.2),
                                        Ndirect.MV_IGNORE,
                                        Ndirect.MV_IGNORE,
                                        Ndirect.MV_IGNORE,
                                        Ndirect.MV_IGNORE);
                                vel = 100;
                                while (vel != 0)
                                        {
                                        nomad150_hardware.get_rv();
                                        vel = nomad150_hardware.get_vtranslation();
                                        }

				// send backup command to robot
				//nomad150_hardware.mv(
					//Ndirect.MV_VM,
					//reverse
					//-Units.MeterToInch10(0.1),
					//Ndirect.MV_PR,
					//0, // stop steer
					//Ndirect.MV_PR,
					//0); // stop turret

				// wait for backup
				//try {Thread.sleep(2000);}
				//catch(Exception e){}
				
				// stop robot
				//nomad150_hardware.mv(
					//Ndirect.MV_VM,
					//0, // stop drive
					//Ndirect.MV_PR,
					//0, // stop steer
					//Ndirect.MV_PR,
					//0); // stop turret
				}

			// if not an oper gripper command
			else if ((old_hard_command != hard_command) ||
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
			// actually no need to sleep, go fast!
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
	}

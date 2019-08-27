/*
 * Nomad150TestTriangle.java
 */

package EDU.gatech.cc.is.nomad150;
import EDU.gatech.cc.is.nomad150.Ndirect;

/**

<B>Introduction</B><BR>
This application is for testing Nomad 150 translation and steering
hardware and the Ndirect class; WARNING: this
program will drive the robot at maximum speed. 
It intializes the robot,
turns the sonars on, waits 10 seconds, turns them off.
then drives around a 5 foot triangle (left hand turns) at maximum speed.
<P>
To run this program, first ensure you are in the correct directory (where
the Nomad150TestTriangle.class file is), then type "java Nomad150TestTriangle".

@author (c) 1997 Tucker Balch, tucker@cc.gatech.edu
@version June 1997
@see Ndirect
@see Nomad150TestSensors
*/

public class Nomad150TestTriangle
	{

	/**
	This method is for testing the Ndirect class; WARNING: this
	routine drives the robot at maximum speed.  It is
	not normally used otherwise.  It intializes the robot,
	turns the sonars on, waits 10 seconds, turns them off.
	then drives around a 5 foot triangle at maximum speed.
	*/
	public static void main(String[] args)
		{
		int i;
		try
			{
			System.out.println("main: Opening connection to robot");
			Ndirect robot = new Ndirect(1, 38400);

			System.out.println("main: Sonars on, sleep 10 sec");
			robot.sn_on(60);
			Thread.sleep(10000);
			robot.sn_off();

			robot.get_rv();
			robot.get_rc();
			System.out.println("main: x, y, theta: "+
				robot.get_x()+" "+
				robot.get_y()+" "+
				robot.get_turret());

			/*--- go 5 feet ---*/
			robot.mv(Ndirect.MV_PR , 600,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE);
			robot.get_rv();
			while(robot.get_vtranslation() != 0)
				{
				System.out.println("main: speed is "+
					robot.get_vtranslation());
				Thread.sleep(100);
				robot.get_rv();
				}
			robot.get_rc();
			System.out.println("main: x, y, theta: "+
				robot.get_x()+" "+
				robot.get_y()+" "+
				robot.get_turret());

			/*--- turn 120 deg left ---*/
			robot.mv(Ndirect.MV_IGNORE , Ndirect.MV_IGNORE,
				Ndirect.MV_PR,
				1200,
				Ndirect.MV_PR,
				1200);
			robot.get_rv();
			while(robot.get_vsteering() != 0)
				{
				System.out.println("main: turn speed is "+
					robot.get_vsteering());
				Thread.sleep(100);
				robot.get_rv();
				}

			/*--- go 5 feet ---*/
			robot.mv(Ndirect.MV_PR , 600,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE);
			robot.get_rv();
			while(robot.get_vtranslation() != 0)
				{
				System.out.println("main: speed is "+
					robot.get_vtranslation());
				Thread.sleep(100);
				robot.get_rv();
				}
			robot.get_rc();
			System.out.println("main: x, y, theta: "+
				robot.get_x()+" "+
				robot.get_y()+" "+
				robot.get_turret());

			/*--- turn 120 deg left ---*/
			robot.mv(Ndirect.MV_IGNORE , Ndirect.MV_IGNORE,
				Ndirect.MV_PR,
				1200,
				Ndirect.MV_PR,
				1200);
			robot.get_rv();
			while(robot.get_vsteering() != 0)
				{
				System.out.println("main: turn speed is "+
					robot.get_vsteering());
				Thread.sleep(100);
				robot.get_rv();
				}

			/*--- go 5 feet ---*/
			robot.mv(Ndirect.MV_PR , 600,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE);
			robot.get_rv();
			while(robot.get_vtranslation() != 0)
				{
				System.out.println("main: speed is "+
					robot.get_vtranslation());
				Thread.sleep(100);
				robot.get_rv();
				}
			robot.get_rc();
			System.out.println("main: x, y, theta: "+
				robot.get_x()+ " "+
				robot.get_y()+ " " +
				robot.get_turret());

			/*--- turn 120 deg left ---*/
			robot.mv(Ndirect.MV_IGNORE , Ndirect.MV_IGNORE,
				Ndirect.MV_PR,
				1200,
				Ndirect.MV_PR,
				1200);
			robot.get_rv();
			while(robot.get_vsteering() != 0)
				{
				System.out.println("main: turn speed is "+
					robot.get_vsteering());
				Thread.sleep(100);
				robot.get_rv();
				}
			robot.get_rc();
			System.out.println("main: x, y, theta: "+
				robot.get_x()+" "+
				robot.get_y()+" "+
				robot.get_turret());

			}
		catch (Exception e)
			{
			System.out.println(e);
			}
		}	
	}


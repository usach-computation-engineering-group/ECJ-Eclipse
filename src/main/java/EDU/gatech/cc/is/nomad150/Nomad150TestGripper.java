/*
 * Nomad150TestGripper.java
 */

package EDU.gatech.cc.is.nomad150;
import EDU.gatech.cc.is.nomad150.Ndirect;
import EDU.gatech.cc.is.util.Units;

/**

<B>Introduction</B><BR>
This application is for testing Nomad 150 servo control
hardware.
<P>
To run this program, first ensure you are in the correct directory (where
the Nomad150TestGripper.class file is), then type "java Nomad150TestGripper".

@author (c)1997 Tucker Balch, All Rights Reserved
@version June 1997
@see Ndirect
@see Nomad150TestSensors
@see Nomad150TestTriangle
*/

public class Nomad150TestGripper
	{

	/**
	This method is for testing the Ndirect class.
	It drives the servos around from one side to another.
	*/
	public static void main(String[] args)
		{
		try
			{
			System.out.println("main: Opening connection to robot");
			Ndirect robot = new Ndirect(1, 38400);

			// limits are 500 and 2000

			while(true)
			{
			// height down
			robot.mv(Ndirect.MV_PWM_LOW_1 , 15000-1900, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_1 , 1900, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);

			// gripper open
			robot.mv(Ndirect.MV_PWM_LOW_0 , 15000-750, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_0 , 750, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);

			// go forward
			robot.mv(Ndirect.MV_VM,Units.MeterToInch10(0.1),
				Ndirect.MV_IGNORE, Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE, Ndirect.MV_IGNORE);

			Thread.sleep(2000);

			// gripper closed
			robot.mv(Ndirect.MV_PWM_LOW_0 , 15000-1800, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_0 , 1800, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);

			// go forward
			robot.mv(Ndirect.MV_VM,Units.MeterToInch10(0.1),
				Ndirect.MV_IGNORE, Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE, Ndirect.MV_IGNORE);

			Thread.sleep(2000);

			// go back
			robot.mv(Ndirect.MV_VM,-Units.MeterToInch10(0.1),
				Ndirect.MV_IGNORE, Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE, Ndirect.MV_IGNORE);


			Thread.sleep(4100);

			//stop
			robot.st();

			// height up
			robot.mv(Ndirect.MV_PWM_LOW_1 , 15000-1350, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_1 , 1350, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);

			Thread.sleep(2000);

			// gripper open
			robot.mv(Ndirect.MV_PWM_LOW_0 , 15000-750, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_0 , 750, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);

			// height down
			robot.mv(Ndirect.MV_PWM_LOW_1 , 15000-1900, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_1 , 1900, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);

			}
			}
		catch (Exception e)
			{
			System.out.println(e);
			}
		}	
	}


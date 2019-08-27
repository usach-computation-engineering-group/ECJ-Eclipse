/*
 * Nomad150TestServos.java
 */

package EDU.gatech.cc.is.nomad150;
import EDU.gatech.cc.is.nomad150.Ndirect;

/**

<B>Introduction</B><BR>
This application is for testing Nomad 150 servo control
hardware.
<P>
To run this program, first ensure you are in the correct directory (where
the Nomad150TestServos.class file is), then type "java Nomad150TestServos".

@author (c)1997 Tucker Balch, All Rights Reserved
@version June 1997
@see Ndirect
@see Nomad150TestSensors
@see Nomad150TestTriangle
*/

public class Nomad150TestServos
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

			for(int i = 1; i<10; i+=1)
				{
				// full range is 500 to 2000
				System.out.println(i);
			robot.mv(Ndirect.MV_PWM_LOW_0 , 15000-500, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_0 , 500, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_LOW_1 , 15000-2000, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_1 , 2000, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
				Thread.sleep(5000);
			robot.mv(Ndirect.MV_PWM_LOW_0 , 15000-2000, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_0 , 2000, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_LOW_1 , 15000-500, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_1 , 500, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
				Thread.sleep(5000);
				}

			}
		catch (Exception e)
			{
			System.out.println(e);
			}
		}	
	}


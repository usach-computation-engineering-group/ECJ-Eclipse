/*
 * Nomad150GripperNeutral.java
 */

package EDU.gatech.cc.is.nomad150;
import EDU.gatech.cc.is.nomad150.Ndirect;
import EDU.gatech.cc.is.util.Units;

/**

<B>Introduction</B><BR>
This application is for placing the nomad's gripper in a neutral position.
<P>
To run this program, first ensure you are in the correct directory (where
the Nomad150GripperNeutral.class file is), then type "java Nomad150GripperNeutral".

@author (c)1997 Tucker Balch, All Rights Reserved
@version June 1997
@see Ndirect
@see Nomad150TestSensors
@see Nomad150TestTriangle
*/

public class Nomad150GripperNeutral
	{

	/**
	This method is for putting the grippers in neutral.
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
			robot.mv(Ndirect.MV_PWM_LOW_1 , 15000-2000, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_1 , 2000, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);

			// gripper neutral
			robot.mv(Ndirect.MV_PWM_LOW_0 , 15000-1250, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			robot.mv(Ndirect.MV_PWM_HIGH_0 , 1250, Ndirect.MV_IGNORE, 0, Ndirect.MV_IGNORE, 0);
			}
			}
		catch (Exception e)
			{
			System.out.println(e);
			}
		}	
	}


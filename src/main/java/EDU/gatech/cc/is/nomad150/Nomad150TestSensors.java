/*
 * Nomad150TestSensors.java
 */

package EDU.gatech.cc.is.nomad150;
import EDU.gatech.cc.is.nomad150.Ndirect;

/**

<B>Introduction</B><BR>
This application is for testing Nomad 150 sonar and bump sensor hardware and
the Ndirect class.
It intializes the robot, turns the sonars on, then prints out 
sensor information.
To run this program, first ensure you are in the correct directory (where
the Nomad150TestSensors.class file is), then type "java Nomad150TestSensors".
@author (c) 1997 Tucker Balch, tucker@cc.gatech.edu
@version June 1997
@see Nomad150TestTriangle
@see Ndirect
*/

public class Nomad150TestSensors
	{

	/**
	This method is for testing the Ndirect class.
	It intializes the robot, turns the sonars on, then prints out 
	sensor information.
	*/
	public static void main(String[] args)
		{
		int i;
		long start_time, total_time;
		double hz;
		int sonars[]; 
		long bumps;

		try
			{
			sonars = new int[16];

			System.out.println("main: Opening connection to robot");
			Ndirect robot = new Ndirect(1, 38400);

			System.out.println("main: Turning Sonars on");
			robot.sn_on(60);

			System.out.println("main: 100 sensing cycles");
			for(i=0; i<10000; i++)
				{
				robot.gs();
				bumps = robot.get_bp();
				robot.get_sn(sonars);
				System.out.print("bumper("+bumps + ") ");
				for(int s = 0; s < 16; s++)
					System.out.print(sonars[s] + " ");
				System.out.println(" ");
				Thread.sleep(100);
				}

			System.out.println("main: 100 ultra cycles to see how fast we are");
			start_time = System.currentTimeMillis();
			for(i=0; i<100; i++)
				robot.get_sn(sonars);
			total_time = System.currentTimeMillis() - start_time;
			hz = 100 / ((double) total_time / 1000);
			System.out.println("main: sonars can be read at "+
				hz + " Hz");

			robot.sn_off();
			}
		catch (Exception e)
			{
			System.out.println(e);
			}
		}	
	}


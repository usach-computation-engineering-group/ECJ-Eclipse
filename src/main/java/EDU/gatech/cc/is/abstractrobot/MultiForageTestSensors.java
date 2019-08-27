/*
 * MultiForageTestSensors.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.abstractrobot.MultiForageN150;
import EDU.gatech.cc.is.util.Vec2;


/**
 * Test Nomad 150 sensor hardware and the MultiForageN150Hard class.
 * <P>
 * To run this program, first ensure you are in the correct directory (where
 * the MultiForageTestSensors.class file is), then type "java 
 * MultiForageTestSensors".
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @see EDU.gatech.cc.is.nomad150.Ndirect
 * @see MultiForageN150
 * @see MultiForageN150Hard
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class MultiForageTestSensors
	{

        /**
	 * This method is for testing Nomad 150 sensor
	 * hardware and the MultiForageN150Hard class.
         */
	public static void main(String[] args)
		{
		Vec2	sensed_obstacles[];

		/*--- open the connection to the robot hardware ---*/
		MultiForageN150Hard abstract_robot;
		try
			{
			abstract_robot = new MultiForageN150Hard(1,38400);

			/*--- get the time we started ---*/
			long start_time = abstract_robot.getTime();
			long curr_time = start_time;
			double cycles = 0;
			abstract_robot.setBaseSpeed(abstract_robot.MAX_TRANSLATION);
			abstract_robot.setSpeed(curr_time, 0);
			sensed_obstacles = abstract_robot.getObstacles(0);
	
			/*--- print obstacles for 10 seconds ---*/
			while ((curr_time - start_time)<10000)
				{
				sensed_obstacles = abstract_robot.getObstacles(
					curr_time);
				System.out.println("---------");
				for(int i=0; i< sensed_obstacles.length; i++)
					{
					System.out.println(sensed_obstacles[i]);
					}
				System.out.println("---------");
				Thread.sleep(1000);
				curr_time = abstract_robot.getTime();
				}

			/*--- during next 20 seconds check reverse ---*/
			while ((curr_time - start_time)<30000)
				{
				sensed_obstacles = abstract_robot.getObstacles(
					curr_time);
				abstract_robot.setSteerHeading(curr_time, 
					180.0);
				abstract_robot.setTurretHeading(curr_time, 
					180.0);
				System.out.println("---------");
				for(int i=0; i< sensed_obstacles.length; i++)
					{
					System.out.println(sensed_obstacles[i]);
					}
				System.out.println("---------");
				Thread.sleep(1000);
				curr_time = abstract_robot.getTime();
				}

			/*--- during final 10 seconds check speed ---*/
			start_time = abstract_robot.getTime();
			while ((curr_time - start_time)<10000)
				{
				abstract_robot.setSteerHeading(curr_time, 0);
				abstract_robot.setTurretHeading(curr_time, 0);
				curr_time = abstract_robot.getTime();
				cycles++;
				curr_time = abstract_robot.getTime();
				}
	
			abstract_robot.quit(); // only do this when done!
			System.out.println("MultiForageTestSensors: "+
				(cycles*1000/(double)curr_time)+ 
				" control cycles per second.");

			}
		catch(Exception e)
			{
			System.out.println(e);
			}
		}
	}

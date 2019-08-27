/*
 * MultiForageTestGripper.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.abstractrobot.MultiForageN150;
import EDU.gatech.cc.is.util.Vec2;


/**
 * This application is for testing Nomad 150 servo hardware
 * and the MultiForageN150Hard class.
 * <P>
 * To run this program, first ensure you are in the correct directory (where
 * the MultiForageTestGripper.class file is), then type "java 
 * MultiForageTestGripper".
 * 
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 * @see MultiForageN150
 */

public class MultiForageTestGripper
	{

        /**
	 * This method is for testing Nomad 150 Gripper
	 * hardware and the MultiForageN150Hard class.
         */
	public static void main(String[] args)
		{
		Vec2	sensed_obstacles[];

		/*--- open the connection to the robot hardware ---*/
		MultiForageN150 abstract_robot;
		try
			{
			abstract_robot = new MultiForageN150Hard(1,38400);

			/*--- get the time we started ---*/
			long start_time = abstract_robot.getTime();
			long curr_time = start_time;
			double cycles = 0;
			abstract_robot.setBaseSpeed(abstract_robot.MAX_TRANSLATION);
			abstract_robot.setSpeed(curr_time, 0);
	
			/*--- open & close gripper a few times ---*/
			abstract_robot.setGripperFingers(curr_time++,0.5);
			Thread.sleep(1000);
			abstract_robot.setGripperFingers(curr_time++,0.0);
			Thread.sleep(1000);
			abstract_robot.setGripperFingers(curr_time++,-1.0);
			Thread.sleep(1000);
			abstract_robot.setGripperFingers(curr_time++,0.0);
			Thread.sleep(1000);
			abstract_robot.setGripperFingers(curr_time++,1.0);
			Thread.sleep(1000);
			abstract_robot.setGripperFingers(curr_time++,0.0);
			Thread.sleep(1000);
			abstract_robot.setGripperFingers(curr_time++,1.0);
			Thread.sleep(1000);
			abstract_robot.setGripperFingers(curr_time++,0.0);
			Thread.sleep(1000);
			abstract_robot.setGripperFingers(curr_time++,1.0);
			Thread.sleep(1000);
			abstract_robot.setGripperFingers(curr_time++,0.0);

			/*--- raise & lower gripper a few times ---*/
			abstract_robot.setGripperHeight(curr_time++,0.0);
			Thread.sleep(1000);
			abstract_robot.setGripperHeight(curr_time++,1.0);
			Thread.sleep(1000);
			abstract_robot.setGripperHeight(curr_time++,0.0);
			Thread.sleep(1000);
			abstract_robot.setGripperHeight(curr_time++,1.0);
			Thread.sleep(1000);
			abstract_robot.setGripperHeight(curr_time++,0.0);
			Thread.sleep(1000);
			abstract_robot.setGripperHeight(curr_time++,1.0);
			Thread.sleep(1000);
			abstract_robot.setGripperHeight(curr_time++,0.0);
			Thread.sleep(1000);
			abstract_robot.setGripperHeight(curr_time++,1.0);
			curr_time = abstract_robot.getTime();

			System.out.println("fingers should be open");
			System.out.println("gripper down");

			/*--- during final 10 seconds check speed ---*/
			start_time = abstract_robot.getTime();
			while ((curr_time - start_time)<10000)
				{
				abstract_robot.setGripperFingers(curr_time, 1.0);
				abstract_robot.setGripperHeight(curr_time, 0.0);
				cycles++;
				cycles++;
				curr_time = abstract_robot.getTime();
				}
	
			((MultiForageN150Hard)abstract_robot).quit(); // only do this when done!
			System.out.println("MultiForageTestGripper: "+
				(cycles*1000/(double)curr_time)+ 
				" control cycles per second.");

			}
		catch(Exception e)
			{
			System.out.println(e);
			}
		}
	}

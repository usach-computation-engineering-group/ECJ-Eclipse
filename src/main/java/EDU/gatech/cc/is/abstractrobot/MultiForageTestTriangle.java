/*
 * MultiForageTestTriangle.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.abstractrobot.MultiForageN150Hard;


/**
 * Test Nomad 150 translation and steering
 * hardware and the MultiForageNomad150 class; WARNING: this
 * program will drive the robot at maximum speed.
 * It intializes the robot,
 * then drives around in a triangle (left hand turns) at maximum speed.
 * Each leg of the triangle is 5 seconds long.
 * <P>
 * To run this program, first ensure you are in the correct directory (where
 * the MultiForageTestTriangle.class file is), then type "java MultiForageTestTriangle".
 * 
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class MultiForageTestTriangle
	{

        /**
	 * This method is for testing Nomad 150 translation and steering
	 * hardware and the MultiForageNomad150 class; WARNING: this
	 * program will drive the robot at maximum speed.
	 * It intializes the robot,
	 * then drives around in a triangle (left hand turns) at maximum speed.
	 * Each leg of the triangle is 5 seconds long.
         */
	public static void main(String[] args)
		{
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
	
			/*--- during first 5 seconds ---*/
			while ((curr_time - start_time)<5000)
				{
				abstract_robot.setSpeed(curr_time, 0.0);/*full stop*/
				abstract_robot.setSteerHeading(curr_time, Math.PI*4/3);
				abstract_robot.setTurretHeading(curr_time, Math.PI*4/3);
				curr_time = abstract_robot.getTime();
				cycles++;
				}

			/*--- during next 5 seconds ---*/
			while ((curr_time - start_time)<10000)
				{
				abstract_robot.setSpeed(curr_time, 1.0);/*full speed*/
				abstract_robot.setSteerHeading(curr_time, 0.0);
				abstract_robot.setTurretHeading(curr_time, 0);
				curr_time = abstract_robot.getTime();
				cycles++;
				}
	
			/*--- during next 5 seconds ---*/
			while ((curr_time - start_time)<15000)
				{
				abstract_robot.setSpeed(curr_time, 1.0);/*full speed*/
				abstract_robot.setSteerHeading(curr_time, Math.PI*2/3);
				abstract_robot.setTurretHeading(curr_time, Math.PI*2/3);
				curr_time = abstract_robot.getTime();
				cycles++;
				}
	
			/*--- during last 5 seconds ---*/
			while ((curr_time - start_time)<20000)
				{
				abstract_robot.setSpeed(curr_time, 1.0);/*full speed*/
				abstract_robot.setSteerHeading(curr_time, Math.PI*4/3);
				abstract_robot.setTurretHeading(curr_time, Math.PI*4/3);
				curr_time = abstract_robot.getTime();
				cycles++;
				}
	
			abstract_robot.quit(); // only do this when done!
			System.out.println("MultiForageTestTriangle: "+
				(cycles*1000/(double)curr_time)+ 
				" control cycles per second.");

			}
		catch(Exception e)
			{
			System.out.println(e);
			}
		}
	}

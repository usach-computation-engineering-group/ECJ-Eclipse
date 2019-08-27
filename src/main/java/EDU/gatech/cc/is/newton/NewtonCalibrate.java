/*
 * NewtonCalibrate.java
 */

package EDU.gatech.cc.is.newton;
import EDU.gatech.cc.is.nomad150.Ndirect;
import EDU.gatech.cc.is.abstractrobot.MultiForageN150;
import EDU.gatech.cc.is.util.Units;

/**
 *
 * <B>Introduction</B><BR>
 * This application is for calibrating the newton vision system.
 * <P>
 * To run this program, first ensure you are in the correct directory (where
 * the NewtonCalibrate.class file is), then type "java NewtonCalibrate".
 * 
 * @author (c) 1997 Tucker Balch, tucker@cc.gatech.edu
 * @version June 1997
 */

public class NewtonCalibrate
	{

	/**
	 * This routine is for calibrating the camera.
	 */
	public static void main(String[] args)
		{
		try
			{
			int vel=100;
			System.out.println("main: Opening connection to robot");
			Ndirect robot = new Ndirect(1, 38400);
			Newton newt = null;
			try
				{
				newt = new Newton(3, 38400);
	                        }
                	catch (Exception e)
                        	{
                        	System.out.println("NewtonCalibrate "+e);
                        	System.out.println("Newton Board not there.");
                        	newt = null;
                        	}


			System.out.println("main: Put the ball in the gripper");
			robot.sn_on(60);
			Thread.sleep(5000);
			robot.sn_off();

			System.out.println("//row	X");
			for(int k=0;k<20;k++) newt.read_frame();
			int num = newt.getNumVis(newt.CHANNEL_A);
			int[] rows  = new int[num];
			int[] areas = new int[num];
			newt.getY(newt.CHANNEL_A,rows);
			newt.getArea(newt.CHANNEL_A,areas);
			for (int j=0; j<num; j++)
				{
				System.out.println(rows[j]+areas[j]/2
					+"	"
					+MultiForageN150.GRIPPER_POSITION
					+" "+areas[j]);
				}

			/*--- go 3.2 meters back ---*/
			robot.mv(Ndirect.MV_PR , -Units.MeterToInch10(3.2),
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE);
                        vel = 100;
                        while (vel != 0)
                                {
                                robot.get_rv();
                                vel = robot.get_vtranslation();
                                }

			/*--- flush the vision buffer ---*/
			for(int k=0;k<20;k++) newt.read_frame();

			/*--- take X data ---*/
			for(double i=0;i<3.2;i+=.2)
				{
				robot.mv(Ndirect.MV_PR ,Units.MeterToInch10(.2),
					Ndirect.MV_IGNORE,
					Ndirect.MV_IGNORE,
					Ndirect.MV_IGNORE,
					Ndirect.MV_IGNORE);
                                vel = 100;
                                while (vel != 0)
                                        {
                                        robot.get_rv();
                                        vel = robot.get_vtranslation();
                                        }
				for(int k=0;k<20;k++) newt.read_frame();
				num = newt.getNumVis(newt.CHANNEL_A);
				rows  = new int[num];
				areas = new int[num];
				newt.getY(newt.CHANNEL_A,rows);
				newt.getArea(newt.CHANNEL_A,areas);
				robot.get_rc();
				for (int j=0; j<num; j++)
					{
					System.out.println(rows[j]+areas[j]/2
						+"	"+
						(-Units.Inch10ToMeter(
							robot.get_x())
						+MultiForageN150.GRIPPER_POSITION
						+" "+areas[j]));
					}
				}
				
			/*--- go 0.2 meters back ---*/
			robot.mv(Ndirect.MV_PR , -Units.MeterToInch10(0.2),
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE);
                        vel = 100;
                        while (vel != 0)
                                {
                                robot.get_rv();
                                vel = robot.get_vtranslation();
                                }

			/*--- flush de buff ---*/
			for(int k=0;k<20;k++) newt.read_frame();

			/*--- turn 90 deg right ---*/
			robot.mv(Ndirect.MV_IGNORE , Ndirect.MV_IGNORE,
				Ndirect.MV_PR,
				-900,
				Ndirect.MV_IGNORE,
				0);
                        vel = 100;
                        while (vel != 0)
                                {
                                robot.get_rv();
                                vel = robot.get_vsteering();
                                }

			/*--- flush de buff ---*/
			for(int k=0;k<20;k++) newt.read_frame();

			/*--- go 1.2 meter right ---*/
			robot.mv(Ndirect.MV_PR , Units.MeterToInch10(1.2),
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE);
                        vel = 100;
                        while (vel != 0)
                                {
                                robot.get_rv();
                                vel = robot.get_vtranslation();
                                }

			/*--- flush de buff ---*/
			for(int k=0;k<20;k++) newt.read_frame();

			/*--- take Y data ---*/
			System.out.println("//col	Y");
			for(double i=-1.2;i<1.0;i+=.1)
				{
				robot.mv(Ndirect.MV_PR,-Units.MeterToInch10(.1),
					Ndirect.MV_IGNORE,
					Ndirect.MV_IGNORE,
					Ndirect.MV_IGNORE,
					Ndirect.MV_IGNORE);
                                vel = 100;
                                while (vel != 0)
                                        {
                                        robot.get_rv();
                                        vel = robot.get_vtranslation();
                                        }
				for(int k=0;k<20;k++) newt.read_frame();
				num = newt.getNumVis(newt.CHANNEL_A);
				int[] cols  = new int[num];
				areas = new int[num];
				newt.getX(newt.CHANNEL_A,cols);
				newt.getArea(newt.CHANNEL_A,areas);
				robot.get_rc();
				for (int j=0; j<num; j++)
					{
					System.out.println(cols[j]
						+"	"+
						-Units.Inch10ToMeter(
							robot.get_y())
						+" "+areas[j]);
					}
				}
			/*--- go back to y=0 ---*/
			robot.get_rc();
			robot.mv(Ndirect.MV_PR , robot.get_y(),
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE);
                        vel = 100;
                        while (vel != 0)
                                {
                                robot.get_rv();
                                vel = robot.get_vtranslation();
                                }

			/*--- turn 90 deg left ---*/
			robot.mv(Ndirect.MV_IGNORE , Ndirect.MV_IGNORE,
				Ndirect.MV_PR,
				900,
				Ndirect.MV_IGNORE,
				0);
                        vel = 100;
                        while (vel != 0)
                                {
                                robot.get_rv();
                                vel = robot.get_vsteering();
                                }

			/*--- go back to x=0 ---*/
			robot.get_rc();
			robot.mv(Ndirect.MV_PR , -robot.get_x(),
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE,
				Ndirect.MV_IGNORE);
                        vel = 100;
                        while (vel != 0)
                                {
                                robot.get_rv();
                                vel = robot.get_vtranslation();
                                }
			}
		catch (Exception e)
			{
			System.out.println(e);
			}
		}	
	}


/*
 * KinSensorSim.java
 */


package EDU.gatech.cc.is.abstractrobot;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;
import EDU.gatech.cc.is.simulation.*;


/**
 * A class that implements the KinSensor interface.  You can use
 * objects of this class in your simulated robot code to easily
 * implement the KinSensor interface for your simulated robot.
 * 
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class KinSensorSim 
	{
	private SimulatedObject	robot;		// the robot
	public	static final boolean DEBUG = false;// set true for debug
						// messages
	private	double	kin_rangeM = 4.0;


	/**
	 * Instantiate a <B>KinSensorSim</B> object.
	 * @param r SimulatedObject, the robot on which the KinSensor resides.
	 */
        public KinSensorSim(SimulatedObject r)
		{
		robot=r;
		if (DEBUG) System.out.println("KinSensorSim: instantiated.");
		}	


	/**
	 * Build an array of our robot opponents.
	 */
	private int[] computeOpponents(SimulatedObject[] all_objects)
		{
		int num_Opponents = 0;
		int our_v_class = robot.getVisionClass();

		/*--- check all objects ---*/
		for(int i = 0; i<all_objects.length; i++)
			{
			/*--- check if it's not a teammate and a robot ---*/
			if (all_objects[i].getVisionClass()!=our_v_class &&
				(all_objects[i] instanceof Simple))
				{
				num_Opponents++;
				}
			}
		
		/*--- now assign the indices in the array ---*/
		int[] opp_ids = new int[num_Opponents];
		int iter=0;
		for(int i = 0; i<all_objects.length; i++)
			{
			/*--- check if it's not a teammate and a robot ---*/
			if (all_objects[i].getVisionClass()!=our_v_class &&
				(all_objects[i] instanceof Simple))
				{
				opp_ids[iter++] = i;
				}
			}
		return(opp_ids);
		}


	/**
	 * Build an array of our robot teammates.
	 */
	private int[] computeTeammates(SimulatedObject[] all_objects)
		{
		int num_Teammates = 0;
		int our_v_class = robot.getVisionClass();

		/*--- check all objects ---*/
		for(int i = 0; i<all_objects.length; i++)
			{
			/*--- check if it's a teammate, robot and not self ---*/
			if (all_objects[i].getVisionClass()==our_v_class &&
				(all_objects[i].getID() != robot.getID())&&
				(all_objects[i] instanceof Simple))
				{
				num_Teammates++;
				}
			}
		
		/*--- now assign the indices in the array ---*/
		int[] team_ids = new int[num_Teammates];
		int iter=0;
		for(int i = 0; i<all_objects.length; i++)
			{
			/*--- check if it's a teammate, robot and not self ---*/
			if (all_objects[i].getVisionClass()==our_v_class &&
				(all_objects[i].getID() != robot.getID())&&
				(all_objects[i] instanceof Simple))
				{
				team_ids[iter++] = i;
				}
			}
		return(team_ids);
		}


        /**
         * Return this robot's player number.  This is distinct
	 * from the simulation-oriented unique_id.
         * @param ignored long, ignored.
         * @return the player number.
         */
	public int getPlayerNumber(SimulatedObject[] all_objects)
		{
		int num_Teammates = computeTeammates(all_objects).length;
		return(robot.getID()%(num_Teammates+1));
		}


	/**
	 * Get an array of Vec2s that point egocentrically from the
	 * center of the robot to the teammates currently sensed by the 
	 * robot.
	 * @param all_objects SimulatedObject[] other objects in the
	 *		simulation.
	 * @return the sensed teammates.
	 */
	public Vec2[] getTeammates(SimulatedObject[] all_objects)
		{
		Vec2 tmp_objs[] = new Vec2[all_objects.length];
		int num_Teammates = 0;
		int[] team_ids = computeTeammates(all_objects);
		Vec2 position = robot.getPosition();

		/*--- check all teammates ---*/
		for(int i = 0; i<team_ids.length; i++)
			{
			Vec2 tmp = all_objects[team_ids[i]].getClosestPoint(
							position);

			// make sure in visible range
			if (tmp.r<kin_rangeM)
			tmp_objs[num_Teammates++] = tmp;
			}
		Vec2[] last_Teammates = new Vec2[num_Teammates];
		for(int i = 0; i<num_Teammates; i++)
			{
			last_Teammates[i] = new Vec2(tmp_objs[i].x,
				tmp_objs[i].y);
			}
		return(last_Teammates);
		}


	/**
	 * Get an array of Vec2s that point egocentrically from the
	 * center of the robot to the opponents currently sensed by the 
	 * robot.
	 * @param all_objects SimulatedObject[] other objects in the
	 *		simulation.
	 * @return the sensed opponents.
	 */
	public Vec2[] getOpponents(SimulatedObject[] all_objects)
		{
		Vec2 tmp_objs[] = new Vec2[all_objects.length];
		int num_Opponents = 0;
		int[] opp_ids = computeOpponents(all_objects);
		Vec2 position = robot.getPosition();

		/*--- check all opponents ---*/
		for(int i = 0; i<opp_ids.length; i++)
			{
			Vec2 tmp = all_objects[opp_ids[i]].getClosestPoint(
							position);
			if (tmp.r<kin_rangeM)
			tmp_objs[num_Opponents++] = tmp;
			}
		Vec2[] last_Opponents = new Vec2[num_Opponents];
		for(int i = 0; i<num_Opponents; i++)
			{
			last_Opponents[i] = new Vec2(tmp_objs[i].x,
				tmp_objs[i].y);
			}
		return(last_Opponents);
		}


	/**
	 * Set the maximum range at which a sensor reading should be considered
	 * kin.  Beyond this range, the readings are ignored.
	 * @param range the range in meters.
	 */
	public void setKinMaxRange(double range)
		{
		kin_rangeM = range;
		}
	}

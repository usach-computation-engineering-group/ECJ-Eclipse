/*
 * SquiggleBallSim.java
 */

package EDU.gatech.cc.is.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;


/**
 * implements a moving attractor for JavaBotSim simulation.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class SquiggleBallSim extends AttractorSim implements SimulatedObject
	{
	private	static final double MAX_TRANSLATION = 0.61; // 2 feet/second
	private	static final double TIMEOUT = 5; 
	private Vec2	velocity = new Vec2(0,MAX_TRANSLATION);
	public	static final boolean DEBUG = false;

	public SquiggleBallSim()
		{
		super();
		velocity.sett(Math.random()*Math.PI*2);
		}

	double	accumtime = 0;
	/**
	 * Take a simulated step;
	 */
	public void takeStep(long time_increment, SimulatedObject[] all_objs)
		{
		if ((picked_up != true)&&(deposited != true))
			{
			double time_incd = (double)time_increment / 1000;
			accumtime += time_incd;
			if (accumtime >= TIMEOUT)
				{
				accumtime = 0;
				velocity.sett(Math.random()*Math.PI*2);
				velocity.setr(MAX_TRANSLATION);
				}
	
			/*--- keep pointer to the other objects ---*/
			all_objects = all_objs;
	
                	/*--- compute a movement step ---*/
                	Vec2 mvstep = new Vec2(velocity.x, velocity.y);
                	mvstep.setr(mvstep.r * time_incd);
	
                	/*--- test the new position to see if in bounds ---*/
                	Vec2 pp = new Vec2(position.x, position.y);
                	pp.add(mvstep);
                	if ((pp.x+RADIUS > right)||(pp.x-RADIUS < left))
                        	{
                        	velocity.setx(0);
                        	mvstep.setx(0);
				velocity.sett(Math.random()*Math.PI*2);
                        	}
                	if ((pp.y+RADIUS > top)||(pp.y-RADIUS < bottom))
                        	{
                        	velocity.sety(0);
                        	mvstep.sety(0);
				velocity.sett(Math.random()*Math.PI*2);
                        	}
	
                	/*--- test the new position to see if on top of obstacle ---*/
                	pp = new Vec2(position.x, position.y);
                	boolean moveok = true;
                	pp.add(mvstep);
                	for (int i=0; i<all_objects.length; i++)
                        	{
                        	if (all_objects[i].isObstacle() &&
                                	(all_objects[i].getID() != unique_id))
                                	{
                                	Vec2 tmp = all_objects[i].getClosestPoint(pp);
                                	if (tmp.r <= 0)
                                        	{
                                        	moveok = false;
						velocity.sett(Math.random()*Math.PI*2);
                                        	break;
                                        	}
                                	}
                        	}
                	if (moveok) position.add(mvstep);
	
                	/*--- test the new position to see if on top of pushable ---*/
                	for (int i=0; i<all_objects.length; i++)
                        	{
                        	if (all_objects[i].isPushable() &&
                                	(all_objects[i].getID() != unique_id))
                                	{
                                	Vec2 tmp = all_objects[i].getClosestPoint(pp);
                                	if (tmp.r < RADIUS)
                                        	{
                                        	tmp.setr(RADIUS - tmp.r);
                                        	all_objects[i].push(tmp, velocity);
                                        	}
                                	}
                        	}
			}
		}
	}

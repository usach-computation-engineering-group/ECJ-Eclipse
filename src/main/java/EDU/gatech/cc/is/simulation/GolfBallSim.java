/*
 * GolfBallSim.java
 */

package EDU.gatech.cc.is.simulation;

import java.awt.*;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.util.Units;


/**
 * A golfball for RoboCup Soccer.
 * <B>Introduction</B><BR>
 * GolfBallSim implements a golf ball for RoboCup
 * soccer simulations.  The ball is also the scorekeeper and 
 * the referee; after all who would know better whether a 
 * scoring event occured?
 * <P>
 * A "shot clock" keeps track of how long since a scoring
 * event occured.  If it times-out, the ball is reset to the
 * center of the field.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class GolfBallSim extends AttractorSim implements SimulatedObject,SocRef 
	{
	/*--- keep track of scoring events ---*/
	private	int state = STATE_BEGIN; // this is so the west kicks off.
	private	static final int STATE_BEGIN = 1;
	private	static final int STATE_EAST_SCORE = 2;
	private	static final int STATE_WEST_SCORE = 3;
	private	static final int STATE_PLAY = 4;
	private	int	eastscore = 0;
	private	int	westscore = 0;

	/*--- shot clock time ---*/
	private	static final double TIMEOUT		= 60.0;
	private	double	shotclock = TIMEOUT;

	/*--- average distance recently moved ---*/
	private	double	avg_dist  = 0;
	private	Vec2	last_pos  = new Vec2();

	/*--- dynamics of the ball ---*/
	private	static final double MAX_TRANSLATION	= 0.50;//meters/sec
	private	static final double DECELERATION	= 0.10; //meters/sec/sec
	protected Vec2	velocity = new Vec2(0,0);

	/*--- set to true to print debug messages ---*/
	public	static final boolean DEBUG = false;


	/**
	 *Instantiate a golf ball.
	 */
	public GolfBallSim()
		{
		super();
		
		// initial speed is 0
		velocity.sett(Math.random()*Math.PI*2);
		velocity.setr(0.0);
		}


	/**
	 * Take a simulated step;
	 */
	public void takeStep(long time_increment, SimulatedObject[] all_objs)
		{
		/*--- remember where we started ---*/
		last_pos = new Vec2(position.x, position.y);

		/*--- take care of state ---*/
		if (state != STATE_PLAY) 
			{
			state = STATE_PLAY;
			}

		/*--- keep pointer to the other objects ---*/
		all_objects = all_objs;

		/*--- keep track of how much time has passed ---*/
		double time_incd = (double)time_increment / 1000;
		shotclock -= time_incd;
	
                /*--- compute a movement step ---*/
                Vec2 mvstep = new Vec2(velocity.x, velocity.y);
                mvstep.setr(mvstep.r * time_incd);
	
                /*--- test the new position to see if in bounds of field ---*/
                Vec2 pp = new Vec2(position.x, position.y);
                pp.add(mvstep);

		// we use the known, hard bounds of the official
		// RoboCup soccer field. Check end zones first:
		// The reason for taking the absolute values of the
		// velocity components is to ensure the ball is moving
		// in the right direction.  Otherwise the ball can
		// sometimes teeter back and forth along a boundary
		// if it is pushed out of bounds.

                if ((pp.x+RADIUS >= 1.37)&&(Math.abs(pp.y)>0.25))
                       	{
			// bounce off right side
                       	velocity.setx(-Math.abs(velocity.x));
                	pp.sub(mvstep);
                       	mvstep.setx(-Math.abs(mvstep.x));
                	pp.add(mvstep);
                       	}
                else if ((pp.x+RADIUS <= -1.37)&&(Math.abs(pp.y)>0.25))
                       	{
			// bounce off left side
                       	velocity.setx(Math.abs(velocity.x));
                	pp.sub(mvstep);
                       	mvstep.setx(Math.abs(mvstep.x));
                	pp.add(mvstep);
                       	}
                if (pp.y+RADIUS >= .7625)
                       	{
			// bounce off top
                       	velocity.sety(-Math.abs(velocity.y));
                	pp.sub(mvstep);
                       	mvstep.sety(-Math.abs(mvstep.y));
                	pp.add(mvstep);
                       	}
		else if (pp.y-RADIUS <= -.7625)
                       	{
			// bounce off bottom
                       	velocity.sety(Math.abs(velocity.y));
                	pp.sub(mvstep);
                       	mvstep.sety(Math.abs(mvstep.y));
                	pp.add(mvstep);
                       	}
	
                /*--- test the new position to see if on top of obstacle ---*/
                pp = new Vec2(position.x, position.y);
                pp.add(mvstep);
                for (int i=0; i<all_objects.length; i++)
                       	{
                       	if (all_objects[i].isObstacle() &&
                               	(all_objects[i].getID() != unique_id))
                               	{
                               	Vec2 tmp = all_objects[i].getClosestPoint(pp);
                               	if (tmp.r <= 0)
                                       	{
					// don't move
					mvstep.setr(0);
					// bounce
					double bounce = Units.BestTurnRad(
						velocity.t, tmp.t);
					velocity.sett(Math.PI + bounce + tmp.t);
					//mvstep.sett(velocity.t);
					//mvstep.setr(velocity.r * time_incd * 0.5);
                                       	break;
                                       	}
                               	}
                       	}
                position.add(mvstep);
	
                /*--- test the new position to see if on top of pushable ---*/
		/* skip this for golf balls, there are no other
		   pushable objects in a soccer game.
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
		*/

		/*--- decelerate ---*/
		double newvel = velocity.r - (DECELERATION * time_incd);
		if (newvel < 0) newvel = 0;
		velocity.setr(newvel);

		/*-- test for score ---*/
		if ((position.x > 1.37)&&(Math.abs(position.y)<0.25))
			{
			state = STATE_WEST_SCORE;
			westscore++;
			position.setr(0);
			velocity.setr(0);
			shotclock = TIMEOUT;
			System.out.println(westscore+" "+eastscore);
			}
		else if ((position.x < -1.37)&&(Math.abs(position.y)<0.25))
			{
			state = STATE_EAST_SCORE;
			eastscore++;
			position.setr(0);
			velocity.setr(0);
			shotclock = TIMEOUT;
			System.out.println(westscore+" "+eastscore);
			}

		/*--- check shotclock ---*/
		if (shotclock <= 0)
			{
			shotclock = TIMEOUT;
			position.setr(0);
			velocity.setr(0);
			}

		/*--- check for no movement ---*/
		last_pos.sub(position);
		avg_dist = avg_dist * 0.9 + Math.abs(last_pos.r);
		if (avg_dist < 0.01)
			{
			shotclock = TIMEOUT;
			position.setr(0);
			velocity.setr(0);
			}
		}

	/**
	 * Handle a push.  This is how to kick or push the ball.
	 */
        public void push(Vec2 d, Vec2 v)
                {
                /*--- move according to the push ---*/
                position.add(d);
		velocity = new Vec2(v.x, v.y);
                }

	/**
	 * Draw the golf ball and display score and shotclock.
	 */
        public void draw(Graphics g, int w, int h,
                double t, double b, double l, double r)
                {
                top =t; bottom =b; left =l; right = r;

                double meterspp = (r - l) / (double)w;
                if (DEBUG) System.out.println("meterspp "+meterspp);
                int radius = (int)(RADIUS / meterspp);
                int xpix = (int)((position.x - l) / meterspp);
                int ypix = (int)((double)h - ((position.y - b) / meterspp));
                if (DEBUG) System.out.println("golfball at"+
                        " at "+xpix+","+ypix);

                /*--- draw the ball ---*/
                g.setColor(foreground);
                g.fillOval(xpix - radius, ypix - radius,
                        radius + radius, radius + radius);

		/*--- draw the score and shot-clock ---*/
		g.setColor(Color.white);
		g.drawString("score: "+westscore+":"+eastscore+
			" shot: "+(int)shotclock,
			(int)((-1.39 -l)/meterspp),
			(int)((double)h - ((.7825 - b)/meterspp))-2);
                }


	/**
	 * True if the game is underway.  If false, the soccer robots
	 * should reset their positions on the field according to
	 * whether they kick off or not.
	 * @return true if game is underway.
	 */
	public boolean playBall()
		{
		if (state == STATE_PLAY)
			return(true);
		else
			return(false);
		}


	/**
	 * True if it is east's turn to kick off.
	 * @return true if it is east's turn to kick off.
	 */
	public boolean eastKickOff()
		{
		if (state == STATE_WEST_SCORE)
			return(true);
		else
			return(false);
		}


	/**
	 * True if it is west's turn to kick off.  This occurs
	 * at the begining of the game, and after east scores.
	 * @return true if it is west's turn to kick off.
	 */
	public boolean westKickOff()
		{
		if ((state == STATE_EAST_SCORE)||(state == STATE_BEGIN))
			return(true);
		else
			return(false);
		}


	/**
	 * True if it is west just scored.
	 * @return true if west just scored.
	 */
	public boolean westJustScored()
		{
		if (state == STATE_WEST_SCORE)
			return(true);
		else
			return(false);
		}


	/**
	 * True if it is east just scored.
	 * @return true if east just scored.
	 */
	public boolean eastJustScored()
		{
		if (state == STATE_EAST_SCORE)
			return(true);
		else
			return(false);
		}
	}

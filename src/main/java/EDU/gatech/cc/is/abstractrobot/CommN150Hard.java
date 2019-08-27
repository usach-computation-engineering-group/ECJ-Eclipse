/*
 * CommN150Hard.java
 */

package EDU.gatech.cc.is.abstractrobot;

import java.util.*;
import java.awt.Color;
import EDU.gatech.cc.is.communication.*;
import EDU.gatech.cc.is.util.*;


/**
 * CommN150Hard implements SimpleN150 for
 * Nomad 150 hardware using the Ndirect class.
 * You should see the specifications in SimpleN150
 * and Ndirect class documentation for details.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @see CommN150
 * @see EDU.gatech.cc.is.nomad150.Ndirect
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class CommN150Hard extends SimpleN150Hard implements CommN150, HardObject
	{
	/**
	 * The transceiver used to communicate with the rest of the robots.
	 */
	protected TransceiverHard t = null;

	/**
	 * Channel for recieving other robot positions.
	 */
	protected CircularBufferEnumeration rec_positions = null;


        /**
         * Instantiate a <B>CommN150Hard</B> object.  You should only
         * instantiate one of these per robot connected to your
         * computer.  Standard call is CommN150Hard(1,38400);
         * @param serial_port 1 = ttys0 (COM1), 2 = ttys1 (COM2) ...
         * @param baud baud rate for communication.
         * @exception Exception If unable to configure the hardware.
         */
	public CommN150Hard(int serial_port, int baud, String server,
		int id) throws Exception
		{
                super(serial_port, baud);

		setID(id);

		/*--- connect the transceiver ---*/
		t = new TransceiverHard(server, id);

		/*--- channel to listen for positions ---*/
		rec_positions = t.getReceiveChannel();
		}


	boolean last_connected = true;
        /**
         * Conduct periodic I/O with the robot.  It runs at most every 
	 * SimpleN150Hard.MIN_CYCLE_TIME milliseconds to gather sensor 
	 * data from the robot, and issue movement commands.
         */
	public void takeStep()
		{
		/*--- do all the hardware stuff ---*/
                super.takeStep();

		/*--- handle KinSensor communication ---*/
		// only do this if communication is connected
		if (t.connected())
			{
			/*--- communicate position to everyone else ---*/
			// send our position in absolute coordinates
			Message m = new PositionMessage(last_Position);
			t.broadcast(m);

			/*--- listen to everyone else ---*/
			// toss position messages into a vector
			Vector othersv = new Vector(30);

			// loop until got all messages
			while(rec_positions.hasMoreElements())
				{
				// get a message
				m = (Message)rec_positions.nextElement();

				// disregard other types of message
				if (m instanceof PositionMessage)
					{
					Vec2 p = new Vec2(
						((PositionMessage)m).val);

					// subtract our position to make it
					// egocentric
					p.sub(last_Position);

					// stow it in the vector
					othersv.addElement(p);
					}
				}
			// make an array to old the other positions
			last_teammates = new Vec2[othersv.size()];
			Enumeration othersenum = othersv.elements();

			// loop to get them all
			for(int i = 0; i<last_teammates.length; i++)
				last_teammates[i] = 
					(Vec2)othersenum.nextElement();
			}
		}


	/*--- KinSensor methods ---*/

	protected Vec2[] last_teammates = new Vec2[0];
        /**
         * Get an array of Vec2s that represent the locations of 
         * teammates (Kin).
         * @param timestamp only get new information if 
         * timestamp > than last call or timestamp == -1.
         * @return the sensed teammates.
         * @see EDU.gatech.cc.is.util.Vec2
         */
        public Vec2[] getTeammates(long timestamp)
		{
		return((Vec2[])last_teammates.clone());
		}


	protected Vec2[] last_opponents = new Vec2[0];
        /**
         * Get an array of Vec2s that represent the
         * locations of opponents.
         * @param timestamp only get new information if 
         *      timestamp > than last call or timestamp == -1.
         * @return the sensed opponents.
         * @see EDU.gatech.cc.is.util.Vec2
         */
        public Vec2[] getOpponents(long timestamp)
		{
		return(last_opponents);
		}


        /**
         * Get the robot's player number, between 0
         * and the number of robots on the team.
         * Don't confuse this with getID which returns a unique number
         * for the object in the simulation as a whole, not on its individual
         * team.  On real hardware however, getID == PlayerNumber.
         * @param timestamp only get new information if 
         *      timestamp > than last call or timestamp == -1.
         * @return the player number.
         */
        public int getPlayerNumber(long timestamp)
		{
		return(unique_id);
		}


        /**
	 * NOT IMPLEMENTED
         * Set the maximum range at which kin may be sensed.  Primarily
         * for use in simulation.
         * @param r double, the maximum range.
         */
        public void setKinMaxRange(double r)
		{
		}


	/*--- Transceiver methods ---*/

        /**
         * Broadcast a message to all teammates, except self.
         * @param m Message, the message to be broadcast.
         */
        public void broadcast(Message m)
		{
		t.broadcast(m);
		}


        /**
         * Transmit a message to just one teammate.  Transmission to
         * self is allowed.
         * @param id int, the ID of the agent to receive the message.
         * @param m Message, the message to transmit.
         * @exception CommunicationException if the receiving agent does not
         *              exist.
         */
        public void unicast(int id, Message m) 
                throws CommunicationException
		{
		t.unicast(id, m);
		}


        /**
         * Transmit a message to specific teammates.  Transmission to
         * self is allowed.
         * @param ids int[], the IDs of the agents to receive the message.
         * @param m Message, the message to transmit.
         * @exception CommunicationException if one of the receiving agents 
         *              does not exist.
         */
        public void multicast(int[] ids, Message m)
                throws CommunicationException
		{
		t.multicast(ids, m);
		}


        /**
         * Get an enumeration of the incoming messages.  The messages
         * are automatically buffered by the implementation.
         * Unless the implementation guarantees it, you cannot
         * count on all messages being delivered.
         * Example, to print all incoming messages:
         * <PRE>
         * Transceiver c = new RobotComm();
         * CircularBufferEnumeration r = c.getReceiveChannel();
         * while (r.hasMoreElements())
         *      System.out.println(r.nextElement());
         * </PRE>
         * @return the Enumeration.
         */
        public CircularBufferEnumeration getReceiveChannel()
		{
		return(t.getReceiveChannel());
		}


        /**
         * Set the maximum range at which communication can occur.
         * In simulation, this corresponds to a simulation of physical limits,
         * on mobile robots it corresponds to a signal strength setting.
         * @param r double, the maximum range.
         */
        public void setCommunicationMaxRange(double r)
		{
		t.setCommunicationMaxRange(r);
		}


        /**
         * Check to see if the transceiver is connected to the server.
         */
        public boolean connected()
		{
		return(t.connected());
		}

	  public Color getForegroundColor() {return Color.black;}
	  public Color getBackgroundColor() {return Color.black;}

	}

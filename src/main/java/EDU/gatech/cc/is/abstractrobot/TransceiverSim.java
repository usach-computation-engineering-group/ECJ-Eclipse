/*
 * TransceiverSim.java
 */


package EDU.gatech.cc.is.abstractrobot;

import java.util.Enumeration;
import EDU.gatech.cc.is.util.CircularBuffer;
import EDU.gatech.cc.is.simulation.SimulatedObject;
import EDU.gatech.cc.is.communication.*;
import EDU.gatech.cc.is.util.*;


/**
 * Implements the Transceiver interface.  You can use
 * objects of this class in your simulated robot code to easily
 * implement the Transceiver interface for the robot.
 * 
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class TransceiverSim
	{
	/**
	 * The maximum number of messages the receive buffer can hold.
	 */
	public static final int BUF_SIZE = 100;	// size of the receive buffer

	private SimulatedObject	robotsim;	// the simulated parts
	private Simple		robot;		// the robot parts
	public	static final boolean DEBUG = false;// set true for debug
						// messages
	private	double	comm_rangeM = 4.0;	// max range for communication
	private	CircularBuffer messages =	// the buffer where
			new CircularBuffer(BUF_SIZE);// messages are stored


	/**
	 * Instantiate a <B>TransceiverSim</B> object.
	 * @param r SimulatedObject, the robot on which the Transceiver 
	 * 		resides.
	 */
        public TransceiverSim(SimulatedObject rs, Simple r)
		{
		robotsim = rs;
		robot = r;
		if (DEBUG) System.out.println("TransceiverSim: instantiated.");
		}	


        /**
         * Transmit a message to a list of teammates.  Transmission
	 * to self allowed.
         * @param ids int[], the IDs of the agents to receive the message.
         * @param m Message, the message to transmit.
         * @exception CommunicationException if one of the receiving agents
         *              does not exist.
         */
        public void multicast(int[] ids, Message m, 
			SimulatedObject[] all_objects)
		throws CommunicationException
		{
		// send the messages
		for (int i=0; i<ids.length; i++)
			{
			unicast(ids[i], m, all_objects);
			}
		}


        /**
         * Transmit a message to all teammates but self.
         * @param m Message, the message to transmit.
         */
        public void broadcast(Message m, SimulatedObject[] all_objects)
		{
		int[] team_ids = computeTeammates(all_objects);

		//if (team_ids.length>0)
		//System.out.println(team_ids.length + " " 
			//+ team_ids[0] + " "
		 	//+ robot.getID());
		// send the messages
		for (int i=0; i<team_ids.length; i++)
			{
			// except to self
			if (robotsim.getID() 
				!= all_objects[team_ids[i]].getID())
				{
				try
					{
					unicast(i, m, all_objects);
					}
				catch(CommunicationException e)
					{
					// should never happen
					System.out.println(e +
				"TransceiverSim internal error");
					}
				}
			}
		}


        /**
         * Transmit a message to just one teammate.  Transmission to
         * self is allowed.
         * @param id int, the ID of the agent to receive the message.
         * @param m Message, the message to transmit.
         * @exception CommunicationException if the receiving agent does not
         *              exist.
         */
        public void unicast(int id, Message m, SimulatedObject[] all_objects)
                throws CommunicationException
		{
		// copy the message
		Message newcopy = new Message();
		try
			{
			newcopy = (Message)m.clone();
			}
		catch(CloneNotSupportedException e)
			{
			// should never happen
			System.out.println(e + "TransceiverSim can't clone");
			}

		// make sure we have the array of teammates.
		int[] team_ids = computeTeammates(all_objects);

		// check for legal ID.
		if ((id<0) || (id>team_ids.length)) throw 
			new CommunicationException("illegal receiver ID: "+id);

		// send the message to the recipient
		all_objects[team_ids[id]].receive(newcopy);
		}


        /**
         * Get an enumeration of the incoming messages.  The messages
         * are automatically buffered by the implementation.
         * You cannot count on all messages being delivered, unless
	 * you read them as fast as they arrive.
         * @return the Enumeration.
         */
        public CircularBufferEnumeration getReceiveChannel()
		{
		return(messages.elements());
		}


        /**
         * Receive a message.
         * @param m the message.
         */
        public void receive(Message m)
		{
		messages.put(m);
		}


	/**
	 * Build an array of our robot teammates.
	 */
	private int[] computeTeammates(SimulatedObject[] all_objects)
		{
		int base = -1;
		int num_Teammates = 0;
		int our_v_class = robotsim.getVisionClass();

		/*--- check all objects ---*/
		for(int i = 0; i<all_objects.length; i++)
			{
			/*--- check if it's a teammate and a robot ---*/
			if ((all_objects[i].getVisionClass()==our_v_class)&&
				(all_objects[i] instanceof Simple))
				// note: self included
				{
				num_Teammates++;
				if (base == -1) base = i;
				}
			}
		
		/*--- now assign the indices in the array ---*/
		int[] team_ids = new int[num_Teammates];
		int iter=0;
		for(int i = 0; i<all_objects.length; i++)
			{
			/*--- check if it's a teammate and a robot ---*/
			if ((all_objects[i].getVisionClass()==our_v_class)&&
				(all_objects[i] instanceof Simple))
				// self included
				{
				// fancy math to make indices line up
				team_ids[((iter++)+base)
					%num_Teammates] = i;
				}
			}
		return(team_ids);
		}


	/**
	 * Set the maximum range at which a sensor reading should be considered
	 * kin.  Beyond this range, the readings are ignored.
	 * @param range the range in meters.
	 */
	public void setCommunicationMaxRange(double range)
		{
		comm_rangeM = range;
		}

        /**
         * Check to see if the transceiver is connected to the server.
         */
        public boolean connected()
                {
                return(true); // in simulation we're always connected
                }
	}

/*
 * TransceiverHard.java
 */

package EDU.gatech.cc.is.communication;

import java.util.Enumeration;
import java.io.*;
import java.net.*;
import RoboComm.RoboComm;
import EDU.gatech.cc.is.util.*;


/**
 * The TransceiverHard class implements the Transceiver interface
 * so a robot can communicate.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class TransceiverHard implements Transceiver, Runnable
	{
        /**
         * The maximum number of messages the receive buffer can hold.
         */
        public static final int BUF_SIZE = 100; // size of the receive buffer

        /**
         * Circular buffer for messages.
         */
        private CircularBuffer messagebuf =       
                        new CircularBuffer(BUF_SIZE);

	/**
	 * Indicates whether done or not
	 */
	private boolean done = false;

	/**
	 * The thread receiving messages.
	 */
	private Thread th = null;

	/**
	 * Indicates whether communication has been enabled, e.g. we are
	 * connected with the server.
	 */
	private boolean enabled = false;

	/**
	 * The id of this agent.
	 */
	private int id = -1;

	/**
	 * Input.
	 */
        ObjectInputStream in = null;

	/**
	 * Output.
	 */
        ObjectOutputStream out = null;

	/**
	 * The socket to the server.
	 */
	Socket sock = null;


	/**
	 * Make a real transceiver object.
	 * @param s String, the server host.
	 * @param i int, the robot id.
	 */
	public TransceiverHard(String s, int i)
		{
		/*--- record the id ---*/
		id = i;

		/*--- try to connect to the server ---*/
                try
                        {
                        sock = new Socket(s,RoboComm.LISTEN_PORT);
			enabled = true;
                        }
                catch (IOException e)
                        {
                        System.out.println("TransceiverHard:"+
				" Unable to connect to "+s+
                                " on port "+RoboComm.LISTEN_PORT+" due to ");
                        System.out.println(e);
                        System.out.println("Maybe the "+
                                "RoboComm server is not running.");
			enabled = false;
                        }

		/*--- open the socket for sending messages ---*/
                // note: can't open receive socket yet because it blocks
		if (enabled)
			{
                	try
                        	{
                        	out =
                                	new ObjectOutputStream(
                                        	new BufferedOutputStream(
                                                sock.getOutputStream()));
                        	}
                	catch (Exception e)
                        	{
                        	System.out.println("TransceiverHard: "+e);
				enabled = false;
                        	}
			}

                /*--- register id with the server ---*/
                try
                        {
			if (enabled)
				{
                        	RegisterMessage m = new RegisterMessage(id);
                        	out.writeObject(m);
                        	out.flush();
				}
                        }
                catch (Exception e)
                        {
                        System.out.println("TransceiverHard: "+e);
			enabled = false;
                        }

                /*--- open receive socket ---*/
                try
                        {
			if (enabled)
				{
                        	in = new ObjectInputStream(
                                        new BufferedInputStream(
                                                sock.getInputStream()));
				}
                        }
                catch (Exception e)
                        {
                        System.out.println("TransceiverHard: "+e);
			enabled = false;
                        }

		if (enabled)
			{
               		System.out.println("TransceiverHard: connected "+
				"to RoboComm server as client "+id);
			}
		else
               		System.out.println("TransceiverHard: "+
				"unable to connect.");

		/*--- start the thread running ---*/
		if (enabled)
			{
			th = new Thread(this);
			th.start();
			}
		}


	/**
	 * Thread to monitor incoming messages.
	 */
	public void run()
		{
                Message m = null;
		while (!done)
                        {
                        try
                                {
                                m = (Message)in.readObject();
				messagebuf.put(m);
                                }
                        catch(Exception e)
                                {
                                System.out.println(e);
				done = true;
				enabled = false;
                                }
			try {Thread.yield();}
			catch(Exception e){}
                        }
		}


	/**
	 * Broadcast a message to all teammates, except self.
	 * @param m Message, the message to be broadcast.
	 */
	public synchronized void broadcast(Message m)
		{
		m.type = m.BROADCAST;
                try
                        {
                        if (enabled) 
				{
				out.writeObject(m);
                        	out.flush();
				}
                        }
                catch (Exception e)
                        {
                        System.out.println("TransceiverHard.broadcast: "+e);
			enabled = false;
			done = true;
                        }
		}


	/**
	 * Transmit a message to just one teammate.  Transmission to
	 * self is allowed.
	 * @param i int, the ID of the agent to receive the message.
	 * @param m Message, the message to transmit.
	 * @exception CommunicationException if the receiving agent does not
	 *		exist.
	 */
	public synchronized void unicast(int i, Message m) 
		throws CommunicationException
		{
		m.type = m.UNICAST;
		m.receivers = new int[1];
		m.receivers[0] = i;
                try
                        {
                        if (enabled) 
				{
				out.writeObject(m);
                        	out.flush();
				}
                        }
                catch (Exception e)
                        {
                        System.out.println("TransceiverHard.unicast: "+e);
			enabled = false;
			done = true;
                        }
		}


	/**
	 * Transmit a message to specific teammates.  Transmission to
	 * self is allowed.
	 * @param ids int[], the IDs of the agents to receive the message.
	 * @param m Message, the message to transmit.
	 * @exception CommunicationException if one of the receiving agents 
	 *		does not exist. NOT IMPLEMENTED.
	 */
	public synchronized void multicast(int[] ids, Message m)
		throws CommunicationException
		{
		m.type = m.MULTICAST;
		m.receivers = ids;
                try
                        {
                        if (enabled) 
				{
				out.writeObject(m);
                        	out.flush();
				}
                        }
                catch (Exception e)
                        {
                        System.out.println("TransceiverHard.multicast: "+e);
			enabled = false;
			done = true;
                        }
		}


	/**
	 * Get an enumeration of the incoming messages.  The messages
	 * are automatically buffered for you.
	 * Since this is implemented as a circular you cannot
	 * count on all messages being delivered unless you read from
	 * the buffer faster than messages are received.
	 * Example, to print all incoming messages:
	 * <PRE>
	 * Transceiver c = new TranscieverHard();
	 * Enumeration r = c.getReceiveChannel();
	 * while (r.hasMoreElements())
 	 * 	System.out.println(r.nextElement());
	 * </PRE>
	 * @return the Enumeration.
	 */
	public CircularBufferEnumeration getReceiveChannel()
		{
		return(messagebuf.elements());
		}


	/**
	 * NOT IMPLEMENTED
	 * Set the maximum range at which communication can occur.
	 * In simulation, this corresponds to a simulation of physical limits,
	 * on mobile robots it corresponds to a signal strength setting.
	 * @param r double, the maximum range.
	 */
	public void setCommunicationMaxRange(double r)
		{
		}


	/**
	 * Check to see if the transceiver is connected to the server.
	 */
	public synchronized boolean connected()
		{
		return(enabled);
		}


	/**
	 * quit.
	 */
	public synchronized void quit()
		{
		done = true;
		enabled = false;
		th.stop();
		}


	/**
	 * Code to test the communication system.
	 */
        public static void main(String[] args)
                {
                String server_host = "localhost";
                int num = 0;

                /*--- announce ---*/
                System.out.println("TransceiverHard demonstration");

                /*--- check arguments ---*/
                try
                        {
                        num = Integer.parseInt(args[0]);
                        }
                catch (Exception e)
                        {
                        System.out.println(
                                "usage: Client num [server_host]");
                        }
                if (args.length == 2)
                        {
                        server_host = args[1];
                        }
                else if (args.length > 2)
                        {
                        System.out.println("usage: Client num [server_host]");
                        System.exit(1);
                        }

		/*--- make the transceiver ---*/
		TransceiverHard t = new TransceiverHard(server_host, num);
		}
	}

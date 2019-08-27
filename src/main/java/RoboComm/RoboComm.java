/*
 * RoboComm.java
 */

package RoboComm;

import java.io.*;
import java.net.*;
import EDU.gatech.cc.is.communication.*;


/**
 * Robot Communication Server.  Provides an easy-to-use robot to robot
 * communication protocol.
 * <P>
 * To run this program type "java RoboComm.RoboComm".
 * <P>
 * For more detailed information, see the
 * <A HREF="../RoboComm/index.html">RoboComm page</A>.
 * <P>
 * Copyright (c)1998 Tucker Balch, all rights reserved.
 *
 * @author Tucker Balch
 * @version $Revision: 1.1.1.2 $
 */

public class RoboComm
	{
	/**
	 * Port number to listen on for new connections.
	 */
	public final static int LISTEN_PORT = 7462; /* my birthday */

	/**
	 * Maximum number of clients/handlers we need.
	 */
	public final static int MAX_HANDLERS = 1000;

	/**
	 * Objects handling each connection.
	 */
	private ConnectionHandler handlers[] = 
		new ConnectionHandler[MAX_HANDLERS];

	/**
	 * Indicate if we should exit.
	 */
	private boolean done = false;

	/**
	 * Socket to listen on.
	 */
	ServerSocket listensock = null;

	/**
	 * Count active clients.
	 */
	private int clients = 0;

	/**
	 * Register a client/handler.
	 *
	 * @param h ConnectionHandler, the handler
	 * @param i int, the id of the client
	 */
	public synchronized void register(ConnectionHandler handler, int id)
		{
		/*--- error handling ---*/
		if ((id < 0)||(id >= MAX_HANDLERS))
			{
			System.out.println("RoboComm: illegal client id " + id);
			return;
			}
		else if (handlers[id]!=null)
			{
			System.out.println("RoboComm: replacing client " + id);
			handlers[id].die();
			clients--;
			}

		/*--- register the client ---*/
		System.out.println("RoboComm: client " + id + " registered");
		handlers[id] = handler;
		clients++;
		}


	/**
	 * Unregister the client/handler.
	 *
	 * @param i int, the id of the client
	 */
	public synchronized void unregister(int id)
		{
		/*--- error handling ---*/
		if ((id < 0)||(id >= MAX_HANDLERS))
			{
			System.out.println("RoboComm.unregister: "+
				"illegal client id: " + id);
			return;
			}

		/*--- un register the client ---*/
		System.out.println("RoboComm.unregister: client " + id + " unregistered");
		handlers[id] = null;
		clients--;
		}


	/**
	 * Kill self.
	 */
	public synchronized void die()
		{
		/*--- tell all handlers to die ---*/
		for(int i=0; i<MAX_HANDLERS; i++)
			{
			if (handlers[i] != null)
				handlers[i].die();
			}

		/*--- stop look and close socket ---*/
		done = true;
                try{listensock.close();}
                catch(Exception e){}
		}


	/**
	 * Transmit a message.
	 * @param m Message, the message to send.
	 */
	public synchronized void transmit(Message m)
		{
		/*--- if point-to-point message ---*/
		if (m.type == m.UNICAST)
			//send it to the receiver
			{
			if (handlers[m.receivers[0]]!=null)
				handlers[m.receivers[0]].send(m);
			else
				System.out.println("RoboComm.transmit: "+
					"attempt to transmit to unregistered "+
					"receiver " + m.receivers[0]);
			}

		/*--- if multicast ---*/
		else if (m.type == m.MULTICAST)
			{
			// send it to the receiver list
			for (int i = 0; i < m.receivers.length; i++)
				{
				if (handlers[m.receivers[i]]!=null)
					handlers[m.receivers[i]].send(m);
				else
					System.out.println(
						"RoboComm.transmit: "+
					"attempt to transmit to unregistered "+
					"receiver " + m.receivers[0]);
				}
			}

		/*--- if broadcast ---*/
		else 
			{
			int j = 0;

			// inefficient if all clients not low-numbered
			for(int i=0; j<clients; i++)
				{
				if (handlers[i] != null)
					{
					// don't resend to self
					if (i != m.sender)
						handlers[i].send(m);
					j++;
					}
				}
			}
		}
			

	/**
	 * Main control loop.
	 */
	public void run()
		{
		/*--- find our hostname ---*/
		// primarily a sanity check
		InetAddress this_host;
		try
			{
			this_host = InetAddress.getLocalHost();
			}
		catch (Exception e)
			{
			System.out.println("May not be connected to the network.");
			System.out.println("Continuing...");
			this_host = null;
			}
		String host_name = "unknown host";
		if (this_host != null)
			host_name = this_host.getHostName();
		System.out.println("RoboComm.run: started on "+host_name);

		/*--- set up server socket ---*/
		try
			{
			listensock = new ServerSocket(LISTEN_PORT);
			}
		catch (IOException e)
			{
			System.out.println("Unable to listen for connections "+
				"on port "+LISTEN_PORT+" due to ");
			System.out.println(e);
			System.out.println("There is probably already a "+
				"copy of RoboComm running on this machine.");
			System.exit(1);
			}
		System.out.println("Listening for connections on port "+LISTEN_PORT);

		/*--- listen for connections ---*/
		while (!done)
			{
			try
				{
				Socket s = listensock.accept();
				ConnectionHandler ch = 
					new ConnectionHandler(s, this);
				}
			catch (IOException e)
				{
				System.out.println("RoboComm.run: error accepting a connection on "+
					"port "+LISTEN_PORT+" due to "+
					e);
				System.out.println("Continuing...");
				}
			}
		}


        /**
	 * Main for RoboComm.
         */
	public static void main(String[] args)
		{
		/*--- announce ---*/
		System.out.println("RoboComm 2.0 (c)1999 Tucker Balch");

		/*--- start running ---*/
		RoboComm rc = new RoboComm();
		rc.run();
		}
	}


/*
 * ConnectionHandler.java
 */

package RoboComm;

import java.io.*;
import java.net.*;
import EDU.gatech.cc.is.communication.*;


/**
 * Manages the connection to a robot.
 * <P>
 * For more detailed information, see the
 * <A HREF="../../../../../RoboComm/docs/index.html">RoboComm page</A>.
 * <P>
 * Copyright (c) 1998 Tucker Balch, all rights reserved.
 *
 * @author Tucker Balch
 * @version $Revision: 1.1.1.1 $
 */

public class ConnectionHandler implements Runnable
	{
	/**
	 * The socket to manage.
	 */
	private Socket sock = null;

	/**
	 * The client id.
	 */
	private int id = -1;

	/**
	 * The server object.
	 */
	private RoboComm server = null;

	/**
	 * Output.
	 */
	private ObjectOutputStream out = null;

	/**
	 * Input.
	 */
	private ObjectInputStream in = null;

	/**
	 * Loop until.
	 */
	private boolean done = false;


	/**
	 * Set up to manage a socket.
	 *
	 * @param s Socket, the socket to listen to.
	 */
	public ConnectionHandler(Socket s, RoboComm ser)
		{
		/*--- remember the socket and server ---*/
		sock = s;
		server = ser;

		/*--- set up the input and output streams ---*/
		try
			{
			out = new ObjectOutputStream(
				new BufferedOutputStream(
					sock.getOutputStream()));
			in = new ObjectInputStream(
				new BufferedInputStream(
					sock.getInputStream()));
			}
		catch (Exception e)
			{
			System.out.println("ConnectionHandler: "+e);
			}

		/*--- start the monitoring thread ---*/
		Thread t = new Thread(this);
		t.start();
		}


	/**
	 * Thread to manage the socket.
	 */
	public void run()
		{
		Message m = null;
		while(!done)
			{
			try
				{
				// listen for messages from the client
				m = (Message)in.readObject();
				}
			catch(Exception e)
				{
				System.out.println("ConnectionHandler.run: "+e);
				if (id!=-1) server.unregister(id);
				done = true;
				}

			/*--- make sure it has the right id on it ---*/
			if (m != null)
				{
				m.sender = id;
				}

			/*--- if it is a null message ---*/
			if (m == null)
				System.out.println("ConnectionHandler.run: got null message");

			/*--- if it is a RegisterMessage ---*/
			else if (m instanceof RegisterMessage)
				{
				if (id != -1)
					// were already registered once
					{
					System.out.println(
						"ConnectionHandler.run: changing registration");
					server.unregister(
						((RegisterMessage)m).val);
					}
				server.register(this, ((RegisterMessage)m).val);
				id = ((RegisterMessage)m).val;
				Message ack = new AckMessage();
				send(ack);
				}

			/*--- if it is a TerminateMessage ---*/
			else if ((m instanceof TerminateMessage)&&(id == -1))
				{
				// protocol is only to die if msg from
				// unregistered client.  Otherwise, forward.
					
				System.out.println(
				"ConnectionHandler.run: TerminateMessage");
				server.die();
				}

			/*--- otherwise, transmit ---*/
			else 
				{
				//hand it to the server for transmission
				server.transmit(m);
				}

			//try {Thread.sleep(100);}
			//catch(Exception e){}
			}
		try{sock.close();}
		catch(Exception e){}
		System.out.println("ConnectionHandler.run: handler for client "+id+" exiting");
		}


	/**
	 * Quit handling the client.  Assume that we have been unregistered.
	 */
	public void die()
		{
		id = -1;
		done = true;
		try{sock.close();}
		catch(Exception e){}
		}


	/**
	 * Send a message to the client.
	 * @param m Message, the message to send.
	 */
	public void send(Message m)
		{
                try
			{
                        out.writeObject(m);
                        out.flush();
                        }
		catch (Exception e)
			{
			System.out.println("ConnectionHandler.send: "+e);
			}
		}
	}

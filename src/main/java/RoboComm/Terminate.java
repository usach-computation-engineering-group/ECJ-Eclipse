/*
 * Terminate.java
 */

package RoboComm;

import java.io.*;
import java.net.*;
import EDU.gatech.cc.is.communication.*;

/**
 * Kill the RoboComm process.
 * <P>
 * To run this program type "java RoboComm.Terminate".
 * <P>
 * For more detailed information, see the
 * <A HREF="../../../../../RoboComm/docs/index.html">RoboComm page</A>.
 * <P>
 * Copyright (c)1998 Tucker Balch, all rights reserved.
 *
 * @author Tucker Balch
 * @version $Revision: 1.1.1.1 $
 */

public class Terminate
	{

        /**
	 * Main for Terminate.
         */
	public static void main(String[] args)
		{
		String server_host = "localhost";

		/*--- announce ---*/
		System.out.println("RoboComm Terminate");

		/*--- check arguments ---*/
		if (args.length == 1)
			{
			server_host = args[0];
			}
                else if (args.length > 1)
                        {
			System.out.println("usage: Terminate server_host");
			System.exit(1);
                        }

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
		System.out.println("Started on "+host_name);

		/*--- try to connect ---*/
		Socket sock = null;
		try
			{
			sock = new Socket(server_host,RoboComm.LISTEN_PORT);
			}
		catch (IOException e)
			{
			System.out.println("Unable to connect to "+server_host+
				" on port "+RoboComm.LISTEN_PORT+" due to ");
			System.out.println(e);
			System.out.println("Maybe the "+
				"RoboComm server is not running.");
			System.exit(1);
			}
		System.out.println("Connected.");

		try
			{ 
			ObjectOutputStream p = 
				new ObjectOutputStream(
					new BufferedOutputStream(
						sock.getOutputStream()));

			TerminateMessage m = new TerminateMessage();
			p.writeObject(m);
			p.flush();
			}
		catch (Exception e){}
		try{Thread.sleep(2000);}
		catch(Exception e){}
		}
	}

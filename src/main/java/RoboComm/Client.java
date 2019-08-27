/*
 * Client.java
 */

package RoboComm;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import EDU.gatech.cc.is.communication.*;

/**
 * Robot communication demonstration client.
 * <P>
 * To run this program type "java RoboComm.Client".
 * <P>
 * For more detailed information, see the
 * <A HREF="../../../../../RoboComm/docs/index.html">RoboComm page</A>.
 * <P>
 * Copyright (c)1998 Tucker Balch, all rights reserved.
 *
 * @author Tucker Balch
 * @version $Revision: 1.1.1.1 $
 */

public class Client
	{
        /**
	 * Main for Client.
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
                Transceiver t = new TransceiverHard(server_host, num);
		Enumeration messages = t.getReceiveChannel();

		/*--- send a few messages ---*/
		try
			{
			t.broadcast(new StringMessage("Hello everyone!"));
			t.unicast(1, new StringMessage("Hello number one"));
			}
		catch (Exception e){}

		/*--- loop to receive messages ---*/
		while (t.connected())
			{
			while (messages.hasMoreElements())
				System.out.print(messages.nextElement());
			try {Thread.sleep(100);}
			catch (Exception e){}
			}
                }
        }

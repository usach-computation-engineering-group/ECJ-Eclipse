/*
 * ClientWaiter.java
 */

package RoboComm;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import EDU.gatech.cc.is.communication.*;
import EDU.gatech.cc.is.util.*;

/**
 * Robot communication demonstration client.
 * <P>
 * To run this program type "java RoboComm.ClientWaiter".
 * <P>
 * For more detailed information, see the
 * <A HREF="../../../../../RoboComm/docs/index.html">RoboComm page</A>.
 * <P>
 * Copyright (c)1998 Tucker Balch, all rights reserved.
 *
 * @author Tucker Balch
 * @version $Revision: 1.1.1.2 $
 */

public class ClientWaiter
	{
        /**
	 * Main for ClientWaiter.
         */
	public static void main(String[] args)
		{
		String server_host = "localhost";
		long sum = 0;
		double num = 0;

                /*--- announce ---*/
                System.out.println("TransceiverHard demonstration");

                /*--- check arguments ---*/
                if (args.length == 1)
                        {
                        server_host = args[0];
                        }
                else 
                        {
                        System.out.println("usage: ClientWaiter server_host");
                        System.exit(1);
                        }

                /*--- make the transceiver ---*/
                Transceiver t = new TransceiverHard(server_host, 2);
		CircularBufferEnumeration messages = t.getReceiveChannel();

		/*--- loop to receive messages ---*/
		Message msg = null;
		boolean done = false;
		System.out.println(messages.waitMatchingElement(
				(new LongMessage()).getClass()));
		while (!done)
			{
			while (messages.hasMoreElements())
				{
				msg = (Message)messages.nextElement();
				if (msg instanceof TerminateMessage)
					done = true;
				else if (msg instanceof LongMessage)
					{
					sum += System.currentTimeMillis() -
					   ((LongMessage)msg).val;
					num++;
					}
				}
			try {Thread.yield();}
			catch (Exception e){}
			}
		System.out.println(num+" messages "+sum+" ms "+
			(((double)sum)/num) + " avg");
		System.exit(0);
		}
	}
                

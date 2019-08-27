/*
 * ClientSender.java
 */

package RoboComm;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import EDU.gatech.cc.is.communication.*;


/**
 * Robot communication demonstration client.
 * <P>
 * To run this program type "java RoboComm.ClientSender".
 * <P>
 * For more detailed information, see the
 * <A HREF="../../../../../RoboComm/docs/index.html">RoboComm page</A>.
 * <P>
 * Copyright (c)1998 Tucker Balch, all rights reserved.
 *
 * @author Tucker Balch
 * @version $Revision: 1.1.1.1 $
 */

public class ClientSender
	{
        /**
	 * Main for ClientSender.
         */
	public static void main(String[] args)
		{
		String server_host = "localhost";
		int d = 0;
		long delay = 0;

                /*--- announce ---*/
                System.out.println("ClientSender demonstration");

                /*--- check arguments ---*/
                try
                        {
                        d = Integer.parseInt(args[0]);
			delay = (long) d;
                	System.out.println("delay: "+delay+" ms");
                        }
                catch (Exception e)
                        {
                        System.out.println(
                                "usage: ClientSender delaymillis server_host");
                        System.exit(1);
                        }
                if (args.length == 2)
                        {
                        server_host = args[1];
                        }
                else 
                        {
                        System.out.println(
                                "usage: ClientSender delaymillis server_host");
                        System.exit(1);
                        }

                /*--- make the transceiver ---*/
                TransceiverHard t = new TransceiverHard(server_host, 1);

		/*--- send a few messages ---*/
		LongMessage msg = new LongMessage(System.currentTimeMillis());
		for(int i = 0; i < 100; i++)
			{
			msg = new LongMessage(System.currentTimeMillis());
			try
				{
				t.unicast(2, msg);
				}
			catch (Exception e){System.out.println(e);}
			try { Thread.sleep(delay); }
			catch (Exception e){System.out.println(e);}
			}

		/*--- tell receiver we're done ---*/
		TerminateMessage tmsg = new TerminateMessage();
		try {t.unicast(2, tmsg); }
		catch (Exception e){}

		/*--- tell the transceiver to quit ---*/
		t.quit();
		System.exit(0);
                }
        }

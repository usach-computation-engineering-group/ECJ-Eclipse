/*
 * Message.java
 */

package EDU.gatech.cc.is.communication;

import java.io.*;


/**
 * message communicated to/from a robot.
 * <P>
 * <A HREF="../COPYRIGHTTB.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class Message implements Cloneable, Serializable
	{
	/**
	 * Message type.
	 */
        public	final static int BROADCAST = 0;

	/**
	 * Message type.
	 */
        public	final static int MULTICAST = 1;

	/**
	 * Message type.
	 */
        public	final static int UNICAST = 2;

	/**
	 * the type of message.  Default is BROADCAST.
	 */
        public	int	type = BROADCAST;

	/**
	 * the ID of the sender.  Ranges from 1 to the number of agents in
	 * the group.
	 */
        public	int	sender = 0;

	/**
	 * IDs of the receivers.  Ignored for broadcast messages.
	 */
        public	int[]	receivers = new int[0];

	/**
	 * create a Message with default values.
	 */
        public  Message() 
		{
		}


	/**
	 * return a printable String representation of the Message.
	 * @return the String representation
	 */
        public  String	toString()
		{
		return(paramString());
		}


	/**
	 * return a printable String representation of the Message.
	 * @return the String representation
	 */
        public  String	paramString()
		{
		String retval =  "sender: " + sender + "\n";
		return(retval);
		}


	/**
	 * clone the message
	 * @return the clone
	 * @exception CloneNotSupportedException if it isn't supported.
	 */
        public  Object	clone() throws CloneNotSupportedException
		{
		return(super.clone());
		}


	/**
	 * test the Message class.
	 */
        public  static void main(String[] args)
		{
		Message fred = new Message();
		fred.sender = 5;

		System.out.println("The original message:");
		System.out.println(fred);

		try
			{
			// Serialize it to a file
        		FileOutputStream ostream = 
				new FileOutputStream("t.tmp");
        		ObjectOutputStream p = 
				new ObjectOutputStream(ostream);
        		p.writeObject(fred);
        		p.flush();
        		ostream.close();

			// read it back out
			FileInputStream istream = new FileInputStream("t.tmp");
			ObjectInputStream p2 = new ObjectInputStream(istream);
			Message today = (Message)p2.readObject();
			istream.close();

			// print it
			System.out.println("The remorphed message:");
			System.out.println(today);
			}
		catch(IOException e){}
		catch(ClassNotFoundException e){}
		try
			{
			Message fred2 = (Message)fred.clone();
			System.out.println("The cloned message:");
			System.out.println(fred2);
			}
		catch(CloneNotSupportedException e){System.out.println(e);}
		}
	}

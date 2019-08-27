/*
 * RegisterMessage.java
 */

package EDU.gatech.cc.is.communication;

import java.io.*;
import EDU.gatech.cc.is.util.*;


/**
 * A message to register the client with the server.  For use internally by
 * RoboComm, not normally used at the application level.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class RegisterMessage extends Message 
	implements Cloneable, Serializable
	{
	/**
	 * Our ID to register.
	 */
        public	int	val = 0;


        /**
         * create a RegisterMessage with default value.
         */
        public  RegisterMessage()
                {
		super();
                }


        /**
         * create a RegisterMessage with specific id.
	 * @param n int, the id of the sender.
         */
        public  RegisterMessage(int i)
                {
		super();
		val = i;
                }


        /**
         * return a printable String representation of the RegisterMessage.
         * @return the String representation
         */
        public  String  paramString()
                {
                return( super.paramString() 
			+ "val: " + val + "\n");
                }


        /**
         * test the RegisterMessage class.
         */
        public  static void main(String[] args)
                {
                RegisterMessage fred = new RegisterMessage();
                System.out.println("The original message:");
                System.out.println(fred);
                }
	}

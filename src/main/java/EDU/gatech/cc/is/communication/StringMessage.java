/*
 * StringMessage.java
 */

package EDU.gatech.cc.is.communication;

import java.io.*;
import EDU.gatech.cc.is.util.*;


/**
 * A String message communicated to/from a robot.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class StringMessage extends Message 
	implements Cloneable, Serializable
	{
	/**
	 * the string to send.
	 */
        public	String	val = "empty";


        /**
         * create a StringMessage with default values.
         */
        public  StringMessage()
                {
		super();
                }


        /**
         * create a StringMessage
	 * @param v String, the string to send.
         */
        public  StringMessage(String v)
                {
		super();
		val = v;
                }


        /**
         * return a printable String representation of the StringMessage.
         * @return the String representation
         */
        public  String  paramString()
                {
                return( super.paramString() 
			+ "val: " + val + "\n");
                }


        /**
         * test the StringMessage class.
         */
        public  static void main(String[] args)
                {
                StringMessage fred = new StringMessage();
                System.out.println("The original message:");
                System.out.println(fred);
                }
	}

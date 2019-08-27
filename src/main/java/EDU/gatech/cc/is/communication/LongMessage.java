/*
 * LongMessage.java
 */

package EDU.gatech.cc.is.communication;

import java.io.*;
import EDU.gatech.cc.is.util.*;


/**
 * A message containing a long communicated to/from a robot.
 * <P>
 * <A HREF="../COPYRIGHTTB.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class LongMessage extends Message 
	implements Cloneable, Serializable
	{
	/**
	 * the long to send.
	 */
        public	long val = 0;


        /**
         * create a LongMessage with default value.
         */
        public  LongMessage()
                {
		super();
                }


        /**
         * create a LongMessage with specified value.
	 * @param v long, the long to send.
         */
        public  LongMessage(long v)
                {
		super();
		val = v;
                }


        /**
         * return a printable String representation of the LongMessage.
         * @return the String representation
         */
        public  String  paramString()
                {
                return( super.paramString() 
			+ "long: " + val + "\n");
                }


        /**
         * test the LongMessage class.
         */
        public  static void main(String[] args)
                {
                StringMessage fred = new StringMessage();
                System.out.println("The original message:");
                System.out.println(fred);
                }
	}

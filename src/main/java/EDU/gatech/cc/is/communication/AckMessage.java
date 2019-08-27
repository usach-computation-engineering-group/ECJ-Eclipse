/*
 * AckMessage.java
 */

package EDU.gatech.cc.is.communication;

import java.io.*;
import EDU.gatech.cc.is.util.*;


/**
 * An acknowledgement message.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class AckMessage extends Message 
		implements Cloneable, Serializable
	{
        /**
         * create an AckMessage with default values.
         */
        public  AckMessage()
                {
		super();
                }


        /**
         * return a printable String representation of the AckMessage.
         * @return the String representation
         */
        public  String  paramString()
                {
                return( super.paramString() );
                }


        /**
         * test the AckMessage class.
         */
        public  static void main(String[] args)
                {
                AckMessage fred = new AckMessage();
                System.out.println("The original message:");
                System.out.println(fred);
                }
	}

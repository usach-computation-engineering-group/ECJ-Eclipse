/*
 * ReinforcementMessage.java
 */

package EDU.gatech.cc.is.communication;

import java.io.*;
import EDU.gatech.cc.is.util.*;


/**
 * Messages concerning reinforcement communicated to/from a robot.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class ReinforcementMessage extends Message 
	implements Cloneable, Serializable
	{
	/**
	 * the reinforcement signal.
	 */
        public	double	val=0;


        /**
         * create a ReinforcementMessage with default values.
         */
        public  ReinforcementMessage()
                {
		super();
                }


        /**
         * return a printable String representation of the ReinforcementMessage.
         * @return the String representation
         */
        public  String  paramString()
                {
                return( super.paramString() 
			+ "val: " + val + "\n");
                }


        /**
         * test the ReinforcementMessage class.
         */
        public  static void main(String[] args)
                {
                ReinforcementMessage fred = new ReinforcementMessage();
                System.out.println("The original message:");
                System.out.println(fred);
                }
	}

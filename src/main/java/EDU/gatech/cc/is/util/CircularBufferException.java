/*
 * CircularBufferException.java
 */

package EDU.gatech.cc.is.util;

/**
 * Signals that an execption of some sort has occured in a FunctionApproximator.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch (tucker@cc.gatech.edu)
 * @version $Revision: 1.2 $
 */

public class CircularBufferException extends Exception 
	{


    	/**
     	 * Constructs a <code>CircularBufferException</code> with no
     	 * detail message. 
     	 */
    	public CircularBufferException() 
		{
        	super();
    		}

    	/**
     	 * Constructs a <code>CircularBufferException</code> with a
     	 * specified detail message.
     	 *
     	 * @param      s  the detail message.
     	 */
     	public CircularBufferException( String msg ) 
		{
         	super(msg);
     		}


	}

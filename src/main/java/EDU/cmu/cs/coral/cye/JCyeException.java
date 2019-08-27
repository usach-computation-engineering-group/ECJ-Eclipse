/*
 * JCyeException.java
 */

package EDU.cmu.cs.coral.cye;

/**
 * Signals that an execption of some sort has occured in a JCye object.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class JCyeException extends Exception 
	{

	/**
	 * Constructs an <code>JCyeException</code> with no
	 * detail message.
	 */
	public JCyeException() 
		{
        	super();
    		}

	/**
	 * Constructs an <code>FunctionApproximatorException</code> with a
	 * specified detail message.
	 *
	 * @param s the detail message.
	 */
	 public JCyeException( String msg ) 
		{
         	super(msg);
     		}
	}

/*
 * CommunicationException.java
 */

package EDU.gatech.cc.is.communication;


/**
 * Signals that an execption of some sort has occured in a communication
 * event.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 tucker Balch
 *
 * @author Tucker Balch (tucker@cc.gatech.edu)
 * @version $Revision: 1.1 $
 */

public class CommunicationException extends Exception 
	{
	/**
 	 * Constructs a <code>CommunicationException</code> with no
 	 * detail message. 
 	 */
	public CommunicationException()
		{
		super();
		}


	/**
	 * Constructs a <code>CommunicationException</code> with a
	 * specified detail message.
	 *
	 * @param      s  the detail message.
	 */
	public CommunicationException( String msg )
		{
		super(msg);
		}
	}

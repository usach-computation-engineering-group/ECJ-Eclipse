/*
 * Newton.java
 */

package EDU.gatech.cc.is.newton;


/**

<B>Introduction</B><BR>
Provides an interface to the Newton Research Labs' Cognachrome
Vision System.  See Newton's Users's Guide for details on their hardware.
<P>
<B>Frame of Reference</B><BR>
If (X,Y) is the center of a colored blob in the image, X is the column, 
numbered from 0 on the left to 200 on the right.  Y is the row, from
0 at the top to 255 on the bottom.
<P>
@author (c)1997 Tucker Balch & David Huggins, All Rights Reserved
@version July 1997
*/

public class Newton
	{
	static
		{
		System.loadLibrary("newton");
		}

	/**
	One of newton's 3 color channels
	@see Newton
	*/
	public static final int CHANNEL_A = 0;
	/**
	One of newton's 3 color channels
	@see Newton
	*/
	public static final int CHANNEL_B = 1;
	/**
	One of newton's 3 color channels
	@see Newton
	*/
	public static final int CHANNEL_C = 2;
	public static final int NEWTON_SUCCESS      =  1;
	public static final int NEWTON_FAILURE      = -1;
	public static final int NEWTON_IO_FAILURE   = -2;
	public static final int NEWTON_UNINITIALIZED= -3;


	/**
	Instantiate a <B>newton.Newton</B> object.  You should only
	instantiate one of these per newton board connected to your
	computer.  Configures the board with default values.
	Standard call is Newton(3,38400);
	@param serial_port 1 = ttys0 (COM1), 2 = ttys1 (COM2) ...
	@param baud (IGNORED FOR NOW!) baud rate for communication.
	@exception Exception If unable to make native code resident.
	*/
	public Newton(int serial_port, int baud) throws Exception
		{
		/*
		 * Ensure the static variables in the native
		 * methods are not freed by the garbage collector.
		 * This is undone in the distructor.
		 */
		int ret_val = make_resident();
		if (ret_val == 1)
			/*
			 * Problem with make_resident.
			 */
			throw new Exception("Error in attempt to make native code resident.");
		ret_val = open_newton(serial_port, baud);
		if (ret_val != NEWTON_SUCCESS)
			/*
			 * Problem with open_newton.
			 */
			throw new Exception("Error in attempt to connect to the newton.");
		}


	/**
	Dispose of a <B>newton.Newton</B> object.  You should never
	call this, the garbage collector calls it.
	@exception Exception If unable to release code resources.
	@exception Throwable If super.finalize() throws it.
	*/
	protected void finalize() throws Exception, Throwable
		{
		super.finalize();
		/*
		 * Release static variables in the native
		 * methods.
		 */
		int ret_val = make_free();
		if (ret_val == 1)
			/*
			 * Problem with make_free.
			 */
			throw new Exception("Error in attempt to release native code residency.");
		}


	/**
	Native code that twiddles stuff to ensure the
	statics in the native code are not freed by the garbage collector.
	They are freed by <B>make_free()</B>.
	This routine is only used internally by the Newton class.
	@return 0 on success 1 otherwise.
	*/
	private native int make_resident();


	/**
	Native code that releases the resources so the memory used by 
	the native code can be collected by the garbage collector.
	They are reserved by <B>make_resident()</B>.
	This routine is only used internally by the Newton class.
	@return 0 on success 1 otherwise.
	*/
	private native int make_free();


	/**
	Opens up the serial port to the newton and initializes things.
	@param serial_port 1 = COM1(Win),ttya(Sun),ttys0(linux).
	@param baud baud rate for communication.
	@return 0 on success 1 otherwise.
	*/
	private native int open_newton(int serial_port, 
		int baud);


	/**
	Reads one frame of visual data from the newton. Use
	the get* methods to retrieve the data.
	@see getNumVis
	@see getX
	@see getY
	@see getArea
	*/
	public native void read_frame();


	/**
	Reports the number of items visible on a certain channel.
	Use the get* methods to retrieve the data.
	@param channel the channel to read (e.g. CHANNEL_A).
	@returns the number of items visible on that channel.
	@see getX
	@see getY
	@see getArea
	*/
	public native int getNumVis(int channel);


	/**
	Reports the X values of the visible blobs on a particular
	channel.  X ranges from 0 to 200 (left to right)
	Use getNumVis first to size your array.
	and represents the column of the center of a blob.
	@param channel the channel to read (e.g. CHANNEL_A).
	@param readings an array in which to store the X values.
	@returns the number of items visible on that channel.
	@see getY
	@see getArea
	*/
	public native void getX(int channel, int values[]);


	/**
	Reports the Y values of visible blobs on a particular
	channel.  Y ranges from 0 to 255 (top to bottom)
	and represents the row of the center of a blob.
	Use getNumVis first to size your array.
	@param channel the channel to read (e.g. CHANNEL_A).
	@param readings an array in which to store the Y values.
	@returns the number of items visible on that channel.
	@see getX
	@see getArea
	@see getNumVis
	*/
	public native void getY(int channel, int values[]);


	/**
	Reports the square root of the area in pixels values 
	of visible blobs on a particular
	channel.
	Use getNumVis first to size your array.
	@param channel the channel to read (e.g. CHANNEL_A).
	@param readings an array in which to store the area values.
	@returns the number of items visible on that channel.
	@see getX
	@see getY
	@see getNumVis
	*/
	public native void getArea(int channel, int values[]);
	}


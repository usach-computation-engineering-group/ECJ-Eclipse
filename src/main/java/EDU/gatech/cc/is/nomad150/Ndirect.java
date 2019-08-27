/*
 * Ndirect.java
 */

package EDU.gatech.cc.is.nomad150;


/**
 * <B>Introduction</B><BR>
 * Provides an interface to the Nomadics Technologies control library
 * for a Nomad 150 robot.  It provides a slightly higher-level
 * interface than the C library functions provided by Nomadics.  
 * See the Nomadic's Language Reference Manual for details.
 * <P>
 * Two demonstration/debugging applications are also provided:
 * Nomad150TestTriangle and Nomad150TestSensors.   Nomad150TestTriangle
 * drives the robot around in a 5 foot triange, while Nomad150TestSensors
 * prints out detected sensor values.  See documentation
 * for them by clicking on the appropriate link below.
 * @author (c) 1997, 1998  Tucker Balch, tucker@cc.gatech.edu
 * @version $revision$
 * @see Nomad150TestTriangle
 * @see Nomad150TestSensors
 */

public class Ndirect
	{
	static
		{
		System.loadLibrary("Ndirect");
		}

	/**
	 * A control law for mv().
	 * @see Ndirect#mv
	 */
	public static final int MV_IGNORE = 0;

	/**
	 * A control law for mv().
	 * @see Ndirect#mv
	 */
	public static final int MV_VM = 1;

	/**
	 * A control law for mv().
	 * @see Ndirect#mv
	 */
	public static final int MV_PR = 2;

	/**
	 * A control law for mv().
	 * @see Ndirect#mv
	 */
	public static final int MV_LP = 3;

	/**
	 * A control law for mv().
	 * @see Ndirect#mv
	 */
	public static final int MV_AC = 4;

	/**
	 * A control law for mv().
	 * @see Ndirect#mv
	 */
	public static final int MV_SP = 5;

	/**
	 * Time in microseconds for servo pulse.
	 */
	public static final int SERVO_TIME	= 15000;

	/**
	 * Time in microseconds for full CW rotation of servo.
	 */
	public static final int SERVO_CW_TIME	= 500;

	/**
	 * Time in microseconds for full CCW rotation of servo.
	 */
	public static final int SERVO_CCW_TIME	= 2000;

	public static final int MV_PWM_LOW_0	=10;
	public static final int MV_PWM_HIGH_0	=11;
	public static final int MV_PWM_LOW_1	=12;
	public static final int MV_PWM_HIGH_1	=13;
	public static final int MV_PWM_LOW_2	=14;
	public static final int MV_PWM_HIGH_2	=15;
	public static final int MV_PWM_LOW_3	=16;
	public static final int MV_PWM_HIGH_3	=17;

	/**
	 * Instantiate a <B>nomad150.Ndirect</B> object.  You should only
	 * instantiate one of these per robot connected to your
	 * computer.  Configures the robot with default values
	 * with calls to zr(), ac(), conf_sn() conf_tm().
	 * Standard call is Ndirect(1,38400);
	 * @param serial_port 1 = ttys0 (COM1), 2 = ttys1 (COM2) ...
	 * @param baud baud rate for communication.
	 * @exception Exception If unable to make native code resident.
	 */
	public Ndirect(int serial_port, int baud) throws Exception
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
		ret_val = open_robot(serial_port, baud);
		if (ret_val == 1)
			/*
			 * Problem with open_robot.
			 */
			throw new Exception("Error in attempt to connect to the robot.");
		}


	/**
	 * Dispose of a <B>nomad150.Ndirect</B> object.  You should never
	 * call this, the garbage collector calls it.
	 * @exception Exception If unable to release code resources.
	 * @exception Throwable If super.finalize() throws it.
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
	 * Native code that twiddles stuff to ensure the
	 * statics in the native code are not freed by the garbage collector.
	 * They are freed by <B>make_free()</B>.
	 * This routine is only used internally by the Ndirect class.
	 * @return 0 on success 1 otherwise.
	 */
	private native int make_resident();


	/**
	 * Native code that releases the resources so the memory used by 
	 * the native code can be collected by the garbage collector.
	 * They are reserved by <B>make_resident()</B>.
	 * This routine is only used internally by the Ndirect class.
	 * @return 0 on success 1 otherwise.
	 */
	private native int make_free();


	/**
	 * Opens up the serial port to the robot and initializes things.
	 * @param serial_port 1 = COM1(Win),ttya(Sun),ttys0(linux).
	 * @param baud baud rate for communication.
	 * @return 0 on success 1 otherwise.
	 */
	private native int open_robot(int serial_port, 
		int baud);


	/**
	 * Defines the robot's steering and turret angles.
	 * @param th steering orientation.
	 * @param tu turret orientation.
	 * @return 0 on success 1 otherwise.
	 */
	public native int da(int th, int tu);


	/**
	 * Defines the position of the robot.
	 * @param x the X coordinate.
	 * @param y the Y coordinate.
	 * @return 0 on success 1 otherwise.
	 */
	public native int dp(int x, int y);


	/**
	 * Move the robot: translation, turret and steering all at once.
	 * The arguments are passed directly to Nomadics mv().  
	 * The control law for each axis must be MV_VM, MV_PR, MV_IGNORE,
	 * MV_LP, MV_SP or MV_AC. See Nomadic's Language Reference Manual 
	 * for details.
	 * @param t_mode control law for translation.
	 * @param t_mv motion value for translation.
	 * @param s_mode control law for steering.
	 * @param s_mv motion value for steering.
	 * @param r_mode control law for turret.
	 * @param r_mv motion value for turret.
	 * @return 0 on success 1 otherwise.
	 */
	public native int mv(
		int t_mode, 
		int t_mv, 
		int s_mode, 
		int s_mv, 
		int r_mode, 
		int r_mv);


	/**
	 * Stop the robot: translation, turret and steering all at once.
	 * @return 0 on success 1 otherwise.
	 */
	public native int st();


	/**
	 * Turns on the sonar range sensors.  Calls Nomadics conf_sn
	 * with a standard sonar firing order.
	 * @param delay The time between sonar firing in milliseconds.  
		The minimum is 60.
	 * @return 0 on success 1 otherwise.
	 */
	public native int sn_on(int delay);


	/**
	 * Turns on the sonar range sensors.  Calls Nomadics conf_sn
	 * with a standard sonar firing order.  Calls
	 * sn_on(60)
	 * @return 0 on success 1 otherwise.
	 */
	public int sn_on()
		{
		return(sn_on(60));
		}


	/**
	 * Turns off the sonar range sensors
	 * @return 0 on success 1 otherwise.
	 */
	public native int sn_off();


	/**
	 * Reads the sonars.
	 * The readings are in inches from the sonar "skin."  The sonars 
	 * are arranged in 22.5 degree increments CCW from 0 to 15.
	 * The data is refreshed either by a move command or
	 * a call to get_rc().
	 * @see Ndirect#get_rc
	 * @param readings The sonar readings will be read into this array.
			It must contain at least 16 elemements.
	 * @return 0 on success 1 otherwise.
 	 */
	public native int get_sn(int readings[]);


	/**
	 * Reads all the sensor data from the robot.
	 * @see Ndirect#get_rc
	 * @return 0 on success 1 otherwise.
	 */
	public native int gs();


	/**
	 * Reads the bumpers.
	 * The data is refreshed either by a move command or
	 * a call to get_rc().
	 * @see Ndirect#get_rc
	 * @return the bumper data.
	 */
	public native long get_bp();


	/**
	 * Gets an update of the sonar, X, Y, turret and steering
	 * data.  This method does not actually return this data, you
	 * have to call one of the various accessor functions to get it.
	 * @return 0 on success 1 otherwise.
	 * @see Ndirect#get_x
	 * @see Ndirect#get_y
	 * @see Ndirect#get_steering
	 * @see Ndirect#get_turret
	 */
	public native int get_rc();


	/**
	 * Get the robot's X coordinate, call get_rc() or mv() first
	 * to ensure this data is current.
	 * @return the X coordinate in tenths of an inch.
	 * @see Ndirect#get_rc
	 */
	public native int get_x();


	/**
	 * Get the robot's Y coordinate, call get_rc() or mv() first
	 * to ensure this data is current.
	 * @return the Y coordinate in tenths of an inch.
	 * @see Ndirect#get_rc
	 */
	public native int get_y();


	/**
	 * Get the robot's steering heading, call get_rc() or mv() first
	 * to ensure this data is current.
	 * @return the steering heading in tenths of a degree.
	 * @see Ndirect#get_rc
	 */
	public native int get_steering();


	/**
	 * Get the robot's turret heading, call get_rc() or mv() first
	 * to ensure this data is current.
	 * @return the turret heading in tenths of a degree.
	 * @see Ndirect#get_rc
	 */
	public native int get_turret();


	/**
	 * Gets an update of the robot's translation, turret and steering
	 * velocities.  This method does not actually return this data, you
	 * have to call the various accessor functions to get it.
	 * @return 0 on success 1 otherwise.
	 * @see Ndirect#get_vtranslation
	 * @see Ndirect#get_vsteering
	 * @see Ndirect#get_vturret
	 */
	public native int get_rv();


	/**
	 * Get the robot's translational velocity, call get_rv() or mv() first
	 * to ensure this data is current.
	 * @return the translational velocity in tenths of an inch/s.
	 * @see Ndirect#get_rv
	 */
	public native int get_vtranslation();


	/**
	 * Get the robot's turret velocity, call get_rv() or mv() first
	 * to ensure this data is current.
	 * @return the turret velocity in tenths of a degree/s.
	 * @see Ndirect#get_rv
	 */
	public native int get_vturret();


	/**
	 * Get the robot's steering velocity, call get_rv() or mv() first
	 * to ensure this data is current.
	 * @return the steering velocity in tenths of a degree/s.
	 * @see Ndirect#get_rv
	 */
	public native int get_vsteering();

	}


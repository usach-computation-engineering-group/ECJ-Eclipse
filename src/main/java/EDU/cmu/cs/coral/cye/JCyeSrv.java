package EDU.cmu.cs.coral.cye;
import java.io.*;
import Serialio.*;

/** 
 * The JCyeSrv class acts as an interactive server for the Cye mobile robot.
 * All user interaction with Cye should take place through this class.
 * 
 * @author Brian J. Chemel
 *
 * @see edu.cmu.cs.JavaCye.JCyeComm
 * @see edu.cmu.cs.JavaCye.JCyeMsg
 * @see edu.cmu.cs.JavaCye.JCyeStatus
 **/
public class JCyeSrv {
    /*
     * Useful Constants
     */
    private final static int NHEADINGS = 1160;
    private final static double TPI = 10.0;
    private final static double ENCODER_TPI = 12.20187;
    
    /*
     * Class Data
     */
    private JCyeComm comm;
    private byte id;
    
    /**
     * The constructor for class <code>JCyeSrv</code>.
     * Creates a new <code>JCyeComm</code> object to handle robot interaction, 
     * and issues a <code>start</code> command to it.
     *
     * @param d the device to open to the robot.
     * @param b the baud rate to open to the robot.
     * @param c connection to the robot (WIRED or OLD_RADIO or NEW_RADIO).
     * @param i the id number of the robot.
     */
    public JCyeSrv(String d, int b, int c, byte i)
    {
        id = i;
	try
		{
		comm = new JCyeComm(d,b,c,i);
		}
	catch (JCyeException e)
		{
		System.err.println(e);
		System.exit(1);
		}
		
	comm.start();
    }

    /**
     * Wait for ms milliseconds
     *
     * @param ms the number of milliseconds to wait
     */
    public void Wait(int ms)
    {
	long endTime = System.currentTimeMillis() + ms;
	long curTime;
	do {
	    curTime = System.currentTimeMillis();
	} while (curTime < endTime);
    }

    /**
     * Sends a <code>JCyeMsg</code> to Cye using the <code>JCyeComm</code> object
     *
     * @param Msg the message (of type <code>JCyeMsg</code>) to be sent
     *
     * @return the <code>JCyeMsg</code> reply from Cye
     */
    public JCyeMsg SendMsg(JCyeMsg Msg)
    {
	return comm.SendMsg(Msg);
    }

    /**
     * Instruct Cye to move to a specified location
     *
     * @param x the desired X position, in world coordinates(inches)
     * @param y the desired Y position, in world coordinates(inches)
     */
    public void SendPositionDestination(double x, double y)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	int X = worldToRobot(x);
	int Y = worldToRobot(y);

	Msg.Add(Msg.CMD_SET_DEST);

	Msg.Add(InttoByte(X,0));
	Msg.Add(InttoByte(X,1));
	Msg.Add(InttoByte(X,2));
	Msg.Add(InttoByte(X,3));
	Msg.Add(InttoByte(Y,0));
	Msg.Add(InttoByte(Y,1));
	Msg.Add(InttoByte(Y,2));
	Msg.Add(InttoByte(Y,3));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Instruct Cye to set the motors to specific PWM values
     *
     * @param m0PWM the PWM value for Motor 0 (right)
     * @param m1PWM the PWM value for Motor 1 (left)
     */
    public void SendMotorsPWM(int m0PWM, int m1PWM)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_SET_MOTORS_PWM);

	Msg.Add(InttoByte(m0PWM, 0));
	Msg.Add(InttoByte(m0PWM, 1));
	Msg.Add(InttoByte(m1PWM, 0));
	Msg.Add(InttoByte(m1PWM, 1));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Instruct Cye to stop its motors.
     */
    public void SendStopMotors()
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_STOP_MOTORS);

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Instruct Cye to rotate to a specifed heading at a specified velocity.
     *
     * @param heading the commanded heading, in world coordinates (radians).
     * @param velocity the velocity at which to turn.
     */
    public void SendHeadingDestination(double heading, int velocity)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	int Heading = radianToEncoder(heading);

	Msg.Add(Msg.CMD_SET_HEADING_DEST);

	Msg.Add(InttoByte((int)Heading,0));
	Msg.Add(InttoByte((int)Heading,1));
	Msg.Add(InttoByte(velocity,0));
	Msg.Add(InttoByte(velocity,1));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Instruct Cye to set the maximum PWM value. I have no idea what this does.
     *
     * @param MaxPWM the maximum PWM value...????
     */
    public void SendMaxPWM(int MaxPWM)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_SET_MAXPWM);

	Msg.Add(InttoByte(MaxPWM, 0));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Instruct Cye to set the internal heading correction factor.
     *
     * @param Correction the correction value
     */
    public void SendHeadingCorrectionFactor(int Correction)
    {
      JCyeMsg Msg = new JCyeMsg(id);
      
      Msg.Add(Msg.CMD_SET_HEADING_CORRECT_FACTOR);

      Msg.Add(InttoByte(Correction, 0));
      Msg.Add(InttoByte(Correction, 1));

      JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Instruct Cye to move with specified motor velocities.
     *
     * @param M0Vel the velocity command value for Motor 0 (right)
     * @param M1Vel the velocity command value for Motor 1 (left)
     */
    public void SendMotorVelocities(int M0Vel, int M1Vel)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_SET_MOTORS_VELOCITY);
	
	Msg.Add(InttoByte(M0Vel, 0));
	Msg.Add(InttoByte(M0Vel, 1));
	Msg.Add(InttoByte(M1Vel, 0));
	Msg.Add(InttoByte(M1Vel, 1));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Tell Cye its true position, overwriting dead-reckoned value.
     *
     * @param x Cye's true X position, in world coordinates (inches)
     * @param y Cye's true Y position, in world coordinates (inches)
     */
    public void SendPosition(double x, double y)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	int X = worldToRobot(x);
	int Y = worldToRobot(y);

	Msg.Add(Msg.CMD_SET_POSITION);

	Msg.Add(InttoByte(X,0));
	Msg.Add(InttoByte(X,1));
	Msg.Add(InttoByte(X,2));
	Msg.Add(InttoByte(X,3));
	Msg.Add(InttoByte(Y,0));
	Msg.Add(InttoByte(Y,1));
	Msg.Add(InttoByte(Y,2));
	Msg.Add(InttoByte(Y,3));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Tell Cye its true heading, overwriting dead-reckoned value.
     *
     * @param H Cye's true heading, in world coordinates (radians)
     */
    public void SendHeading(double h)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	int H = radianToEncoder(h);

	Msg.Add(Msg.CMD_SET_HEADING);

	Msg.Add(InttoByte(H,0));
	Msg.Add(InttoByte(H,1));
	Msg.Add(InttoByte(H,2));
	Msg.Add(InttoByte(H,3));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Instruct Cye to set its maximum speed. I have no idea what this does either.
     *
     * @param MaxSpeed the maximum speed value
     */
    public void SendMaxSpeed(int MaxSpeed)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_SET_MAXSPEED);

	Msg.Add(InttoByte(MaxSpeed, 0));
	Msg.Add(InttoByte(MaxSpeed, 1));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Instruct Cye to move to a specific (x,y) position at a specified velocity.
     *
     * @param x the commanded x position, in world coordinates (inches)
     * @param y the commanded y position, in world coordinates (inches)
     * @param v the velocity at which to move
     */
    public void SendPositionVelocityDestination(double x, double y, int v)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	int X = worldToRobot(x);
	int Y = worldToRobot(y);

	Msg.Add(Msg.CMD_SET_POSITIONVELOCITY_DEST);

	Msg.Add(InttoByte(X,0));
	Msg.Add(InttoByte(X,1));
	Msg.Add(InttoByte(X,2));
	Msg.Add(InttoByte(X,3));
	Msg.Add(InttoByte(Y,0));
	Msg.Add(InttoByte(Y,1));
	Msg.Add(InttoByte(Y,2));
	Msg.Add(InttoByte(Y,3));
	Msg.Add(InttoByte(v,0));
	Msg.Add(InttoByte(v,1));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Instruct Cye to move straight with a specified velocity at a specified heading.
     *
     * @param v the commanded velocity
     * @param h the commanded heading, in world coordinates (radians)
     */
    public void SendStraightVelocity(int v, double h)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	int H = radianToEncoder(h);

	Msg.Add(Msg.CMD_GO_STRAIGHT_VELOCITY);

	Msg.Add(InttoByte(v, 0));
	Msg.Add(InttoByte(v, 1));
	Msg.Add(InttoByte(H, 0));
	Msg.Add(InttoByte(H, 1));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Not implemented. What does this do?
     */
    public void SendSensitiveObstacleDetection(int Sensitive)
    {

    }

    /**
     * Not implemented. What does this do?
     */
    public void SendTurn(double Heading, int Velocity, int Diameter)
    {

    }

    /**
     * Not implemented. What does this do?
     */
    public void SendMotorControlConstants(byte Kp, byte Ki, byte Kd, byte Kb, byte Kpwm)
    {

    }

    /**
     * Set the length of the "bungee-cord" virtual handle on the front of Cye.
     *
     * @param Length the length of the handle
     */
    public void SendHandleLength(int Length)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_SET_HANDLE_LENGTH);

	Msg.Add(InttoByte(Length, 0));
	Msg.Add(InttoByte(Length, 1));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Turn on or off Cye's buzzer.
     *
     * @param OnOff duh
     */
    public void SendBuzzerOn(boolean OnOff)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_BUZZER_ON);

	byte b = (byte)0;
	if(OnOff == true) b = (byte)1;
	Msg.Add(b);

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /** 
     * Wanna play a tune? Set the buzzer frequency to a specific value.
     *
     * @param Frequency the frequency
     */
    public void SendBuzzerFrequency(int Frequency)
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_BUZZER_FREQUENCY);

	Msg.Add(InttoByte(Frequency, 0));
	Msg.Add(InttoByte(Frequency, 1));

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Not implemented. What does this do?
     */
    public void SendVelocityDirection(double Heading, int Velocity)
    {

    }

    /**
     * Request state information from Cye.
     */
    public void SendRequestState()
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_REQUEST_STATE);

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Instruct Cye to commit ritual suicide.
     */
    public void SendPowerOff()
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_POWER_OFF);

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Send a poll message to the robot.
     * This normally does not need to be called by the user; automatic idle polling is implemented at a lower level.
     */
    public void SendPoll()
    {
	JCyeMsg Msg = new JCyeMsg(id);

	Msg.Add(Msg.CMD_POLL_REQUEST);

	JCyeMsg rxMsg = SendMsg(Msg);
    }

    /**
     * Return a specific byte (8 bits) ripped out of an integer (32 bits).
     *
     * @param l the integer
     * @param i the byte position (0..3)
     *
     * @return the specified byte from l
     */
    public byte InttoByte(int l, int i)
    {
	switch(i) {
	case 0:
	    return (byte) (l & 0xFF);
	case 1:
	    return (byte) ((l >> 8) & 0xFF);
	case 2:
	    return (byte) ((l >> 16) & 0xFF);
	case 3:
	    return (byte) ((l >> 24) & 0xFF);
	default:
	    return (byte)-1;
	}
    }

    /**
     * Return the last reported X position of Cye.
     *
     * @return the last known X position of Cye, according to its dead-reckoning,
     * in world coordinates (inches).
     */
    public double GetLastX()
    {
	int X = comm.GetLastX();

	return robotToWorld(X);
    }

    /**
     * Return the last reported Y position of Cye.
     *
     * @return the last known Y position of Cye, according to its dead-reckoning,
     * in world coordinates (inches).
     */
    public double GetLastY()
    {
	int Y = comm.GetLastY();

	return robotToWorld(Y);
    }

    /**
     * Return the last reported Heading of Cye.
     *
     * @return the last known Heading of Cye, according to its dead-reckoning,
     * in world coordinates (radians).
     */
    public double GetLastH()
    {
	int H = comm.GetLastH();

	return encoderToRadian(H);
    }

    /**
     * Return the last reported battery charge of Cye.
     *
     * @return the last known battery charge, in volts.
     */
    public double GetLastB()    {
	return comm.GetLastB();
    }

    /**
     * Return the state of Cye's obstacle detector.
     * Returns <code>true</code> if Cye has hit an obstacle since the last call to
     * <code>ClearObstacle</code>, false otherwise.
     *
     * @return the state of Cye's obstacle detector.
     */
    public boolean GetObstacle() {
	return comm.GetObstacle();
    }

    /**
     * Clears Cye's obstacle detector. Use this method to reset the obstacle detection
     * logic.
     */
    public void ClearObstacle() {
	comm.ClearObstacle();
    }

    /**
     * Convert a heading from world coordinates (radians) to robot coordinates (encoder values).
     *
     * @param h a heading in radians
     *
     * @return the heading converted to robot coordinates
     */
    public int radianToEncoder(double h)
    {
	return (int)((h * NHEADINGS)/(2.0 * Math.PI));
    }

    /**
     * Convert a heading from robot coordinates (encoder values) to world coordinates (radians).
     *
     * @param e a heading in robot coordinates
     *
     * @return the heading converted to radians
     */
    public double encoderToRadian(int e)
    {
	return ((e * 2.0 * Math.PI)/NHEADINGS);
    }

    /**
     * Convert a position value from robot coordinates (encoder ticks) to world coordinates (inches).
     *
     * @param r a position value in robot coordinates
     *
     * @return the position value converted to inches
     */
    public double robotToWorld(int r)
    {
	return ((r * TPI)/(10.0 * ENCODER_TPI));
    }

    /**
     * Convert a position value from world coordinates (inches) to robot coordinates (encoder ticks).
     *
     * @param w a position value in world coordinates (inches)
     *
     * @return the position value converted to robot coordinates
     */
    public int worldToRobot(double w)
    {
	return (int)((w * 10.0 * ENCODER_TPI)/TPI);
    }
}

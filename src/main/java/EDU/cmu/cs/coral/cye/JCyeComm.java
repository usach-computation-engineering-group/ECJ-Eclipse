package EDU.cmu.cs.coral.cye;
import java.io.*;
import Serialio.*;

/**
 * Handles all of the communication between the JCyeSrv class 
 * and the Cye robot.  Thread is extended, to enable the CyeComm 
 * object to run in the background and poll the robot at a set frequency.
 *
 * If you are creating an instance of this class manually in your code, you
 * are almost certainly doing something wrong.  This class should only 
 * be created as a side effect of instantiating a JCyeSrv object.
 *
 * @author Brian Chemel and Tucker Balch
 *
 * @see java.lang.Thread
 * @see EDU.cmu.cs.coral.cye.JCyeSrv
 * @see EDU.cmu.cs.coral.cye.JCyeMsg
 * @see EDU.cmu.cs.coral.cye.JCyeStatus
 */
public class JCyeComm extends Thread 
	{
	/*
	 * Useful Constants
	 */

	/**
	 * Indicates the connection to the robot is wired (no radio 
	 * warm up reqd).
	 */

	public final static int WIRED = 0;
	/**
	 * Indicates the connection is via old radio (must wait for warm up).
	 */
	public final static int OLD_RADIO = 1;
	/**
	 * Indicates the connection is via new radio (must send a bunch of padding chars).
	 */
	public final static int NEW_RADIO = 2;

	/**
	 * Indicates the connection is to an original robot.
	 */
	public final static byte ORIGINAL_ROBOT = 0;

	/**
	 * Indicates the connection is to a newer black robot;
	 */
	public final static byte BLACK_ROBOT = 1;

	/**
	 * Indicates the connection is to a newer orange robot;
	 */
	public final static byte ORANGE_ROBOT = 3;

	/**
	 * Indicates the connection is to a newer yellow robot;
	 */
	public final static byte YELLOW_ROBOT = 4;

	/**
	 * Indicates the connection is to a newer chrom robot;
	 */
	public final static byte CHROME_ROBOT = 5;

	/*
	 * Useful Constants
	 */
	    private final static int INIT_ATTEMPTS = 5;
	private final static int TX_ATTEMPTS = 10;
	private final static int SLEEP_TIME = 100;
	private final static int RADIO_WARMUP_TIME = 45;
	private final static int TX_DRAIN_TIME = 40;
	    private final static int PADDING_CHARS = 40;
	    private final static byte PADDING_CHARACTER = (byte)240;
	private final static boolean DEBUG = false;

	/*
	 * Class Data
	 */ 
	private String dev_name = "/dev/ttyS0";
	private int baudrate = 9600;
	private SerialPortLocal serPort;
	private SerialConfig serCfg;
	private int SendMsgNum;
	private int AckMsgNum;
	private int lastX;
	private int lastY;
	private int lastH;
	private double lastB;
	private boolean sawObstacle;
	private int connection = WIRED;
	private byte id = 0;

	/**
 	 * The constructor for class <code>JCyeComm</comm>.
 	 * Initializes the serial port driver, then carries 
	 * out the ISTART/ASTART sequence to initialize the robot.
	 *
	 * @param d the name of the serial device to open.
	 * @param b baud rate (9600 or 19200).
	 * @param c connection type (WIRED or OLD_RADIO or NEW_RADIO).
	 * @param i ID number.
	 */
	protected JCyeComm(String d, int b, int c, byte i) throws JCyeException
		{
		dev_name = d;

		if (b == 9600)
			baudrate = SerialConfig.BR_9600;
		else if (b == 19200)
			baudrate = SerialConfig.BR_19200;
		else
			throw (new JCyeException(" invalid baud rate"));

		connection = c;
		if ((c != WIRED)&&(c != OLD_RADIO)&&(c != NEW_RADIO))
			throw (new JCyeException(" invalid connection type"));

		if(DEBUG) {
		    System.out.println("Connection type: ");
		    switch(c) {
		    case(WIRED):
			System.out.println(" Wired");
			break;
		    case(OLD_RADIO):
			System.out.println(" Old Radio");
			break;
		    case(NEW_RADIO):
			System.out.println(" New Radio");
			break;
		    default:
			System.out.println(" Unknown");
			break;
		    }
		}

		id = i;

		SendMsgNum = 0;
		AckMsgNum = 0;

		try
			{
			InitializeSerialPort();
			}
		catch (IOException e)
			{
			System.err.println(e);
			throw (new JCyeException("Couldn't intialize port"));
			}

		boolean initSuccess = InitializeRobot();
		int initRetry = 0;
		while((initSuccess == false) && (initRetry < INIT_ATTEMPTS)) {
		    initSuccess = InitializeRobot();
		    initRetry++;
		}

		if(initSuccess == false)
			{
	    		throw (new JCyeException("Error: Unable to initialize serial comm with robot."));
			}
		}

	/**
	 * Initialize the <code>JCyeComm</code> object's 
	 * <code>SerialPortLocal</code>.  First creates a 
	 * <code>SerialConfig</code> object with the proper settings,
	 * then uses that to create the <code>SerialPortLocal</code>.
	 */
	private void InitializeSerialPort() throws IOException
		{
		    if(DEBUG) {
			System.out.println("Initializing serial connection with: ");
			System.out.println(" device name: " + dev_name);
			System.out.println(" baud rate: " + baudrate);
		    }
 		// Create a SerialConfig object with the 
		// proper settings for OLD radio link
		serCfg = new SerialConfig(dev_name);
		serCfg.setBitRate(baudrate);
		serCfg.setDataBits(serCfg.LN_8BITS);
		serCfg.setParity(serCfg.PY_NONE);
		serCfg.setStopBits(serCfg.ST_1BITS);
		serCfg.setHandshake(serCfg.HS_NONE);
		serCfg.setHardFlow(false);

		// Create the SerialPortLocal object
		serPort = new SerialPortLocal(serCfg);
		serPort.setTimeoutRx(100);
		serPort.setTimeoutTx(5);
		serPort.setDTR(true);
		serPort.txFlush();
		serPort.rxFlush();
		}

	/**
	 * Initializes the connection with the Cye robot.
	 * This consists of sending an ISTART message, and 
	 * waiting for the ASTART acknowledgement back from 
	 * the robot.
	 *
	 * @return the success (<code>true</code>) or failure 
	 * of the initialization sequence.
	 */
	private boolean InitializeRobot()
		{
		// Send ISTART message
		JCyeMsg InitMsg = new JCyeMsg(id);

		InitMsg.SetRxMsgNum(0);
		InitMsg.SetTxMsgNum(0);
		InitMsg.Add(InitMsg.CMD_ISTART);

		JCyeMsg ackMsg = TransmitUntilAck(InitMsg, TX_ATTEMPTS);

		// Look for ASTART message, return false if no valid 
		// ASTART response.
		if(ackMsg.GetBufByte(2) == ackMsg.CMD_ASTART) return true;
		return false;
		}

	/**
	 * Sends a message (represented as an object of class 
	 * <code>JCyeMsg</code>) to the Cye robot, and waits 
	 * for an acknowledgement or retry failure.
	 * 
	 * @param Msg the <code>JCyeMsg</code> to send to the robot.
	 *
	 * @return the acknowledgement message from the robot, 
	 *  represented as an object of type <code>JCyeMsg</code>.
	 */
	protected JCyeMsg SendMsg (JCyeMsg Msg)
		{
		JCyeMsg reply = TransmitUntilAck(Msg, TX_ATTEMPTS);

		return reply;
		}

	/**
	 * The background <code>run</code> method for 
	 * <code>JCyeComm</code>.  This method polls the 
	 * robot (with a <code>CMD_POLL_REQUEST</code> message,
	 * then goes back to sleep for <code>SLEEP_TIME</code> ms.
	 */
	public void run()
		{
		while(true) 
			{
			// If we're idle, poll the robot
			JCyeMsg PollMsg = new JCyeMsg(id);
			PollMsg.Add(PollMsg.CMD_POLL_REQUEST);
			// PollMsg.Add(PollMsg.CMD_REQUEST_STATE);
			TransmitUntilAck(PollMsg, 1);
    
			// Go to sleep for SLEEP_TIME.
			try 
				{
				Thread.sleep(SLEEP_TIME);
				}
			catch (InterruptedException e) 
				{
				System.err.println("Error in JCyeComm run.");
				System.err.println(e);
				}
			}
		}

	/**
	 * Transmit a <code>JCyeMsg</code> over the serial port, 
	 * with no acknowledgement or retry.  First, call the 
	 * <code>JCyeMsg</code>'s <code>toTransmitArray</code> method, 
	 * which mashes all of the random message fields down 
	 * into a byte array.  Then, spit that flattened byte 
	 * array out the serial port.
	 *
	 * @param Msg the <code>JCyeMsg</code> to transmit.
	 */
	private void Transmit(JCyeMsg Msg)
		{
		JCyeMsg txMsg = Msg.toTransmitArray();

		int l = txMsg.GetMsgLen() - 1;

		try 
			{
			    if (connection == OLD_RADIO) {
				serPort.setDTR(false);

				try
					{
					Thread.sleep(RADIO_WARMUP_TIME);
					}
				catch (Exception e) {}
			    }
			    
			    if (connection == NEW_RADIO) {
				// Write 40 padding chars to sync radio
				for(int i = 0; i < PADDING_CHARS; i++) {
				    serPort.putByte(PADDING_CHARACTER);
				}
			    }
				
			    serPort.putData(txMsg.GetMsgBuf(), 0, 
				txMsg.GetMsgLen() - 1);
			    while(serPort.txBufCount() != 0);

			    if (connection == OLD_RADIO) {
				try
					{
					Thread.sleep(TX_DRAIN_TIME);
					}
				catch (Exception e) {}

				serPort.setDTR(true);
			    }

			}

		catch (Exception e) 
			{
			/*--- Just log the error for now ---*/
			System.err.println("Error in JCyeComm Transmit.");
			System.err.println(e);
			// System.exit(1);
			}
		}


	/**
	 * Transmit a <code>JCyeMsg</code> until we receive an 
	 * acknowledge message, with a specified number of retry 
	 * attempts if we're not immediately successful.
	 * This method makes use of the methods <code>Transmit</code> 
	 * and <code>Receive</code> to do the dirty work.
	 *
	 * @param Msg the <code>JCyeMsg</code> to transmit.
	 * @param attempts the number of retry attempts permitted 
	 * before we return failure.
	 *
	 * @return the acknowledgement <code>JCyeMsg</code> 
	 *  received from the robot. In case of
	 *  failure, MsgID will be set to -1.
	 */
	private synchronized JCyeMsg TransmitUntilAck(JCyeMsg Msg, int attempts)
		{
		Msg.SetMsgID(Msg.GetBufByte(0));

		switch(Msg.GetMsgID()) 
			{
			case 11:
    				SendMsgNum = 0;
    				Msg.SetTxMsgNum(SendMsgNum);
				break;
			case 10:
				Msg.SetTxMsgNum(SendMsgNum);
				break;
			default:
				SendMsgNum++;
				SendMsgNum &= 0x0F;
				Msg.SetTxMsgNum(SendMsgNum);
				break;
			}

		JCyeMsg rxMsg = new JCyeMsg(id);
		boolean gotAck = false;
		boolean polling = false;
		int txFailures = 0;

		Msg.SetRxMsgNum(AckMsgNum);

		if(DEBUG) 
			{
			System.out.println("Transmitting Message: ");
			System.out.print(" Msg ID:" + Msg.GetMsgID());
			if(Msg.GetMsgID() > 32) 
				System.out.println(" (" 
				+ (char)Msg.GetMsgID() + ")");
			else
				System.out.println();
			System.out.println(" RX/TX Msg Num: " 
			+ Msg.GetRxMsgNum() + "/" + Msg.GetTxMsgNum());
			}

		while ((!polling) && (!gotAck) && (txFailures < attempts)) 
			{
    			if(DEBUG) 
				{
				System.out.print("  Attempt number " 
					+ (txFailures + 1) 
					+ " to transmit message with ID " 
					+ Msg.GetMsgID());
				System.out.println(" and RX/TX " 
					+ Msg.GetRxMsgNum() + "/" 
					+ Msg.GetTxMsgNum());
				System.out.println("  Transmit packet begin timestamp(ms): " 
					+ System.currentTimeMillis());
				}

			// Transmit the damn Msg.
			Transmit(Msg);

			if(DEBUG) 
				{
				System.out.println("  Transmit packet end timestamp(ms): " 
					+ System.currentTimeMillis());
				System.out.println("  Message has been sent. Waiting for ack.");
				}

			rxMsg = Receive();
	    
			if(DEBUG) 
				{
				System.out.print("  Received message with ID " 
					+ rxMsg.GetMsgID());
				if(rxMsg.GetMsgID() > 32)
					System.out.println(" (" 
					+ (char)rxMsg.GetMsgID() + ")");
				else
					System.out.println();
				System.out.println("  Receive packet end timestamp(ms): " 
					+ System.currentTimeMillis());
				System.out.println("   Robot RX/TX Msg Num: " 
					+ rxMsg.GetRxMsgNum() + "/" 
					+ rxMsg.GetTxMsgNum());
				System.out.println("   Robot MsgLen: " 
					+ rxMsg.GetMsgLen());
				}

			AckMsgNum = rxMsg.GetTxMsgNum();
			Msg.SetRxMsgNum(AckMsgNum);
			if(DEBUG) 
				{
				System.out.println("   Updated AckMsgNum to " 
					+ AckMsgNum);
				}

			if(Msg.GetTxMsgNum() == rxMsg.GetRxMsgNum()) 
				{
				if(DEBUG) 
					{
					System.out.println("   Got ack match.");
					}

				gotAck = true;
				} 
			else if(Msg.GetBufByte(0) == Msg.CMD_POLL_REQUEST) 
				{
				if(DEBUG) 
					{
					System.out.println("   Bad ack match, but we're just polling.");
					}
				polling = true;
				}
			if(gotAck == false) 
				{
				if(DEBUG)
					{
					System.out.println("  Did not receive valid ack message.");
					}
				txFailures++;
				}
			}

		if(gotAck == true) 
			{
			if(DEBUG) 
				{
				System.out.println("  Receieved a valid ack message.");
				}
			HandleAckMsg(rxMsg);
			}

		if(DEBUG) 
			{
			System.out.println("Done with transmit.\n\n");
			}

		return rxMsg;
		}

	/**
 	 * Receive a message from the Cye robot.
	 * The message must be bracketed by a STX/ETX pair.
	 * After we have received a valid message, the packet 
	 * headers are parsed using <code>JCyeMsg</code>'s 
	 * <code>ParseReply</code> method.
	 *
	 * @return the <code>JCyeMsg</code> received from 
	 *  the robot. If there is a reception
	 *  error, MsgID is set to -1.
	 */
	private JCyeMsg Receive()
		{
		JCyeMsg Msg = new JCyeMsg(id);

		int i = 0;
		int bufCount = 0;
		int STXwaitcount = 0;

		if(DEBUG)
			{
			System.out.println("     Receive packet begin timestamp(ms): " 
			+ System.currentTimeMillis());
			System.out.print("     Received chars: ");
			}

		try 
			{
			while((i != 2) && (i != -1) && (STXwaitcount++ < 32)) 
				{
				i = serPort.getByte();
				if(DEBUG) 
					{
					if(i == 2) 
						{
						System.out.println();
						System.out.print("     RX data packet: < ");
						}
					System.out.print(i + " ");
					}
				}

			if(STXwaitcount >= 32) 
				{
				if(DEBUG) 
					{
					System.out.println("Garbage on rx.");
					}
				Msg.SetMsgID(-1);
				return Msg;
				}

			// Check for timeout on STX character
			if(i == -1)
				{
				if(DEBUG) 
					{
					System.out.println("Timeout waiting for STX.");
					}
				Msg.SetMsgID(-1);
				return Msg;
				}

			while((i != 3) && (i != -1) && (bufCount++ < 32)) 
				{
				i = serPort.getByte();
				if(DEBUG) 
					{
					System.out.print(i + " ");
					if(i == 3) System.out.print(">");
					}

				if(i == Msg.ESC) 
					{
					i = serPort.getByte();
					if(DEBUG) 
						{
						System.out.print(i + " ");
						}
					switch(i) 
						{
						case((byte)'1'):
							Msg.Add(Msg.STX);
							break;
						case((byte)'2'):
							Msg.Add(Msg.ETX);
							break;
						case(27):
							Msg.Add(Msg.ESC);
							break;
						}
					}
				else Msg.Add((byte)i);
				}

			if(bufCount == 32) 
				{
				if(DEBUG) 
					{
					System.out.println("RX buffer overflow waiting for ETX.");
					}
				Msg.SetMsgID(-1);
				return Msg;
				}

			// Check for timeout on ETX character
			if(i == -1) 
				{
				if(DEBUG) 	
					{
					System.out.println("Timeout waiting for ETX.");
					}
				Msg.SetMsgID(-1);
				return Msg;
				}
			}

		catch (Exception ioe) 
			{
			System.err.println(ioe);
			System.exit(1);
			}

		Msg.ParseReply();

		if(DEBUG) 
			{
			System.out.println();
			System.out.println("     RX packet timestamp(ms): " 
				+ System.currentTimeMillis());
			}

		return Msg;
		}

	/**
	 * Decode a <code>JCyeMsg</code> reply from the robot.
	 * This method parses the data encoded in the reply, 
	 * and sets the various and sundry fields in <code>JCyeComm</code>.
	 * It is assumed that the message is already known to be 
	 * valid; passing an invalid message to this method can 
	 * seriously hose the status variables.
	 *
	 * @param Msg the <code>JCyeMsg</code> to be decoded.
	 */
	private void HandleAckMsg(JCyeMsg Msg)
		{
		switch(Msg.GetMsgID())
			{
			case (byte)'Q':
				JCyeStatus stat = new JCyeStatus(Msg);
				lastX = stat.GetX();
				lastY = stat.GetY();
				lastH = stat.GetHeading();
				lastB = stat.GetBattery();
				if(DEBUG)
					{
					System.out.println("   Received status update message.");
					System.out.println("    Robot X: " + lastX);
					System.out.println("    Robot Y: " + lastY);
					System.out.println("    Heading: " + lastH);
					System.out.println("    Battery: " + lastB);
					}
				break;
			case (byte)'O':
				if(DEBUG)
					{
					System.out.println("   Received obstacle update message.");
					}
				sawObstacle = true;
				break;
			case (byte)'S':
				if(DEBUG) 
					{	   
					System.out.println("   Received charge state update message.");
					}
				break;
			case (byte)'t':
				if(DEBUG) 
					{
					System.out.println("   Received software revision number message.");
					}
				break;
			case (byte)'h':
				if(DEBUG) 
					{
					System.out.println("   Received robot at home message.");
					}
				break;
			default:
				if(DEBUG) 
					{
					System.out.println("   Received unknown ack message.");
					}
				break;
			}
		}


	/**
	 * Returns the last reported X position of el roboto.
	 *
	 * @return the X position
	 */
	protected int GetLastX()
		{
		return lastX;
		}

	/**
	 * Returns the last reported Y position of el roboto.
	 *
	 * @return the Y position
	 */
	protected int GetLastY()
		{
		return lastY;
		}

	/**
	 * Returns the last reported heading of el roboto.
	 *
	 * @return the heading
	 */
	protected int GetLastH()
		{
		return lastH;
		}

	/**
	 * Returns the last reported battery voltage of el roboto.
	 *
	 * @return the battery voltage
	 */
	protected double GetLastB()
		{
		return lastB;
		}

	/**
	 * Returns the status of the obstacle detector. If an obstacle 
	 * has been hit since the last call to <code>ClearObstacle</code>, 
	 * return true.
	 *
	 * @return the obstacle detector status.
	 */
	protected boolean GetObstacle()
		{
		return sawObstacle;
		}

	/**
	 * Resets the obstacle detector. Call this method to reset 
	 * the detection logic.
	 */
	protected void ClearObstacle()
		{
		sawObstacle = false;
		}

	/**
	 * Convert a (signed) byte [-127..128] to an (unsigned) 
	 * integer [0..255].
	 *
	 * @param b the (signed) byte
	 *
	 * @return the (unsigned) integer
	 */
	private int bToI(byte b)
		{
		if(b < 0) return ((int)b + 256);

		return ((int)b);
		}
	}

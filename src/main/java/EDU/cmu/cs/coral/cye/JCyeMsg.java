package EDU.cmu.cs.coral.cye;
import java.io.*;

/**
 * JCyeMsg implements the message protocol used by the Cye robot.
 * This class should NOT be used by a end-user of the JavaCye package.
 */
public class JCyeMsg {
    /*
     * Useful Constants
     */
    public final static int MAX_MSG_LEN = 128;
    // Source and Destination addresses
    public final static byte PC_SRC_ADDR = 2;
    // PC-to-Robot Message Codes
    public final static byte CMD_SET_DEST = (byte)'b';
    public final static byte CMD_SET_HEADING_DEST = (byte)'H';
    public final static byte CMD_SET_TURN = (byte)'T';
    public final static byte CMD_SET_MOTORS_PWM = (byte)'D';
    public final static byte CMD_SET_MAXPWM = (byte)'M';
    public final static byte CMD_SET_MAXSPEED = (byte)'s';
    public final static byte CMD_SET_MOTORS_VELOCITY = (byte)'V';
    public final static byte CMD_SET_HEADING_CORRECT_FACTOR = (byte)'c';
    public final static byte CMD_SET_POSITIONVELOCITY_DEST = (byte)'v';
    public final static byte CMD_STOP_MOTORS = (byte)'S';
    public final static byte CMD_REQUEST_POSITION = (byte)'P';
    public final static byte CMD_SET_HEADING = (byte)'h';
    public final static byte CMD_SET_POSITION = (byte)'p';
    public final static byte CMD_GO_STRAIGHT_VELOCITY = (byte)'a';
    public final static byte CMD_SET_SENSITIVE_OBS = (byte)'o';
    public final static byte CMD_SET_MOTION_CONTROL_CONSTANTS = (byte)'K';
    public final static byte CMD_SET_HANDLE_LENGTH = (byte)'L';
    public final static byte CMD_BUZZER_ON = (byte)'B';
    public final static byte CMD_BUZZER_FREQUENCY = (byte)'f';
    public final static byte CMD_SET_VELOCITY_DIRECTION = (byte)'d';
    public final static byte CMD_REQUEST_STATE = (byte)'r';
    public final static byte CMD_POWER_OFF = (byte)'O';
    public final static byte CMD_POLL_REQUEST = 10;
    public final static byte CMD_ISTART = 11;
    public final static byte CMD_ASTART = 12;
    public final static byte CMD_ACK_REQUEST = 13;  
    // Robot-to-PC Message Codes
    public final static byte REPLY_STATUS_UPDATE = (byte)'Q';
    public final static byte REPLY_OBSTACLE = (byte)'O';
    public final static byte REPLY_CHARGE = (byte)'S';
    public final static byte REPLY_SOFTWARE_REV = (byte)'t';
    public final static byte REPLY_AT_HOME = (byte)'h';
    // Flag characters
    public final static byte FLAG = (byte)240;
    public final static int FLAG_NUM = 4;
    // Serial protocol special characters
    public final static byte ESC = 27;
    public final static byte STX = 2;
    public final static byte ETX = 3;
    public final static byte ESC_STX = (byte)'1';
    public final static byte ESC_ETX = (byte)'2';

    /*
     * Class Data
     */
    private byte[] MsgBuf;
    private int MsgLen;
    private byte SrcAddr;
    private byte DestAddr;
    private byte RxMsgNum;
    private byte TxMsgNum;
    private byte MsgID;
   
    /**
     * JCyeMsg constructor
     *
     * @param d, the destination address.
     */
    protected JCyeMsg(byte d)
    {
	MsgBuf = new byte[MAX_MSG_LEN];
	MsgLen = 0;
	SrcAddr = PC_SRC_ADDR;
	DestAddr = d;
	RxMsgNum = 0;
	TxMsgNum = 0;
    }

    /**
     * Pretty-prints a JCyeMsg object to System.out.
     */
    protected void Print()
    {
	System.out.println();
	System.out.println("MsgLen: " + MsgLen);
	System.out.println("SrcAddr: " + SrcAddr);
	System.out.println("DestAddr: " + DestAddr);
	System.out.println("RxMsgNum: " + RxMsgNum);
	System.out.println("TxMsgNum: " + TxMsgNum);
	System.out.println("Msg ID: " + (char)MsgBuf[0]);
	for(int i = 0; i < MsgLen; i++) {
	    System.out.print((int)MsgBuf[i] + " ");
	}
	System.out.println();
	System.out.println();
    }
    
    /**
     * Returns the raw buffer data.
     *
     * @return the byte array of raw data
     */
    protected byte[] GetMsgBuf()
    {
	return MsgBuf;
    }
    
    /**
     * Returns the message length.
     * @return the message length
     */
    protected int GetMsgLen()
    {
	return MsgLen;
    }

    /**
     * Converts all of the ugly protocol information into a pretty byte array ready to be pushed through the serial port.
     * Implements CRC on transmit end. Adds flag bytes to start and end of packet.
     * @return a JCyeMsg object with the raw data buffer full of nicely-processed bytes
     */
    protected JCyeMsg toTransmitArray()
    {
	JCyeMsg NewMsg = new JCyeMsg(DestAddr);

	NewMsg.Add(CombineBytes(SrcAddr, DestAddr));
	NewMsg.Add(CombineBytes((byte)(RxMsgNum&0x0F), (byte)(TxMsgNum&0x0F)));
	for(int i = 0; i < MsgLen; i++) {
	    NewMsg.Add(MsgBuf[i]);
	}

	int crc = CalcCRC(NewMsg.GetMsgBuf(), NewMsg.GetMsgLen());
	byte crc_l = (byte)(crc & 0x00FF);
	byte crc_h = (byte)((crc & 0xFF00) >> 8);
	
	NewMsg.Add(crc_l);
	NewMsg.Add(crc_h);
 
	JCyeMsg Msg = new JCyeMsg(DestAddr);
	
	for(int i = 0; i < FLAG_NUM; i++) {
	    Msg.Add(FLAG);
	}

	Msg.Add(STX);
	
	for(int i = 0; i < NewMsg.GetMsgLen(); i++) {
	    switch (NewMsg.GetBufByte(i)) {
	    case(ESC):
		Msg.Add(ESC);
		Msg.Add(ESC);
		break;
	    case(STX):
		Msg.Add(ESC);
		Msg.Add(ESC_STX);
		break;
	    case(ETX):
		Msg.Add(ESC);
		Msg.Add(ESC_ETX);
		break;
	    default:
		Msg.Add(NewMsg.GetBufByte(i));
		break;
	    }
	}
	Msg.Add(ETX);
	Msg.Add(FLAG);

	Msg.Add((byte)0);

	Msg.SetSrcAddr(SrcAddr);
	Msg.SetDestAddr(DestAddr);
	Msg.SetTxMsgNum(TxMsgNum);
	Msg.SetRxMsgNum(RxMsgNum);

	return(Msg);		
    }

    /**
     * Adds a byte to the message buffer.
     * @param b the byte to add
     */
    protected void Add(byte b)
    {
	MsgBuf[MsgLen++] = b;
    }

    /**
     * Sets a particular buffer byte to a specified value.
     * @param i the buffer index
     * @param b the byte
     */
    protected void SetBufByte(int i, byte b)
    {
	MsgBuf[i] = b;
    }

    /**
     * Gets a particular buffer byte.
     * @param i the buffer index
     * @return b the desired byte
     */
    protected byte GetBufByte(int i)
    {
	return(MsgBuf[i]);
    }

    /**
     * Sets the message destination address.
     * @param Addr the address
     */
    protected void SetDestAddr(int Addr)
    {
	DestAddr = (byte) Addr;
    }

    /**
     * Gets the message destination address.
     * @return the address
     */
    protected int GetDestAddr()
    {
	return (int) DestAddr;
    }

    /** 
     * Sets the message source address.
     * @param Addr the address
     */
    protected void SetSrcAddr(int Addr)
    {
	SrcAddr = (byte) Addr;
    }

    /**
     * Gets the message source address.
     * @return the address
     */
    protected int GetSrcAddr()
    {
	return (int) SrcAddr;
    }

    /**
     * Sets the message RX number.
     * @param Num the RX number
     */
    protected void SetRxMsgNum(int Num)
    {
	RxMsgNum = (byte) Num;
    }

    /**
     * Gets the message RX number.
     * @return the RX number
     */
    protected int GetRxMsgNum()
    {
	return (int) RxMsgNum;
    }

    /**
     * Sets the TX message number.
     * @param Num the TX number
     */
    protected void SetTxMsgNum(int Num)
    {
	TxMsgNum = (byte) Num;
    }

    /**
     * Gets the TX message number.
     * @return the TX number
     */
    protected int GetTxMsgNum()
    {
	return (int) TxMsgNum;
    }

    /**
     * Sets the message ID number.
     * @param ID the number
     */
    protected void SetMsgID(int ID)
    {
	MsgID = (byte) ID;
    }

    /**
     * Gets the message ID number.
     * @return the number
     */
    protected int GetMsgID()
    {
	return (int) MsgID;
    }

    /**
     * Calculates the CRC value for a byte array.
     * @param a the byte array
     * @param len the length of the array
     */
    protected int CalcCRC(byte[] a, int len)
    {
        int[] wCRC16a={
	0000000,	0140301,	0140601,	0000500,
	0141401,	0001700,	0001200,	0141101,
	0143001,	0003300,	0003600,	0143501,
	0002400,	0142701,	0142201,	0002100,
	};
	int[] wCRC16b={
	0000000,	0146001,	0154001,	0012000,
	0170001,	0036000,	0024000,	0162001,
	0120001,	0066000,	0074000,	0132001,
	0050000,	0116001,	0104001,	0043000,
	};
	byte	pb;
	byte	bTmp;
	int   pulSeed=0;

	for (int i = 0; i < len; i++)
	    {
		bTmp  = (byte)(a[i] ^ pulSeed);
		//bTmp=(byte)(((int)*pb)^(pulSeed));	// Xor CRC with new char
		pulSeed=((pulSeed)>>8) ^ wCRC16a[bTmp&0x0F] ^ wCRC16b[(bTmp>>4)&0x0F];
	    }
	return pulSeed;
    }

    /**
     * Extract address, message number, and message type from a freshly-received JCyeMsg.
     */
    protected void ParseReply()
    {
	byte[] addr = SplitByte(MsgBuf[0]);
	SrcAddr = addr[0];
	DestAddr = addr[1];

	byte[] num = SplitByte(MsgBuf[1]);
	RxMsgNum = num[0];
	TxMsgNum = num[1];
	
	MsgID = MsgBuf[2];
    }	

    /**
     * Split a byte into two four-bit values.
     * @param b the byte to split
     * @return a byte array with the two four-bit values
     */
    protected byte[] SplitByte(byte b)
    {
	byte[] r = new byte[2];

	r[0] = (byte)((b >> 4) & 0x0F);
	r[1] = (byte)(b & 0x0F);

	return r;
    }

    /**
     * Combine two four-bit values into a single byte.
     * @param b1 the first four-bit value
     * @param b2 the second four-bit value
     * @return the combined byte
     */
    protected byte CombineBytes(byte b1, byte b2)
    {
	byte t = (byte)((b1 << 4) & 0xF0);
	t |= (byte)(b2 & 0x0F);

	return t;
    }
}

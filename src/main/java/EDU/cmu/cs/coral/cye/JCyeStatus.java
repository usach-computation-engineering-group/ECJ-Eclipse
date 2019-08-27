package EDU.cmu.cs.coral.cye;
import java.io.*;

/**
 * <code>JCyeStatus</code> is implemented to help with parsing robot status messages.
 * This class should never be accessed by user code.
 */
public class JCyeStatus {
    /*
     * Useful constants
     */
    private static double TPI = 100.0;
    private static double RTPI = 20.0;
    private static double NHEADINGS = 255; 

    /*
     * Class Data
     */
    private byte[] MsgBuf;
    private int MsgID;
    private int X;
    private int Y;
    private int H;
    private double B;

    /**
     * Constructor for <code>JCyeStatus</code>.
     * Does nothing.
     */
    public JCyeStatus()
    {

    }

    /**
     * Constructor for <code>JCyeStatus</code>.
     * Turns a <code>JCyeMsg</code> received from the robot into a <code>JCyeStatus</code> object.
     *
     * @param Msg a <code>JCyeMsg</code> received from the Cye robot.
     */
    public JCyeStatus(JCyeMsg Msg)
    {
	MsgBuf = Msg.GetMsgBuf();

	int s = 2;

	MsgID = MsgBuf[s];
	
	X = bytesToInt(MsgBuf[s + 1], MsgBuf[s + 2], MsgBuf[s + 3], MsgBuf[s + 4]);
	Y = bytesToInt(MsgBuf[s + 5], MsgBuf[s + 6], MsgBuf[s + 7], MsgBuf[s + 8]);
	H = bytesToShort(MsgBuf[s + 9], MsgBuf[s + 10]);
	byte b = MsgBuf[s + 13];
	if(b >= 0)
	    B = (b * 0.01919) + 9.7;
	else {
	    int i = 256 + b;
	    B = (i * 0.01919) + 9.7;
	}
    }

    /**
     * Converts four bytes into an integer.
     * @param b1,b2,b3,b4 the bytes.
     * @return the integer.
     */
    private int bytesToInt(byte b1, byte b2, byte b3, byte b4)
    {
	int i1 = byteToInt(b1);
	int i2 = byteToInt(b2);
	int i3 = byteToInt(b3);
	int i4 = byteToInt(b4);

	int l = (i1 & 0xFF);
	l |= ((i2 & 0xFF) << 8);
	l |= ((i3 & 0xFF) << 16);
	l |= ((i4 & 0xFF) << 24);

	return l;
    }

    /**
     * Converts two bytes into a short.
     * @param b1,b2 the bytes.
     * @return the short.
     */
    private short bytesToShort(byte b1, byte b2)
    {
	short i1 = (short)byteToInt(b1);
	short i2 = (short)byteToInt(b2);

	short i = (short)(i1 & 0xFF);
	i |= ((i2 & 0xFF) << 8);

	return i;
    }

    /**
     * Converts a byte to an int.
     * @param b the byte.
     * @return the integer.
     */
    public int byteToInt(byte b)
    {
	if(b >= 0) return (int) b;
	return (256 + (int) b);
    }

    /**
     * Get the message ID.
     * @return the ID
     */
    public int GetMsgID()
    {
	return MsgID;
    }

    /**
     * Get the x value (in robot coordinates).
     * @return the x value.
     */
    public int GetX()
    {
	return X;
    }

    /**
     * Get the y value (in robot coordinates).
     * @return the y value.
     */
    public int GetY()
    {
	return Y;
    }

    /**
     * Get the heading (in robot coordinates).
     * @return the heading.
     */
    public int GetHeading()
    {
	return H;
    }

    /**
     * Get the battery voltage.
     * @return the voltage.
     */
    public double GetBattery()
    {
	return B;
    }
}

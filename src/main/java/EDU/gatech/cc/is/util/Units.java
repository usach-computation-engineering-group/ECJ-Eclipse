/*
 * Units.java
 */

package	EDU.gatech.cc.is.util;

import java.lang.System;
import java.io.StreamTokenizer;

/**
 * Routines for units conversion. 
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class Units 
	{

	/**
	 * Handy to have 2 PI around.
	 */
	public static final double PI2 = 2*Math.PI;

	/**
	 * A very large number
	 */
	public static final double HUGE = 99999999999999f;

	/**
	 * Convert degrees to radians.
	 * @param  deg double, degrees.
	 * @return radians.
	 */
	public static double DegToRad(double deg)
		{
		return(Math.PI*2*deg/360);
		}


	/**
	 * Convert degrees to radians.
	 * @param deg int, degrees.
	 * @return radians.
	 */
	public static double DegToRad(int deg)
		{
		return(Math.PI*2*((double)deg)/360);
		}


	/**
	 * Convert 10ths of degrees to radians.
	 * @param deg10 int, 10ths of degrees.
	 * @return radians.
	 */
	public static double Deg10ToRad(int deg10)
		{
		return(Math.PI*2*deg10/3600);
		}


	/**
	 * Convert radians to degrees.
	 * @param rad double, radians.
	 * @return degrees.
	 */
	public static double RadToDeg(double rad)
		{
		return(rad*360/(Math.PI*2));
		}


	/**
	 * Convert radians to 10ths of degrees.
	 * @param rad double, radians.
	 * @return 10ths of degrees.
	 */
	public static int RadToDeg10(double rad)
		{
		return((int)(rad*3600/(Math.PI*2)));
		}


	/**
	 * Convert inches to meters.
	 * @param inch int, inches.
	 * @return meters.
	 */
	public static double InchToMeter(int inch)
		{
		return(((double)inch)*0.0254);
		}


	/**
	 * Convert inches to meters.
	 * @param inch double, inches.
	 * @return meters.
	 */
	public static double InchToMeter(double inch)
		{
		return(inch*0.0254);
		}


	/**
	 * Convert 10ths of inches to meters.
	 * @param inch int, inches.
	 * @return 10ths of meters.
	 */
	public static double Inch10ToMeter(int inch10)
		{
		return(((double)inch10)*0.00254);
		}


	/**
	 * Convert meters to 10ths of inches.
	 * @param meter double, meters.
	 * @return inches.
	 */
	public static int MeterToInch10(double meter)
		{
		return((int)(meter/0.00254));
		}


	/**
	 * Convert meters to inches.
	 * @param meter double, meters.
	 * @return inches.
	 */
	public static double MeterToInch(double meter)
		{
		return(meter/0.0254);
		}


	/**
	 * Limit the angle to between 0 and 2 PI.
	 * @param rad double, angle in radians to be clipped.
	 * @return the clipped angle.
	 */
	public static double ClipRad(double rad)
		{
		while (rad >= PI2) rad -= PI2;
		while (rad < 0)    rad += PI2;
		return(rad);
		}


	/**
	 * Limit the angle to between 0 and 359.9999.
	 * @param deg double, angle in degrees to be clipped.
	 * @return the clipped angle.
	 */
	public static double ClipDeg(double deg)
		{
		while (deg >= 360) deg -= 360;
		while (deg < 0)    deg += 360;
		return(deg);
		}


	/**
	 * Compute the best direction and angle to turn from the start 
	 * angle to the finish angle in degrees.
	 * @param start The starting angle.
	 * @param finish The desired angle.
	 * @return the required turn.
	 */
	public static double BestTurnDeg(double start, double finish)
		{
		/*--- normalize to between 0 and 360 ---*/
		while (start >= 360) start -= 360;
		while (start < 0)   start += 360;
		while (finish >= 360) finish -= 360;
		while (finish < 0)   finish += 360;

		/*--- compute best turn ---*/
		double rot = finish - start;
		if (rot > 180) rot -= 360;
		else if (rot < -180) rot += 360;

		return(rot);
		}


        /**
         * read a double from a stream. Assumes you have just read the
	 * token.
	 *
	 * @param in StreamTokenizer, the stream to read from.
	 * @return the double.
	 * @exception Exception if there is no number there.
         */
        public static double readDouble(StreamTokenizer in) throws Exception
                {
                double num = in.nval;
                int exp = 0;
                in.ordinaryChars('\0', ' ');
                in.nextToken();
                in.whitespaceChars('\0', ' ');
                if (in.ttype == in.TT_WORD &&
                Character.toUpperCase(in.sval.charAt(0))=='E')
                        {
                        try
                                {
                                exp = Integer.parseInt(in.sval.substring(1));
                                }
                        catch(NumberFormatException e)
                                {
                                in.pushBack();
                                }
                        }
                num = num * Math.pow(10,exp);
                return(num);
                }


	/**
	 * Compute the best direction and angle to turn from the start 
	 * angle to the finish angle in radians.
	 * @param start The starting angle.
	 * @param finish The desired angle.
	 * @returns The required turn.
	 */
	public static double BestTurnRad(double start, double finish)
		{
		/*--- normalize to between 0 and 2PI ---*/
		while (start >= PI2) start -= PI2;
		while (start < 0)   start += PI2;
		while (finish >= PI2) finish -= PI2;
		while (finish < 0)   finish += PI2;

		/*--- compute best turn ---*/
		double rot = finish - start;
		if (rot > Math.PI) rot -= PI2;
		else if (rot < -Math.PI) rot += PI2;

		return(rot);
		}


	/**
	 * Test the units functions.
	 */
	public static void main(String[] args)
		{
		if (DegToRad(360.0)!=(2*Math.PI))
			System.out.println("bug1");
		else if (DegToRad((int)360)!=(2*Math.PI))
			System.out.println("bug2");
		else if (Deg10ToRad((int)3600)!=(2*Math.PI))
			System.out.println("bug3");
		else if (RadToDeg(2*Math.PI)!=360.0)
			System.out.println("bug4");
		else if (RadToDeg10(2*Math.PI)!=3600)
			System.out.println("bug5");
		else if (Inch10ToMeter(400)!=1.016)
			System.out.println("bug6");
		else if (MeterToInch10(1.016)!=400)
			System.out.println("bug7");
		else if (BestTurnDeg(10,20)!=10)
			System.out.println("bug8");
		else if (BestTurnDeg(1,360)!=-1)
			System.out.println("bug9 "+BestTurnDeg(1,360));
		else if (BestTurnDeg(1,359)!=-2)
			System.out.println("bug10 "+BestTurnDeg(1,359));
		else if (BestTurnDeg(359,1)!=2)
			System.out.println("bug11");
		else if (BestTurnDeg(10,200)!=-170)
			System.out.println("bug12");
		else if (BestTurnDeg(200,10)!=170)
			System.out.println("bug13");
		else if (BestTurnDeg(721,1079)!=-2)
			System.out.println("bug14");
		else /* no bugs */
			System.out.println("no bugs detected");
		}
	}

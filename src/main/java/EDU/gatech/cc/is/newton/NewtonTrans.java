/*
 * NewtonTrans.java
 */

package EDU.gatech.cc.is.newton;

import	java.io.*;
import	EDU.gatech.cc.is.util.Vec2;

/**

<B>Introduction</B><BR>
Translates objects seen by an newton cognachrome board into
robot-centric coordinates using a linear interpolation method.
It (will soon) catagorizes the blobs for channels A & B according to size.
<P>
<B>Frames of Reference</B><BR>
If (X,Y) is the center of a colored blob in the image, X is the column, 
numbered from 0 on the left to 200 on the right.  Y is the row, from
0 at the top to 255 on the bottom.
<P>
In robot coordinates +x is forward, +y is to the left.
A Nomad 150 is .24659467 meters in radius.
<P>
<B>File Format</B><BR>
To work properly, NewtonTrans must read a configuration file, passed
to it at construction time.  Here is the format:
<PRE>
a_ratio          // true blob area = range * a_ratio * pixels_in_blob 
num_x            // number of X data elements 
row x_val        // the row of the bottom of the blob and the x coordinate 
row x_val        // MUST BE IN ASCENDING ROW ORDER !
row x_val
row x_val
row x_val
num_y            // number of Y data elements 
col y_val        // the col of the center of the blob and the y coordinate 
col y_val
col y_val
col y_val
col y_val
</PRE>
Here is an example file:
<PRE>
0.00007676243
3
91  2.40559
129 1.31339
235 0.36089
3
95  -0.762
136 0
190 0.762
</PRE>

@author (c)1997 Tucker Balch, All Rights Reserved
@version July 1997
*/

public class NewtonTrans
	{
	protected double[] xLut = new double[256];
	protected double[] yLut = new double[256];
	protected double	a_ratio;
	protected	Newton	newt;
	private   double[]	ranges = new double[MAX_BLOBS];
	private   double[]	areasd = new double[MAX_BLOBS];
	public static final double NO_DATA = -999;
	public static final int MAX_BLOBS = 50;
	public static final double BIG = 0.08; // square meters
	public static final double ALL_MIN = 0.0027; // square meters
	public static final boolean DEBUG = false;
	
	/**
	Instantiate a <B>newton.NewtonTrans</B> object.
	@param n the Newton object to get data from.
	@param f the configuration file name.
	*/
	public NewtonTrans(Newton n, String f) 
		{
		String token;
		// initialize global variables
		newt = n;
		for(int i=0; i<256; i++)
			xLut[i] = NO_DATA;
		for(int i=0; i<256; i++)
			yLut[i] = NO_DATA;
		a_ratio = 0.00007;

		try
			{
			// set up to to reading
			FileReader file = new FileReader(f);
			StreamTokenizer in = new StreamTokenizer(file);

			token = "begining of file";

			// get a_ratio
			if (in.nextToken()==StreamTokenizer.TT_NUMBER)
				a_ratio = in.nval;
			else
				{
				token = in.sval;
				throw new IOException();
				}

			// get x_num
			int x_num;
			if (in.nextToken()==StreamTokenizer.TT_NUMBER)
				x_num = (int)in.nval;
			else
				{
				token = in.sval;
				throw new IOException();
				}

			// fill in the X look up table
			in.nextToken();
			double start_row = in.nval;
			in.nextToken();
			double start_x = in.nval;
			for (int i=0; i<(x_num-1); i++)
				{
				in.nextToken();
				double next_row = in.nval;
				in.nextToken();
				double next_x = in.nval;
				double step = (next_x-start_x)/
						(next_row - start_row);
				for(int j = 0; 
					j <=(int)(next_row-start_row); j++)
						xLut[j+(int)start_row] 
							= start_x 
							+(double)j*step;
				start_row = next_row;
				start_x = next_x;
				}

			// get y_num
			int y_num;
			if (in.nextToken()==StreamTokenizer.TT_NUMBER)
				y_num = (int)in.nval;
			else
				{
				token = in.sval;
				throw new IOException();
				}

			// fill in the Y look up table
			in.nextToken();
			double start_col = in.nval;
			in.nextToken();
			double start_y = in.nval;
			for (int i=0; i<(y_num-1); i++)
				{
				in.nextToken();
				double next_col = in.nval;
				in.nextToken();
				double next_y = in.nval;
				double step = (next_y-start_y)/
						(next_col - start_col);
				for(int j = 0; 
					j <=(int)(next_col-start_col); j++)
						yLut[j+(int)start_col] 
							= start_y 
							+(double)j*step;
				start_col = next_col;
				start_y = next_y;
				}
			}
		catch(IOException e)
			{
			System.out.println(
				"NewtonTrans: bad format " + 
				"in configuration file, or file doesn't exist");
			}
		}

	/**
	Tell the newton to get a data frame.
	*/
	public void read_frame()
		{
		if (newt!=null) newt.read_frame();
		}


	/**
	Get an array of Vec2s that point egocentrically from the
	turret, or position of the camera to a perceived object.
	@param chan the channel (color) of the data to get.
	@return the array of visible objects.
	*/
	public Vec2[] getVisualObjects(int chan)
		{
		Vec2[] retval = new Vec2[0];
		if ((chan < 0)||(chan>6))
			{
			System.out.println("NewtonTrans.getVisualObjects:"
				+" illegal channel number: "+chan);
			return(retval);
			}
		if (newt!=null) 
			{
			// get the blob data
			int num = newt.getNumVis(chan%3);
			int[] rows = new int[num];
			int[] cols = new int[num];
			int[] areas = new int[num];
			newt.getY(chan%3,rows);
			newt.getX(chan%3,cols);
			newt.getArea(chan%3,areas);
			if (DEBUG/*true*/) 
				System.out.println("NewtonTrans.getVisualObjects: "
					+num+" blobs on channel "+chan);

			//compute the bottoms and areas of the blobs
			int[] bot = new int[num];
			for(int i=0; i<num; i++)
				{
				bot[i] = rows[i] + (areas[i]/2);
				if (bot[i]>255) bot[i] = 255;
				ranges[i] = yLut[cols[i]]*yLut[cols[i]]
						+xLut[bot[i]]*xLut[bot[i]];
				ranges[i] = Math.sqrt(ranges[i]);
				areasd[i] = ranges[i] * (double)(areas[i]*
						areas[i]) * a_ratio;
				}

			// compute how many blobs are valid
			int valid = 0;
			for(int i=0; i<num; i++)
				{
				if (DEBUG) System.out.println(cols[i]+" "+rows[i]+
					" "+areas[i]);
				if	(	
					// if looking for small things
					((chan<=2)
					&&(ranges[i]<Math.abs(NO_DATA))	
					&&(areasd[i]<BIG)
					&&(areas[i]>=5))//noise
					||	
					// if looking for big things
					((chan>2)
					&&(ranges[i]<Math.abs(NO_DATA))	
					&&(areasd[i]>=BIG)
					&&(areas[i]>=5))//noise
					)
					valid++;
				}
			if (DEBUG/*true*/) 
				System.out.println("NewtonTrans.getVisualObjects: "
					+valid+" valid blobs on channel "+chan);

			// fill in the array
			retval = new Vec2[valid];
			int j = 0;
			for(int i=0; i<num; i++)
				{
				if	(	
					// if looking for small things
					((chan<=2)
					&&(ranges[i]<Math.abs(NO_DATA))	
					&&(areasd[i]<BIG)
					&&(areas[i]>=5))//noise
					||	
					// if looking for big things
					((chan>2)
					&&(ranges[i]<Math.abs(NO_DATA))	
					&&(areasd[i]>=BIG)
					&&(areas[i]>=5))//noise
					)		
					{
					retval[j] = new Vec2(
						xLut[bot[i]],
						yLut[cols[i]]);
					j++;
					}
				}
			}
		return(retval);
		}

	/**
	Test NewtonTrans
	*/
	public static void main(String args[])
		{
		Newton newt = null;

		try
			{
			newt = new Newton(3,38400);
			}
		catch (Exception e)
			{
			System.out.println(e);
			}

		// the first part just demonstrates the file reading
		NewtonTrans nt = new NewtonTrans(newt, "newton.cfg");
		//System.out.println(nt.a_ratio);
		//System.out.println("-----");
		//for(int i=0; i<255; i++)
			//{
			//System.out.print(i+" ");
			//if (i<=255)
				//{
				//System.out.print(nt.xLut[i]);
				//System.out.println(" "+nt.yLut[i]);
				//}
			//else
				//System.out.println(nt.xLut[i]);
			//}

		// now print the data
		while(true)
			{
			newt.read_frame();
			System.out.println("------");
			Vec2[] things = nt.getVisualObjects(Newton.CHANNEL_A);
			for (int i=0;i<things.length;i++)
				System.out.println(things[i]);
			}
		}
	}


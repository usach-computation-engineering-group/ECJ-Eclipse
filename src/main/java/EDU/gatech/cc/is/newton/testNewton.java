/*
 * testNewton.java
 */

package EDU.gatech.cc.is.newton;

/**

<B>Introduction</B><BR>
This application is for testing the lowest level java newton  interface.
<P>
To run this program, first ensure you are in the correct directory (where
the testNewton.class file is), then type "java testNewton".

@author (c)1997 Tucker Balch, All Rights Reserved
@version July 1997
@see Newton
*/

public class testNewton
	{

	/**
	This method is for testing the newton native interface.
	*/
	public static void main(String[] args)
		{
		try
			{
			System.out.println("main: Opening connection to newton");
			Newton newt = new Newton(3, 38400);//ttyc

			while(true)
				{
				int[] X;
				int[] Y;
				int[] Area;
				int   num;
			
				newt.read_frame();
				num = newt.getNumVis(newt.CHANNEL_A);
				X = new int[num];
				Y = new int[num];
				Area = new int[num];
				newt.getX(newt.CHANNEL_A,X);
				newt.getY(newt.CHANNEL_A,Y);
				newt.getArea(newt.CHANNEL_A,Area);
				System.out.println("------");
				for (int i=0; i<num; i++)
					System.out.println(X[i]+" "+Y[i]+" "+Area[i]);
				}
			}
		catch (Exception e)
			{
			System.out.println(e);
			}
		}	
	}


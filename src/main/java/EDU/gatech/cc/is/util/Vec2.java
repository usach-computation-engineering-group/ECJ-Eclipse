/*
 * Vec2.java
 */

package	EDU.gatech.cc.is.util;

import java.io.*;
import java.lang.*;

/**
 * A class for manipulating 2d vectors.  Both polar and cartesian components 
 * are always available.  The fields x, y, t (theta) and r are directly 
 * available for reading, but you should <B> never </B> set them directly.  
 * Use setx, sety, sett and setr instead.  This (non OO) approach was chosen 
 * deliberately for speed reasons; no whining.
 * <P>
 * +x is right, +y is up. t is in radians with t=0 in the +x direction and
 * t = PI in the -r direction, increasing CCW.
 * <P>
 * NOTE: Vec2's can have direction without magnitude, i.e. theta has meaning
 * even if x, y and r == 0.
 * 
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class Vec2 implements Cloneable, Serializable
	{
	// default is unit vector heading in +x (left)
	/**
	 * The x component of the cartesian view of the vector; <B>never</B>
	 * set directly, use setx instead.
	 * @see Vec2#setx
	 */
	public	double	x=1;

	/**
	 * The y component of the cartesian view of the vector; <B>never</B>
	 * set directly, use sety instead.
	 * @see Vec2#sety
	 */
	public	double	y=0;

	/**
	 * The theta component of the polar view of the vector; <B>never</B>
	 * set directly, use sett instead.
	 * @see Vec2#sett
	 */
	public	double	t=0;

	/**
	 * The r component of the polar view of the vector; <B>never</B>
	 * set directly, use setr instead.
	 * @see Vec2#setr
	 */
	public	double	r=1;

	// Since we use them so much
	public 	static final	double	PI2	= 2.0 * Math.PI;
	public	static final	double	PI	= Math.PI;


	/**
	 *Create a new vector, (1,0).
	 */
	public 	Vec2()
		{
		x = 1;
		y = 0;
		t = 0;
		r = 1;
		}
		
	/**
	 * Create a new vector, (x0,y0).
	 * @param x0 the x component.
	 * @param y0 the y component.
	 */
	public 	Vec2(double x0, double y0)
		{
		x = x0;
		y = y0;
		t = Math.atan2(y,x);
		r = Math.sqrt(x*x + y*y);
		}
		
	/**
	 * Create a new vector by copying the input parameter.
	 * @param v Vec2, the vector to copy.
	 */
	public 	Vec2(Vec2 v)
		{
		x = v.x;
		y = v.y;
		t = v.t;
		r = v.r;
		}
		
	/**
	 * Create a new vector by cloning.
	 * @param v Vec2, the vector to copy.
	 */
	public 	Object clone()
		{
		return(new Vec2(this));
		}
		
	/**
	 * Set the x component. t and r are reset as well.
	 * @param newx the x component.
	 */
	public	void	setx(double newx)
		{
		x = newx;
		r = Math.sqrt(x*x + y*y);
		if (r > 0.0)
			t = Math.atan2(y,x);
		}	

	/**
	 * Set the y component. t and r are reset as well.
	 * @param newy the x component.
	 */
	public	void	sety(double newy)
		{
		y = newy;
		r = Math.sqrt(x*x + y*y);
		if (r > 0.0)
			t = Math.atan2(y,x);
		}	

	/**
	 * Set t. x and y are reset as well.
	 * @param newt the t component.
	 */
	public	void	sett(double newt)
		{
		t = newt;
		while (t > PI2) t = t - PI2;
		while (t < 0)   t = t + PI2;
		y = r*Math.sin(t);
		x = r*Math.cos(t);
		}	

	/**
	 * Set r. x, y and t may be reset as well.
	 * @param newr the r component.
	 */
	public	void	setr(double newr)
		{
                r = newr;

                y = r*Math.sin(t);
                x = r*Math.cos(t);
                if (r < 0)
                        {
			t = Units.ClipRad(t + Math.PI);
                        r = r*-1;
                        }
		}	
		
	/**
	 * Rotate the vector. x, y and t are reset.
	 * @param rot how many radians to rotate, CCW is positive.
	 */
	public void	rotate(double rot)
		{
		sett(t + rot);
		}

	/**
	 * Subtract other vector from self, this = this - other
	 * @param other the vector to subtract.
	 */
	public void	sub(Vec2 other)
		{
		x = x - other.x;
		y = y - other.y;
		r = Math.sqrt(x*x + y*y);
		if (r > 0)
			t = Math.atan2(y,x);
		}

	/**
	 * Same as setr.  Kept for comatibility.
	 * @param newr the r component.
	 */
	public void	normalize(double newr)
		{
		setr(newr);
		}

	/**
	 * Add another vector to self, this = this + other
	 * @param other the vector to add.
	 */
	public void	add(Vec2 other)
		{
		x = x + other.x;
		y = y + other.y;
		r = Math.sqrt(x*x + y*y);
		if (r > 0)
			t = Math.atan2(y,x);
		}

	/** 
	 * Provides info on which octant (0-7) the vector lies in.
	 * 0 indicates 0 radians +- PI/8 1-7 continue CCW.
	 * @return 0 - 7, depending on which direction the vector is pointing.  
	 */
	public int	octant()
		{
		double	temp = t + Math.PI/8;

		if (temp<0) temp += Math.PI*2;
		return ((int)(temp/(Math.PI/4))%8);
		}

	/** 
	 * Provides info on which quadrant (0-3) the vector lies in.
	 * 0 indicates 0 radians +- PI/4 1-3 continue CCW.
	 * @return 0 - 3, depending on which direction the vector is pointing.  
	 */
	public int	quadrant()
		{
		double	temp = t + Math.PI/4;

		if (temp<0) temp += Math.PI*2;
		return ((int)(temp/(Math.PI/2))%4);
		}

	/**
	 * Generate a string value for the vector.
	 * @return "(x,y) (r,t)"
	 */
	public String	toString()
		{
		return ("(" + x + "," + y + ") (" + r + "," + t + ")");
		}

	/**
	 * A test routine.
	 */
	public static void main(String[] args)
		{
		Vec2 temp1 = new Vec2();
		Vec2 temp2 = new Vec2();

		System.out.println("Initial vector  "+temp1);
		temp1.add(temp2);
		System.out.println("Doubled         "+temp1);
		temp1.rotate(Math.PI/2);
		System.out.println("Rotated +90 deg "+temp1);
		temp1.rotate(Math.PI/2);
		System.out.println("Rotated +90 deg "+temp1);
		temp1.sub(temp2);
		System.out.println("Subtract 1,0    "+temp1);
		temp1.setx(0.0);
		System.out.println("Setx to  0      "+temp1);
		temp1.setr(2.0);
		System.out.println("Setr to  2.0    "+temp1);
		temp1.sett(Math.PI);
		System.out.println("Sett to  PI     "+temp1);
		temp1.setr(0);
		System.out.println("Setr to  0      "+temp1);
		temp1.setr(1);
		System.out.println("Setr to  1      "+temp1);
		temp1.setx(0);
		System.out.println("Setx to  0      "+temp1);
		temp1.sety(0);
		System.out.println("Sety to  0      "+temp1);

		temp1.setr(1);
		temp1.sett(0);
		for(int i=0; i<16; i++)
			{
			System.out.println("t ="+temp1.t+" quad ="+
				temp1.quadrant()+" octant="+temp1.octant());
			temp1.sett(temp1.t + Math.PI/8);
			}
		}
		
	}

/*
 * CircularBuffer.java
 */

package EDU.gatech.cc.is.util;

import java.util.Enumeration;
import java.io.*;

/**
 * Implements a circular buffer for storing things.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class CircularBuffer implements Cloneable, Serializable
	{
	/**
	 * size of the buffer.  Default size is 10.
	 */
	protected int buf_size = 10;


	/**
	 * current cell in the buffer.
	 */
	protected int current = 0;


	/**
	 * total number of items ever added to the buffer.
	 * This can help reveal if data is ever lost by an enumeration.
	 */
	protected int total = 0;


	/**
	 * the buffer itself.
	 */
        protected Object[] buffer = new Object[buf_size];


	/**
	 * create a CircularBuffer with default values.
	 */
	public CircularBuffer()
		{
		}


	/**
	 * create a CircularBuffer with a specific number of slots.
	 * @param s int, number of slots.
	 */
	public CircularBuffer(int s)
		{
		buf_size = s;
        	buffer = new Object[buf_size];
		}


	/**
	 * adds an item to the CircularBuffer.
	 * @param i Object, the item to add to the buffer.
	 */
	public synchronized void put(Object i)
		{
		buffer[current] = i;
		total++;
		current++;
		if (current>=buf_size) current = 0;
		notifyAll();
		}


	/**
	 * clears this circular buffer.
	 */
	public synchronized void clear()
		{
		total=0;
		current=0;
		}


	/**
	 * returns an enumeration of the values in this circular buffer.
	 * @return the enumeration.
	 */
	public CircularBufferEnumeration elements()
		{
		return(new CircularBufferEnumeration(this));
		}

	}


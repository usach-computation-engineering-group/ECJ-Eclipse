/*
 * CircularBufferEnumeration.java
 */

package EDU.gatech.cc.is.util;

import java.io.*;
import java.util.*;

/**
 * Implements an enumeration of items in a circular buffer.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class CircularBufferEnumeration 
	implements Enumeration, Cloneable, Serializable
	{
	/**
	 * the buffer we are enumerating.
	 */
	private CircularBuffer	buffer;


	/**
	 * the last cell read.
	 */
	protected int position = 0;


	/**
	 * total number of items ever read from the buffer by this enumeration.
	 */
	protected int total = 0;


	/**
	 * create a CircularBufferEnumeration with a circular buffer.
	 * @param cb CircularBuffer, the buffer to enumerate.
	 */
	public CircularBufferEnumeration(CircularBuffer cb)
		{
		buffer = cb;
		if (buffer.total>=buffer.buf_size)
			{
			position = buffer.current;
			total = buffer.total - buffer.buf_size;
			}
		}


	/**
	 * Tests if this enumeration contains more elements. 
	 * @return true if this enumeration contains more elements; 
	 * false otherwise. 
	 */
	public synchronized boolean hasMoreElements()
		{
		if (buffer.total>total) return(true);
		else return(false);
		}


	/**
	 * Returns the next element of this enumeration. 
	 * @return the next element of this enumeration. 
	 * @throws NoSuchElementException if no more elements exist. 
	 */
	public synchronized Object nextElement() throws NoSuchElementException
		{
		if (buffer.total==total) throw new NoSuchElementException();
		total++;
		Object retval = buffer.buffer[position];
		position++;
		if (position >= buffer.buf_size) position = 0;
		return(retval);
		}


	/**
	 * Returns the next element matching the requested class.
	 * Implicitly consumes other types of elements.
	 * @return the next element of this enumeration. 
	 */
	public Object waitMatchingElement(Class c)
		{
		Object retval = null;
		while (!c.isInstance(retval))
			{
			synchronized (buffer)
				{
				try 
					{
					retval = nextElement();
					}
				catch(NoSuchElementException e)
					{
					// wait at most 100ms
					try {buffer.wait();}
					catch(InterruptedException ie) {}
					}
				}
			}
		return(retval);
		}


	/**
	 * Tests circular buffer enumeration.
	 */
	public static void main(String[] args)
		{
		CircularBuffer cb = new CircularBuffer(13);
		CircularBufferEnumeration cbe = 
			new CircularBufferEnumeration(cb);
		for(int i=0; i<5; i++)
			{
			String item = String.valueOf(i);
			cb.put(item);
			}
		try
			{
			while (cbe.hasMoreElements())
				{
				String item = (String)cbe.nextElement();
				System.out.print(item+" ");
				}
			}
		catch(NoSuchElementException e){}
		for(int i=0; i<13; i++)
			{
			String item = String.valueOf(i);
			cb.put(item);
			}
		try
			{
			while (true)
				{
				String item = (String)cbe.nextElement();
				System.out.print(item+" ");
				}
			}
		catch(NoSuchElementException e){System.out.println("OK");}
		}
	}


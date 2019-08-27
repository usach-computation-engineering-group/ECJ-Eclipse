/*
 * Polygon2.java
 */

package EDU.cmu.cs.coral.util; 

import java.io.*;
import java.lang.*;
import java.util.*;
import EDU.gatech.cc.is.util.*;

/**
 * A class for manipulating Polygon2s.  
 * 
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1998 Tucker Balch and Carnegie Mellon University
 *
 * @author Rosemary Emery
 * @version $Revision: 1.1 $
 
 */

public class Polygon2 implements Cloneable, Serializable
	{
	// vertices are stored in clockwise order
	// edge zero runs from vertices[0] to vertices[1], edge one from vertices[1] to vertices[2]
	// edge n from vertices[n] to vertices[0] where n is total number of edges
	public Vector vertices = new Vector(); // a vector of Vec2s, each one implementing

	public Polygon2()
		{
		vertices = new Vector();
		}

	public Polygon2(Vector vec)
		{
		vertices = new Vector();
		for (int i=0;i<vec.size();i++)
			{
			vertices.addElement(vec.elementAt(i));
			}
		}

	public Polygon2(Vec2 vertex)
		{
		vertices = new Vector();
		vertices.addElement(vertex);
		}

	public Polygon2(Polygon2 p)
		{
		vertices = new Vector();
		for (int i=0;i<p.vertices.size();i++)
			{
			vertices.addElement(p.vertices.elementAt(i));
			}
		}

	/**
	 * Create a new Polygon2 by cloning.
	 * @param p Polygon2, the Polygon2 to copy.
	 */
	public 	Object clone()
		{
		return(new Polygon2(this));
		}

	/**
	 * add a vertex to the Polygon2. 
	 * @param vertex the new vertex.
	 */

	public void addVertex(Vec2 vertex)
		{
		vertices.addElement(vertex);
		}
	/**
	 * return the ith vertex of the Polygon2. 
	 * @param vertexNumber the ith vertex.
	 */

	public Vec2 returnVertex(int vertexNumber)
		{
		if (vertexNumber < vertices.size())
			{
			return (Vec2)vertices.elementAt(vertexNumber);
			}
		return null;
		}

	/**
	 * translate the Polygon2 to a new origin 
	 * @param offset, the new origin of the translated Polygon2
       */
	public void offsetVertices(Vec2 offset)
		{
		Vec2 temp;
		for(int i=0;i<vertices.size();i++)
			{
			temp = (Vec2)vertices.elementAt(i);
			temp.add(offset);
			vertices.removeElementAt(i);
			vertices.insertElementAt(temp, i);
			}
		}

	public boolean pointWithinPolygon(Vec2 point)
		{
		int i = 0;
		double outside;
		Vec2 temp1, temp2;
		boolean flag = true;
		// assumes vertices given clockwise
		while(i<vertices.size())
			{
			temp1 = (Vec2)vertices.elementAt(i);
			temp2 = (Vec2)vertices.elementAt((i+1)%(vertices.size()));
			outside = (point.y-temp1.y)*(temp2.x-temp1.x)-(point.x-temp1.x)*(temp2.y-temp1.y);
			if (outside > 0) // point is to left of line (if on line stop)
				{
				flag = false;
				break; 
				}
			i++;
			}

		return flag;
		}

	public boolean lineIntersectsWithPolygon(Vec2 vertex1, Vec2 vertex2)
		{
		int i=0;
		Vec2 tempVec1, tempVec2;
		Vec2 V1;
		Vec2 V2 = new Vec2(vertex2.x-vertex1.x, vertex2.y-vertex1.y);
		double t,s, denom;
		while(i<vertices.size())
			{
			tempVec1 = (Vec2)vertices.elementAt(i);
			tempVec2 = (Vec2)vertices.elementAt((i+1)%(vertices.size()));
			V1 = new Vec2(tempVec2.x-tempVec1.x, tempVec2.y-tempVec1.y);
			denom = -V1.x*V2.y + V1.y*V2.x;
			t = (-1.0*V2.y*(vertex1.x - tempVec1.x) + V2.x*(vertex1.y-tempVec1.y))/denom;
			s = (-1.0*V1.y*(vertex1.x-tempVec1.x) + V1.x*(vertex1.y-tempVec1.y))/denom;
			if ((t > 0.005 && t < 0.995) && (s > 0.005 && s < 0.995))
				{
//				System.out.println("s t " + s + " " + t);
//				System.out.println("t*vec 1 " + (t*V1.x + tempVec1.x) + " " + (t*V1.y + tempVec1.y));
//				System.out.println("s*vec 2 " + (s*V2.x + vertex1.x) + " " + (s*V2.y + vertex1.y));

				// as the intersection point is tV1 + tempVec1 t must lie between
				// 0 and 1 in order for the intersection point to lie between tempVec1 and
				// tempVec2
				// similarly it is equal to sV2 + vertex1 and so s must lie between
				// 0 and 1 in order for the intersection point to lie between vertex1
				// and vertex 2, thus if both t and s are between these limits the
				// intersection point is on the edge of the polygon and some form
				// of collision (or just touching) is occuring

				return true;
				}
			i++;
			}
		return false;
		}
	}

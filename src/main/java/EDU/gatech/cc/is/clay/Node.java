/*
 * Node.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;

/**
 * The basic unit for constructing behaviors in Clay.
 * Nodes may have complex constructors - indicating which other
 * nodes are embedded within them.  This is the super-class of all
 * nodes.
 * <P>
 * For detailed information on how to configure behaviors, see the
 * <A HREF="../clay/docs/index.html">Clay page</A>.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1997, 1998 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.1 $
 */

public class Node
	{
	/**
	 * Turns debug printing on or off.
	 */
	public	final static boolean DEBUG = false;

	/**
	 * The nodes recursively embedded in this node.
	 */
	protected Node[] embedded_nodes;

	/**
	 * Begins a new trial.  This is implemented with an empty method,
	 * so that classes that extend Node may choose to ignore it.
	 */
	public void initTrial()
		{
		}
	}

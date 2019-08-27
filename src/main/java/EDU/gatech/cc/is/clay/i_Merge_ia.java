/*
 * i_Merge_ia.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;

/**
 * Merge the output of several embedded integer nodes
 * into a single state number.
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

public class i_Merge_ia extends NodeInt
	{
	private	long	lasttime = -1;
	private	int	lastval;
	private	NodeScalar[] imbedded;
	private	int	sizes[];
	private int	count;

	/**
	Instantiate an i_Merge_ia node.

	@param imbeddedin NodeInt[], the array of integer nodes to
		merge.
	@param sizes int[], the maximum value of each node.
	*/
	public i_Merge_ia(NodeInt[] imbeddedin, int[] sizesin)
		{
		imbedded = imbeddedin;
		sizes = sizesin;
		count = imbedded.length;
		if (count != sizes.length)
			System.out.println("i_Merge_ia: number of nodes doesn't match number of sizes");
		}
	
	/**
	Get the merged value.
	
	@return the int value.
	*/
	public int Value(long timestamp)
		{
		if (timestamp != lasttime)
			{
			int i;
			lastval = imbedded[0].intValue(timestamp);
			for(i=1; i<count; i++)
				{	
				lastval = lastval * sizes[i-1];
				lastval += imbedded[i].intValue(timestamp);
				}
			lasttime = timestamp;
			}
		return lastval;
		}
	}

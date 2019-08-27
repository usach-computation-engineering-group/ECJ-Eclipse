/*
 * i_Merge_ba.java
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;

/**
 * Merge the outputs of embedded boolean nodes
 * into a single int. embedded[0] is most significant bit,
 * embedded[embedded.length] is least significant bit.
 * Configuration is by setting the embedded[] array directly.
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

public class i_Merge_ba extends NodeInt
	{
	private	long	lasttime = -1;
	private	int	lastval;

	/**
	Maximum number of embedded nodes
	*/
	public static final int MAX_EMBEDDED = 20;

	public	NodeBoolean[] embedded = new NodeBoolean[MAX_EMBEDDED];

	/**
	Instantiate an i_Merge_ba node.
	*/
	public i_Merge_ba()
		{
		}
	
	/**
	Get the integer value.
	
	@param timestamp long, the time of the request.
	@return the integer value.
	*/
	public int Value(long timestamp)
		{
		if (timestamp != lasttime)
			{
			lastval = 0;
			lasttime = timestamp;
			int i;
			if (embedded[0] != null)
				lastval = embedded[0].intValue(timestamp);
			for(i=1; embedded[i]!=null ; i++)
				{	
				lastval = lastval * 2;
				lastval += embedded[i].intValue(timestamp);
				}
			if (false) System.out.println(lastval);
			}
		return lastval;
		}
	}

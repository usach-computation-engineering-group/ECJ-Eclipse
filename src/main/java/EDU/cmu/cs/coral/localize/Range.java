/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999 by John Sweeney and Carnegie Mellon University.
 */


package EDU.cmu.cs.coral.localize;

import java.lang.Math;
import java.lang.Double;
import java.lang.NumberFormatException;
import java.util.StringTokenizer;

public class Range {

    public double low;
    public double high;

    public Range() {
	low = Double.MIN_VALUE;
	high = Double.MAX_VALUE;
    }

    public Range(double l, double h) {
	low = l;
	high = h;
    }

    public Range(String str) {
	StringTokenizer st = new StringTokenizer(str);

	if (st.countTokens() != 2) {
	    low = Double.MIN_VALUE;
	    high = Double.MAX_VALUE;
	}else {
	    try {
		low = Double.valueOf(st.nextToken()).doubleValue();
		high = Double.valueOf(st.nextToken()).doubleValue();
	    }
	    catch (NumberFormatException e) {       
		System.out.println(e.toString());
	    }
	}
    }
}

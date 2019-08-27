/*filename: Sample.java
 */
/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;

public class Sample {
    public static final int x = 0; //x position
    public static final int y = 1; //y position
    public static final int t = 2; //theta (heading angle)
    public static final int w = 3; //weight
    public static final int size = 4; 
    public double[] data;

    public Sample() {
	data = new double[size];

    }

    public Sample(double xx, double yy, double tt) {
	data = new double[size];

	data[x] = xx;
	data[y] = yy;
	data[t] = tt;
	data[w] = 1.0;
    }

    public Sample(Sample s) {
	data = new double[size];
	data[x] = s.data[x];
	data[y] = s.data[y];
	data[t] = s.data[t];
	data[w] = s.data[w];
    }

    public String toString() {
	String str = new String("("+data[x]+", "+data[y]+", "+data[t]+", "+
				data[w]+")");

	return str;
    }
}

    

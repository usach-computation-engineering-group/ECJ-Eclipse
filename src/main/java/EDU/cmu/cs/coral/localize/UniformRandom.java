/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;
import java.util.Random;
import EDU.gatech.cc.is.util.Vec2;

public class UniformRandom extends Random {

    double low;
    double high;

    public UniformRandom(long seed, double l, double h) {
	super(seed);

	low = l;
	high = h;
    }

    public double getValue() {
	return (low + ((high - low) * nextDouble()));
    }

    public double getValue(double l, double h) {
	return (l + ((h-l)*nextDouble()));
    }

    public void setRange(double l, double h) {high = h; low = l;}
}

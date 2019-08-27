/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;

import EDU.gatech.cc.is.util.Vec2;

public class UniformSampler {

    protected UniformRandom ur;

    protected int numVars;

    protected double [] rangeLow;
    protected double [] rangeHigh;


    public UniformSampler(long s, int nv) {
	numVars = nv;

	ur = new UniformRandom(s, 0.0, 1.0);

	rangeLow = new double[numVars];
	rangeHigh = new double[numVars];

	for (int i =0;i < numVars; i++) {
	    rangeLow[i] = 0.0;
	    rangeHigh[i] = 1.0;
	}
    }

    public Sample generateSample() {
	Sample s = new Sample();

	for (int i =0; i < numVars; i++) {
	    ur.setRange(rangeLow[i], rangeHigh[i]);

	    s.data[i] = ur.getValue();
	}
	
	return s;
    }

  public double [] generateSampleArray() {
    double [] res = new double[3];

    for (int i =0; i < numVars; i++) {
      ur.setRange(rangeLow[i], rangeHigh[i]);
      res[i] = ur.getValue();
    }

    return res;
  }

    public void setRange(int i, double lo, double hi) {
	rangeLow[i] = lo;
	rangeHigh[i] = hi;
    }

    public void setRange(int i, Range r) {
	rangeLow[i] = r.low;
	rangeHigh[i] = r.high;
    }

    public void setRange(int i, Vec2 v) {
	rangeLow[i] = v.x;
	rangeHigh[i] = v.y;
    }
}

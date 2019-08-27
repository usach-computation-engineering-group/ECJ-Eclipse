/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;

public abstract class SampleUpdater extends GaussianEvaluator {


    public SampleUpdater(int nv) {
	super(nv);
    }


    public abstract void updateSample(Sample s);
}

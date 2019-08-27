
/** this will help evaluate gaussians....
    This is adapted from Scott Lenser's code.  
*/

/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;

import java.lang.Math;

public class GaussianEvaluator extends Gaussian {
    
    protected double minProb;
    protected static final double NORM = 1.0/ Math.sqrt(2.0*Math.PI);
    public GaussianEvaluator() {
	super();
	minProb = 0.0;
    }
    
    public void init(int nv) {
	super.init(nv);
    }
    
    public GaussianEvaluator(int nv) {
	super(nv);
	
	minProb = 0.0;
    }
    
    public double[] evaluate(double[] input) {
       
	
	double x,val;
	double [] out = new double[numVars];
	for (int i = 0; i < numVars; i++) {
	    x = (input[i] - mean[i])/ dev[i];

	    val = Math.exp(-(x*x)/2)*NORM /*/dev[i]*/;

	    
	    if (val < minProb) 
		val = minProb;
	    //    System.out.println("GE: in["+i+"] = "+input[i]+" mean = "+mean[i]+" val = "+val);
	    out[i] = val;
	}
	
	return out;
    }
    
    public void setMinProb(double p) {
	minProb = p;
    }
}

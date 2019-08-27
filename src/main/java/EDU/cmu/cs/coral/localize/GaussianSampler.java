/** generates gaussian's and stuff.
    heavily adapted from scott lenser's code!
*/

/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;
import java.lang.Math;


public class GaussianSampler extends Gaussian implements Sampler{
    
    protected Range [] range;
    protected Range [] normRange;
    protected boolean [] noBounds;
    protected static boolean iset;
    protected static double gset;

    protected static boolean isetTails;
    protected static double gsetTails, lastL, lastU;
    protected static UniformRandom ur;

    public GaussianSampler(int nv, long seed) {
	super(nv,seed);
	
	range = new Range[numVars];
	noBounds = new boolean[numVars];
	normRange = new Range[numVars];
	
	for (int i =0; i < numVars; i++) {
	    range[i] = new Range();
	   
	    noBounds[i] = true;
	    normRange[i] = new Range();
	}
	    
	iset = false;
	isetTails = false;

	ur = new UniformRandom(seed,0.0, 1.0);
    }
    public GaussianSampler(int nv) {
	super(nv);
	
	range = new Range[numVars];
	noBounds = new boolean[numVars];
	normRange = new Range[numVars];
	
	for (int i =0; i < numVars; i++) {
	    range[i] = new Range();
	   
	    noBounds[i] = true;
	    normRange[i] = new Range();
	}
	    
	iset = false;
	isetTails = false;

	ur = new UniformRandom(getSeed(),0.0, 1.0);
    }
    
    public void setRange(int i, double l, double h) {
	range[i].low = l;
	range[i].high = h;
	
	normRange[i].low = (range[i].low - mean[i]) / dev[i];
	normRange[i].high = (range[i].high - mean[i]) / dev[i];
	
	if (range[i].low == Double.MIN_VALUE &&
	    range[i].high == Double.MAX_VALUE)
	    noBounds[i] = true;
	else
	    noBounds[i] = false; 
    }
    
    public void setRange(int i, Range r) {
	setRange(i, r.low, r.high);
    }
    
    
    public double generateDist(double meen, double stddev) {
	double v1,v2, r, a, fac, res;
	v1 = v2 =0.0;
	//	UniformRandom  ur = new UniformRandom(getSeed(), -1.0, 1.0);
	ur.setRange(-1.0, 1.0);
	if (iset == false) {
	    a = r = 1.0;
	    while (r >= a) {
		v1 = ur.getValue();
		v2 = ur.getValue();
		
		r = v1*v1 + v2*v2;
	    }
	    
	    fac = Math.sqrt(-2.0*Math.log(r)/r);
	    gset = v1*fac;
	    iset = true;
	    res = v2*fac;
	} else {
	    iset = false;
	    res = gset;
	}
	
	return (meen+res*stddev);
    }
    
    protected double generateDist(double meen, double stddev, double normL, 
				  double normU) {
	double l = normL;
	double u = normU;
       
	double fac, r, v1, v2, m, a, res;
	//	UniformRandom ur = new UniformRandom(getSeed(), 0, 1.0);
	ur.setRange(0.0, 1.0);
	do {
	    if (isetTails == false || l != lastL || u != lastU) {
		lastL = l;
		lastU = u;

		if (u < 0 || l > 0) {
		    m = Math.max(l,-u);
		    a = Math.exp(-0.5 * m * m);
		    
		    do {
			ur.setRange(-a,a);
			v1 = ur.getValue();
			v2 = ur.getValue();
			r = v1*v1 + v2*v2;
		    } while( r >= a );
		} 
		else {
		    a = 1.0;
		    
		    do {
			ur.setRange(-1.0, 1.0);
			v1 = ur.getValue();
			v2 = ur.getValue();
			r = v1*v1 + v2*v2;
		    } while( r >= a );
		}

		fac = Math.sqrt(-2.0 * Math.log(r) / r);
		gsetTails = v1*fac;
		isetTails = true;

		res = v2*fac;
	    }
	    else {
		isetTails = false;
		res = gset;
	    }

	    if ((u < 0 && res > 0) || (l > 0 && res < 0))
		res = -res;
	} while (res < l || res > u);
	
	return (meen + res*stddev);
    }
		
    public Sample generateSample() {
	Sample s = new Sample();
	for (int i =0; i < numVars; i++) {
	    if (noBounds[i] == true) {
		s.data[i] = generateDist(mean[i], dev[i]);
	    }else {
		s.data[i] = generateDist(mean[i], dev[i], 
					 normRange[i].low, normRange[i].high);
	    }
	}

	return s;
    }

}
    
      

/* filename: gaussian.java
 */
/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */

/* this was adpated from scott lenser's code.*/

package EDU.cmu.cs.coral.localize;

/*things a gaussian needs*/

public abstract class Gaussian {
  
  protected static long randSeed=2; //seed value for all rands in this gaussian
  protected int numVars;
  protected double [] mean;
  protected double [] dev;
  
    public Gaussian(long seed) {
	numVars = 1;
	mean = new double[numVars];
	dev = new double[numVars];

	for (int i = 0;i < numVars; i++) {
	    mean[i] = 0.0;
	    dev[i] = 1.0;
	}

	randSeed = seed;
    }

  public Gaussian() {
	numVars = 1;
	mean = new double[numVars];
	dev = new double[numVars];

	for (int i = 0;i < numVars; i++) {
	    mean[i] = 0.0;
	    dev[i] = 1.0;
	}
    }


    public void init(int nv) {
	numVars = nv;
    
    mean = new double[numVars];
    dev = new double[numVars];
    
    for (int i =0 ; i < numVars; i++) {
      mean[i] = 0.0;
      dev[i] = 1.0;
    }
  }
   
  public Gaussian(int nv, long seed) {
    numVars = nv;
    
    mean = new double[numVars];
    dev = new double[numVars];
    
    for (int i =0 ; i < numVars; i++) {
      mean[i] = 0.0;
      dev[i] = 1.0;
    }
    randSeed = seed;
  }
  
  public Gaussian(int nv) {
    numVars = nv;
    
    mean = new double[numVars];
    dev = new double[numVars];
    
    for (int i =0 ; i < numVars; i++) {
      mean[i] = 0.0;
      dev[i] = 1.0;
    }
  }

  
  public int getNumVars() { return numVars;}
  
  public void setMean(int i, double mn) {mean[i] = mn;} //set the mean for var i
  public void setDev(int i, double dv) {dev[i] = dv;} //set the standard deviation for var i
  
  public double getMean(int i) { return mean[i]; }
  public double getDev(int i) { return dev[i]; }

  public long getSeed() { return randSeed;}
  public void setSeed(long s) { randSeed = s;}
}
  

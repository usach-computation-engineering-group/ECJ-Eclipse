/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;

import java.util.Random;


public class SampleSet {
  
    protected int numSamples;
    protected int current;
    protected Sample []samples;
    protected static boolean samplesNormalized;
    protected static UniformRandom ur; 
    protected double [] cumWeights;
    //   protected SampleSet newSampleSet;
    protected Sample [] newSamples;

    protected double [] mean;
    protected double [] var;
    
    public SampleSet(int n) {
	numSamples = n;
	
	samples = new Sample[numSamples];
	newSamples = new Sample[numSamples];
	for (int i =0; i < numSamples; i++) {
	    samples[i] = new Sample();
	    newSamples[i] = new Sample();
	}

	samplesNormalized = false;
	current = 0;

	ur = new UniformRandom(23, 0.0, 1.0);

	cumWeights = new double[numSamples+1];
	//	newSampleSet = new SampleSet(numSamples);
	
    }

    public SampleSet(SampleSet s) {
	numSamples = s.numSamples;

	samples = new Sample[numSamples];
	samplesNormalized = false;
	current =0;

	for (int i =0; i< numSamples; i++) {
	    samples[i] = new Sample(s.samples[i]);
	}
    }
	
    public int getNumSamples() {return numSamples;}

    public void reset() { current =0;}

    public Sample getNextSample() {
	if (current >= numSamples)
	    current =0;

	return samples[current++];
    }
    
    public boolean haveMoreSamples() {
	if (current < (numSamples))
	    return true;
	return false;
    }
    
    public void setSample(int i, Sample s) {
	samples[i] = s;
    }
    
    public double normalizeSamples() {
	if (samplesNormalized == true) 
	    return -1.0;
	
	double cumWeight = 0.0;
	for (int i =0; i < numSamples; i++) {
	    cumWeights[i] = cumWeight;
	    cumWeight+= samples[i].data[Sample.w];
	}
	cumWeights[numSamples] = cumWeight;

	//	System.out.println("SS: normalize: cumWeight = "+cumWeight);
	
	for (int i= 0; i < newSamples.length; i++) {
	    double cumWeightToFind = ur.getValue(0.0, cumWeight);
	    
	    int low, hi, mid;

	    low = 0;
	    hi = numSamples-1;
	    
	    while (low < hi-1) {
		mid = (low + hi) / 2;

		if (cumWeightToFind < cumWeights[mid]) 
		    hi = mid;
		else 
		    low = mid;
	    }
	    
	    
	    newSamples[i].data[0] = samples[low].data[0];
	    newSamples[i].data[1] = samples[low].data[1];
	    newSamples[i].data[2] = samples[low].data[2];
	    newSamples[i].data[Sample.w] = 1.0;
	}

	Sample []tmp = samples;
	samples = newSamples;
	newSamples = tmp;

	samplesNormalized = true;
	return cumWeight/numSamples;
    }
	
  public Sample getMeanVariance() {
      return new Sample(7, 7, 7);
  }


  public void setNormalized(boolean n) {
    samplesNormalized = n;
  }
}



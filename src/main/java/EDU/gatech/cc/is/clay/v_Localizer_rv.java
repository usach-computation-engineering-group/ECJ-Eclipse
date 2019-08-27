/* Filename: v_Localizer_rv.java
 * Author: John Sweeney
 */

/* This code is part of the clay package of TeamBots.
 * Copyright (c) 1999 by John Sweeney and Carnegie Mellon University.
 */

package EDU.gatech.cc.is.clay;

import java.lang.*;

import EDU.gatech.cc.is.simulation.*;
import EDU.gatech.cc.is.util.*;
import EDU.gatech.cc.is.abstractrobot.Simple;

import EDU.cmu.cs.coral.localize.*;
import EDU.cmu.cs.coral.simulation.*;
import EDU.cmu.cs.coral.abstractrobot.*;
import EDU.cmu.cs.coral.abstractrobot.DisplayVectors;

import java.lang.Thread;
import java.lang.InterruptedException;
import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * This determines the robots location, given an abstract_robot and a visible
 * landmark as
 * input.  This node is triggered on sighting a landmark and will 
 * determine the global position of the robot given the egocentric
 * distance to the landmark.
 *
 * @author John Sweeney
 * @version $Revision: 1.7 $
 */


public class v_Localizer_rv extends NodeVec2 {
    
    public static final boolean DEBUG = Node.DEBUG;
  
    public Vec2 last_val; //what we outputted last time
  public long lasttime; //last timestamp
  
  protected LocalizationRobot robot;
  protected SimulatedObject[] landmarks;
  protected boolean [] ambigLandmarks;
  protected boolean doneAmbigUpdate;
  protected int numAmbigLM;
  protected int [] ambigLMIndex;

    protected DoubleRectangle[] landmarkAreas;

  protected double []samplesX; //the samples x coord
  protected double [] samplesY; //the y coord
  protected double [] samplesT; //the theta
  protected double [] samplesW; //the samples weight

  protected int [] ambigClosest;

  //this is for normalization
  protected double [] newSamplesX;
  protected double [] newSamplesY;
  protected double [] newSamplesT;

  protected boolean samplesAreNormalized;

  protected SampleSet samples;
  
    protected int numCorrectVisionClass;

    protected Vec2 [] samplesPos;
    protected Vec2 [] samplesMag;
    protected Color [] samplesColor;

  protected static LandmarkSampler landmarkSampler;
  protected LineLandmarkSampler lineSampler;

    protected int numSamples;
   
    protected UniformSampler us;
    protected UniformRandom ur;

    protected int getPosCount;
  
    protected FileWriter errorOutFile;
    protected int epochCounter;


    protected long moveUpdateCnt;
    protected long sensorUpdateCnt;

  protected LandmarkSampleUpdater lmUpdater;
  protected MovementSampleUpdater moveUpdater;
  protected LineSampleUpdater lineUpdater;

  protected int numSensorVars;
  protected int numMoveVars;

  protected LineSim [] allTheLines;
  protected int useLines;
  protected FileWriter logFile;

  protected Random intGen;
    /**
     * Instantiate a v_Localizer_rv node
     * @param ar SimpleInterface, the abstract_robot object
     */
    public v_Localizer_rv(LineLocalizationRobot ar) {
	if (DEBUG) {  
	    System.out.println("v_Localizer_r: instantiated");
	}
	
	robot = ar;
	
	lasttime = -1;
	last_val = new Vec2(0,0);
	int SeeD = ((Simple)robot).getDictionary().getInt("LOCALIZER_RANDOM_SEED");;

	us = new UniformSampler(SeeD, 3);
	ur = new UniformRandom(SeeD, 0.0, 1.0);

	numSamples = ((Simple)robot).getDictionary().getInt("LOCALIZER_NUM_SAMPLES");
	
	//these are the samples of our position
	samples = new SampleSet(numSamples);

	//allocate space for all the samples...
	samplesX = new double[numSamples];
	samplesY = new double[numSamples];
	samplesT = new double[numSamples];
	samplesW = new double[numSamples];
	newSamplesX = new double[numSamples];
	newSamplesY = new double[numSamples];
	newSamplesT = new double[numSamples];

	landmarkSampler = new LandmarkSampler(2, robot);
	lineSampler = new LineLandmarkSampler(2, ((LineLocalizationRobot)robot).getLines(), 
					      (LineLocalizationRobot)robot);

	//get the landmarks and set up the landmark areas...
	landmarks = robot.getLandmarks();
	ambigLandmarks = robot.getAmbigLandmarks();
	numAmbigLM = 0;
	for (int i = 0; i < ambigLandmarks.length; i++) {
	  if (ambigLandmarks[i]) {
	    numAmbigLM++;
	  }
	}
	ambigLMIndex = new int[numAmbigLM];
	int j =0;
	for (int i=0; i < ambigLandmarks.length; i++) {
	  if (ambigLandmarks[i]) {
	    ambigLMIndex[j++] = i;
	    System.out.println("ambigLMIndex["+(j-1)+"]="+ambigLMIndex[j-1]);
	  }
	}

	ambigClosest = new int[numSamples];
	intGen = new Random();

	landmarkAreas = new DoubleRectangle[landmarks.length];
	for (int i =0; i < landmarkAreas.length; i++) {
            double newx, newy, side;
            newx= (double)landmarks[i].getPosition().x - 
                ((LandmarkSim)landmarks[i]).getRadius();
            newy = (double) landmarks[i].getPosition().y + 
                ((LandmarkSim)landmarks[i]).getRadius();
            side = ((LandmarkSim)landmarks[i]).getRadius()*2.0;
            landmarkAreas[i] = new DoubleRectangle(newx, newy, side, side);
	}

	//set up the landmark updater
	numSensorVars = 2;
	lmUpdater = new LandmarkSampleUpdater(numSensorVars);
	
	//set up movement updater
	numMoveVars = 3;
	moveUpdater = new MovementSampleUpdater(numMoveVars);

	//uniformly distribute the points
	resetPosition();

	//these are for displaying the points
	samplesPos = new Vec2[numSamples+5];
	samplesMag = new Vec2[numSamples+5];
	samplesColor = new Color[numSamples+5];
       
	//make an "arrow" vector for each sample
	//samples.reset();
	int i =0;
	for (i=0; i < numSamples; i++) {
	  samplesPos[i] = new Vec2(samplesX[i],samplesY[i]);
	    samplesMag[i] = new Vec2(Math.cos(samplesT[i])*0.3,
				     Math.sin(samplesT[i])*0.3);
	}

	//these are for the estimated position indicator
	for ( ; i < samplesPos.length ; i++) {
            samplesPos[i] = new Vec2(0,0);
            samplesMag[i] = new Vec2(0,0);
	}

	//this will give the robot the poitns to display
	((Simple)robot).displayVectors = new DisplayVectors(samplesPos, samplesMag);

	numCorrectVisionClass = 0;
	for (i=0; i < landmarks.length; i++) {
	    if (landmarks[i].getVisionClass() == 0) 
		numCorrectVisionClass++;
	}

	//this is for line localization....
	allTheLines = ((LineLocalizationRobot)robot).getLines();

	//set up the line updater
	lineUpdater = new LineSampleUpdater(2); //2 vars

	lineUpdater.setMapLines(allTheLines);
	//double lr = (double) ((Simple)robot).getDictionary().getDouble("LINE_LOCALIZER_RESOLUTION");
	//lineUpdater.setLineScanResolution( lr);
	//lineUpdater.setVisionRange(((SimpleCye)robot).VISION_RANGE);
	//lineUpdater.setFieldOfView(((SimpleCye)robot).VISION_FOV_RAD);

	useLines = ((Simple)robot).getDictionary().getInt("USE_LINES");

	//check to see if we are logging the output here...
	String logFileName = ((Simple)robot).getDictionary().getString("LOCALIZER_LOG_FILE");
	logFile = null;
	if (logFileName != null) {
	  try {
	    logFile = new FileWriter(logFileName);
	  }
	  catch (IOException e) {
	    System.out.println("could not open "+logFileName+" for writing!");
	    logFile = null;
	  }
	}
	
	getPosCount = 0;

	epochCounter = 0;

        moveUpdateCnt=0;
        sensorUpdateCnt=0;
    }
   
 
    /**
     * Returns a Vec2 representing the robots belief of where it is
     * @param timestamp long, only get new info if timestamp > than last
     * call of timestamp == -1.
     * @return the robots estimate of its position
     */ 
    public Vec2 Value(long timestamp) {
	Vec2 calc,t,pos;
	boolean gotit = false;
	int seenLandmark= -1;
	Sample position = new Sample(0,0,0);
	int i=0;
	int seenLM;
	//	System.out.println("v_Localizer_r: Value()");
	
	//	if (!startupWaitHack) {
	/*	    try {
                    Thread.sleep(1000,0);
                    }
                    catch (InterruptedException e) {
                    System.out.println("foo foo");
                    } */
        //	    startupWaitHack = true;
        //	}

	if ((timestamp > lasttime)||(timestamp == -1)) {
	    //reset the timestamp
	    if (timestamp > 0) 
		lasttime = timestamp;
	 
	    //update position estimate from movement info
	    updatePositionMovement();
	 
	    //update position est from sensor inf
	     seenLM = updatePositionSensor();

	     if (useLines != 0) {
	       if (seenLM == 0) {
		 updatePositionLines(timestamp);
	       }
	     }
	    //whats our current position estimate?
	    position = getPosition();
	    //    System.out.println("VLOC: "+position.toString());
	               
	    //  Vec2 [] results = ((LineLocalizationRobot)robot).getVisualLines(timestamp,7);
	    /*
	    for (int y =0; y < results.length; y += 2) {
	      if (results[y] != null) {
		System.out.println("VLC: results["+y+"] = "+results[y].toString());
		System.out.println("VLC: results["+y+1+"] = "+results[y+1].toString());
		
	      } 
	    }
	    */
	    //	    System.out.println("MVCNT: "+moveUpdateCnt+" SNSCNT: "+sensorUpdateCnt);

	    last_val.setx(position.data[Sample.x]);
	    last_val.sety(position.data[Sample.y]);
	    //	    System.out.println("epoch = "+epochCounter);
	    
	    Vec2 realPos = ((Simple)robot).getPosition(timestamp);

	    //lets log it if we have a file...
	    if (logFile != null) {
	      try {
		//<estimated x> <est y> <est theta> <actual x> <act y> <act theta>
		while (position.data[Sample.t] < 0.0)
		  position.data[Sample.t] += Math.PI*2.0;
		
		logFile.write(epochCounter+++" "+
			      position.data[Sample.x]+" "+
			      position.data[Sample.y]+" "+
			      position.data[Sample.t]+" "+
			      realPos.x+" "+
			      realPos.y+" "+
			      ((Simple)robot).getSteerHeading(timestamp)+"\n");
	      }
	      catch (IOException e) {
		System.out.println("could not write to file!");
	      }
	    }
	    
	    //String err = new String(epochCounter+++" "+Math.abs(realPos.x - last_val.x)+
	    //				    " "+Math.abs(realPos.y - last_val.y)+"\n");

	}
	
	//this sets the points which we display on the screen
	for (i =0; i < numSamples; i++) {
	   
	    samplesPos[i].setx(samplesX[i]);
	    samplesPos[i].sety(samplesY[i]);

	    samplesMag[i].sett(samplesT[i]);
	    samplesMag[i].setr(0.3);
	    
	    samplesColor[i] = Color.black;
	}

	//we dont reset the value of i from previous for loop because we want
	//to access more samples...

	//this is for est pos indicator
	samplesPos[i] = new Vec2(last_val.x, last_val.y);
	samplesPos[i+1] = samplesPos[i];
	samplesPos[i+2] = samplesPos[i];
	samplesPos[i+3] = samplesPos[i];
	samplesPos[i+4] = samplesPos[i];

	//this gives the position indicator a "+" shape
	samplesMag[i] = new Vec2(0.0, 0.5);
	samplesMag[i+1] = new Vec2(0.5, 0.0);
	samplesMag[i+2] = new Vec2(0.0, -0.5);
	samplesMag[i+3] = new Vec2(-0.5, 0.0);
	//this is set in the average orientation direction
	samplesMag[i+4] = new Vec2(0.0, 0.5);
	samplesMag[i+4].sett(position.data[Sample.t]);

	//color each axis of the + a different color
	samplesColor[i] = Color.blue;
	samplesColor[i+1] = Color.red;
	samplesColor[i+2] = Color.blue;
	samplesColor[i+3] = Color.red;
	//this is green to indicate est. heading
	samplesColor[i+4] = Color.green;

	//display vector field
	((Simple)robot).displayVectors.set(samplesPos, samplesMag, samplesColor);


	//	((JohnRobotSim)robot).setEstimatePosition(last_val);

	return (new Vec2(last_val.x, last_val.y));
    }
  
  
  protected int updatePositionSensor() {
    double minProbGaussianSense = 0.02;
    
    int numLandmarks = robot.getNumLandmarks(); //use this call so we're not depentent on how landmarks are stored
    
    double [] param = new double[4];
    double goodSampleProb;
    CircularBufferEnumeration msgChannel;
    int numCreatedSamples=0;
    double ignoreLandmarkThreshold = 1.0 / (double) numLandmarks;
    int numSeenLandmark = 0;
    int seenLandmark =-1;
    int curr;

    doneAmbigUpdate = false;

    for (int i =0; i < numLandmarks; i++) {
      
      if (robot.getSeenLandmarkConfidence(i) < ignoreLandmarkThreshold) {
	continue;
      }

      lmUpdater.setMinProb(minProbGaussianSense);

      if (ambigLandmarks[i]) { 
	System.out.println("VLOC: UPS: updating ambig on lm="+i);
	updateAmbig(i);
      }
      else {
	((Simple)robot).setDisplayString("seeing "+i);
	
	numSeenLandmark++;
	seenLandmark = i;
	lmUpdater.setLocation(getLandmarkLocation(i));
	
	param[0] = robot.getLandmarkDistance(i);
	param[1] = robot.getLandmarkDistanceVariance(i);
	param[2] = robot.getLandmarkAngle(i);
	param[3] = robot.getLandmarkAngleVariance(i);
	
	for(int j =0;j < numSensorVars; j++) {
	  lmUpdater.setMean(j, param[j*2]);
	  lmUpdater.setDev(j, param[j*2+1]);
	}
	
	for (int j = 0; j < numSamples; j++) {  
	  samplesW[j] = lmUpdater.updateSample(samplesX[j], samplesY[j], 
					       samplesT[j], samplesW[j]);
	  // System.out.println("VLOC: UPS: samplesW[]="+samplesW[j]);
	}
	
	samplesAreNormalized = false;
      }
    }
    
    if (doneAmbigUpdate) {
      //already resampled so we can quit here
      doneAmbigUpdate =false;
      return numSeenLandmark;
    }

    sensorUpdateCnt += numSeenLandmark;
    
    goodSampleProb = normalizeSamples();

    if (goodSampleProb >= 0.0)
      System.out.println("VLOC: UPS: goodSampProb = "+goodSampleProb);
    double conf = 0.0;
    
    /* IDEA: keep x separate distributions, where x == numLandmarks,
       and then when we see a lm, update each sep dist.
    */
    
    if (numSeenLandmark > 0) {
      double expect = 0.054; //this is for samples 2 std dev from mean
      //double expect = ur.getValue(0.01, 0.058);
      //double expect = 0.058;
      //expect = expect*expect;
      expect = Math.pow(expect, 2.0*numSeenLandmark);
      
      //double numSensorSamplesReal = (1 - goodSampleProb/(expect))*
      //   samples.getNumSamples() + 0.5 - 1.0;
      double numSensorSamplesReal = (1 - goodSampleProb/(expect*.4))*
	samples.getNumSamples() + 0.5 - 1.0;
      // numSensorSamplesReal *= ur.getValue(0.7, 1.0);
   
      landmarkSampler.setPosition(getLandmarkLocation(seenLandmark));
      
      param[0] = robot.getLandmarkDistance(seenLandmark);
      param[1] = robot.getLandmarkDistanceVariance(seenLandmark);
      param[2] = robot.getLandmarkAngle(seenLandmark); //make sure this is right units
      param[3] = robot.getLandmarkAngleVariance(seenLandmark);
      
      for(int j = 0; j < landmarkSampler.getNumVars(); j++) {
	landmarkSampler.setMean(j,param[j*2]);
	landmarkSampler.setDev(j,param[j*2 + 1]);
      }
      System.out.println("VLOC: UPS: numSensorSamplesReal = "+numSensorSamplesReal);
      if (numSensorSamplesReal > numSamples)
	numSensorSamplesReal = 0;
      
      for (int j =0; ((double)j) < numSensorSamplesReal; j++) {
	Sample samp = landmarkSampler.generateSample();
	/*	
		int onlm = onLandmark(samp);
		while (onlm >= 0) {
		//we're on lm onlm!
		System.out.println("on landmark! "+onlm);
		Vec2 lmLoc = getLandmarkLocation(onlm);
		double t = ur.getValue(0.0, 2*Math.PI);
		Vec2 adjust = new Vec2(lmLoc.x, lmLoc.y);
		Vec2 delta = new Vec2(Math.cos(t)*(getLandmarkRadius(onlm)+0.1),
		Math.sin(t)*(getLandmarkRadius(onlm)+0.1));
		adjust.add(delta);
		samp.data[Sample.x] = adjust.x;
		samp.data[Sample.y] = adjust.y;
		
		robot.clipToMap(samp);
		onlm = onLandmark(samp);
		}
	*/
	robot.clipToMap(samp);
	
	numCreatedSamples++;
	
	//	System.out.println("added sample = " + samp.toString());
	//samples.setSample(j, samp);
	samplesX[j] = samp.data[Sample.x];
	samplesY[j] = samp.data[Sample.y];
	samplesT[j] = samp.data[Sample.t];
	samplesW[j] = samp.data[Sample.w];
      }
    }
    
    return numSeenLandmark;
  }
  
  
  protected void updateAmbig(int lm) {
    double start, end;
    double [] param = new double[4];
    double goodSampleProb;

    if (doneAmbigUpdate) {
      return;
    }

    //this is an ambiguous landmark we've seen...
  
    double currAmbig = 0;
    
    param[0] = robot.getLandmarkDistance(lm);
    param[1] = robot.getLandmarkDistanceVariance(lm);
    param[2] = robot.getLandmarkAngle(lm);
    param[3] = robot.getLandmarkAngleVariance(lm);
    
    for(int k =0;k < numSensorVars; k++) {
      lmUpdater.setMean(k, param[k*2]);
      lmUpdater.setDev(k, param[k*2+1]);
    }

    /*
    for (int j =0; j < ambigLandmarks.length; j++) {
      if (!ambigLandmarks[j]) {
	continue;
      }
      
      lmUpdater.setLocation(getLandmarkLocation(j));
      
           
      start = currAmbig / (double)numAmbigLM;
      start *= (double)numSamples;
      end = (currAmbig+1) / (double)numAmbigLM;
      end *= (double)numSamples;
      
      for (int k = (int)start; k < (int)end; k++) {
	samplesW[k] = lmUpdater.updateSample(samplesX[k], samplesY[k], 
					     samplesT[k], samplesW[k]);
      }
      currAmbig += 1.0;
    }
    */
    
    double best = -999999;
    for (int i =0; i < numSamples; i++) {
      best = -999999;
      
      for (int j =0; j < numAmbigLM; j++) {
	lmUpdater.setLocation(getLandmarkLocation(ambigLMIndex[j]));
	
	samplesW[i] = lmUpdater.updateSample(samplesX[i], samplesY[i],
	 				     samplesT[i], samplesW[i]);
	//	System.out.println("VLOC: UA: samplesW[]="+samplesW[i]+" j="+j);
	if (samplesW[i] > best) {
	  best = samplesW[i];
	  ambigClosest[i] = j;
	  // System.out.println("VLOC: UPA: best="+best+" clos="+j);
	}
      }

      samplesW[i] = best;
    }
    
    samplesAreNormalized = false;

    //now lets generate samples....
    goodSampleProb = normalizeSamples();
    
    if (goodSampleProb >= 0.0)
      System.out.println("VLOC: UA: goodSampProb = "+goodSampleProb);
    double conf = 0.0;
    
    double expect = 0.025; 
    
    expect = Math.pow(expect, 2.0);
    
    double numSensorSamplesReal = (1 - goodSampleProb/(expect*.4))*
      samples.getNumSamples() + 0.5 - 1.0;
    numSensorSamplesReal *= ur.getValue(0.3, 0.8);

    //numSensorSamplesReal /= (double)numAmbigLM;

    currAmbig = 0.0;

    for(int j = 0; j < landmarkSampler.getNumVars(); j++) {
      landmarkSampler.setMean(j,param[j*2]);
      landmarkSampler.setDev(j,param[j*2 + 1]);
    }
    /*
    for (int i =0; i < ambigLMIndex.length; i++) {
    
      landmarkSampler.setPosition(getLandmarkLocation(ambigLMIndex[i]));
      
      System.out.println("VLOC: UA: numSensorSamplesReal (x "+
			 numAmbigLM+" ambig LMS)= "+numSensorSamplesReal);
      if (numSensorSamplesReal > numSamples)
	numSensorSamplesReal = 0;
      
      start = currAmbig / (double)numAmbigLM * (double)numSamples;
      end = (currAmbig+1.0) / (double)numAmbigLM * (double)numSamples;

      for (int j =(int)start; ((double)j) < numSensorSamplesReal; j++) {
	Sample samp = landmarkSampler.generateSample();
	
	robot.clipToMap(samp);
	
	//numCreatedSamples++;
	
	samplesX[j] = samp.data[Sample.x];
	samplesY[j] = samp.data[Sample.y];
	samplesT[j] = samp.data[Sample.t];
	samplesW[j] = samp.data[Sample.w];
      }
      
      currAmbig += 1.0;
    }
    */
    System.out.println("VLOC: UA: numSensorSamplesReal="+numSensorSamplesReal);
    int idx, sidx;
    for (int i =0; i < numSensorSamplesReal; i++) {
      idx = intGen.nextInt() % numAmbigLM;
      if (idx < 0) {
	idx = -idx;
      }
      landmarkSampler.setPosition(getLandmarkLocation(ambigLMIndex[idx]));
      Sample samp = landmarkSampler.generateSample();
      
      robot.clipToMap(samp);
      
      //numCreatedSamples++;
      if (intGen.nextInt()%2 == 0) {
	sidx = i;
      }
      else {
	sidx = (numSamples-1) - i;
      }
      samplesX[sidx] = samp.data[Sample.x];
      samplesY[sidx] = samp.data[Sample.y];
      samplesT[sidx] = samp.data[Sample.t];
      samplesW[sidx] = samp.data[Sample.w];
    }
      

    doneAmbigUpdate = true;
  }   
  
  protected double Vec2Dot(Vec2 a, Vec2 b) {
    return a.x*b.x+a.y*b.y;
  }

  protected void updatePositionLines(long timestamp) {
    int lineChannel = 7;
    //do stuff with lines like we did with landmarks...
    Vec2 [] seenLines = ((LineLocalizationRobot)robot).getVisualLines(timestamp, lineChannel);
    double lengthThreshold = ((Simple)robot).getDictionary().getDouble("LINE_LOCALIZER_LENGTH_THRESHOLD");

    Vec2 tempLine;
    Vec2 dist;
    double slope=-777, b=-777;

    if (seenLines == null)
      return;

    int numSeenLines = 0;
    int numLineSensorVars = 2;
    double minProbGaussianSense = 0.1;
    double norm=-777;
    double [] param = new double[4];
    double angle, theta, psi;
    double seenLength=0;

    for (int i =0; i < seenLines.length; i+= 2) {
      
      if (seenLines[i] == null) {
	//we didnt see this line...
	continue;
      }
  
      ((Simple)robot).setDisplayString("sL "+i);
 
      //get the vector from our position to the closest point on the line we see
      //the line defined by the two vectors seenLines[i] and seenLines[i+1]
      if (seenLines[i].y < 0 && seenLines[i+1].y < 0) {
	Vec2 tmp = (Vec2)seenLines[i].clone();
	seenLines[i] =  (Vec2)seenLines[i+1].clone();
	seenLines[i+1] = tmp;
      }
      
      tempLine = new Vec2(seenLines[i+1]);
      tempLine.sub(seenLines[i]);
      seenLength = tempLine.r;

      if (seenLength < lengthThreshold) {
	//this line is too small so ignore it
	continue;
      }
      
      slope = tempLine.y / tempLine.x;

      if (slope > 9999999) {
	numSeenLines++;

	//it's a vertical line
	double fov = Math.acos(Vec2Dot(seenLines[i], seenLines[i+1]) / 
			       (seenLines[i].r * seenLines[i+1].r));
	
	theta = Math.acos(Vec2Dot(seenLines[i], tempLine) / 
			  (seenLines[i].r*tempLine.r));
	if (theta > Math.PI/2.0) {
	  psi = theta - (0.5*fov) - (Math.PI/2.0);
	}else {
	  fov = SimpleCye.VISION_FOV_RAD;
	  psi = (Math.PI/2.0) - theta + (0.5*fov);
	}
	dist = new Vec2(0,0);
	//dist.setr( seenLines[i].r * Math.asin(psi));
	dist.setr(seenLines[i].x);
	//get the theta!  its the seenLines[i].t + the other part...
	//dist.sett( (Math.PI/2.0) - psi + 0.5*SimpleCye.VISION_FOV_RAD);
	
	dist.sett(Units.ClipRad(psi)); 
	System.out.println("VLOC: UPL: vert: theta="+Units.RadToDeg(theta)+" psi="+Units.RadToDeg(psi)+" fov="+Units.RadToDeg(fov));
	System.out.println("VLOCK UPL: vert: dist.r="+dist.r+" dist.t(deg)="+Units.RadToDeg(dist.t));
       
      } else {

	//forget about it right now...
	
	/*	b = seenLines[i].y - slope*seenLines[i].x;
	
	norm = -b / (1 + slope*slope);
	
	dist = new Vec2(norm*slope, -1*norm);

	dist.t = Units.ClipRad(dist.t);
      
	Vec2 mid = new Vec2(seenLines[i]);
	mid.sub(dist);
	dist.t = Math.tan(dist.r / mid.r) + 0.5* SimpleCye.VISION_FOV_RAD;
	System.out.println("VLOC: UPL: nonvert: dist.r="+dist.r+" dist.t (deg) ="+Units.RadToDeg(dist.t));
	*/
	continue;
      }
    

      System.out.println("VLOC: UPL: line length="+tempLine.r+" b="+b+" norm="+norm+" slope="+slope);
          
      //      lineUpdater.setSeenLine(getLine(i/2).getStart(), getLine(i/2).getEnd());
      //lineUpdater.setSeenLine(dist, angle);
      lineUpdater.setMinProb(minProbGaussianSense);
      
      param[0] = dist.r; //robot.getLineDistance(i); //est distance to line
      param[1] = 0.01; //robot.getLineDistanceVariance(i);
      param[2] = Units.ClipRad(dist.t); //robot.getLineAngle(i); //est angle relative to normal of line
      param[3] = 0.01; //robot.getLineAngleVariance(i);
      
      //we need to set deviances ourselves..
      //param[0] = 0.001; //deviation on the distance to the midpoint of seen line segment
      //param[1] = 0.001; //deviation on the theta of that segment
      
      for(int j =0;j < numLineSensorVars; j++) {
	lineUpdater.setMean(j, param[j*2]);
	lineUpdater.setDev(j, param[j]);
      }
      
      for (int j = 0; j < numSamples; j++) {
	samplesW[j] = lineUpdater.updateSample(samplesX[j], samplesY[j], 
					       samplesT[j], samplesW[j]);
      }
      
      samplesAreNormalized = false;
    }
    
    double goodSampleProb = normalizeSamples();
    
    if (goodSampleProb >= 0.0) {
      System.out.println("VLOC: ULS: goodSampleProb = "+goodSampleProb);
    } 

    if (numSeenLines > 0) {
      double expect = 0.014;
      //      expect = Math.pow(expect, 2.0*numSeenLines);
      double numLineSamplesReal = (1 - goodSampleProb/(expect*0.4))*
	samples.getNumSamples() + 0.5 - 1.0;
      System.out.println("VLOC: UPL: numLineSamplesReal="+numLineSamplesReal);
      //keep the same params from up there (assume 1 line seen FIX)
      

      for (int j =0; j < lineSampler.getNumVars(); j++) {
	lineSampler.setMean(j, param[j*2]);
	lineSampler.setDev(j, param[j*2+1]);
      }

      if (numLineSamplesReal > numSamples) {
	numLineSamplesReal = 0;
      }
     
      lineSampler.setSeenLineLength(seenLength);
      
      for (int j = 0; (double)j < numLineSamplesReal; j++) {
	Sample samp = lineSampler.generateSample();

	//	System.out.println("VLOC: UPL: s.x="+samp.x+" s.y="+samp.y+" s.t="+Units.RadToDeg(samp.t));
	robot.clipToMap(samp);
	
	samplesX[j] = samp.data[Sample.x];
	samplesY[j] = samp.data[Sample.y];
	samplesT[j] = samp.data[Sample.t];
	samplesW[j] = samp.data[Sample.w];
      }
	
    }
  }

  protected LineSim getLine(int i) {
    return allTheLines[i];
  }

    protected Sample getPosition() {
	Sample res;
	//	System.out.println("VLOCRV: getPosition() start");

	//samples.normalizeSamples();
	normalizeSamples();
	
	double[]  mean = new double[Sample.size-1];
	double[] var = new double[mean.length];
		
	mean[0] = mean[1] = mean[2] = 0.0;
	var[0] = var[1] = var[2] = 0.0;
	double x,y,t;

	for (int i = 0; i < numSamples; i++) {
	  x = samplesX[i];
	  y = samplesY[i];
	  t = samplesT[i];

	  mean[0] += x;
	  mean[1] += y;
	  mean[2] += Math.cos(t);

	  //	  var[0] += x*x;
	  //  var[1] += y*y;
	  var[2] += Math.sin(t);
	}
	
	double sum =0.0;


	//calc means for X and Y
	mean[0] /= numSamples;
	mean[1] /= numSamples;
	//find variances for X and Y
	/*	var[0] /= numSamples;
		var[1] /= numSamples;
		var[0] -= mean[0]*mean[0];
		var[1] -= mean[1]*mean[1];
	*/
	double cosMean = mean[2]/numSamples;
	double sinMean = var[2]/numSamples;
	
	mean[2] = Math.atan2(sinMean, cosMean); 
	//var[2] = 1 - Math.sqrt(cosMean*cosMean+sinMean*sinMean);
	/*
	  if (var[2] < 0.0)
	  var[2] = 0.0;
	*/
	res = new Sample(mean[0], mean[1], mean[2]);
	//	System.out.println("VLOCRV: getPosition(): mean is "+res.toString());
	return res;
    }
    
    
    protected void updatePositionMovement() {

        moveUpdateCnt++;
	double [] param = robot.getMovementDistParams();

	for (int i =0; i < numMoveVars; i++) {
	  // 	  System.out.println("VLOC: i = "+i+" UPM: mean = "+param[i*2]+" dev = "+param[i*2+1]);
	    moveUpdater.setMean(i, param[i*2]);
	    
	    moveUpdater.setDev(i, param[i*2+1]);
	}
	

	double [] res;
	for(int i =0; i < numSamples; i++) {
	    //   	    System.out.println("VLOC: UPM: old = "+s.toString());
	    res = moveUpdater.updateSample(samplesX[i], samplesY[i], samplesT[i]);
	   
	    /*
              int onlm = onLandmark(s);
              while (onlm >= 0) {
	      //we're on lm onlm!
	      System.out.println("on landmark! "+onlm);
	      Vec2 lmLoc = getLandmarkLocation(onlm);
	      double t = ur.getValue(0.0, 2*Math.PI);
	      Vec2 adjust = new Vec2(lmLoc.x, lmLoc.y);
	      Vec2 delta = new Vec2(Math.cos(t)*(getLandmarkRadius(onlm)+0.1),
              Math.sin(t)*(getLandmarkRadius(onlm)+0.1));
	      adjust.add(delta);
	      s.data[Sample.x] = adjust.x;
	      s.data[Sample.y] = adjust.y;
	      
	      robot.clipToMap(s);
	      
	      onlm = onLandmark(s);
              }
	    */
	    //FIX:  sample could still end up on LM...
            //robot.clipToMap(s);

	    res  = robot.clipToMap(res[0], res[1], res[2]);
	    
	    samplesX[i] = res[0];
	    samplesY[i] = res[1];
	    samplesT[i] = res[2];
	}
	
    }
 
    protected void resetPosition() {	    
	Sample s;

	System.out.println("VLOCRV: resetPosition() start");

	Range resetX, resetY, resetT;

	//get the map dimensions from the dsc file
	resetX = new Range(((Simple)robot).getDictionary().getString("MAP_RANGE_X"));
 	resetY = new Range(((Simple)robot).getDictionary().getString("MAP_RANGE_Y"));
 	resetT = new Range(((Simple)robot).getDictionary().getString("MAP_RANGE_THETA"));

	//set up the unifrom dist sampler
	us.setRange(0, resetX);
        us.setRange(1, resetY);
        us.setRange(2, resetT);

	//generate uniform dist of samples
	double [] res;
	for (int i =0; i < numSamples; i++) {
	    res = us.generateSampleArray();
	    samplesX[i] = res[0];
	    samplesY[i] = res[1];
	    samplesT[i] = res[2];
	    samplesW[i] =1.0;
	   
	}
	samplesAreNormalized = true;

	//	samples.setNormalized(true);
    }


  public double normalizeSamples() {
    if (samplesAreNormalized == true) 
	    return -1.0;
    
    double [] cumWeights = new double[numSamples+1];
    
    double cumWeight = 0.0;
    for (int i =0; i < numSamples; i++) {
      cumWeights[i] = cumWeight;
      cumWeight+= samplesW[i];
    }
    cumWeights[numSamples] = cumWeight;
    
    //	System.out.println("SS: normalize: cumWeight = "+cumWeight);
    
    //here we resample from the samples....
    for (int i= 0; i < numSamples; i++) {
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
      
     
      newSamplesX[i] = samplesX[low];
      newSamplesY[i] = samplesY[low];
      newSamplesT[i] = samplesT[low];
      samplesW[i] = 1.0;
    }
    
    /*
      Sample []tmp = samples;
      samples = newSamples;
      newSamples = tmp;
    */
    samplesX = newSamplesX;
    samplesY = newSamplesY;
    samplesT = newSamplesT;

    samplesAreNormalized = true;

    System.out.println("cumWeight = "+cumWeight);
    return cumWeight/numSamples;
  }
  
    protected int onLandmark(Sample s) {
        for (int i =0; i < landmarkAreas.length; i++) {
            if (landmarkAreas[i].contains(s.data[Sample.x], s.data[Sample.y])) {
                return i;
            }
        }
        return -1;
    }

    protected Vec2 getLandmarkLocation(int lm) {
        return landmarks[lm].getPosition();
    }
  
    protected double getLandmarkRadius(int lm) {
        return ((LandmarkSim)landmarks[lm]).getRadius();
    }
}
	    

	    
	    

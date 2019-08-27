/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;
import java.lang.Math;


public class MovementSampleUpdater extends GaussianSampler {

    //    protected LocalizationRobot robot;

    public MovementSampleUpdater(int nv) {
	super(nv);
	
       

	for(int i =0; i < numVars; i++) {
	    setRange(i, Double.MIN_VALUE, Double.MAX_VALUE);
	}
    }

   
    public void updateSample(Sample s) {
	Sample newSample;
	double x = s.data[Sample.x];
	double y = s.data[Sample.y];
	double theta = s.data[Sample.t];
	
	//	do {
	  newSample = generateSample();
	  
	  double egoMoveDist = newSample.data[Sample.x];
	  double egoMoveDir = newSample.data[Sample.y];
	  double egoDirChg = newSample.data[Sample.t];
	  
	  double newX, newY, newDir;
	  //		System.out.println("MSU: update: egoMoveDist = "+egoMoveDist+" egoMoveDir = "+egoMoveDir+" egoDirChg = " + egoDirChg);
	  double movementAngle = theta+egoMoveDir;
	  
	  double deltaX = egoMoveDist * Math.cos(movementAngle);
	  double deltaY = egoMoveDist * Math.sin(movementAngle);
	  
	  newX = x + deltaX;
	  newY = y + deltaY;
	  
	  newDir = theta + egoDirChg;
	  
	  while (newDir < 0.0)
	    newDir += 2.0 *Math.PI;
	  while (newDir >= 2.0*Math.PI)
	    newDir -= 2.0*Math.PI;

	  newSample.data[Sample.x] = newX;
	  newSample.data[Sample.y] = newY;
	  newSample.data[Sample.t] = newDir;
	  //newSample.data[Sample.t] = egoDirChg;
	  //	  robot.clipToMap(newSample);

	  //	}while (!robot.inEnvironment(newSample.data[Sample.x], newSample.data[Sample.y]));
	
	s.data[Sample.x] = newSample.data[Sample.x];
	s.data[Sample.y] = newSample.data[Sample.y];
	s.data[Sample.t] = newSample.data[Sample.t];
    }

  public double[] updateSample(double x, double y, double theta) {
    double [] res = new double[3];

    //get a new sample using mean/stddev already given
    Sample newSample = generateSample();

    double egoMoveDist = newSample.data[Sample.x];
    double egoMoveDir = newSample.data[Sample.y];
    double egoDirChg = newSample.data[Sample.t];
    
    double newX, newY, newDir;
    double movementAngle = theta+egoMoveDir;
    
    double deltaX = egoMoveDist * Math.cos(movementAngle);
    double deltaY = egoMoveDist * Math.sin(movementAngle);
    
    newX = x + deltaX;
    newY = y + deltaY;
    
    newDir = theta + egoDirChg;
    
    while (newDir < 0.0)
      newDir += 2.0 *Math.PI;
    while (newDir >= 2.0*Math.PI)
      newDir -= 2.0*Math.PI;
    
    newSample.data[Sample.x] = newX;
    newSample.data[Sample.y] = newY;
    newSample.data[Sample.t] = newDir;
  
    
    res[0] = newSample.data[Sample.x];
    res[1] = newSample.data[Sample.y];
    res[2] = newSample.data[Sample.t];

    return res;
  }
  
}
    

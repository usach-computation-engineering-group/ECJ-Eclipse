/** LandmarkSampleUpdate.java
 * heavily borrowed from scott lenser's code.
 */
/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;
import java.lang.Math;
import EDU.gatech.cc.is.util.Vec2;


public class LandmarkSampleUpdater extends GaussianEvaluator {
    //the marker's location
    protected Vec2 location;

    public LandmarkSampleUpdater() {
    
	location = new Vec2();
    }
	
    
    public LandmarkSampleUpdater(int nv) {
	super(nv);
       
	location = new Vec2();
    }
    
    public void setLocation(Vec2 loc) {
	setLocation(loc.x, loc.y);
    }

    public void setLocation(double x, double y) {
	location.setx(x);
	location.sety(y);
    }

    public void updateSample(Sample s) {
	double dist;
	double allo_angle;
	double ego_angle;
	double x = s.data[Sample.x];
	double y = s.data[Sample.y];
	double theta = s.data[Sample.t];

	double [] in = new double[2];
	double [] out;

	//distance from sample to landmark
	dist = Math.sqrt( ((location.x - x)*(location.x - x)) +
		     ((location.y - y)*(location.y - y)));

	//angle between the two positions
	allo_angle = Math.atan2(location.y-y, location.x-x);
	
	if (allo_angle < 0) {
	  allo_angle = -allo_angle;
	}
	
	if (theta < 0)
	  ego_angle = allo_angle +theta;
	else
	  ego_angle = allo_angle - theta;
	
	//ego_angle = allo_angle - theta;
	
	while (ego_angle < -Math.PI)
	    ego_angle += 2.0*Math.PI;

	while (ego_angle >= Math.PI)
	    ego_angle -= 2.0*Math.PI;
	/*   
	if (theta < 0) theta = -theta;
	ego_angle = theta+allo_angle;
	System.out.println("ego_angle = "+ego_angle);
	*/
	in[0]=dist;
	in[1]=ego_angle;

	//		System.out.println("LSU: update: dist = "+dist+" ego_angle = "+ego_angle+" distMean = "+mean[0]+" angleMean = "+mean[1]);
	
	out = evaluate(in);
	//		System.out.println("LSU: out[0] = "+out[0] + " out[1] = "+out[1]);
	s.data[Sample.w] *= out[0];
	s.data[Sample.w] *= out[1];
    }
  
  public double updateSample(double x, double y, double theta, double weight) {
    double dist;
    double allo_angle;
    double ego_angle;

    double [] in = new double[2];
    double [] out;
    
    //distance from sample to landmark
    dist = Math.sqrt( ((location.x - x)*(location.x - x)) +
		      ((location.y - y)*(location.y - y)));
    
    //angle between the two positions
    allo_angle = Math.atan2(location.y-y, location.x-x);
    
    if (allo_angle < 0) {
      allo_angle = -allo_angle;
    }
    
    if (theta < 0)
      ego_angle = allo_angle +theta;
    else
      ego_angle = allo_angle - theta;
    
    //ego_angle = allo_angle - theta;
    
    while (ego_angle < -Math.PI)
      ego_angle += 2.0*Math.PI;
    
    while (ego_angle >= Math.PI)
      ego_angle -= 2.0*Math.PI;
    /*   
	 if (theta < 0) theta = -theta;
	 ego_angle = theta+allo_angle;
	 System.out.println("ego_angle = "+ego_angle);
    */
    in[0]=dist;
    in[1]=ego_angle;
    
    //		System.out.println("LSU: update: dist = "+dist+" ego_angle = "+ego_angle+" distMean = "+mean[0]+" angleMean = "+mean[1]);
    
    out = evaluate(in);
    //		System.out.println("LSU: out[0] = "+out[0] + " out[1] = "+out[1]);
    weight *= out[0];
    weight *= out[1];
    
    return weight;
  }
  
}

/** LandmarkSampler.java
 * heavily adapted from scott lenser's code!
 */
/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;
import EDU.gatech.cc.is.util.Vec2;

public class LandmarkSampler extends GaussianSampler {    
    
    protected Vec2 location;
    protected LocalizationRobot robot;
    protected static UniformRandom ur;
    public LandmarkSampler(int nv, LocalizationRobot r) {
      super(nv);
      location = new Vec2();
      
      robot = r;

      ur = new UniformRandom(4, 0.0, 1.0);
    }


    public void setPosition(Vec2 p) {
	location.setx( p.x);
	location.sety (p.y);
    }

    public void setPosition(double x, double y) {
	location = new Vec2(x,y);
    }
    
    public Sample generateSample() {
	Sample s;
	double x,y,t;
	double lowAngle, hiAngle;
	double allo_angle;
       
	do {
	    s = super.generateSample();
	    
	    double dist = s.data[0]; 
	    double ego_angle = s.data[1];

	    //need to get bounds on our bearing towards the lm
	    //assume ego_angle is angle from landmark to robot
	    //FIX account for size of landmark into how much you can see
	    /*
	    ego_angle += Math.PI;
	    
	    lowAngle = ego_angle - (0.5 * VisionRobot.fovAngle);
	    if (lowAngle < 0.0) {
		lowAngle += Math.PI;
	    }
	    hiAngle = ego_angle+(0.5 * VisionRobot.fovAngle);
	    
	    if (lowAngle > hiAngle)
		lowAngle += Math.PI;

	    allo_angle = ur.getValue(lowAngle, hiAngle);
	    //allo_angle = ur.getValue(0.0, 2.0*Math.PI);
	    
	    x = location.x - dist*Math.cos(allo_angle);
	    y = location.y - dist*Math.sin(allo_angle);
	    */
	    // t = allo_angle - ego_angle;
	    // allo_angle = Math.atan2(location.x - x, location.y-y);
	    //    t = allo_angle - ego_angle;
	    /*while(t < -Math.PI)
		t += 2.0*Math.PI;

	    while(t >= Math.PI)
		t -= 2.0 * Math.PI;
	    */
	    
	    allo_angle = ur.getValue(0.0, 2*Math.PI);
	    x = location.x - dist*Math.cos(allo_angle);
	    y = location.y - dist*Math.sin(allo_angle);
	    
	    Vec2 v = new Vec2(location.x,location.y);
	    v.sub(new Vec2(x,y));
	    t = v.t + ego_angle;
	    /*
	    Vec2 tmp = new Vec2(x,y);
	    //System.out.println("l = "+location.toString()+" tmp = "+tmp.toString()+" sub = "+v.toString());
	    */
	}while (!robot.onMap(x,y));

	
	s.data[Sample.x] = x;
	s.data[Sample.y] = y;
	s.data[Sample.t] = t;
	//	System.out.println("generated sample = "+s.toString());

	return s;
    }
}
    
	
    

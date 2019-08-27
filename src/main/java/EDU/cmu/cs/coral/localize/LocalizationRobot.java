/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;
import EDU.gatech.cc.is.util.Vec2;
import EDU.gatech.cc.is.simulation.SimulatedObject;

public interface LocalizationRobot {
  
  

    public SimulatedObject [] getLandmarks();
    public boolean [] getAmbigLandmarks();

    public int getNumLandmarks();
    
    public double getSeenLandmarkConfidence(int lm);
    
  //  public Vec2 getLandmarkLocation(int lm);
    
  //  public double getLandmarkRadius(int lm);
    
    public double getLandmarkDistance(int lm);
    
    public double getLandmarkDistanceVariance(int lm);
    
    public double getLandmarkAngle(int lm);
    
    public double getLandmarkAngleVariance(int lm);
    
    public double [] getMovementDistParams();
    
  // public Range getMapRangeX();
  //  public Range getMapRangeY();
  //  public Range getMapRangeTheta();
    
  //   public int onLandmark(double x, double y);
    public boolean onMap(double x, double y);

    public void clipToMap(Sample s);
    public double [] clipToMap(double x, double y, double theta);
}

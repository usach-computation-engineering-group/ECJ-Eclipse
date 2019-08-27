/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */

package EDU.cmu.cs.coral.localize;
import EDU.gatech.cc.is.util.Vec2;
//import EDU.cmu.cs.coral.localize.LocalizationRobot;
import EDU.cmu.cs.coral.simulation.LineSim;


public interface LineLocalizationRobot extends LocalizationRobot {

  public LineSim [] getLines();

  
  public Vec2 [] getVisualLines(long timestamp, int channel);
}



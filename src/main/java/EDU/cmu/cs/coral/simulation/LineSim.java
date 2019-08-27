
package EDU.cmu.cs.coral.simulation;

import EDU.cmu.cs.coral.simulation.LinearObstacleSim;
import EDU.cmu.cs.coral.util.Circle2;
import EDU.cmu.cs.coral.util.Polygon2;
import EDU.gatech.cc.is.util.Vec2;

public class LineSim extends LinearObstacleSim
{
  public LineSim() { super(); }

  //this class is like a linear obstatcle, but without the
  //obstacle bit!  so we can collide with it as much as we want
  //without worrying.
  public boolean checkCollision(Circle2 c) { return false; }
  public boolean checkCollision(Polygon2 p) { return false; }

  public double evaluate(double x) { return m*x+b; }
  public boolean pointOnLine(Vec2 v) {
    return (((m*v.x+b) <= (v.y+RADIUS)) || 
	    ((m*v.x+b) >= (v.y-RADIUS)));
  }

  public Vec2 getStart() { return (Vec2)start.clone(); }
  public Vec2 getEnd() { return (Vec2)end.clone();}

}

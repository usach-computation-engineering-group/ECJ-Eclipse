

/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */


package EDU.cmu.cs.coral.localize;

import EDU.cmu.cs.coral.simulation.*;
import EDU.gatech.cc.is.util.*;

public class LineSampleUpdater extends GaussianEvaluator {

  protected Vec2 [] egoLine; //the end points of the seen line segment, ego-coords
  protected Vec2 toMid; //vec2 to mid point of seen line
  protected LineSim [] lines; //all the lines in the map

  protected  double lineRes; //the resolution in meters we are looking at lines

  protected double visionRange;
  protected double fieldOfView;

  protected Vec2 [] linesRes;

  public LineSampleUpdater(int nv) {
    super(nv);
  }

  /*
  public void setSeenLine(Vec2 [] thelines) {
    egoLine = thelines;

    //find the dist from the midpoint of this segment
    toMid = new Vec2(egoLine[0]);
    toMid.sub(egoLine[1]);

    //this is now halfway...
    toMid.setr(toMid.r/2);

    toMid.add(egoLine[1]);

    setMean(0, toMid.r);
 
    setMean(1, toMid.t);
  }

  public void setSeenLine(Vec2 one, Vec2 two) {
    toMid = new Vec2(one);
    toMid.sub(two);
    toMid.setr(toMid.r/2);
    toMid.add(two);
    setMean(0, toMid.r);
    setMean(1, toMid.t);
  }
  */
  
  public void setMapLines(LineSim [] theLines) {
    lines = theLines;
  }
  /*
  public void setLineScanResolution(double lr) {
    lineRes = lr;
  }

  public void setVisionRange(double vr) { visionRange = vr;}
  public void setFieldOfView(double fov) { fieldOfView = fov; }
  */

  public double updateSample(double x, double y, double theta, double weight) {
    Vec2 clos;
    double diff, best = 999999;
    double [] in = new double[2];
    double [] out = new double[2];
    double dist, angle;

    for (int i =0; i < lines.length; i++) {
      
      //      clos = lines[i].getClosestPoint(new Vec2(x,y));  //update this if slow
      Vec2 strt, end;
      strt = lines[i].getStart();
      end = lines[i].getEnd();

      if (end.y / strt.x > 99999) {
	//vertical...
	dist = Math.abs(strt.x - x);

	if (strt.x < x) {
	  //line on left side...
	  angle = Math.PI - theta;
	} else {
	  angle = theta;
	}
      }else {
	clos = lines[i].getClosestPoint(new Vec2(x,y));
	angle = Units.ClipRad(clos.t - theta);
	dist = clos.r;
      }
      diff = Math.abs(mean[0] - dist) ;
	
      diff += Math.abs(mean[1] - angle);
	//System.out.println("LSU: diff="+diff+" clos.r="+clos.r+" clos.t="+clos.t);
      if (diff < best) {
	best = diff;
	in[0] = dist;
	in[1] = angle;
      }
    }

    //in[] holds the r and t of a vec  which is the vec from the point to the closest point
    //on each line, that is the closest to the mean values we have for this 
    //updater.

    //System.out.println("LSU: best="+best+" in.r="+in[0]+" in.t="+in[1]+" mean.r="+mean[0]+" mean.t="+mean[1]);

    out = evaluate(in);

    weight *= out[0];
    weight *= out[1];

    return weight;
  }
}

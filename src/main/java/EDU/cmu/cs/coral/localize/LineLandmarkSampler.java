/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;
import EDU.gatech.cc.is.util.Vec2;
import EDU.cmu.cs.coral.simulation.LineSim;
import EDU.gatech.cc.is.util.Units;

public class LineLandmarkSampler extends GaussianSampler {
  protected LineLocalizationRobot robot;
  protected LineSim [] lines;
  protected UniformRandom ur;
  protected double [] length;
  protected double totalLength;
  protected double angle;
  protected double seenLength;

  public LineLandmarkSampler(int nv, LineSim [] theLines, LineLocalizationRobot r) {
    super(nv);
    robot = r;
    lines = theLines;
    length = new double[lines.length];

    totalLength = 0;
    seenLength = 0;

    Vec2 start, end;
    for (int i =0; i < lines.length; i++) {
      start = (Vec2)lines[i].getStart().clone();
      end = (Vec2)lines[i].getEnd().clone();

      end.sub(start);
      
      length[i] = end.r;

      totalLength += length[i];
    }
    System.out.println("LLS: totalLength="+totalLength);
    //FIX add better seed value...
    ur = new UniformRandom(31337, 0.0, totalLength);
  }

  public void setAngle(double a) {
    angle = a;
  }

  public void setSeenLineLength(double l) {
    seenLength = l;
  }

  public Sample generateSample() {

    Sample s;
    double pos;
    int i;
    Vec2 start, end;
    double theta, psi;
    double where, slope;
    double x,y;
    do {

      //generate a sample x meters away from a line...
      s = super.generateSample();

      //now we have to choose a place to put the sample relative to the lines
      pos = ur.getValue(0.0, totalLength);

      //now we have to transform that to a line
      double total=0.0;
      for (i = 0; pos > total; i++) {
	total += length[i];
      }
      //  System.out.println("LLS: i-1="+(i-1)+" pos="+pos+" total="+total);
      //i-1 is the index of the line we are going to put this sample...
      
      start = (Vec2)lines[i-1].getStart().clone();
      end = (Vec2)lines[i-1].getEnd().clone();
      
      //this is the line
      end.sub(start);
      
      psi = end.t;

      where = pos - (total - length[i-1]);
      /*    if (where < seenLength || where > (length[i-1] - seenLength)) {
	where = ur.getValue(seenLength, length[i-1]-seenLength);
      }
      */  
      slope = end.y / end.x;

      /* s.data[1] -= Math.PI;
      s.data[1] = Units.ClipRad(s.data[1]);*/
      if (slope > 99999) {
	//vertical
	
	if (start.y > 0) {
	  y = start.y - where;
	} else {
	  y = start.y + where;
	}
	if (start.x > 0) {
	  //right side...
	  x = start.x - s.data[0];
	  theta = s.data[1];
	  
	} else {
	  //left side...
	  x = start.x + s.data[0];
	  theta = Math.PI + s.data[1];
	}
      }else {
	//non vert, prolly horz...
	
	if (start.x > 0) {
	  x = start.x - where;
	} else {
	  x = start.x + where;
	}
	if (start.y > 0) {
	  //top
	  y = start.y - s.data[0];
	  theta = Math.PI/2.0 + s.data[1];
	} else {
	  //bottom
	  y = start.y + s.data[0];
	  theta = (3.0/2.0)*Math.PI + s.data[1];
	}
      }
      
      //end.sett(end.t - Math.tan(s.data[0]/end.r));

      //this is the point...
      //start.add(end);
      
      
    } while (!robot.onMap(x, y));
    
    s.data[Sample.t] = theta;//-(psi + ( Math.PI/2.0 - mean[1]));
    s.data[Sample.x] = x;
    s.data[Sample.y] = y;
    s.data[Sample.w] = 1.0;
    return s;
    
  }
}

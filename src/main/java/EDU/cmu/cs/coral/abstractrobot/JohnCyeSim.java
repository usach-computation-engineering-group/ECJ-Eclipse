/*
 * JohnRobotSim.java
 */
/* This code is part of the abstractrobot package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.abstractrobot;

import java.awt.*;
import java.util.*;
import EDU.gatech.cc.is.util.*;
import EDU.gatech.cc.is.simulation.*;
import EDU.gatech.cc.is.abstractrobot.VisualObjectSensor;
import EDU.gatech.cc.is.abstractrobot.Simple;
import EDU.gatech.cc.is.abstractrobot.SimpleInterface;
import EDU.gatech.cc.is.communication.Message;

import EDU.cmu.cs.coral.simulation.ColorTransitionSim;
import EDU.cmu.cs.coral.simulation.LandmarkSim;
import EDU.cmu.cs.coral.simulation.LineSim;
import EDU.cmu.cs.coral.localize.VisionRobot;
import EDU.cmu.cs.coral.localize.LocalizationRobot;
import EDU.cmu.cs.coral.localize.LineLocalizationRobot;
import EDU.cmu.cs.coral.localize.Sample;
import EDU.cmu.cs.coral.localize.Range;
import EDU.cmu.cs.coral.localize.GaussianSampler;
import EDU.cmu.cs.coral.localize.DoubleRectangle;

/** this is an implementation of some localization using the SimpleCyeSim
 * as a base.  there are a few things needed for localization not present
 * in a the standard Sim, such as particular heading information routines,
 * communication, and some other stuff.
 *
 *
 * @see SimpleCyeSim
 * @author John Sweeney
 * @version 0.1
 */


public class JohnCyeSim extends SimpleCyeSim implements SimulatedObject,
							SimpleInterface,
							VisualObjectSensor,
							LocalizationRobot,
							LineLocalizationRobot
{
    protected Point last;

    protected LandmarkSim [] landmarks; //keep track of these
  //  protected SimulatedObject [] all_objects; //and the rest too

 
  protected LineSim [] lines; //these are the lines on the field...
    protected double desired_heading; //which way we want to go
    public Color foreground, background; //how to be drawn
   
    protected long time; //our time
    protected long lastTimeIncrement;
    protected double timed; //more accurate?

    protected double base_speed;
    protected double desired_speed;
    protected double desired_turret_heading;
    protected	double	obstacle_rangeM = 1.0;
  
    protected	long	last_VisualObjectst = 0;
    protected Vec2[]	last_VisualObjects;
    protected	int	num_VisualObjects = 0;
    protected int last_channel = 0;


  protected boolean [] ambig; // the landmarks which are ambiguous
  protected int numAmbigLandmark;

  protected Vec2 [] linesRes; //result from getVisualLines
  protected int horzRes; //horizontal scanning resoluation (in parts) for looking for lines
  protected int vertRes; //vert scannig rez for same (in parts)
  protected long lastVisualLinesTime;
  protected int lastChannel;
  protected double lineRes; //the resoluation for sacnning (in meters)
 

    protected	long	last_Obstaclest = 0;
    protected	Vec2	last_Obstacles[];
    protected	int	num_Obstacles;

    protected DoubleRectangle [] landmarkRectangle;

  //  protected Vec2 [] fieldPos;
  //  protected Vec2 [] fieldMag;
  //  protected Color [] fieldColor;
  //  protected Color singleVectorColor;  //  protected boolean haveField;
  //  protected boolean vectorsHaveColor;
  //  protected Vec2 estPos;
    
    protected double oldMoveR, oldMoveT, oldHeading;

  protected String display_string = "";//"this string left blank";

    protected double clippingOffset;
    protected double clippingStdDev;
    protected double clippingDirStdDev;
    protected GaussianSampler gs; 

  protected Random intGen;

  //  public static final double RADIUS = 0.247; //this will change for cye 
  //  public static final double VISION_RANGE = 1.5;
  //    public static final int VISION_FOV_DEG = 100;
  //   public static final double VISION_FOV_RAD = Units.DegToRad(100);
    public final double MAX_STEER = 0.7854;
    public final double MAX_TURRET = 0.7854;
    public final double MAX_TRANSLATION = 0.508;

    public final boolean DEBUG = false;

    public JohnCyeSim() {
	super();
	
	lastTimeIncrement =0;

	landmarks = null;
	lines = null;

	clippingStdDev = 0.25;
	clippingOffset = 0.5;
	clippingDirStdDev = 0.098; //4 degrees
	gs  = new GaussianSampler(3); //used in clipToMap
	gs.setRange(2, -Math.PI, Math.PI);
	for (int i =0; i < 3; i++) {
	  gs.setMean(i, 0.0);
	  gs.setDev(i, clippingStdDev);
	}
	gs.setDev(2, clippingDirStdDev);
	
	oldMoveR = oldMoveT = oldHeading = 0.0;

	intGen = new Random();
    }

    public void init(double x, double y, double t, double ignore,
		     Color fg, Color bg, int vc, int id, long s) {
       
      super.init(x, y, t, ignore, fg, bg, vc, id, s);

	last = new Point(0,0);
	
	landmarks = null;
	lines = null;
	linesRes = null;


	//	lineRes = getDictionary().getDouble("LINE_LOCALIZER_RESOLUTION"); 
	lineRes = 0.05;
	horzRes = 20;
	vertRes = 20;
    }
    
   
   

  // public void receive(Message m) {/*N/A*/}

 
    public void drawID(Graphics g, int w, int h, double t, double b,
		       double l, double r) {
	
	top =t; bottom =b; left =l; right =r;
	
	if (DEBUG) System.out.println("draw "+
				      w + " " +
				      h + " " +
				      t + " " +
				      b + " " +
				      l + " " +
				      r + " ");
	double meterspp = (r - l) / (double)w;
	int radius = (int)(RADIUS / meterspp);
	int xpix = (int)((position.x - l) / meterspp);
	int ypix = (int)((double)h - ((position.y - b) / meterspp));
	
	/*--- draw ID ---*/
	g.setColor(background);
	//	g.drawString("JohnCyeSim!",
	//     xpix-radius,ypix-radius);
	/*	int xx, yy;
	if (linesRes != null) {
	  for (int i =0;i < linesRes.length; i+= 2) {
	    if (linesRes[i] != null) {
	      //this is ego centric....
	      Vec2 globTo = new Vec2(linesRes[i]);
	      Vec2 globFrom = new Vec2(linesRes[i+1]);
	      
	      globFrom.add(position);
	      globTo.add(position);
	      
	      //start of line
	      xpix = (int) ( (globFrom.x - l) / meterspp);
	      xx = (int) ( (globTo.x - l) / meterspp);
	      
	      ypix = (int) ( (double)h - ((globFrom.y - b) / meterspp));
	      yy = (int) ( (double)h - ((globTo.y - b) / meterspp));
	      
	      g.drawLine(xpix, ypix, xx, yy);
	    }
	  }
	}
	*/
	  
	
    }
    
  public void takeStep(long time_increment, SimulatedObject [] all_objs) {
    //need to get the landmarks, and call it here because of initialization
    //sequence...
    // getLandmarks();
    super.takeStep(time_increment, all_objs);
    //System.out.println("mvstep.r = "+mvstep.r+" t = "+mvstep.t);
    getLandmarks();
    getLines();
  }
  
  
  
  //now for the ones in SimpleInterface not covered in SimulatedObject
  
  
  
  /* these are specific to LocalizationRobot*/
  public SimulatedObject[] getLandmarks() {
    if (landmarks == null) {
      int i;
      int num=0;
      //landmarks = new SimulatedObject[all_objects.length];
      
      int ambigChannel = getDictionary().getInt("LOCALIZER_AMBIG_LM_CHANNEL");
      numAmbigLandmark = 0;

      for (i = 0; i < all_objects.length; i++) {
	if (all_objects[i] instanceof LandmarkSim ||
	    all_objects[i] instanceof ColorTransitionSim) {  
	  System.out.println("GL: obj "+i+" is lm! getP = "+
			     all_objects[i].getPosition().toString());
	  // landmarks[i] = all_objects[i];
	  num++;
	  if (all_objects[i].getVisionClass() == ambigChannel) {
	    numAmbigLandmark++;
	  }

	}
	else {
	  System.out.println("GL: obj "+i+" is not lm...");
	  //    landmarks[i] = null;
	}
      }
      
      landmarks = new LandmarkSim[num];
      ambig = new boolean[num];
     
      for (i = 0, num = 0 ; i < all_objects.length; i++) {
	if (all_objects[i] instanceof LandmarkSim ||
	    all_objects[i] instanceof ColorTransitionSim) {
	  landmarks[num] = (LandmarkSim) all_objects[i];
	  
	  //check if ambi landmark, and if so put in array
	  if (all_objects[i].getVisionClass() == ambigChannel) {
	    ambig[num] = true;
	  }
	  
	  num++;
	}
      }
      
      System.out.println("JRS: getLandmarks: got "+num+" landmarks");
      
      landmarkRectangle = new DoubleRectangle[landmarks.length];
      
      for (i =0; i < landmarkRectangle.length; i++) {
	double newx, newy, side;
	newx= (double)landmarks[i].getPosition().x - landmarks[i].getRadius();
	newy = (double) landmarks[i].getPosition().y + landmarks[i].getRadius();
	side = landmarks[i].getRadius()*2.0;
	landmarkRectangle[i] = new DoubleRectangle(newx, newy, side, side);
      }					       
    }
    return landmarks;
  }
  
  public boolean [] getAmbigLandmarks() { return ambig; }
 
    public int getNumLandmarks() {
	if (landmarks == null)
	    getLandmarks();
	return landmarks.length;
    }

    public double getSeenLandmarkConfidence(int lm) {
      //if the landmark is in visual range 100% conf, else 0%
      double noise = visionNoiseGetNext();

      Vec2 temp = landmarks[lm].getCenter(position);
      //dont add noise to whether we see the landmark
      /*
      temp.setr((1.0 - noise ) * temp.r);
      noise = visionNoiseGetNext();
      temp.sett((1.0 - noise) * temp.t);
      */

      if ((temp.r < SimpleCye.VISION_RANGE) &&
	    (Math.abs(Units.BestTurnRad(steer.t,temp.t))
	     < (SimpleCye.VISION_FOV_RAD/2.0))) {
	
	if (ambig[lm]) {
	  return 1.0 / (double)(numAmbigLandmark);
	}
	
	return 1.0;
      }
	
      return 0.0;
    }
  
    

    public double getLandmarkDistance(int lm) {

     
      Vec2 tmp = new Vec2(landmarks[lm].getPosition());
      
      tmp.sub(position);
      
      //add some noise to this....assumption is mean is 0
      double noise = visionNoiseGetNext();
      tmp.setr( (1.0-noise)*tmp.r);
      
      return tmp.r;
    }

  //make this next one a param from the dsc file?
  public double getLandmarkDistanceVariance(int lm) {
    return getDictionary().getDouble("LOCALIZER_LM_DIST_VAR");
  }
  
  public double getLandmarkAngle(int lm) {
    Vec2 tmp = new Vec2(landmarks[lm].getPosition());
    double noise =  visionNoiseGetNext(); //make it noisy
   
    tmp.sub(position);
    tmp.sett((1.0-noise)*tmp.t);

    return tmp.t - steer.t;
  }

  //make this a param from dsc file??
    public double getLandmarkAngleVariance(int lm) {
      return getDictionary().getDouble("LOCALIZER_LM_ANGLE_VAR");

    }

  public void clipToMap(Sample s) {
    //clip the sample to our map
    //		System.out.println("clip to map "+s.toString());
    double x = s.data[Sample.x];
    double y = s.data[Sample.y];
    boolean moved = false;
    
    if (x < left) {
      x = left + clippingOffset;
      moved = true;
    }
    else if (x > right) { 
      x = right - clippingOffset;
      moved = true;
    }
    if (y > top) { 
      y = top - clippingOffset;
      moved = true;
    }
    else if (y < bottom) {
      y = bottom + clippingOffset;
      moved = true;
    }
    
    if (moved) {
      
      
      Sample newSample = gs.generateSample();
      //    System.out.println("JRS: ctm: got new samp");
      x += newSample.data[Sample.x];
      y += newSample.data[Sample.y];
      
      s.data[Sample.x] = x;
      s.data[Sample.y] = y;
      s.data[Sample.t] += newSample.data[Sample.t];
      
    }
  }
  
  public double [] clipToMap(double x, double y, double theta) {
    double [] res = new double[3];

    boolean moved = false;
    
    if (x < left) {
	x = left + clippingOffset;
	moved = true;
      }
      else if (x > right) { 
	x = right - clippingOffset;
	moved = true;
      }
      if (y > top) { 
	y = top - clippingOffset;
	moved = true;
      }
      else if (y < bottom) {
	y = bottom + clippingOffset;
	moved = true;
      }
      
      if (moved) {
		
	Sample newSample = gs.generateSample();

	res[0] = x + newSample.data[Sample.x];
	res[1] = y + newSample.data[Sample.y];
	res[2] = theta + newSample.data[Sample.t];
      }
      else {
	res[0] = x;
	res[1] = y;
	res[2] = theta;
      }

      return res;
    }
  
    public boolean onMap(double x, double y) {
	if (x < left ||  x > right)
	    return false;
	//y coord is off
	if (y > top || y < bottom)
	    return false;
	return true;
    }

    public double [] getMovementDistParams() {
	double [] params = new double[6]; //for x y and t movement mn/std
	
	params[0] = mvstep.r; //movement mag mean
        // standard deviation 2% distance error after 1m
	params[1] = Math.sqrt(Math.pow(.02    ,2.0) * (mvstep.r / 1.0)); //move mag std dev
	params[2] = mvstep.t - oldHeading; //move dir mean
        // standard deviation .5 degree per .5 meters
	params[3] = Math.sqrt(Math.pow(.5 * Math.PI / 180,2.0) * (mvstep.r / 0.5));  //move dir std dev
	params[4] = steer.t - oldHeading; //turrent direction
	params[5] = Math.sqrt(Math.pow(.5 * Math.PI / 180,2.0) * (mvstep.r / 0.5));
	
	oldMoveR = mvstep.r;
	oldMoveT = mvstep.t;
	oldHeading = steer.t;
	return params;
    }

  /*---- For LineLocalizationRobot ---*/

  public LineSim [] getLines() {
    if (lines == null) {
      int i;
      int num=0;
      //landmarks = new SimulatedObject[all_objects.length];
      for (i = 0; i < all_objects.length; i++) {
	if (all_objects[i] instanceof LineSim) {  
	  System.out.println("GLIN: obj "+i+" is line! getP = "+
			     all_objects[i].getPosition().toString());
	  // landmarks[i] = all_objects[i];
	  num++;
	}
	else {
	  System.out.println("GLIN: obj "+i+" is not line...");
	  //    landmarks[i] = null;
	}
      }
      
      lines = new LineSim[num];
      
      for (i = 0,num=0; i < all_objects.length; i++) {
	if (all_objects[i] instanceof LineSim) {
	  lines[num] = (LineSim) all_objects[i];
	  num++;
	}
      }
      
      System.out.println("JRS: getLines: got "+num+" lines");
      
      return lines;
      
    }
    return lines;
  }
  
  public Vec2 [] getVisualLines(long timestamp, int channel) {
    //only look if new info given....
    if ((timestamp > lastVisualLinesTime)|| 
	(timestamp == -1)) {
      
      if (timestamp != -1) 
	lastVisualLinesTime = timestamp;
      
      //scan our FOV with rays coming from camera to end of view
      //from left to right like a radar.  at each line we will 
      //look at vertRes points to see if they are on a line
      //so i guess it's O(horzRes*vertRes*numLines)
      //note: because this is simulation, we take adavantage of the fact
      //that we know where we are exactly in the world.  for later versions
      //we can use that to narrow done the number of lines we look at
      /*
      double REALLYBIG = 999999;
      Vec2 [] left = new Vec2[lines.length]; //hold leftmost points for each line seen
      Vec2 [] right = new Vec2[lines.length]; //holld rightmost points for each line seen
      for (int i =0;i < lines.length; i++) {
	if (steer.t < Math.PI) {
	  left[i] = new Vec2(REALLYBIG,0);
	  right[i] = new Vec2(-REALLYBIG,0);
	}
	else {
	  left[i] = new Vec2(-REALLYBIG,0);
	  right[i] = new Vec2(REALLYBIG,0);
	}
      }
      
      int total=0; //num lines seen
      Vec2 tmpPos = new Vec2(position);
      double horzOffset = steer.t - (VISION_FOV_RAD/2);
      double testT, testR;
      Vec2 test; //this is egocentric vec to point we are checking for lineness
      //start at one otherwise we're checking our center
      for (int i = 1; i < horzRes; i++) {
	testT = horzOffset + ((VISION_FOV_RAD)/(double)horzRes)*i;
	for (int j =1; j < vertRes; j++) {
	  test = new Vec2(0,0);
	  test.setr((VISION_RANGE)/(double)vertRes*j);
	  test.sett(testT);
	  test.add(tmpPos); //check on lineness from global center
	  for (int k = 0; k < lines.length; k++) {
	    if (lines[k].pointOnLine(test)) {
	      //ok so we can see a point on this line...
	      System.out.println("GVL: can see line "+k+" at "+test.toString());
	      
	      if (steer.t < Math.PI) {
		if (test.x < left[i].x)
		  left[i].setx(test.x);
		if (test.x > right[i].x)
		  right[i].setx(test.x);
	      }
	      else {
		if (test.x > left[i].x)
		  left[i].setx( test.x);
		if (test.x < right[i].x)
		  right[i].setx(test.x);
	      }
	      total++;
	    }
	  }
	}
      }
      
      linesRes = new Vec2[total*2]; //hold the endpoints
      int count =0;
      i
      for (int i =0; i < lines.length; i++) {
	if (Math.abs(left[i].x) != REALLYBIG) {
	  linesRes[count] =  new Vec2(left[i].x, lines[i].evaluate(left[i].x));
	  linesRes[count+1] = new Vec2(right[i].x, lines[i].evaluate(right[i].x));
	  linesRes[count].sub(tmpPos);
	  linesRes[count+1].sub(tmpPos);
	  count += 2;
	}
      }
      
      return linesRes;
      */
      
      Vec2 beginSeen, endSeen;
      linesRes = new Vec2[lines.length*2];
      boolean haveSeen;
      //for each line, we look and see if we can find who's looking at it...
      //this could be done globally by the lines to save time (for multiple robots)
      
      for (int i =0; i < lines.length; i++) {
	Vec2 start = lines[i].getStart();
	Vec2 end = lines[i].getEnd();
	
	//fromRobot is the vector pointing to one end of a line we are testing
	//for visibility
	Vec2 fromRobot = new Vec2(start);

	fromRobot.sub(position);
	Vec2 tmp = new Vec2(end);
	tmp.sub(start);
	double totalLength = tmp.r;

	int numDiv = (int) (totalLength / lineRes); //number of divisions in this line...

	haveSeen = false; //havent seen this line yet
	beginSeen = null;
	endSeen = null;
	int j;
	for (j = 0; j < numDiv; j++) {
	  tmp.setr( j*lineRes );
	  fromRobot.add(tmp);

	  if ((fromRobot.r < SimpleCye.VISION_RANGE) &&
	      (Math.abs(Units.BestTurnRad(steer.t, fromRobot.t)) <
	       (SimpleCye.VISION_FOV_RAD/2))) {
	    //we can see this point on the line...
	    /*   System.out.println("seen line start "+start.toString()+" end "+end.toString());
	    System.out.println("GVL: fR.r = "+fromRobot.r+" s.t = "+steer.t+
			       " fR.t = "+fromRobot.t+" ang = "+Math.abs(Units.BestTurnRad(steer.t, fromRobot.t)));
	    System.out.println("pos = "+position.toString());
	    */
	    //ok we start to see this line
	    if (!haveSeen) {
	     
	      haveSeen = true;
	      beginSeen = new Vec2(fromRobot);
	      	    
	    }
	  }
	  else {
	    //if havent seen it and seen before, stop seeing
	    if (haveSeen) {
	      haveSeen = false;

	      //point back to start of line
	      fromRobot.sub(tmp);

	      //the previous point on the line was the last one we could see
	      tmp.setr((j-1)*lineRes);
	      fromRobot.add(tmp);
	      endSeen = new Vec2(fromRobot);

	      //point back to start of the line
	      fromRobot.sub(tmp);
	      //      System.out.println("end 1 j -1 = "+(j-1));
	      //     System.out.println("GVL: seeting endpoint on line "+i+" ts ="+timestamp);
	      Vec2 seg = new Vec2(endSeen);
	      seg.sub(beginSeen);
	      //    System.out.println("GVL: seg = "+seg.toString());
	      
	      break; //no need to finish this
	    }
	  }
	  fromRobot.sub(tmp);
	}

	if (haveSeen) {
	  //the line continued past of FOV
	  //the previous point was the last one seen
	  tmp.setr((j-1)*lineRes);
	  fromRobot.add(tmp);
	  endSeen = new Vec2(fromRobot);
	  //	  System.out.println("end 2 j-1 = "+(j-1));
		  
	}

	//set the vector to the first point we see
	if (beginSeen != null) 
	  linesRes[i*2] = new Vec2(beginSeen);
	else
	  linesRes[i*2] = null;

	//set the vector to the end point of visible part of the line
	if (endSeen != null) {
	  linesRes[2*i+1] = new Vec2(endSeen);
	  System.out.println("LINES: ("+linesRes[i*2].x+", "+linesRes[i*2].y+") <-> ("+
			     linesRes[2*i+1].x+", "+linesRes[2*i+1].y+")");
	}
	else {
	  linesRes[2*i+1]= null;
	}

	
      }
      //these are pointing egocentric to endpoints of the line??
      return linesRes;
      
    }
    return linesRes;
    
  }




}

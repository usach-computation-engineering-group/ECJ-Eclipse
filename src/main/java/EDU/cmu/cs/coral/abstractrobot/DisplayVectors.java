/* This code is part of the abstractrobot package of TeamBots.
 * Copyright (c) 1999 by John Sweeney and Carnegie Mellon University.
 */

package EDU.cmu.cs.coral.abstractrobot;

import EDU.gatech.cc.is.util.Vec2;
import java.awt.Color;
import java.lang.Math;
import java.awt.Graphics;

/**
  * Defines a class for handling the drawing of an array of Vec2s
  * in positions given by another array of Vec2s.
  */
public class DisplayVectors {
  protected Vec2 [] vecPos;
  protected Vec2 [] vecMag;
  protected Color [] colors;
  protected boolean haveSingleColor;
  protected Color singleColor;
  protected final Color DEFAULT_VEC_COLOR = Color.black;
  protected int dotSize = 4;
 
  public static final int ARROW_NO_POINT = 1;
  
  protected int arrowStyle = ARROW_NO_POINT;

  public DisplayVectors() {
    vecPos = null;
    vecMag = null;
    haveSingleColor = true;
    singleColor = DEFAULT_VEC_COLOR;
  }


  /** 
    * create a display vector with default color
    */
  public DisplayVectors(Vec2 [] vecP, Vec2 [] vecM) { 
    
    vecPos = vecP;
    vecMag = vecM;

    haveSingleColor = true;
    singleColor = DEFAULT_VEC_COLOR; 
  }
    

  public DisplayVectors(Vec2 [] vecP, Vec2 [] vecM, Color c) {
    
    vecPos = vecP;
    vecMag = vecM;
    
    haveSingleColor = true;
    singleColor = c;

  }

  public DisplayVectors(Vec2 [] vecP, Vec2 [] vecM, Color [] theColors) {
    
    vecPos = vecP;
    vecMag = vecM;

    haveSingleColor = false;
    
    colors= theColors;
  }

  public void set(Vec2 [] vecP, Vec2 [] vecM) {
    vecPos = vecP;
    vecMag = vecM;

    haveSingleColor = true;
    singleColor = DEFAULT_VEC_COLOR;
  }

  public void set(Vec2 [] vecP, Vec2 [] vecM, Color c) {
    vecPos = vecP;
    vecMag = vecM;
    
    haveSingleColor = true;
    singleColor = c;
  }

  public void set(Vec2 [] vecP, Vec2 [] vecM, Color [] c) {
    vecPos = vecP;
    vecMag = vecM;
    
    haveSingleColor = false;
    colors = c;
  }

  public void setDotSize(int dr) { dotSize = dr;}

  public void setArrowStyle(int style) { arrowStyle = style; }

  public void draw(Graphics g, int w, int h, double t, double b,
		   double l, double r) {
    
    double meterspp = (r - l) / (double)w;
    double xpix, ypix;
    double cosx, siny;

    if (vecPos == null || vecMag == null)
      return;

    if (arrowStyle == ARROW_NO_POINT) {
      
      for (int i = 0; i < vecPos.length; i++) {
	
	if (haveSingleColor) 
	  g.setColor(singleColor);
	else {
	  g.setColor(colors[i]);
	}
      	
	xpix = (int) ( (vecPos[i].x - l) / meterspp);
	ypix = (int) ( (double)h - ((vecPos[i].y - b)/meterspp));
	
	g.fillOval((int)xpix-(dotSize/2), (int)ypix-(dotSize/2), dotSize,dotSize);
	
	cosx = (int) (Math.cos(vecMag[i].t)*vecMag[i].r / meterspp);
	siny = (int) (Math.sin(vecMag[i].t)*vecMag[i].r / meterspp);
	
	g.drawLine((int)xpix,(int)ypix, (int)(xpix+cosx), (int)(ypix-siny));
      }
    }
  }



}

/* This code is part of the localize package of TeamBots.
 * Copyright (c) 1999, 2000 by John Sweeney and Carnegie Mellon University
 */
package EDU.cmu.cs.coral.localize;


import java.awt.*;
import EDU.gatech.cc.is.simulation.SimulatedObject;


public class LocalizationCanvas extends Canvas {

    protected SampleSet samples;

    protected Frame parent;

    protected int width, height;
    protected Image imageBuffer;
    protected Graphics graphicsBuffer;
    protected double top, bottom, left, right;

    protected SimulatedObject [] objs;

    public LocalizationCanvas(Frame p, int w, int h, double t, double b,
			      double l, double r) {
	
	parent = p;

	width = w;
	height = h;
	
	top =t ;
	bottom = b;
	left = l;
	right = r;
	setBackground(Color.white);
	setBounds(0,0, width, height);

	imageBuffer = createImage(width, height);
	graphicsBuffer = imageBuffer.getGraphics();
    }

    public LocalizationCanvas() { imageBuffer = null;
    }
    
    public void setSampleSet(SampleSet ss) {
	samples = ss;
    }

    public SampleSet getSampleSet() { return samples; }

    public void setDrawObjects(SimulatedObject [] theObjs) {
	objs = theObjs;
    }

    /*    public void setRealPosition() {}
	  public void setEstPosition() {}
    */
    public void paint(Graphics g) {
	System.out.println("LocalizationCanvas: paint: start");
	if (samples != null) {
	    Sample s;
	    int shade;
	    Color c;

	    graphicsBuffer.setColor(Color.white);
	    graphicsBuffer.fillRect(0,0,width, height);


	    for (int i = 0; i < objs.length; i++) {
		objs[i].draw(graphicsBuffer, width, height,
			     top, bottom, left, right);
	    }

	    synchronized (samples) {
	    samples.reset();
	    int i =0 ;
	    double meterspp = ((right-left)/(double)width);
	    while (samples.haveMoreSamples()) {
		s = samples.getNextSample();
		//			System.out.println("Sample["+i+"] = "+s.toString());
		i++;
		double tcol = s.data[Sample.w] * 254.0;
		shade = 255 - (int)tcol;

		drawArrow(graphicsBuffer,s, shade, meterspp);
	       
		/*    double x = (s.data[Sample.x] - left) / meterspp;
	       double y = (top - s.data[Sample.y])/meterspp;
	       graphicsBuffer.setColor(Color.black);
	       graphicsBuffer.drawOval((int)x,
				       (int)y,
				       2,2);
		*/
	    }
	    System.out.println("done");
	    g.drawImage(imageBuffer, 0, 0, this);
	    }
	}
	else {
	    System.out.println("LCANVAS: paint: didnt draw any samples!");
	}
    }


    public synchronized void update(Graphics g) {
	paint(g);
    }

    protected void drawArrow(Graphics g, Sample s, int shade, double meterspp) {
	int x, y;
	
	double cosx, siny;
	
	x = (int)((s.data[Sample.x] - left) / meterspp);
	y = (int)((top - s.data[Sample.y])/ meterspp);
	
	cosx = Math.cos(s.data[Sample.t]);
	siny = Math.sin(s.data[Sample.t]);
	cosx *= 5.0;
	siny *= 5.0;
	g.setColor(Color.red);
	g.drawLine(x+ (int)(cosx), y+(int)(siny),
		   x,y);
	g.setColor(new Color(shade, shade, shade));
	g.drawLine(x,y, x- (int)cosx, y-(int)siny);
	
    }

    public void setInfo(Frame p, int w, int h, double t, double b,
			double l, double r) {
	parent =p;
	width =w ;
	height =h;
	top = t;
	bottom =b ;
	left = l;
	right = r;

	if (imageBuffer == null) {
	    System.out.println("LocalizationCanvas: width = "+width+" height ="+height);
	    imageBuffer = createImage(width, height);
	    graphicsBuffer = imageBuffer.getGraphics();
	}

	setBounds(0,0,width,height);
    }
}	

       

		  
		

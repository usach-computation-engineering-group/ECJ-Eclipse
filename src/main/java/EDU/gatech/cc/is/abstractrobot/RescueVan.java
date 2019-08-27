/*
 * RescueVan.java
 */

package EDU.gatech.cc.is.abstractrobot;

import EDU.gatech.cc.is.communication.Transceiver;
import EDU.gatech.cc.is.util.*;


/**
 * Provides an abstract interface to the hardware of
 * a rescue van.
 */

public interface RescueVan extends SimpleInterface,
	VisualObjectSensor, MultiCarry, KinSensor, Transceiver
	{
	// some useful numbers
        public  static final double  VISION_RANGE = 1000;
        public  static final int     VISION_FOV_DEG = 100;
        public  static final double  VISION_FOV_RAD = Units.DegToRad(100);
        public  static final double  PICKUP_CAPTURE_RADIUS = 200; // 0.2 km
        public  static final int     MAX_CAPACITY = 1000; // 10 people
        public  static final double  MAX_TRANSLATION = 44; // m/s = 100mph
        public  static final double  MAX_STEER = 0.7854; 
        public  static final double  RADIUS = 2.5; 
	}

package EDU.cmu.cs.coral.localize;


public class DoubleRectangle {
    public double x;
    public double y;
    public double width;
    public double height;

    /*x,y is top left corner*/
    public DoubleRectangle(double xx, double yy, double ww, double hh) {
	x = xx;
	y = xx;
	width = ww;
	height = hh;
    }

    /* note this assumes the rect is in space iwth origin in the middle*/
    public boolean contains(double xx, double yy) {
	if ( xx < x || xx > (x+width)) {
	    return false;
	}
	if (yy > y || yy < (y-height)) {
	    return false;
	}

	return true;
    }
    
    public String toString() {
	return new String("("+x+", "+y+") w = "+width+" h = "+height);
    }

}

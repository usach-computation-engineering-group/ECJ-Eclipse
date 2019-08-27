/*
 * CMVision.java
 */

package CMVision;
//package	EDU.cmu.cs.coral.vision;

/**
 * Java Native Interface to blob finding code using Video 4 Linux II.
 * <P>
 * @author James Bruce
 * @version $Revision: 1.2 $
 */

import java.io.*;

public class JCMVision
{
    static boolean libraryLoaded = false;
    static
    {
	try {
            // need to do: setenv LD_LIBRARY_PATH <dir with libcmvision.so>
	    System.loadLibrary("cmvision");
	    libraryLoaded = true;
	} catch (Exception e) {
	    System.out.println("Unable to load cmuvision library: "+ e.getMessage());
	}
    }

    public class Region
    {
	public int color; // classified color
	public int area;  // area in pixels

	public int x1; // involving box
	public int y1;
	public int x2;
	public int y2;

	public double cen_x; // centroid
	public double cen_y;
    }

    /**
     * Maximum number of colors we can track.
     */
    public static final int CMV_MAX_COLORS = 32;

    /**
     * Maximum number of regions we can track.
     */
    public static final int CMV_MAX_BLOBS = 1024;

    public JCMVision()
    {
	if (!libraryLoaded)
	    throw new RuntimeException("CMVision library not loaded!");
    }

    /**
     * Initialize the video capture device and vision system.
     *
     * @param device, device number (usually 0)
     * @param width, image width for vision system
     * @param height, image height for vision system
     * @return true on success, false on failure
     */
    public native boolean init(int device,int width,int height);

    /**
     * Close the video device and stop the vision system.
     *
     * @return true on success, false on failure
     */
    public native boolean quit();

    /**
     * Capture and process the most recent image.
     *
     * @return true on success, false on failure
     */
    public native boolean processFrame();

    /**
     * Find out how many regions of one color are visible.
     *
     * @param color_id, one of [1..CMV_MAX_COLORS].
     * @return number of regions of that color.
     */
    public native int getNumRegions(int color_id);

    /**
     * Get an array of the regions objects of a particular color.
     * If more regions are requested than are visible, only the
     * first getNumRegions() entries in the region array are valid.
     *
     * @param color_id, one of [1..CMV_MAX_COLORS].
     * @param number, how many regions to put into the array.
     * @return an array of class Region, of size min(number,getNumRegions(color_id)).
     */
    public Region[] getRegions(int color_id,int number){
	int fields = 8; // number of fields in the region struct
        int available = getNumRegions(color_id);

        int num = Math.min(number,available);

	int serbuf[] = new int[num * fields];
	Region reg[] = new Region[num];
	Region r;
	int i,ofs;

	if(num == 0) return(reg);

	getSerRegions(serbuf,color_id,num);

	for(i=0; i<num; i++){
	    ofs = i * 8;
	    r = new Region();
	    r.color = serbuf[ofs + 0];
	    r.area  = serbuf[ofs + 1];
	    r.x1    = serbuf[ofs + 2];
	    r.y1    = serbuf[ofs + 3];
	    r.x2    = serbuf[ofs + 4];
	    r.y2    = serbuf[ofs + 5];
	    r.cen_x = serbuf[ofs + 6] / 1000.0;
	    r.cen_y = serbuf[ofs + 7] / 1000.0;
	    reg[i] = r;
	}
	// System.out.println("");

	return(reg);
    }

    /**
     * The native version of the above function that passes the data
     * from C++ to Java by serializing it into an integer array
     */
    native void getSerRegions(int serbuf[],int color_id,int number);
}


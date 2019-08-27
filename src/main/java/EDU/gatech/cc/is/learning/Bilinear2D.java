/*
 * Bilinear2D.Java
 */

package EDU.gatech.cc.is.learning;


import java.io.*;
import java.lang.Math;

/**
 * Transform points from a two-dimensional space to another two
 * dimensional space.
 * <P>
 * <B>Introduction</B><BR>
 * The Bilinear2D is a vectorial function that transforms points from a
 * two-dimensional space to another two-dimensional space. The transformation
 * is specified using a grid at the domain space and associating the points at
 * the target space that correspond to each node of the grid. 
 * <P>
 * <B>Computation</B><BR>
 * The evaluation of query points is performed using a bilinear interpolation
 * of the target points associated with the four neighboring points 
 * to the query point.
 * <P>
 * The domain space is specified by a rectangle and a grid of points.
 * The rectangle is defined using four numbers: <code>min_x_limit</code>,
 * <code>max_x_limit</code>, <code>min_y_limit</code>, and
 * <code>max_y_limit</code>.
 * The grid of points is defined using the number of points along the x and
 * y axes: <code>npoints_x</code> and <code>npoints_y</code>, and a
 * bidimensional array of 2D doubles (i.e., the points in the range space).
 * <P>
 * <B>File format</B><BR>
 * A text file can be used to define a Bilinear2D function. The format is as
 * follows: <BR>
 * <PRE>
 * min_x_limit:   [value]
 * max_x_limit:   [value]
 * min_y_limit:   [value]
 * max_y_limit:   [value]
 * npoints_x:     [r]
 * npoints_y:     [s]
 * [x_00],[y_00]  [x_01],[y_01] ... [x_0r],[y_0r]
 * [x_10],[y_10]  [x_11],[y_11] ... [x_1r],[y_1r]
 *       .              .                 .
 *       .              .                 .
 *       .              .                 .
 * [x_s0],[y_s0]  [x_s1],[y_s1] ... [x_sr],[y_sr]
 * </PRE>
 *
 * Example:<BR>
 * <PRE>
 * min_x_limit:   0
 * max_x_limit:   50
 * min_y_limit:   0
 * max_y_limit:   50
 * npoints_x:     5
 * npoints_y:     5
 * 10,20  7.5,20    5,20  2.5,20   0,20
 * 10,15  7.5,15    5,15  2.5,15   0,15
 * 10,10  7.5,10    5,10  2.5,10   0,10
 * 10, 5  7.5, 5    5, 5  2.5, 5   0, 5
 * 10, 0  7.5, 0    5, 0  2.5, 0   0, 0
 * </PRE>
 *
 * <P>
 * Copyright (c)1997 Georgia Tech Research Corporation
 *
 * @author Juan Carlos Santamaria (carlos@cc.gatech.edu)
 * @see FunctionApproximator
 * @version $Revision: 1.1 $
 */

public class Bilinear2D extends FunctionApproximator
	implements Cloneable, Serializable
{
    //
    //  information for X variable
    // 
    
    /**
     * Maximum value for the x variable in the domain space.
     */
    public final double   max_x_limit;
    
    /**
     * Minimum value for the x variable in the domain space.
     */
    public final double   min_x_limit;

    /**
     * Range of the x variable in the domain space: (max_limit - min_limit).
     */
    public final double   range_x;

    /**
     * Number of grid points in the x axis.
     */
    public final int      npoints_x;
    
    /**
     * Resolution of the x axis: (max_limit - min_limit) / (npoints - 1)
     */
    public final double   resolution_x;

    //
    // information for Y variable
    //
    
    /**
     * Maximum value for the y variable in the domain space.
     */
    public final double   max_y_limit;
    
    /**
     * Minimum value for the y variable in the domain space.
     */
    public final double   min_y_limit;
    
    /**
     * Range of the y variable in the domain space: (max_limit - min_limit).
     */
    public final double   range_y;
    
    /**
     * Number of grid points in the y axis.
     */
    public final int      npoints_y;

    /**
     * Resolution of the y axis: (max_limit - min_limit) / (npoints - 1)
     */
    public final double   resolution_y;

    //
    // table
    //
    
    /**
     * Grid of points.
     */
    protected double[]    table[][];

    /**
     * Create an instance of a Bilinear function approximator that maps
     * points from a two-dimensional space (x,y) to another two-dimensional
     * space (u,v).
     *
     * @ param     min_x_limit   the minimum value for the x axis.
     * @ param     max_x_limit   the maximum value for the x axis.
     * @ param     min_y_limit   the minimum value for the y axis.
     * @ param     max_y_limit   the maximum value for the y axis.
     * @ param     npoints_x     the width of the grid of points.
     * @ param     npoints_y     the height of the grid of points.
     * @ param     table         the grid of (u,v) points.
     */
    public Bilinear2D( double min_x_limit, double max_x_limit, int npoints_x,
                       double min_y_limit, double max_y_limit, int npoints_y,
                       double[] table[][] ) {
        
        // state this is a 2D to 2D function approximator

        super(2,2);

        // copy grid definition

        double[] tmp[][] = new double[npoints_x][npoints_y][2];
        
	for( int i=0 ; i<npoints_x ; i++ )
	    for( int j=0 ; j<npoints_y ; j++ ) {
                tmp[i][j][0] = table[i][j][0];
                tmp[i][j][1] = table[i][j][1];
	    }

        // store the definition of variable x
    
        this.min_x_limit  = min_x_limit;
        this.max_x_limit  = max_x_limit;
        this.range_x      = max_x_limit - min_x_limit;
        this.npoints_x    = npoints_x;
        this.resolution_x = range_x/(npoints_x-1);

        // store the definition of variable y
    
        this.min_y_limit  = min_y_limit;
        this.max_y_limit  = max_y_limit;
        this.range_y      = max_y_limit - min_y_limit;
        this.npoints_y    = npoints_y;
        this.resolution_y = range_y/(npoints_y-1);

        // store the grid
        
        this.table        = tmp;
    }

    /**
     * Loads the definition of a Bilinear2D function approximator from a file.
     *
     * @param      filename   the file name.
     * @return     an instance of the Bilinear2D defined by the file.
     * @exception  IOException                    if an I/O error occurs.
     * @exception  FunctionApproximatorException  if a parse error occurs.
     * @see        FunctionApproximator
     */
    public Bilinear2D( String filename )
           throws FunctionApproximatorException, IOException {

        // state this is a 2D to 2D function approximator

        super(2,2);

        // local variables
        
        double min_x_limit, max_x_limit, min_y_limit, max_y_limit;
        int    npoints_x, npoints_y;

        // read definition from file
        
        FileReader      file = new FileReader(filename);
        StreamTokenizer in   = new StreamTokenizer(file);

        in.wordChars('A','_');  // include '_' as a word character
        
        min_x_limit = 0;        // default initialization
        max_x_limit = 1;
        min_y_limit = 0;
        max_y_limit = 1;
        npoints_x   = 2;
        npoints_y   = 2;
        
        int    flags = 0x3F;    // read six fields
        
        while ( flags!=0 ) {
            if ( in.nextToken() == StreamTokenizer.TT_WORD ) {
                if ( in.sval.equals("min_x_limit") ) {
                    if ( (in.nextToken()==':') &&
                         (in.nextToken()==StreamTokenizer.TT_NUMBER) ) {
                        min_x_limit = in.nval;
                        flags &= ~0x01;
                    }
                    else {
                        throw new FunctionApproximatorException(
                                      "Bilinear2D: " +
                                      "bad file format at " +
                                      "field min_x_limit in " +
                                      filename );
                    }
                }
                else if ( in.sval.equals("max_x_limit") ) {
                    if ( (in.nextToken()==':') &&
                         (in.nextToken()==StreamTokenizer.TT_NUMBER) ) {
                        max_x_limit = in.nval;
                        flags &= ~0x02;
                    }
                    else {
                        throw new FunctionApproximatorException(
                                      "Bilinear2D: " +
                                      "bad file format at " +
                                      "field max_x_limit in " +
                                      filename );
                    }
                }
                else if ( in.sval.equals("min_y_limit") ) {
                    if ( (in.nextToken()==':') &&
                         (in.nextToken()==StreamTokenizer.TT_NUMBER) ) {
                        min_y_limit = in.nval;
                        flags &= ~0x04;
                    }
                    else {
                        throw new FunctionApproximatorException(
                                      "Bilinear2D: " +
                                      "bad file format at " +
                                      "field min_y_limit in " +
                                      filename );
                    }
                }
                else if ( in.sval.equals("max_y_limit") ) {
                    if ( (in.nextToken()==':') &&
                         (in.nextToken()==StreamTokenizer.TT_NUMBER) ) {
                        max_y_limit = in.nval;
                        flags &= ~0x08;
                    }
                    else {
                        throw new FunctionApproximatorException(
                                      "Bilinear2D: " +
                                      "bad file format at " +
                                      "field max_y_limit in " +
                                      filename );
                    }
                }
                else if ( in.sval.equals("npoints_x") ) {
                    if ( (in.nextToken()==':') &&
                         (in.nextToken()==StreamTokenizer.TT_NUMBER) ) {
                        npoints_x = (int)in.nval;
                        flags &= ~0x10;
                    }
                    else {
                        throw new FunctionApproximatorException(
                                      "Bilinear2D: " +
                                      "bad file format at " +
                                      "field npoints_x in " +
                                      filename );
                    }
                }
                else if ( in.sval.equals("npoints_y") ) {
                    if ( (in.nextToken()==':') &&
                         (in.nextToken()==StreamTokenizer.TT_NUMBER) ) {
                        npoints_y = (int)in.nval;
                        flags &= ~0x20;
                    }
                    else {
                        throw new FunctionApproximatorException(
                                      "Bilinear2D: " +
                                      "bad file format at " +
                                      "field npoints_y in " +
                                      filename );
                    }
                }   
            }
            else {
                throw new FunctionApproximatorException(
                              "Bilinear2D: " +
                              "bad file format in "+filename );
            }
        }

        // read table

        double[] tmp[][] = new double[npoints_x][npoints_y][2];

        for( int j=0 ; j<npoints_y ; j++ ) 
            for( int i=0 ; i<npoints_x ; i++ ) {
                if ( in.nextToken()==StreamTokenizer.TT_NUMBER ) 
                    tmp[i][j][0] = in.nval;
                else  
                    throw new FunctionApproximatorException(
                                      "Bilinear2D: " +
                                      "bad file format at point " +
                                      "("+i+","+j+") in "+
                                      filename );
                if ( in.nextToken()!=',' ) 
                    throw new FunctionApproximatorException(
                                      "Bilinear2D: " +
                                      "bad file format at point " +
                                      "("+i+","+j+") in "+
                                      filename );
                if ( in.nextToken()==StreamTokenizer.TT_NUMBER ) 
                    tmp[i][j][1] = in.nval;
                else  
                    throw new FunctionApproximatorException(
                                      "Bilinear2D: " +
                                      "bad file format at point " +
                                      "("+i+","+j+") in "+
                                      filename );
            }

        // store the definition of variable x
    
        this.min_x_limit  = min_x_limit;
        this.max_x_limit  = max_x_limit;
        this.range_x      = max_x_limit - min_x_limit;
        this.npoints_x    = npoints_x;
        this.resolution_x = range_x/(npoints_x-1);

        // store the definition of variable y
    
        this.min_y_limit  = min_y_limit;
        this.max_y_limit  = max_y_limit;
        this.range_y      = max_y_limit - min_y_limit;
        this.npoints_y    = npoints_y;
        this.resolution_y = range_y/(npoints_y-1);

        // store the grid
        
        this.table        = tmp;
    }

    /**
     * Save a definition of this instance in a file.
     *
     * @param      filename   the file name.
     * @exception  IOException                    if an I/O error occurs.
     * @exception  FunctionApproximatorException  if something wrong occurs.
     */
    public void saveDefinition( String filename )
    throws FunctionApproximatorException, IOException {
        FileWriter  file = new FileWriter(filename);
        PrintWriter out  = new PrintWriter( file, true );

        // write the definition of variable x
        
        out.println( "min_x_limit: " + min_x_limit );
        out.println( "max_x_limit: " + max_x_limit );
        out.println( "npoints_x:   " + npoints_x );
        
        // write the definition of variable x
        
        out.println( "min_y_limit: " + min_y_limit );
        out.println( "max_y_limit: " + max_y_limit );
        out.println( "npoints_y:   " + npoints_y );

        // write the definition of the grid

        for( int j=0 ; j<npoints_y ; j++ ) {
            out.print("   ");
            for( int i=0 ; i<npoints_x ; i++ ) {
                out.print(table[i][j][0]+","+table[i][j][1]+"   ");
            }
            out.println();
        }
    }
    
    /**
     * Computes and returns the bilinear interpolation associated with the
     * point. The point is clipped to the domain rectangle.
     *
     * @param     point  the input point (two-dimensional array of doubles).
     * @return    the output point (two-dimensional array of doubles).
     */
    public double[] query( double[] point ) {

        double[] p_ll, p_lr, p_ul, p_ur;
        int      Ix1, Ix2, Iy1, Iy2;
        double   val_x, val_y;
        double   v_x, v_y;

        // compute index for variable x
    
        val_x = Math.min( max_x_limit, Math.max( point[0], min_x_limit ) );
        Ix1   = (int)Math.floor( (val_x - min_x_limit) / resolution_x );
        v_x   = Math.IEEEremainder( val_x - min_x_limit, resolution_x ) /
                resolution_x;
        if ( v_x<0 )
            v_x = 1+v_x;
        Ix2   = Ix1 + 1;
        if ( Ix2==npoints_x )
            Ix2--;
     
        // compute index for variable y
    
        val_y = Math.min( max_y_limit, Math.max( point[1], min_y_limit ) );
        Iy1   = (int)Math.floor( (val_y - min_y_limit) / resolution_y );
        v_y   = Math.IEEEremainder( val_y - min_y_limit, resolution_y ) /
                resolution_y;
        if ( v_y<0 )
            v_y = 1+v_y;
        Iy2   = Iy1 + 1;
        if ( Iy2==npoints_y )
            Iy2--;

        // retrieve the four neighbors

        p_ll = table[Ix1][Iy1];
        p_lr = table[Ix2][Iy1];
        p_ul = table[Ix1][Iy2];
        p_ur = table[Ix2][Iy2];

        // compute the result

        double[] result = new double[2];
        
        result[0] = p_ll[0] + 
                   (p_lr[0]-p_ll[0])*v_x + 
                   (p_ul[0]-p_ll[0])*v_y + 
                   (p_ur[0]-p_ul[0]-p_lr[0]+p_ll[0])*v_x*v_y;

        result[1] = p_ll[1] + 
                   (p_lr[1]-p_ll[1])*v_x + 
                   (p_ul[1]-p_ll[1])*v_y + 
                   (p_ur[1]-p_ul[1]-p_lr[1]+p_ll[1])*v_x*v_y;

        return result;
    }

    /**
     * Computes and returns the bilinear interpolation associated with the
     * point. The point is clipped to the domain rectangle.
     *
     * @param     x   the x ordinate of the input point.
     * @param     y   the y ordinate of the input point.
     * @return    the output point (two-dimensional array of doubles).
     */
    public double[] query( int x, int y ) {
        double[] point = new double[2];
        point[0] = x;
        point[1] = y;
        return query( point );
    }

    /**
     * Not implemented.
     *
     * @exception FunctionApproximatorException  always.
     */
    public void update( double[] q, double[] p )
    throws FunctionApproximatorException {
        throw new FunctionApproximatorException( "update: not implemented" );
    }
}



/*
 * FunctionApproximator.java
 */

package EDU.gatech.cc.is.learning;

import java.io.*;
import java.io.IOException;

/**
 * Provides an abstract interface to 
 * various function approximator implementations.
 * <P>
 * Copyright (c)2000 Georgia Tech Research Corporation
 *
 * @author Juan Carlos Santamaria (carlos@cc.gatech.edu)
 * @version $Revision: 1.1 $
 */

public abstract class FunctionApproximator
	implements Cloneable, Serializable
{
    /**
     * The dimension of the domain space.
     */
    public final int domain_dim;

    /**
     * The dimension of the range space.
     */
    public final int range_dim;

    /**
     * Create an instance of a function approximator.  The approximator
     * will map from <code>n</code> dimensions to <code>m</code> dimensions.
     *
     * @param      n   dimension of the domain space.
     * @param      m   dimension of the range space.
     */
    public FunctionApproximator( int n, int m ) {
        domain_dim = n;
        range_dim  = m;
    }

    /**
     * Create an instance of a function approximator according to the
     * definition in a given file. The file format depends on the
     * particular implementation.
     *
     * @param      filename   dimension of the domain space.
     * @exception  FunctionApproximatorException  always.
     */
    public FunctionApproximator( String filename )
    throws FunctionApproximatorException
    {
        // NOTE: This is a dummy body. It initializes the variables to some
        // value to avoid a compilation error. The the subclass constructor
        // is responsible for setting these variables.
           
        domain_dim = 0;
        range_dim  = 0;

        throw new FunctionApproximatorException(
                      "FunctionApproximator: not implemented. "+
                      "See documentation." );
    }

    /**
     * Save a definition of this instance in a file.
     *
     * @param      filename   the file name.
     * @exception  IOException                    if an I/O error occurs.
     * @exception  FunctionApproximatorException  if something wrong occurs.
     */
    public abstract void saveDefinition( String filename )
    throws FunctionApproximatorException, IOException;
    
    /**
     * Query the function approximator.
     *
     * @param      q   an n-dimensional array of doubles specifying a point
     *                to evaluate.
     *
     * @return     an m-dimensional array of doubles representing the
     *             value at the query point.
     * @exception  FunctionApproximatorException  if something wrong occurs.
     */
    public abstract double[] query(double[] q)
    throws FunctionApproximatorException;

    /**
     * Add a case to the approximator's learning set.  For some
     * learners, this has no effect.
     *
     * @param      q   an n-dimensional array of doubles specifying a point
     *                 in the domain space.
     * @param      p   an m-dimensional array of doubles specifying the
     *                 associate point in the range space.
     * @exception  FunctionApproximatorException  if something wrong occurs.
     */
    public abstract void update(double[] q, double[] p)
    throws FunctionApproximatorException;
    
}

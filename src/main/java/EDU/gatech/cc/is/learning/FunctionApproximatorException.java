/*
 * FunctionApproximatorException.java
 */

package EDU.gatech.cc.is.learning;

import java.io.Serializable;

/**
 * Signals that an execption of some sort has occured in a FunctionApproximator.
 * <P>
 * Copyright (c)1997 Georgia Tech Research Corporation
 *
 * @author Juan Carlos Santamaria (carlos@cc.gatech.edu)
 * @version $Revision: 1.1 $
 */

public class FunctionApproximatorException extends Exception {
    /**
     * Constructs an <code>FunctionApproximatorException</code> with no
     * detail message. 
     */
    public FunctionApproximatorException() {
        super();
    }

    /**
     * Constructs an <code>FunctionApproximatorException</code> with a
     * specified detail message.
     *
     * @param      s  the detail message.
     */
     public FunctionApproximatorException( String msg ) {
         super(msg);
     }
}

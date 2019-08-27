package EDU.cmu.cs.coral.util;

import java.io.*;

/**
 * This is a generic exception for unexpected circumstances.  It is somewhat
 * similar to RuntimeException, except that it has constructors to take
 * other throwable objects.
 *
 * @author Will Uther <will@cs.cmu.edu>
 */

public class UnexpectedException extends java.lang.RuntimeException {

	static private final boolean defaultPrintMessage = true;
	
	public UnexpectedException() {
		this("");
	}

	// take another exception and rethrow it
	// the message includes the original exception class and message
	public UnexpectedException(java.lang.Throwable t) {
		this(t, defaultPrintMessage);
	}
	
	public UnexpectedException(java.lang.Throwable t, boolean printMessage) {
		this(makeStringFromThrowable(t, null), printMessage);
	}
	
	public UnexpectedException(java.lang.Throwable t, PrintStream msgStream) {
		this(makeStringFromThrowable(t, null), msgStream);
	}
	
	public UnexpectedException(java.lang.Throwable t, String label) {
		this(t, label, defaultPrintMessage);
	}
	
	public UnexpectedException(java.lang.Throwable t, String label, boolean printMessage) {
		this(makeStringFromThrowable(t, label), printMessage);
	}
	
	public UnexpectedException(java.lang.Throwable t, String label, PrintStream msgStream) {
		this(makeStringFromThrowable(t, label), msgStream);
	}
	
	public UnexpectedException(String s) {
		this(s, defaultPrintMessage);
	}
	
	public UnexpectedException(String s, boolean printMessage) {
		this(s, printMessage?System.err:null);
	}
	
	public UnexpectedException(String s, PrintStream msgStream) {
		super(s);
		if (msgStream != null) {
			msgStream.println(getMessage());
			msgStream.flush();
		}
	}

	protected static final String makeStringFromThrowable(java.lang.Throwable t, String label) {
		if (t == null) {
			if (label == null)
				return "Null passed as Exception";
			else
				return label + ": null";
		} else if (t instanceof UnexpectedException) {
			if (label == null)
				return "Rethrowing: " + t.getMessage();
			else
				return "Rethrowing (" + label + "): " + t.getMessage();
		} else {
			java.io.CharArrayWriter myStream = new java.io.CharArrayWriter();
			java.io.PrintWriter pStream = new java.io.PrintWriter(myStream);

			t.printStackTrace(pStream);
			pStream.close();
		
			if (label == null) {
				return "Exception: " + t.getClass().getName() + "\n" +
					"Message: " + t.getMessage() + "\n" +
					"at: " + myStream.toString() + "\n";
			} else {
				return "Exception: " + t.getClass().getName() + "\n" +
					"Label: " + label + 
					"Message: " + t.getMessage() + "\n" +
					"at: " + myStream.toString() + "\n";
			}
		}
	}
	
	public static final boolean isPrintingMessages() {
		return defaultPrintMessage;
	}
}


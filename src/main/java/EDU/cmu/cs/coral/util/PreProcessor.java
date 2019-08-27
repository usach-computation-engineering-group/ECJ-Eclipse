/*
 * PreProcessor.java
 */

package	EDU.cmu.cs.coral.util;

import java.io.*;
import java.util.Hashtable;

/**
 * Pre-process a text file.  Specifically, handle "define" statements.
 * Provides all the functionality of a StreamTokenizer.
 * <P>
 * <A HREF="../COPYRIGHT.html">Copyright</A>
 * (c)1999 Tucker Balch and Carnegie Mellon University
 *
 * @author Tucker Balch
 * @version $Revision: 1.8 $
 */

public class PreProcessor extends Reader
        {
	private StreamTokenizer st;
	private StringBuffer buffer = new StringBuffer("");
	private int pos = 0;
	private int num_chars = 0;
	private Hashtable defines = new Hashtable();

	/**
	 *
	 */
        public PreProcessor(InputStream is)
                {
		st = new StreamTokenizer(new InputStreamReader(is));
		scan();
                }

	/**
	 *
	 */
        public PreProcessor(Reader r)
                {
		st = new StreamTokenizer(r);
		scan();
		}

	/**
	 *
	 */
	private void scan()
		{
		st.wordChars('A','_'); // let _ be a word char
		st.quoteChar('"');     // " is the quote char
		st.eolIsSignificant(true); // pay attention to end of line

		try
			{
			while(st.nextToken() != StreamTokenizer.TT_EOF)
				{
				// it is a "define" statement
				if ((st.ttype == StreamTokenizer.TT_WORD)
					&& ((st.sval).equalsIgnoreCase("define")))
					{
					st.nextToken();
					String first = st.sval;
					st.nextToken();
					String second = new String("foo");
					//need to get either a string or number next
					if (st.ttype == 
					    StreamTokenizer.TT_WORD) {
					    second = new String(st.sval);
					}
					else if (st.ttype == 
						 StreamTokenizer.TT_NUMBER) {
					    second = String.valueOf(st.nval);
					}
					else if (st.ttype != StreamTokenizer.TT_EOL &&
						 st.ttype != StreamTokenizer.TT_EOF) {
					  /* note: there appears to be a bug in the java.io
					     library which assigns st.ttype to something other than
					     it should be when we read in a "-denoted string.
					     so even though it should've been handled in the TT_WORD
					     check, it wasn't, but we know that it's meant to be a string...
					  */
					  second = new String(st.sval);
					}

					//check if the definition is already defined...
					String already = (String) defines.get(second);
					if (already != null) {
					  //we already have a def, so substitute
					  second = already;
					}
					defines.put(first, second);
					System.out.println(
						first + " defined as "
						+ second);
					}
				//look for a dictionary, and put quotes around obj arg
				else if ((st.ttype == StreamTokenizer.TT_WORD) &&
				    (st.sval.equals("dictionary"))) {
				  buffer.append(st.sval);
				  buffer.append(" ");
				  st.nextToken();
				  buffer.append(st.sval);
				  buffer.append(" ");
				  buffer.append("\"");
				  st.nextToken();
				  boolean first = true;
				  while (st.ttype != StreamTokenizer.TT_EOL) {
				    
				    if (first) {
				      first = false;
				    }else {
				      buffer.append(" ");
				    }
				    if (st.ttype == StreamTokenizer.TT_NUMBER)
				      buffer.append(st.nval);
				    else {
				      String deffed = (String) defines.get(st.sval);
				      if (deffed != null) { 
					buffer.append(deffed);
				      } else {
					buffer.append(st.sval);
				      }
				    }
				    
				      
				    st.nextToken();
				  }

				  buffer.append("\"\n");
				}

				// it is a string
				else if (st.ttype == StreamTokenizer.TT_WORD)
					{
					// check if it has been defined
					String value = (String)defines.get(st.sval);
					if (value != null)
						{
						buffer.append(value);
						}
					else
						buffer.append(st.sval);
					buffer.append(" ");
					}
					
				// it is a number
				else if (st.ttype == StreamTokenizer.TT_NUMBER)
					{
					buffer.append(st.nval);
					buffer.append(" ");
					}

				// it is a quoted object
				else if (st.ttype == '"')
					{
					buffer.append('"');
					buffer.append(st.sval);
					buffer.append('"');
					buffer.append(" ");
					}

				// it is a newline
				else if (st.ttype == StreamTokenizer.TT_EOL)
					{
					buffer.append('\n');
					}
				}
			}
		catch(java.io.IOException e)
			{
			}

		num_chars = buffer.length();
		//		System.out.println(buffer);
		System.out.println(num_chars);
		}

	public void close()
		{
		}

	public int read(char[] cbuf, int off, int len)
		{
		if ((pos+len)>num_chars)
			len = num_chars - pos;
		if (len <= 0)
			len = -1;
		if (len > 0)
			{
			try
				{
				buffer.getChars(pos, pos+len, cbuf, off);
				}
			catch(Exception e)
				{
				len = -1;
				}
			}
		pos = pos + len;
		return(len);
		}
	}


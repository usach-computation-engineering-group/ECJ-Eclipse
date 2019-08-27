/*
 * DialogMessageJoke.java
 */

package	EDU.gatech.cc.is.util;

import java.lang.System;
import java.lang.Runtime;
import java.lang.Process;
import java.io.StreamTokenizer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.*;
import java.awt.event.*;

/**
 * A class for displaying a joke message to the user.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
*/

public class DialogMessageJoke extends Dialog implements ActionListener, MouseMotionListener
        {
	private Button ok;
	private int buttonx;
	private TextArea ta;
	private	String message;
	private Frame parent;
	
	/**
	 * Pop up a dialog message box.
	 * @param parent Frame, the parent of the box.
	 * @param title  String, what to put in the title bar.
	 * @param msg    String, what to say.
	 */
        public DialogMessageJoke(Frame par, String title, String msg)
                {
                super(par, title);
		setLayout(null);
		setSize(400,260);
		parent = par;

                // compute position
                Point p = parent.getLocation();
                Dimension d = parent.getSize();
                int desiredx = (d.width-400)/2;
                int desiredy = (d.height-260)/2;
                setLocation(desiredx+p.x, desiredy+p.y);

		ta = new TextArea("",7,60, TextArea.SCROLLBARS_NONE);
		ta.setEditable(false);
		ta.setText(msg);
                this.add(ta);
		ta.setBounds(getInsets().left,
			getInsets().top,
			400-getInsets().right-getInsets().left,
			200-getInsets().top-getInsets().bottom);
                ok = new Button("OK");
		ok.setBounds(170,210,60,30);
		buttonx=200;
                ok.addActionListener(this);
		this.addMouseMotionListener(this);
                this.add(ok);
                this.pack();
                this.show();
                }


        /**
         * Intercept addNotify
         */
        public void addNotify()
                {
                super.addNotify();

                // do these again after the dialog box is really there.
                if (ta != null) ta.setBounds(getInsets().left,
                        getInsets().top,
                        400-getInsets().right-getInsets().left,
                        200-getInsets().top-getInsets().bottom);
		else System.out.println("no ta");
                ok.setBounds(170,210,60,30);

                // compute position
                Point p = parent.getLocation();
                Dimension d = parent.getSize();
                int desiredx = (d.width-400)/2;
                int desiredy = (d.height-260)/2;
                setLocation(desiredx+p.x, desiredy+p.y);
                }


	/**
	 * Handle the OK button push.
	 */
        public void actionPerformed(ActionEvent e)
                {
                dispose();
                }


	/**
	 * Handle drag.
	 */
        public void mouseDragged(MouseEvent e)
                {
		mouseMoved(e);
                }


	/**
	 * Handle move.
	 */
        public void mouseMoved(MouseEvent e)
                {
		int range = 0;
		int mousex = e.getX();
		int mousey = e.getY();
		if ((mousey > 200)&&(mousey < 245))
			{
			range = buttonx - mousex;
			if (range>0)
				{
				if (range<30)
					{
					ok.setLocation(mousex+40,210);
					buttonx=mousex+40-30;
					}
				else if(range<60)
					{
					ok.setLocation(buttonx+4-30,210);
					buttonx=buttonx+4;
					}
				else if (range>90)
					{
					ok.setLocation(buttonx-4-30,210);
					buttonx=buttonx-4;
					}
				//else if (range<30)
					//{
					//ok.setLocation(buttonx+8-30,210);
					//buttonx=buttonx+8;
					//}
				}
			else
				{
				if (range>-30)
					{
					ok.setLocation(mousex-100,210);
					buttonx=mousex-100+30;
					}
				else if(range>-60)
					{
					ok.setLocation(buttonx-4-30,210);
					buttonx=buttonx-4;
					}
				else if (range<-90)
					{
					ok.setLocation(buttonx+4-30,210);
					buttonx=buttonx+4;
					}
				//else if (range>-30)
					//{
					//ok.setLocation(buttonx-8-30,210);
					//buttonx=buttonx-8;
					//}
				}
			}
		else
			{
			buttonx = 200;
			ok.setLocation(buttonx-30,210);
			}
                }
        }


/*
 * DialogMessage.java
 */

package	EDU.gatech.cc.is.util;

import java.lang.System;
import java.awt.*;
import java.awt.event.*;

/**
 * A class for displaying a simple message to the user.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch
 * @version $Revision: 1.2 $
 */

public class DialogMessage extends Dialog implements ActionListener
        {
	private TextArea ta;
	private Button ok;
	private Frame parent;

	/**
	 * Pop up a dialog message box.
	 * @param parent Frame, the parent of the box.
	 * @param title  String, what to put in the title bar.
	 * @param msg    String, what to say.
	 */
        public DialogMessage(Frame par, String title, String msg)
                {
                super(par, title);
		setLayout(null);
		setModal(true);
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
                ok.addActionListener(this);
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
		ta.setBounds(getInsets().left,
			getInsets().top,
			400-getInsets().right-getInsets().left,
			200-getInsets().top-getInsets().bottom);
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
		setModal(false);
                dispose();
                }
        }


/**
 * i_ReinforcementLearner_idTest.java 
 */

package EDU.gatech.cc.is.learning;

import java.io.Serializable;

/**
 * Application to test a reinforcement learner.
 * To test your learner, change the name of the learner
 * instantiated below.
 * <P>
 * Copyright (c)2000 Tucker Balch
 *
 * @author Tucker Balch (tucker@cc.gatech.edu)
 * @version $Revision: 1.1 $
 */

public abstract class i_ReinforcementLearner_idTest
	implements Cloneable, Serializable
	{
        public static void main(String argv[])
        	{
        	int             i, j;
        	i_ReinforcementLearner_id l = new i_SLearner_id(1, 3,
			i_QLearner_id.DISCOUNTED);
		//          put your learner here ^^^^^^^^^^^^^

	
        	// Learn action 1 in state 0;
		i = l.initTrial(0);
        	System.out.println("-----------------");
                System.out.println("Trying to learn action 1 in state 0");
                System.out.println("The initial policy is:\n"+l.toString());
		j = 0;
        	while(i!=1)
                	{
                	i = l.query(0, -1);
			j++;
			if (j>1000) break;
                	}
                System.out.println("The final policy is:\n"+l.toString());
		if (j<1000)
			{
			System.out.println("Done in "+j+" steps.");
			System.out.println("Passed.");
			}
		else
			{
			System.out.println("Failed.");
			System.exit(1);
			}
        	System.out.println("-----------------");
                System.out.println("Trying to learn action 2 in state 0");
                System.out.println("The initial policy is:\n"+l.toString());
		j = 0;
        	while(i!=2)
                	{
                	i = l.query(0, -1);
			j++;
			if (j>1000) break;
                	}
                System.out.println("The final policy is:\n"+l.toString());
		if (j<1000)
			{
			System.out.println("Done in "+j+" steps.");
			System.out.println("Passed.");
			}
		else
			{
			System.out.println("Failed.");
			System.exit(1);
			}
        	System.out.println("-----------------");
        	System.out.println("Now reinforcing action 0 for 1000 steps" );
                System.out.println("The initial policy is:\n"+l.toString());
        	for (j=0;j<1000;j++)
                	{
                	if (i == 0)
                        	i = l.query(0, 1);
                	else
                        	i = l.query(0, -1);
                	//System.out.println(l.toString());
                	}
                if (i == 0)
                       	l.endTrial(1,1);
                else
                       	l.endTrial(-1,-1);
                System.out.println("The final policy is:\n"+l.toString());
        	System.out.println("-----------------");
        	System.out.println("Passed all tests.");
        	}
	}

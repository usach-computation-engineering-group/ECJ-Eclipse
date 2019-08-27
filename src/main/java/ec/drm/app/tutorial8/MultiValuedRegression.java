/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.drm.app.tutorial8;
import ec.util.*;
import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;
import ec.drm.*;

public class MultiValuedRegression extends GPProblem implements SimpleProblemForm
    {
	private static final long serialVersionUID = 1L;
	
    public MyProblemData data;
    public double currentX;
    public double currentY;
    
    public DoubleData input;
    
    public Object clone()
        {
        MultiValuedRegression newobj = (MultiValuedRegression) (super.clone());
        newobj.input = (DoubleData)(input.clone());
        return newobj;
        }

    public void setup(final EvolutionState state,
                      final Parameter base)
        {
        // very important, remember this
        super.setup(state,base);

        // set up our input -- don't want to use the default base, it's unsafe here
        input = (DoubleData) state.parameters.getInstanceForParameterEq(
            base.push(P_DATA), null, DoubleData.class);
        input.setup(state,base.push(P_DATA));
        
    	EvolutionAgent agent = (EvolutionAgent)state;
    	data = (MyProblemData)agent.data;
        }

    public void evaluate(final EvolutionState state, 
                         final Individual ind, 
                         final int threadnum)
        {
        if (!ind.evaluated)  // don't bother reevaluating
            {
            int hits = 0;
            double sum = 0.0;
            double expectedResult;
            double result;
            for (int i=0;i<data.train_in.length;i++)
                {
            	currentX = data.train_in[i][0];
            	currentY = data.train_in[i][1];
                ((GPIndividual)ind).trees[0].child.eval(
                    state,threadnum,input,stack,((GPIndividual)ind),this);

                expectedResult = data.train_out[i];
                result = Math.abs(expectedResult - input.x);
                if (result <= 0.01) hits++;
                sum += result;                  
                }

            // the fitness better be KozaFitness!
            KozaFitness f = ((KozaFitness)ind.fitness);
            f.setStandardizedFitness(state,(float)sum);
            f.hits = hits;
            ind.evaluated = true;
            }
        }
    
    public void describe(final Individual ind, 
    					final EvolutionState state, 
    					final int threadnum, 
    					final int log,
    					final int verbosity)
		{
    	ind.evaluated = false;
        int hits = 0;
        double sum = 0.0;
        double expectedResult;
        double result;
        for (int i=0;i<data.test_in.length;i++)
            {
        	currentX = data.test_in[i][0];
            currentY = data.test_in[i][1];
            ((GPIndividual)ind).trees[0].child.eval(
                state,threadnum,input,stack,((GPIndividual)ind),this);
            expectedResult = data.test_out[i];
            result = Math.abs(expectedResult - input.x);
            if (result <= 0.01) hits++;
            sum += result;                  
            }

        // the fitness better be KozaFitness!
        KozaFitness f = ((KozaFitness)ind.fitness);
        f.setStandardizedFitness(state,(float)sum);
        f.hits = hits;
        ind.evaluated = true;
        }

	@Override
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		// TODO Auto-generated method stub
		
	}
    }


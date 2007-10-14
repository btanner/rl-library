/*******************************************************************************
 *  MountainCarAgent.cpp
 *  
 *  Created by Adam White, created on March 29 2007.
 *  Copyright (c) 2005 UAlberta. All rights reserved.
 *
 *  Description.
 *
 *		A Sarsa Lambda control Agent with Tile coding Function Approximation. 
 *		This agent has been tuned for the Mountain Car task. It is based on 
 *		Sutton's code, found here:
 *
 *      http://www.cs.ualberta.ca/~sutton/MountainCar/MountainCar2.cp
 *
 *      The code follows the psuedo-code for linear, gradient-descent 
 *		Sarsa(lambda) given in Figure 8.8 of the book "Reinforcement Learning: 
 *		An Introduction", by Sutton and Barto. One difference is that we use 
 *		the implementation trick mentioned on page 189 to only keep track of 
 *		the traces that are larger than "min-trace". 
 *					
 ********************************************************************************/

#include "MountainCarAgent.h"
#include "tiles.h"




// RLGLUE INTERFACE FUNCTIONS ----------------------------------------------
void agent_init(Task_specification ts)
{
	task_spec_struct ps;
	parse_task_spec(ts, &ps);

	oldAction.numInts = ps.num_discrete_action_dims;
	oldAction.numDoubles = ps.num_continuous_action_dims;
	oldAction.intArray = (int*)malloc(sizeof(int)*oldAction.numInts);
	memset(oldAction.intArray, 0, sizeof(int)*oldAction.numInts);
	oldAction.doubleArray = (double*)malloc(sizeof(double)*oldAction.numDoubles);
	memset(oldAction.doubleArray, 0, sizeof(double)*oldAction.numDoubles);
	
	newAction.numInts = ps.num_discrete_action_dims;
	newAction.numDoubles = ps.num_continuous_action_dims;
	newAction.intArray = (int*)malloc(sizeof(int)*newAction.numInts);
	memset(newAction.intArray, 0, sizeof(int)*newAction.numInts);
	newAction.doubleArray = (double*)malloc(sizeof(double)*newAction.numDoubles);
	memset(newAction.doubleArray, 0, sizeof(double)*newAction.numDoubles);
	
	for (int i=0; i<MEMORY_SIZE; i++) {
		weights[i]= 0.0;                     // clear weights 
		traces[i] = 0.0;                     // clear all traces
	}

	epsilon = 0.1;                    // probability of random action
	alpha = 0.5;                      // step size parameter
	lambda = 0.95;                    // trace-decay parameters	
}


Action agent_start(Observation o)
{
    DecayTraces(0.0);                                           
    computeActiveFeatures(o);                                                     
    computeActionValues(); 
	
	selectEpsilonGreedyAction(oldAction);
                                     
	return oldAction;
}

Action agent_step(Reward r, Observation o)
{
	updateTraces();
	
	computeActionValues(oldAction);    //compute value of Q[oldObservation][oldAction]
	double delta = r - QSA[getAction(oldAction)];
	
	computeActiveFeatures(o);                                              
	computeActionValues();			  //new action values based on new observation											
	
	selectEpsilonGreedyAction(newAction);

	delta += RLgamma * QSA[getAction(newAction)];
	
    updateWeights(delta);

	copyRLStruct(oldAction,newAction);    

	return newAction;
}

void agent_end(Reward r)
{
	updateTraces();
		
	computeActionValues(oldAction);
	double delta = r - QSA[getAction(oldAction)];
			
	updateWeights(delta);
	
	epsilon *= 0.99;
}

void agent_cleanup()
{
  printf("cleanup\n");
	if(oldAction.intArray!=0){
		free(oldAction.intArray);
		oldAction.intArray=0;
	}
	if(oldAction.doubleArray!=0){
		free(oldAction.doubleArray);
		oldAction.doubleArray=0;
	}
	if(newAction.intArray!=0){
		free(newAction.intArray);
		newAction.intArray=0;
	}
	if(newAction.doubleArray!=0){
		free(newAction.doubleArray);
		newAction.doubleArray=0;
	}
}
 
void agent_freeze()
{
	alpha = 0.0;
} 

Message agent_message(Message){
	return "There are no Messages accepted by SarsaLambdaAgent agent_message currently\n";
}
 
// HELPER FUNCTIONS ----------------------------------------------
 
void selectEpsilonGreedyAction(Action& action)
{
//select an action according to epsilon greedy policy 

	if(drand48() <= epsilon) 
		action.intArray[0] = (int)(drand48()*(NUM_ACTIONS));
	else
		action.intArray[0] = argmax(QSA);
}

void updateTraces()
{
//At the beggining of every episode, decay traces, clear action traces and replace current trace

	DecayTraces(RLgamma*lambda);                              

	for (int a=0; a<NUM_ACTIONS; a++)                        
		if (a != getAction(oldAction))
			for (int j=0; j<NUM_TILINGS; j++) ClearTrace(activeFeatures[a][j]);
        for (int j=0; j<NUM_TILINGS; j++) SetTrace(activeFeatures[getAction(oldAction)][j],1.0); // replace traces
}

void updateWeights(double delta)
{
//Update weights with nonzero traces using td-error

	double temp = (alpha/NUM_TILINGS)*delta;
	for (int i=0; i<num_nonzero_traces; i++)                
	{ 
		int index = nonzero_traces[i];
		weights[index] += temp * traces[index];
	}  
}

void computeActionValues() 
{
// Compute all the action values from current activeFeatures and weights

	for (int a=0; a<NUM_ACTIONS; a++) 
	{
		QSA[a] = 0;
		for (int j=0; j<NUM_TILINGS; j++) 
			QSA[a] += weights[activeFeatures[a][j]];
	}
}
		 
void computeActionValues(Action a) 
{
// Compute a particular action value from current activeFeatures and weights

	QSA[getAction(a)] = 0;
    for (int j=0; j<NUM_TILINGS; j++) 
		QSA[getAction(a)] += weights[activeFeatures[getAction(a)][j]];
}

void computeActiveFeatures(Observation o)
{
// get set of active features for current observation. One for each action

	float inputObservations[NUM_OBSERVATIONS];
    inputObservations[0] = getObservation(o,0)/ POS_WIDTH;
    inputObservations[1] = getObservation(o,1)/ VEL_WIDTH;
	
    for (int a=0; a<NUM_ACTIONS; a++)
        tiles(&activeFeatures[a][0],NUM_TILINGS,MEMORY_SIZE, inputObservations,NUM_OBSERVATIONS,a);
}

int argmax(double QSA[NUM_ACTIONS])
{
// Returns index (action) of largest entry in QSA array, breaking ties randomly

	int best_action = 0;
    double best_value = QSA[0];
    int num_ties = 1;                    // actually the number of ties plus 1
    double value;
	
	for (int a=1; a<NUM_ACTIONS; a++) 
	{
		value = QSA[a];
        if (value >= best_value) 
            if (value > best_value)
			{
				best_value = value;
				best_action = a;
			}
            else 
			{
				num_ties++;
				if (0 == (int)(drand48()*num_ties))
				{
					best_value = value;
					best_action = a;
				}
			}
	}
    return best_action;
}


int getAction(Action action){
	return action.intArray[0];
}
double getObservation(Observation o,int i){
	return o.doubleArray[i];
}

void printValue(){
	int i = 0;
	printf("THE VALUE FUNCTION \n");
	for(i=0; i<NUM_ACTIONS; i++){
		printf("Current Action Value %f \n", QSA[i]);
	}
}

// ------------------------ Suttons Trace Code -----------------------------------------------------
void SetTrace(int f, float new_trace_value)
// Set the trace for feature f to the given value, which must be positive
  { if (traces[f] >= minimum_trace) traces[f] = new_trace_value;         // trace already exists
    else { while (num_nonzero_traces >= MAX_NONZERO_TRACES) IncreaseMinTrace(); // ensure room for new trace
           traces[f] = new_trace_value;
           nonzero_traces[num_nonzero_traces] = f;
           nonzero_traces_inverse[f] = num_nonzero_traces;
           num_nonzero_traces++;}}

void ClearTrace(int f)       
// Clear any trace for feature f
{ 
	if (!(traces[f]==0.0)) 
        ClearExistentTrace(f,nonzero_traces_inverse[f]); 
}

void ClearExistentTrace(int f, int loc)
// Clear the trace for feature f at location loc in the list of nonzero traces
  { traces[f] = 0.0;
    num_nonzero_traces--;
    nonzero_traces[loc] = nonzero_traces[num_nonzero_traces];
    nonzero_traces_inverse[nonzero_traces[loc]] = loc;}

void DecayTraces(float decay_rate)
// Decays all the (nonzero) traces by decay_rate, removing those below minimum_trace
{ 
	for (int loc=num_nonzero_traces-1; loc>=0; loc--)      // necessary to loop downwards
    { 
		int f = nonzero_traces[loc];
        traces[f] *= decay_rate;
        if (traces[f] < minimum_trace) ClearExistentTrace(f,loc);
	}

}

void IncreaseMinTrace()
// Try to make room for more traces by incrementing minimum_trace by 10%, 
// culling any traces that fall below the new minimum
  { minimum_trace += 0.1 * minimum_trace;
    for (int loc=num_nonzero_traces-1; loc>=0; loc--)      // necessary to loop downwards
      { int f = nonzero_traces[loc];
        if (traces[f] < minimum_trace) ClearExistentTrace(f,loc);};}
  
// ------------------------ Suttons Trace Code -----------------------------------------------------




/*
 * Author: Ioannis Partalas, March 13 2009.
 *
 * 
 * Implementation of a Sarsa Lambda agent with tile coding function approximation
 * for the 3D Mountain Car task. It is based on the agent for the 2D Mountain Car
 * task created by Adam White.
 *
 */

#include "sarsa-lambda-tilecoding-cpp.h"


int policy_frozen=0;
int exploring_frozen=0;
int episodes=0;
double lastQValue = 0;


void agent_init(const char* task_spec)
{
    taskspec_t *ts=(taskspec_t*)malloc(sizeof(taskspec_t));
    int decode_result = decode_taskspec( ts, task_spec );
    if(decode_result!=0){
	cout<< "Could not decode task spec, code: "<< decode_result <<" for task spec: " << task_spec<< endl;
	exit(1);
    }

    free_taskspec_struct(ts); 
    free(ts); 

    tmp_obs = new double[4];

    allocateRLStruct(&this_action,1,0,0);
    allocateRLStruct(&last_action,1,0,0);


    last_observation = allocateRLStructPointer(0,4,0);


    for (int i=0; i<MEMORY_SIZE; i++) {
	weights[i]= 0.0;                     // clear weights 
	traces[i] = 0.0;                     // clear all traces
    }

    epsilon = 0.5;
    alpha = 0.2;                      // step size parameter
    lambda = 0.95;                    // trace-decay parameters	
    RLgamma = 1.0;                    // gamma parameter


}


const action_t  *agent_start(const observation_t *this_observation)
{
    DecayTraces(0.0); 

    episodes++;
    tmp_obs[0] = this_observation->doubleArray[0];
    tmp_obs[1] = this_observation->doubleArray[1];
    tmp_obs[2] = this_observation->doubleArray[2];
    tmp_obs[3] = this_observation->doubleArray[3];

    computeActiveFeatures(tmp_obs);                                                     
    computeActionValues(); 


    tmp_action = selectEpsilonGreedyAction();
//    cout<< "Action selected: "<< tmp_action <<"\n";
    this_action.intArray[0] = tmp_action;

    replaceRLStruct(&this_action,&last_action);
    replaceRLStruct(this_observation,last_observation);

    return &this_action;
}


const action_t *agent_step(double reward, const observation_t *this_observation)
{
    updateTraces();
    tmp_obs[0] = this_observation->doubleArray[0];
    tmp_obs[1] = this_observation->doubleArray[1];
    tmp_obs[2] = this_observation->doubleArray[2];
    tmp_obs[3] = this_observation->doubleArray[3];

   // cout<< "Observation: " << tmp_obs[0] << " " << tmp_obs[1] << " "<< tmp_obs[2] << " " << tmp_obs[3]<<"\n";

    double delta = reward - lastQValue;

    computeActiveFeatures(tmp_obs);                                              
    computeActionValues();			  											

    tmp_action = selectEpsilonGreedyAction();
//    cout<< "Action selected: "<< tmp_action <<"\n";

    delta += RLgamma * lastQValue;

    if(!policy_frozen)
    {
	updateWeights(delta);
    }

    this_action.intArray[0] = tmp_action;

    replaceRLStruct(&this_action,&last_action);
    replaceRLStruct(this_observation, last_observation);

    return &this_action;

}

void agent_end(double reward)
{
    updateTraces();

    computeActionValues(tmp_action);
    double delta = reward - QSA[tmp_action];

    if(!policy_frozen)
    {
	updateWeights(delta);
    }

    epsilon *= 0.99;
}



void agent_cleanup()
{
    clearRLStruct(&this_action);
    clearRLStruct(&last_action);
    freeRLStructPointer(last_observation);

}
 
const char* agent_message(const char* inMessage){
    static char buffer[128];

    char* temp;
    temp = (char *)malloc(strlen(inMessage)+1);
    temp = strcpy(temp,inMessage);

    // Freeze learning.

    if(strcmp(inMessage,"freeze learning")==0){
	policy_frozen=1;
	return "message understood, policy frozen";
    }

    // Unfreeze learning

    if(strcmp(inMessage,"unfreeze learning")==0){
	policy_frozen=0;
	return "message understood, policy unfrozen";
    }
    
    // Freeze exploring
    
    if(strcmp(inMessage,"freeze exploring")==0){
	exploring_frozen=1;
	return "message understood, exploring frozen";
    }
    
    // Unfreeze exploring

    if(strcmp(inMessage,"unfreeze exploring")==0){
	exploring_frozen=0;
	return "message understood, exploring unfrozen";
    }

    // Save learned weights

    if(strncmp(inMessage,"save weights",12)==0)
    {
	char *token;
	token = strtok(temp," ");
	token = strtok(temp," ");
	token = strtok(temp," ");

	save_weights(token);
	return "Message understood, saving weights";
    }


    return "SarsaAgent mountain car 3D does not understand your message.";
}
 
// HELPER FUNCTIONS ----------------------------------------------
 
int selectEpsilonGreedyAction()
{
//select an action according to epsilon greedy policy 

    if(!exploring_frozen){
	if( (double)rand()/((double) RAND_MAX + 1) <= epsilon) 
	{

		int actiond =   (int)(((double)rand()/((double) RAND_MAX+1))*NUM_ACTIONS);
		lastQValue = QSA[actiond];
		return actiond;
	}
    }

    double v;

    int ac = argmax(QSA,NUM_ACTIONS,v);
    lastQValue = QSA[ac];
    
    return ac;
}

void updateTraces()
{
//At the beggining of every episode, decay traces, clear action traces and replace current trace

	DecayTraces(RLgamma*lambda);                              

	for (int a=0; a<NUM_ACTIONS; a++)                        
		if (a != tmp_action)
			for (int j=0; j<NUM_TILINGS; j++) ClearTrace(activeFeatures[a][j]);
        for (int j=0; j<NUM_TILINGS; j++) SetTrace(activeFeatures[tmp_action][j],1.0); // replace traces
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
		 
void computeActionValues(int a) 
{
// Compute a particular action value from current activeFeatures and weights

	QSA[a] = 0;
    for (int j=0; j<NUM_TILINGS; j++) 
		QSA[a] += weights[activeFeatures[a][j]];
}


void computeActiveFeatures(double* o)
{
// get set of active features for current observation. One for each action

    float inputObservations[NUM_OBSERVATIONS];
    inputObservations[0] = o[0]/ POS_WIDTH;
    inputObservations[1] = o[1]/ POS_WIDTH;
    inputObservations[2] = o[2]/ VEL_WIDTH;
    inputObservations[3] = o[3]/ VEL_WIDTH;
	
    for (int a=0; a<NUM_ACTIONS; a++)
        tiles(&activeFeatures[a][0],NUM_TILINGS,MEMORY_SIZE, inputObservations,NUM_OBSERVATIONS,a);
}


int argmax(double QSA[],int size,double &bestvalue)
{
// Returns index (action) of largest entry in QSA array, breaking ties randomly

    int best_action = 0;
    
    double best_value = QSA[0];


   
    int num_ties = 1;                    // actually the number of ties plus 1
    double value;
	
	for (int a=1; a<size; a++) 
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
		    if (0 == (int)(rand()*num_ties))
		    {
			best_value = value;
			best_action = a;
		    }
		}
	}
    return best_action;
}

/*
 * This function saves the weights in a file.
 * Parameters: a filename.
 */
void save_weights(char *filename)
{
    ofstream f;


    f.open(filename);
    for(int i=0;i<MEMORY_SIZE;i++)
	f << weights[i] << "\n";
    f.close();
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




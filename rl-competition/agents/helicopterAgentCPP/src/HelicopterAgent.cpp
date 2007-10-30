////////////////////////////////////////////////////////////////////////////////
// Authors: Pieter Abbeel, Adam Coates, Andrew Y. Ng --- Stanford University ///
////////////////////////////////////////////////////////////////////////////////

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "Glue_utilities.h"

#include "HelicopterAgent.h"

struct agent_data_t
{
	Action action;
} agent_data;

void print_observation(Observation o)
{
	printf("Observation = [");
	for (int i = 0; i < 12; i++)
	{
		printf("%f,",o.doubleArray[i]);
	}
	printf("]\n");
}

void agent_init(const Task_specification task_spec)
{  
	task_spec_struct tss;					/*declare task_spec_struct*/
	parse_task_spec(task_spec, &tss);		/*Parsing task_specification*/	

	/*allocating memory for one Action*/
	agent_data.action.numInts     =  tss.num_discrete_action_dims;
	agent_data.action.intArray    = (int*)malloc(sizeof(int)*agent_data.action.numInts);
	agent_data.action.numDoubles  = tss.num_continuous_action_dims;
	agent_data.action.doubleArray = (double*)malloc(sizeof(double)*agent_data.action.numDoubles);

	if(agent_data.action.numInts != 0 || agent_data.action.numDoubles != 4){
		printf("Weak baseline controller: unexpected action structure\n");
		printf("Expected: numInts = 0, numDoubles = 4; received: numInts = %d, numDoubles = %d\n", agent_data.action.numInts, agent_data.action.numDoubles);
		printf("Exiting ... \n");
		exit(0);
	}
}



Action agent_start(Observation o)
{	
	agent_policy(o, agent_data.action);
	return (agent_data.action);
}


Action agent_step(Reward r, Observation o)
{
	agent_policy(o, agent_data.action);
	return (agent_data.action);	
}


void agent_end(Reward r)
{ 
	// nothing to be done
}



void agent_cleanup(){
	/*free all the memory*/
	free(agent_data.action.doubleArray);

	/*clear all values in the actions*/
	agent_data.action.numInts     = 0;
	agent_data.action.numDoubles  = 0;
	agent_data.action.intArray    = 0;
	agent_data.action.doubleArray = 0;

}

Message agent_message(const Message message){
	/*no messages currently implemented*/
	return "Weak baseline controller does not respond to any messages.";
}

void agent_freeze(){
	// this agent is already frozen from the start, really [it's a fixed policy agent]
}






void agent_policy(const Observation o, Action& action)
{
		const double weights[12] = {0.0196, 0.7475, 0.0367, 0.0185, 0.7904, 0.0322, 0.1969, 0.0513, 0.1348, 0.02,
			0, 0.23};
		const int y_w = 0;
		const int roll_w = 1;
		const int v_w = 2;
		const int x_w = 3;
		const int pitch_w = 4;
		const int u_w = 5;
		const int yaw_w = 6;
		const int z_w = 7;
		const int w_w = 8;
		const int ail_trim = 9;
		const int el_trim = 10;
		const int coll_trim = 11;

		//x/y/z_error = body(x - x_target)
		//q_error = inverse(Q_target) * Q, where Q is the orientation of the helicopter
		//roll/pitch/yaw_error = scaled_axis(q_error)

		// collective control
		double coll = weights[z_w] * o.doubleArray[z_err] +
			weights[w_w] * o.doubleArray[w_err] +
			weights[coll_trim];

		// forward-backward control
		double elevator =  -weights[x_w] * o.doubleArray[x_err] +
			-weights[u_w] * o.doubleArray[u_err] +
			weights[pitch_w] * o.doubleArray[qy_err] +
			weights[el_trim];


		// left-right control
		double aileron =
			-weights[y_w] * o.doubleArray[y_err] +
			-weights[v_w] * o.doubleArray[v_err] +
			-weights[roll_w] * o.doubleArray[qx_err] +
			weights[ail_trim];

		double rudder =
			-weights[yaw_w] * o.doubleArray[qz_err];


		action.doubleArray[0] = aileron;
		action.doubleArray[1] = elevator;
		action.doubleArray[2] = rudder;
		action.doubleArray[3] = coll;
}




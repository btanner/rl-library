/*
 *  RandomAgent.cpp
 *  
 *
 *  Created by Leah Hackman on 06/07/07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

#include "RandomAgent.h"

void agent_init(Task_specification task_spec)
{
		parse_task_spec(task_spec, &ps);
		action.numInts = ps.num_discrete_action_dims;
		action.numDoubles = ps.num_continuous_action_dims;
		action.intArray = new int [action.numInts];
		action.doubleArray = new double [action.numDoubles];
}

Action agent_start(Observation o)
{
	randomify(action);
	return action;
}

Action agent_step(Reward r, Observation o){
	randomify(action);
	return action;
}

void agent_end(Reward r)
{
}

void agent_cleanup()
{
	delete [] action.intArray;
	delete [] action.doubleArray;
}

void agent_freeze()
{
}

Message agent_message(Message)
{
	return "Not implemented";
}

void randomify(Action& action){
	double dmin, dmax;
	for(int i =0; i < ps.action_dim; i++){
	dmin= ps.action_mins[i];
	dmax = ps.action_maxs[i];
	if(ps.action_types[i] == 'i')
	action.intArray[i] = (int)(drand48()*(dmax-dmin + 1) + dmin);
	else
	action.doubleArray[i] = (drand48()*(dmax-dmin + 1)+dmin);
	}
}
	
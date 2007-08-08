/*
 *  RandomAgent.h
 *  
 *
 *  Created by Leah Hackman on 06/07/07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

#ifndef RandomAgent_h   
#define RandomAgent_h

#include "RL_common.h"
#include "Glue_utilities.h"

Action action;
task_spec_struct ps;

void agent_init(Task_specification task_spec);
Action agent_start(Observation o);
Action agent_step(Reward r, Observation o);
void agent_end(Reward r);
void agent_cleanup();
void agent_freeze();
Message agent_message(Message);

void randomify(Action& action);
void ask(Observation o, Action& action);

#endif
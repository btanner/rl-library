#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <assert.h>
#include "Glue_utilities.h"
#include "RandomAgent.h"

#define NUM_STATES 108
#define NUM_ACTIONS 3

Action action;
Action previous_action;
Observation previous_observation;
double value[NUM_STATES][NUM_ACTIONS];
double sarsa_alpha = 0.1;
double sarsa_gamma = 0.9;
int freeze = 0;

void agent_init(const Task_specification task_spec)
{  
  task_spec_struct tss;					/*declare task_spec_struct*/
  srand(0);/*seed the randomness*/
  printf("agent_init(): %s\n", task_spec);
  
  assert (task_spec != 0);
  parse_task_spec(task_spec, &tss);		/*Parsing task_specification*/	

/*allocating memory for one Action*/
  action.numInts     =  tss.num_discrete_action_dims;
  action.intArray    = (int*)malloc(sizeof(int)*action.numInts);
  action.numDoubles  = 0;
  action.doubleArray = 0;

/*allocating memory for one Action*/
  previous_action.numInts     = tss.num_discrete_action_dims;
  previous_action.intArray    = (int*)malloc(sizeof(int)*previous_action.numInts);
  previous_action.numDoubles  = tss.num_continuous_action_dims;
  previous_action.doubleArray = 0;
  
  free(tss.obs_types);
  free(tss.obs_mins);
  free(tss.obs_maxs);

  free(tss.action_types);
  free(tss.action_mins);
  free(tss.action_maxs);

  /*reset the value function*/
  memset(value, 0, sizeof(double)*NUM_STATES*NUM_ACTIONS);
  printf("agent_init() done\n");
}

Action agent_start(Observation o)
{	
  printf("agent_start()\n");
  action.intArray[0] = egreedy(o);

  return action;	
}


Action agent_step(Reward r, Observation o)
{
  printf("agent_step()\n");
  action.intArray[0] = egreedy(o);
  return action;
}

void agent_end(Reward r)
{ 
  printf("agent_end()\n");
}

void agent_cleanup(){
/*free all the memory*/
  free(action.intArray);
  free(previous_action.intArray);
  free(previous_observation.intArray);

/*clear all values in the actions*/
  action.numInts     = 0;
  action.numDoubles  = 0;
  action.intArray    = 0;
  action.doubleArray = 0;

  previous_action.numInts     = 0;
  previous_action.numDoubles  = 0;
  previous_action.intArray    = 0;
  previous_action.doubleArray = 0;
}

Message agent_message(const Message message){
  /*no messages currently implemented*/
  return "This agent does not respond to any messages.";
}

void agent_freeze(){
  /*sets the agent to freeze mode*/
  freeze = 1;
}

int egreedy(Observation o){
  return rand()%3;
}

/////////////////////////////
// This file is a wrapper
// that exists to call
// an agent compatabile
// with the RL-Glue framework
//
// *met 8/07
/////////////////////////////

#include <string.h>  // for memcpy
#include <stdlib.h>  // for rand
#include "LearningAgent.h" //*met 8/07

/* Constructor:
   numFeatures and numActions are required.  Additional parameter
   can be added to the constructor.  For example:
*/
LearningAgent::LearningAgent( int numFeatures, int numActions,
			      bool learning,
			      char *loadWeightsFile,
			      char *saveWeightsFile ):
  SMDPAgent( numFeatures, numActions )
{
  /* Contruct learner here */
  /* For example: */
  m_learning = learning;

  current_observation.numInts =0;
  current_observation.numDoubles = 13;  //Keepaway has 13 state variables
  current_observation.intArray =(int*)malloc(sizeof(int)*current_observation.numInts);
  memset(current_observation.intArray, 0, sizeof(int)*current_observation.numInts);
  current_observation.doubleArray = (double*)malloc(sizeof(double)*current_observation.numDoubles);
  memset(current_observation.doubleArray, 0, sizeof(double)*current_observation.numDoubles);

  printf("Try to init RL-Glue agent\n");
  agentServer.startServer();
  agentServer.acceptConnection();

  char* env_task_spec = new char[1000];

  double ranges[13];
  ranges[0]=25.0;
  ranges[1]=50.0;
  ranges[2]=50.0;
  ranges[3]=50.0;
  ranges[4]=50.0;
  ranges[5]=25.0;
  ranges[6]=25.0;
  ranges[7]=25.0;
  ranges[8]=25.0;
  ranges[9]=50.0;
  ranges[10]=50.0;
  ranges[11]=180;
  ranges[12]=180;

  sprintf(env_task_spec,"1:e:13_[f,f,f,f,f,f,f,f,f,f,f,f,f]");

  for (int i=0; i<13; i++)
    sprintf(env_task_spec,"%s_[0.0000,%f]", env_task_spec, ranges[i]);
  sprintf(env_task_spec,"%s:1_[i]_[0,2]", env_task_spec);

  //initialize the agent
  Task_specification theTaskSpec = env_task_spec;
  agent_init(theTaskSpec);

  printf("RL-Glue agent initialized\n");
}

/* Start Episode
   Use the current state to choose an action [0..numKeepers-1]
*/
int LearningAgent::startEpisode( double state[] )
{
  printf("startEpisode() in LearningAgent.cc\n");
  /* Choose first action */
  for (int i=0; i < (int)current_observation.numDoubles; i++){
    current_observation.doubleArray[i] = state[i];
  }
  m_lastAction = agent_start(current_observation).intArray[0];
  memcpy( m_lastState, state, getNumFeatures() * sizeof( double ) );
  return m_lastAction;
}

/* Step
   Update based on previous step and choose next action
*/
int LearningAgent::step( double reward, double state[] )
{
  //ro.r = reward;
  for (int i=0; i<(int)current_observation.numDoubles; i++){
    current_observation.doubleArray[i] = state[i];
  }
  if ( m_learning )
    m_lastAction = agent_step(reward, current_observation).intArray[0];
  memcpy( m_lastState, state, getNumFeatures() * sizeof( double ) );
  return m_lastAction;  
}

void LearningAgent::endEpisode( double reward )
{
  if ( m_learning ){
    //ro.r = reward;
    agent_end(reward);
  }
}

#ifndef LEARNING_AGENT
#define LEARNING_AGENT

#include "SMDPAgent.h"
#include "RL_glue.h" //*met 8/07
#include "AgentServer.h"
#include <stdio.h>


class LearningAgent:public SMDPAgent
{
  /* Add private methods and variables here */
  /* For example: */
  bool   m_learning, m_saving;
  char   m_saveWeightsFile[128];
  int    m_lastAction;
  double m_lastState[MAX_STATE_VARS];
  Observation current_observation;
  Reward_observation ro;

  AgentServer agentServer;

  void loadWeights ( char  *filename );
  void saveWeights ( char  *filename );
  void update      ( double state[], int action, double reward );
  int  selectAction( double state[] );

 public:
  LearningAgent( int   numFeatures,
		 int   numActions,
		 bool  learning,
		 char *loadWeightsFile,
		 char *saveWeightsFile );

  int  startEpisode( double state[] );
  int  step( double reward, double state[] );
  void endEpisode( double reward );
  void setParams(int iCutoffEpisodes, int iStopLearningEpisodes){exit(1);}
} ;

#endif

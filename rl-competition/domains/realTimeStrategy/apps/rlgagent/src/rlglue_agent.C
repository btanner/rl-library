
#include "Global.H"
#include "Helpers.H"
#include "MiniGameState.H"
#include "rlglue_agent.H"
#include "Profiler.H"

#include <stdlib.h>
#include <time.h>

#include <iostream>
#include <vector>

using namespace std;
using namespace boost;

static bool debug = false;

/*  Agent's variables  */
static MiniGameParameters * parms = NULL;
static MiniGameState * state = NULL;
static string statestr;
static string tsstr;
static string mpstr;

static Action myAction;

static Profiler profiler; 

/*
 * Initializes the agent. This function is called only once per experiment. 
 */
void agent_init(const Task_specification task_spec)
{
  srand(time(NULL));
  DPR << "RLG> Calling agent_init" << endl;
  
  myAction.numInts = 0; 
  myAction.numDoubles = 0;
  myAction.intArray = NULL;
  myAction.doubleArray = NULL;
  
  parms = new MiniGameParameters;
    
  string tsstr;
  tsstr.append(task_spec); // convert to a string 

  DPR << "Received task spec: " << tsstr << endl;
  
  DPR << "Deserializing parameters ..." << endl;
  parms->deserialize(tsstr);
  
  //profiler.setFilename("rlgagent_profiler.log");
}

/*
 * Chooses an action for the first time step of an epsisode. 
 */
Action agent_start(Observation o)
{  
  DPR << "RLG> Calling agent_start" << endl;
  DPR << "Received state: " << intArray2string(o.intArray, o.numInts) << endl;
  
  if (state != NULL) delete state;
  state = new MiniGameState;

  // Fill the game state from the RL-Glue data
  rlg_obs2view(*state, o);
  
  // Build up a set of actions
  // eg. 1 move 10 20 5#2 move 20 10 2 # ... 
  vector<int> actions;
  get_actions(actions, *state, *parms); 

  // convert the actions to RL-Glue format
  rlg_vector2action(myAction, actions);
  
  // Return the action data
  return myAction;
}

/*
 * Chooses an action given the observation and reward obtained
 * from the previously chosen action. 
 */
Action agent_step(Reward r, Observation o)
{
  DPR << "RLG> Starting agent_step" << endl;
  DPR << "Received state: " << intArray2string(o.intArray, o.numInts) << endl;

  // First, convert the RL-Glue observation to our game state
  rlg_obs2view(*state, o);

  // Build up a set of actions
  // eg. 1 move 10 20 5#2 move 20 10 2 # ... 
  
  vector<int> actions;
  get_actions(actions, *state, *parms); 

  // Convert the set of actions to RL-Glue format
  rlg_vector2action(myAction, actions);
  
  // Return the action data
  return myAction;
}

/* 
 * Called on the last (terminal) step to indicate the terminal 
 * reward that received from the last chosen action.  
 */  
void agent_end(Reward r)
{
  DPR << "RLG> Starting agent_end" << endl;
}

void agent_cleanup()
{
  DPR << "RLG> Starting agent_step" << endl;  
  
  delete state;
  delete parms;
  
  if (myAction.intArray != NULL)
    free(myAction.intArray);
  
  if (myAction.doubleArray != NULL)
    free(myAction.doubleArray);
}

Message agent_message(const Message msg)
{
  DPR << "RLG> Starting agent_message" << endl;  
  return "no messages";
}

void agent_freeze(){
  DPR << "RLG> Starting agent_freeze" << endl;

  // unimplemented
}


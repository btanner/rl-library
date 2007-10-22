
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

void agent_init(const Task_specification task_spec)
{
  srand(time(NULL));
  DPR << "RLG> Calling agent_init" << endl;
  
  myAction.numInts = 0; 
  myAction.numDoubles = 0;
  myAction.intArray = NULL;
  myAction.doubleArray = NULL;
  
  parms = new MiniGameParameters;
  
  string ts;
  ts.append(task_spec);
  string::size_type loc = ts.find("mps=", 0 );
  string tsstr = ts.substr(0, loc-1);
  mpstr = ts.substr(loc+4);
  
  tsstr.append(task_spec); // convert to a string 

  DPR << "Received task spec: " << tsstr << endl;
  
  DPR << "Deserializing parameters ..." << endl;
  parms->deserialize(tsstr);
  
  //profiler.setFilename("rlgagent_profiler.log");
}

Action agent_start(Observation o)
{  
  //profiler.start();
  
  DPR << "RLG> Calling agent_start" << endl;
  DPR << "Received state: " << intArray2string(o.intArray, o.numInts) << endl;
  
  if (state != NULL) delete state;
  state = new MiniGameState;
  state->setMPstr(mpstr); 
  
  //string statestr = build_state_string(o);     
  //DPR << "State string is " << statestr << endl; 
  
  // now deserialize it. remember, we are always player 1
  //state.clear_obj(); 
  //state->decode_view(1, statestr);
  rlg_obs2view(*state, o);
  
  // Build up a set of actions
  // eg. 1 move 10 20 5#2 move 20 10 2 # ... 
  vector<int> actions;
  get_actions(actions, *state, *parms); 
  //string actionstr = join(actions, "#"); 
  //DPR << "action string is " << actionstr << endl; 
  //rlg_convert_actionstr(myAction, actionstr);
  rlg_vector2action(myAction, actions);
  
  return myAction;
}

Action agent_step(Reward r, Observation o)
{
  //profiler.stamp("agent_step 0");
  
  DPR << "RLG> Starting agent_step" << endl;
  DPR << "Received state: " << intArray2string(o.intArray, o.numInts) << endl;

  //if (state != NULL) delete state;
  //state = new MiniGameState;
  //state->setMPstr(mpstr);
  
  // What to do with the reward .... hmmm... ?
  
  //string statestr = build_state_string(o);   
  //DPR << "State string is " << statestr << endl; 
  
  // now deserialize it. remember, we are always player 1
  //state.clear_obj(); 
  //state->decode_view(1, statestr);
  rlg_obs2view(*state, o);

  //profiler.stamp("agent_step 1");
  
  // Build up a set of actions
  // eg. 1 move 10 20 5#2 move 20 10 2 # ... 
  
  vector<int> actions;
  get_actions(actions, *state, *parms); 

  //profiler.stamp("agent_step 2");
  
  //string actionstr = join(actions, "#"); 
  //DPR << "action string is " << actionstr << endl; 
  //rlg_convert_actionstr(myAction, actionstr);  
  rlg_vector2action(myAction, actions);
  
  //profiler.stamp("agent_step 3");

  return myAction;
}

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
  
  /*sets the agent to freeze mode*/
  //freeze = 1;
}


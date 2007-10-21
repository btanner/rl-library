
#include "Helpers.H"
#include "MiniGameState.H"
#include "Player.H"
#include "AggressivePlayer.H"
#include "rlglue_env.H"
#include "SDL_GUI.H"
#include "SDL_init.H"

#include <stdlib.h>
#include <time.h>

#include <iostream>
#include <map>
#include <string>

#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/replace.hpp>

using namespace std;

static bool debug = false; 

static MiniGameState * statePtr;
static MiniGameParameters * parms;
static Player * opponent; 
boost::array<std::string, MiniGameState::PLAYER_NUM> views;
static SDL_GUI<MiniGameState> gui;
static bool use_gui = true; 
static std::map<std::string, SDL_GUI<MiniGameState>::Marker> markers;

static int time_step;
static bool inited = false; 
static string task_spec;
static char * task_spec_cstr = NULL; 
static Observation obs; 
static Reward_observation rewobs;

void init_gui(MiniGameState & state) 
{
  if (use_gui)
  {
    SDL_init::video_init();
    markers["worker"] = SDL_GUI<MiniGameState>::MARKER_H; // mark workers
    gui.init(parms->width, parms->height, state, markers);
    gui.display();
  }
}

static void init() 
{
  srand(time(NULL)); 
  
  time_step = 1;
  
  obs.intArray = NULL;
  obs.doubleArray = NULL; 
  rewobs.o.intArray = NULL; 
  rewobs.o.doubleArray = NULL; 

  statePtr = new MiniGameState; 
  //opponent = new RandomPlayer(0);
  //opponent = new TestPlayer(0);
  opponent = new AggressivePlayer(0);
  parms = new MiniGameParameters;
  
  statePtr->init(*parms);
  init_gui(*statePtr);
  
  ostringstream os; 
  parms->serialize(os); 
  task_spec = os.str(); 
  
  // append mineral patch coords
  ostringstream os2;
  os2 << ",mps=";
  FORALL(statePtr->all_objs, iter)
  {
    GameObj<MiniGameState> * objPtr = (*iter);
    if (objPtr->get_type() == "mineral_patch")    
      os2 << objPtr->x << "-" << objPtr->y << "-";     
  }
  
  string mps = os2.str();
  mps = mps.substr(0, mps.length()-1);
  
  task_spec = task_spec + mps;  

  int len = task_spec.length()+1;
  
  task_spec_cstr = (char *)malloc(len*sizeof(char));
  memset(task_spec_cstr, 0, len); 
  strcpy(task_spec_cstr, task_spec.c_str());  
  
  inited = true;
}

/* RL-Glue Interface */

/** 
 * Creates the necessary data structures. 
 * Returns a description of the environment at the start of the episode. 
 */
Task_specification env_init()
{    
  DPR << "RLG> Starting env_init ..." << endl;
  
  if (!inited)
    init(); 
  
  return (Task_specification)task_spec_cstr; 
}

/**
 * Send the first observation to the client. 
 */

Observation env_start()
{
  DPR << endl << "### Starting time step " << time_step << endl; 
  DPR << "RLG> Starting env_start ..." << endl;
  
  FORS (i, MiniGameState::PLAYER_NUM) {
    ostringstream os;
    statePtr->encode_view(i, os);
    views[i] = os.str();
  }

  // The RL agent will always be player 1. The opponent is player 0.
  
  DPR << "Encoded view (P0) = " << views[0] << endl; 
  DPR << "Encoded view (P1) = " << views[1] << endl; 
  
  // convert the string to a char array
  //rlg_convert_view(obs, views[1]);
  MiniGameState* viewStatePtr = new MiniGameState;
  viewStatePtr->decode_view(1, views[1]);
  rlg_view2obs(obs, *viewStatePtr);
  delete viewStatePtr;
  
  return obs;
}

Reward_observation env_step(Action a)
{
  time_step++;
  DPR << endl << "### Starting time step " << time_step << endl;   
  DPR << "RLG> Starting env_step" << endl; 
  
  boost::array<std::string, MiniGameState::PLAYER_NUM> actions;

  // get the opponent's actions
  actions[0] = opponent->receive_actions(views[0], *parms);

  // convert the RL to ortslite actions
  //actions[1] = rlg_convert_actions(a);
  actions[1] = rlg_action2str(a); 
  
  DPR << "opp actions = " << actions[0] << endl; 
  DPR << "rlagent actions = " << actions[1] << endl; 
  
  statePtr->simulation_step(actions, views);
  
  if (use_gui)
  {
    gui.event();
    gui.display();
    gui.delay(125);
  }
  
  if (statePtr->finished()) {
    // Game is done! 
    // determine winner & reward
    rewobs.terminal = 1; 
  }
  
  // The RL agent will always be player 1. The opponent is player 0.  
  //string statestr = statePtr->srv_encode_view(1);
  string statestr = views[1];
  DPR << "statestr is "; 
  if (debug) prettyPrintView(views[1]);

  // convert the string to a char array
  //rlg_convert_view(rewobs.o, statestr);
  MiniGameState* viewStatePtr = new MiniGameState;
  viewStatePtr->decode_view(1, views[1]);
  rlg_view2obs(rewobs.o, *viewStatePtr);
  delete viewStatePtr;

  return rewobs;
}

void env_cleanup()
{
  DPR << "RLG> Starting env_cleanup" << endl; 
  
  delete statePtr; 
  delete opponent;
  delete parms;

  if (task_spec_cstr != NULL)
    free(task_spec_cstr);
  
  if (obs.intArray != NULL) 
    free(obs.intArray); 

  if (obs.doubleArray != NULL) 
    free(obs.doubleArray); 
  
  if (rewobs.o.intArray != NULL) 
    free(rewobs.o.intArray); 

  if (rewobs.o.doubleArray != NULL) 
    free(rewobs.o.doubleArray); 
}

void env_set_state(State_key sk)
{
  /* Unimplemented */
  memset(&sk, 0, sizeof(sk));
}
     
void env_set_random_seed(Random_seed_key rsk)
{
  /* Unimplemented */
  memset(&rsk, 0, sizeof(rsk));
}

State_key env_get_state()
{
  State_key theKey;
  return theKey;
}

Random_seed_key env_get_random_seed()
{
  Random_seed_key theKey;
  return theKey;
}

Message env_message(const Message inMessage) {
  DPR << "received message " << inMessage << endl;
  
  if (strcmp(inMessage, "TO=3 FROM=0 CMD=4 VALTYPE=3 VALS=NULL") == 0)
  {
    char * resp = "TO=0 FROM=3 CMD=0 VALTYPE=0 VALS=1_1"; 
    DPR << "responding: " << resp << endl; 
    return resp; 
  }
  else if (strcmp(inMessage, "TO=3 FROM=0 CMD=6 VALTYPE=3 VALS=NULL") == 0)
  {
    char * resp = "TO=0 FROM=3 CMD=0 VALTYPE=0 VALS=visualizers.RealTimeStrategyVisualizer.RealTimeStrategyVisualizer";
    DPR << "responding: " << resp << endl;
    return resp;
  }
  else if (strcmp(inMessage, "TO=3 FROM=0 CMD=3 VALTYPE=1 VALS=GetRTSSpec") == 0)
  {
    if (!inited)
      init(); 
    
    // construct it all
    string tmpts = task_spec;
    boost::replace_all(tmpts, "=", "$");
    
    // task_spec_cstr
    //boost::replace_all(tmpts, " ", "_");
    string resp = "TO=0 FROM=3 CMD=0 VALTYPE=1 VALS=";    
    resp.append(tmpts);
    
    cout << "responding: " << resp << endl; 
    
    return (char*)resp.c_str();
  }
  
  return "message not handled. "; 
}



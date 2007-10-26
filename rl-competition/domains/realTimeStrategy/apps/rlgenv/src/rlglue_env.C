
#include "Helpers.H"
#include "MiniGameState.H"
#include "Player.H"
#include "RLComp08Bot1.H"
#include "rlglue_env.H"
#include "SDL_GUI.H"
#include "SDL_init.H"
#include "Profiler.H"

#include <stdlib.h>
#include <time.h>

#include <iostream>
#include <map>
#include <string>

#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/replace.hpp>

#define MAX_STEPS 10000

using namespace std;

// Set to true for DPR messages to print. 
static bool debug = false;

// GUI vars. To enable SDL GUI, set use_gui to true 
// *and* ENABLE_GUI=1 in Makefile
static SDL_GUI<MiniGameState> gui;
static bool use_gui = false;
static int gui_delay = 25;
static std::map<std::string, SDL_GUI<MiniGameState>::Marker> markers;

// State variables
static MiniGameState * statePtr;
static MiniGameParameters * parms;
static Player * opponent; 
boost::array<std::string, MiniGameState::PLAYER_NUM> views;
static int time_step;
static bool inited = false; 
static string task_spec;
static char * task_spec_cstr = NULL; 
static char * msg_response = NULL; 
static Observation obs; 
static Reward_observation rewobs;

// Only sometimes used for testing timing
static Profiler profiler;  

void timing_start()
{
  system("rm /tmp/rlgenv.log");
  system("echo \"Started at\" >> /tmp/rlgenv.log");
  system("date >> /tmp/rlgenv.log"); 
}

void timing_end()
{
  system("echo \"Ended at\" >> /tmp/rlgenv.log");
  system("date >> /tmp/rlgenv.log"); 
}

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
  rewobs.terminal = 0;
  rewobs.r = 0;

  statePtr = new MiniGameState; 
  opponent = new RLComp08Bot1(0); 
  parms = new MiniGameParameters;
  
  statePtr->init(*parms);
  init_gui(*statePtr);
  
  ostringstream os; 
  parms->serialize(os); 
  task_spec = os.str(); 
  
  int len = task_spec.length()+1;
  
  task_spec_cstr = (char *)malloc(len*sizeof(char));
  memset(task_spec_cstr, 0, len); 
  strcpy(task_spec_cstr, task_spec.c_str());  
  
  inited = true;
}

void uninit()
{
  if (statePtr != NULL)
  { delete statePtr; statePtr = NULL; }
  
  if (opponent != NULL)
  { delete opponent; opponent = NULL; }
  
  if (parms != NULL)
  { delete parms; parms = NULL; }

  if (task_spec_cstr != NULL)
  { free(task_spec_cstr); task_spec_cstr = NULL; }
  
  if (obs.intArray != NULL) 
  { free(obs.intArray); obs.intArray = NULL; } 

  if (obs.doubleArray != NULL) 
  { free(obs.doubleArray); obs.doubleArray = NULL; } 
  
  inited = false;   
}

/* RL-Glue Interface functions */

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
  
  if (!inited)
    init();   
  
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

/* 
 * Send observation after execution action. 
 */ 
Reward_observation env_step(Action a)
{
  time_step++;
  
  DPR << endl << "### Starting time step " << time_step << endl;   
  DPR << "RLG> Starting env_step" << endl;
  
  boost::array<std::string, MiniGameState::PLAYER_NUM> actions;

  // get the opponent's (bot's) actions
  opponent->set_state(statePtr);
  opponent->set_parms(parms); 
  actions[0] = opponent->receive_actions(views[0]);

  // convert the actions to RL-Glue format
  actions[1] = rlg_action2str(a); 

  DPR << "opp actions = " << actions[0] << endl; 
  DPR << "rlagent actions = " << actions[1] << endl; 

  // simulate a time step. all game mechanics happen in this call
  // views is populated with the strings that correspond to the view of the players
  statePtr->simulation_step(actions, views);

  // if using the gui, update the display now
  if (use_gui)
  {
    gui.event();
    gui.display();
    gui.delay(gui_delay);
  }

  // check if the game is ended.   
  int gameVal = statePtr->check_win();
  if (gameVal >= 0 || time_step >= MAX_STEPS) {
    // Game is done! 
    // determine winner & reward
    if (gameVal == 0)
      rewobs.r = 0;
    else if (gameVal == 1)
      rewobs.r = 100 - (int)(15*((double)time_step) / ((double)MAX_STEPS)); 
    else if (gameVal == 2 || time_step >= MAX_STEPS)
    {
      int sc0 = statePtr->get_score(0);
      int sc1 = statePtr->get_score(1);
      
      if (sc1 == sc0)
        rewobs.r = 50;
      else if (sc1 > sc0)
        rewobs.r = 55;
      else
        rewobs.r = 45;
    }
    
    rewobs.terminal = 1; 
    
    uninit();

    return rewobs;
  }
  
  // The RL agent will always be player 1. The opponent is player 0.  
  string statestr = views[1]; 
  DPR << "statestr is "; 
  if (debug) prettyPrintView(views[1]);

  // convert the the RL agent's view to RL-Glue format  
  int total = statePtr->encode_view_rlg(1, rewobs.o.intArray, RLG_OBJ_ATTRS);
  rewobs.o.numInts = total;

  // send back the observation
  return rewobs;
}

void env_cleanup()
{
  DPR << "RLG> Starting env_cleanup" << endl; 
  
  uninit();
  
  if (rewobs.o.intArray != NULL) 
  { free(rewobs.o.intArray); rewobs.o.intArray = NULL; } 

  if (rewobs.o.doubleArray != NULL) 
  { free(rewobs.o.doubleArray); rewobs.o.doubleArray = NULL; } 
    
  if (msg_response != NULL)
  { free(msg_response); msg_response = NULL; }
  
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

/*
 * Responds to message sent from the RL-Viz app. 
 */
Message env_message(const Message inMessage) {
  DPR << "received message " << inMessage << endl;

  if (msg_response == NULL) 
    msg_response = (char*)malloc(1000*sizeof(char));  
  
  if (strcmp(inMessage, "TO=3 FROM=0 CMD=4 VALTYPE=3 VALS=NULL") == 0)
  {    
    strcpy(msg_response, "TO=0 FROM=3 CMD=0 VALTYPE=0 VALS=1_1"); 
    DPR << "responding: " << msg_response << endl; 
    return msg_response; 
  }
  else if (strcmp(inMessage, "TO=3 FROM=0 CMD=6 VALTYPE=3 VALS=NULL") == 0)
  {
    strcpy(msg_response, "TO=0 FROM=3 CMD=0 VALTYPE=0 VALS=visualizers.RealTimeStrategyVisualizer.RealTimeStrategyVisualizer");
    DPR << "responding: " << msg_response << endl;
    return msg_response;
  }
  else if (strcmp(inMessage, "TO=3 FROM=0 CMD=3 VALTYPE=1 VALS=GetRTSSpec") == 0)
  {
    if (!inited)
      init(); 
    
    // construct it all
    string tmpts = task_spec;
    boost::replace_all(tmpts, "=", "$");
    
    string resp = "TO=0 FROM=3 CMD=0 VALTYPE=1 VALS=";    
    resp.append(tmpts);
    
    DPR << "responding: " << resp << endl; 
    
    msg_response = (char*)realloc(msg_response, (resp.length()+10)*sizeof(char)); 
    strcpy(msg_response, resp.c_str());
    
    return msg_response;
  }
  
  return "message not handled. "; 
}



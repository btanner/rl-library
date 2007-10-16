
#include "Helpers.H"
#include "MiniGameState.H"
#include "Player.H"
#include "rlglue_env.H"
#include "SDL_GUI.H"
#include "SDL_init.H"

#include <stdlib.h>
#include <time.h>

#include <iostream>
#include <map>
#include <string>

#define DEBUG 1

using namespace std;

static MiniGameState * statePtr;
static MiniGameParameters * parms;
static Player * opponent; 
boost::array<std::string, MiniGameState::PLAYER_NUM> views;
static SDL_GUI<MiniGameState> gui;
static std::map<std::string, SDL_GUI<MiniGameState>::Marker> markers;

static int time_step;
static string task_spec;
static char * task_spec_cstr = NULL; 
static Observation obs; 
static Reward_observation rewobs; 

void init_gui(MiniGameState & state) 
{
  SDL_init::video_init();
  markers["worker"] = SDL_GUI<MiniGameState>::MARKER_H; // mark workers
  gui.init(parms->width, parms->height, state, markers);
  gui.display();
}
/* RL-Glue Interface */

/** 
 * Creates the necessary data structures. 
 * Returns a description of the environment at the start of the episode. 
 */
Task_specification env_init()
{    
  DPR << "RLG> Starting env_init ..." << endl;
  
  srand(time(NULL)); 
  
  time_step = 1;
  
  obs.intArray = NULL;
  obs.doubleArray = NULL; 
  rewobs.o.intArray = NULL; 
  rewobs.o.doubleArray = NULL; 

  statePtr = new MiniGameState; 
  //opponent = new RandomPlayer(0);
  opponent = new TestPlayer(0);
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
  
  cout << "opp actions = " << actions[0] << endl; 
  cout << "rlagent actions = " << actions[1] << endl; 
  
  statePtr->simulation_step(actions, views);
  
  gui.event();
  gui.display();
  gui.delay(125);  
  
  if (statePtr->finished()) {
    // Game is done! 
    // determine winner & reward
    rewobs.terminal = 1; 
  }
  
  // The RL agent will always be player 1. The opponent is player 0.  
  //string statestr = statePtr->srv_encode_view(1);
  string statestr = views[1];
  cout << "statestr is "; 
  prettyPrintView(views[1]);

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
  DPR << "received message" << inMessage << endl;
  return "rlgenv does not respond to any messages.";
}



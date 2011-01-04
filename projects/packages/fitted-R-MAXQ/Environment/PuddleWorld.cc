// env_ function prototypes types 
#include <rlglue/Environment_common.h>	  

// helpful functions for allocating structs and cleaning them up 
#include <rlglue/utils/C/RLStruct_util.h> 

#include <algorithm>
#include <cmath>
#include <iterator>
#include <sstream>
#include <vector>

#include "Random.h"

namespace {
  // Declare RL Glue variables.
  observation_t current_observation;
  reward_observation_terminal_t ro;

  const double STEP = 0.05;
  const int PUDDLES = 2;
  const double PUDDLERADIUS = 0.1;
  const int START_SET_DIM = 2;
  const int START_SET_SIZE = 50;
  const int STATE_DIM = 2;

  std::vector<std::vector<std::vector<double> > > P;
  std::vector<double> GOAL;
  std::vector<double> current_state;
  Random rng;
}

double dot(double *u, double *v) {
  return u[0] * v[0] + u[1] * v[1];
}

double distance(const std::vector<double> &state, int p)
{
  double v[2];
  double w[2];
  double z[2];
  double d; 

  for(int i=0;i<STATE_DIM;i++){
    v[i] = P[p][1][i] - P[p][0][i];
    w[i] = state[i] - P[p][0][i];
  }

  double c1 = dot(w,v);
  if ( c1 <= 0 ) {
    d = 0.0; 
    for(int i=0;i<STATE_DIM;i++)
      d += (state[i] - P[p][0][i]) * (state[i] - P[p][0][i]);
    d = std::sqrt(d); 
    return d; 
  }

  double c2 = dot(v,v);
  if ( c2 <= c1 ) {
    d = 0.0; 
    for(int i=0;i<STATE_DIM;i++)
      d += (state[i] - P[p][1][i]) * (state[i] - P[p][1][i]);
    d = std::sqrt(d); 
    return d; 
  }

  double b = c1 / c2;
  d = 0.0;
  for(int i=0;i<STATE_DIM;i++) {
    z[i] =  P[p][0][i] + b * v[i];
    d += (state[i] - z[i]) * (state[i] - z[i]);
  }
  d = std::sqrt(d); 
  return d;
}

double getReward() {
  double r; 
  int p; 
  double d; 

  // standard penalty
  r = -1; 

  // puddle penalty
  for (p=0;p<PUDDLES;p++) {
    d = distance(current_state, p);
    if ( d < PUDDLERADIUS)
      r += - 400.0 * (PUDDLERADIUS - d); 
  }

  return r;
}

double apply(int a) {
  double noise = 0.0;
 
  // compute next state out of current state and action
  if (a==0)
    current_state[1] += STEP; // UP
  else if (a==1)
    current_state[1] -= STEP; // DOWN
  else if (a==2) 
    current_state[0] += STEP; // RIGHT
  else if (a==3)
    current_state[0] -= STEP; // LEFT
  else {
    printf("Invalid action %d %d \n", a, a);
    exit(1);
  }

  // add noise
  for(int i=0;i<STATE_DIM;i++){
    noise = rng.normal() * 0.01;
    current_state[i] += noise; 
    //printf("Noise: %5.3lf ", noise);
  }
 
  // bring state within bounds
  for(int i=0;i<STATE_DIM;i++){
    if (current_state[i]>1.0)
      current_state[i] = 1.0;
    else if (current_state[i]<0.0)
      current_state[i] = 0.0;
  }
 
  //printf("New state: (%5.3lf,%5.3lf) ", current_state[0], current_state[1]); 


  // prepare return values;
  return getReward();

 //printf("Reward: %8.6lf) ", ro.r); 
}

bool terminalp() {
  return fabs(GOAL[0]-current_state[0]) + fabs(GOAL[1]-current_state[1])
    < 0.1;
}

const char *env_init()
{
  P.resize(PUDDLES,
	   std::vector<std::vector<double> >(2, std::vector<double>(2)));
  GOAL.resize(STATE_DIM);
  current_state.resize(STATE_DIM);

  P[0][0][0] = 0.10; //puddle #1, point #1, x
  P[0][0][1] = 0.75; //puddle #1, point #1, y
  P[0][1][0] = 0.45; //puddle #1, point #2, x
  P[0][1][1] = 0.75; //puddle #1, point #2, y
  
  P[1][0][0] = 0.45; //puddle #2, point #1, x
  P[1][0][1] = 0.40; //puddle #2, point #1, y
  P[1][1][0] = 0.45; //puddle #2, point #2, x
  P[1][1][1] = 0.80; //puddle #2, point #2, y
   	   
  //-------------- RANDOM GOAL POS

  double d1=0.0, d2 = 0.0;
  while( d1 < PUDDLERADIUS || d2 < PUDDLERADIUS) {
    // GOAL[0] = rng.uniform();
    // GOAL[1] = rng.uniform();
    GOAL[0] = 1;
    GOAL[1] = 1;
    d1 = distance(GOAL, 0);
    d2 = distance(GOAL, 1);
  }

  // Handle RL Glue stuff.
  allocateRLStruct(&current_observation, 0, 2, 0);
  std::fill(current_observation.doubleArray,
	    current_observation.doubleArray + current_observation.numDoubles,
	    0);
  ro.observation = &current_observation;
  ro.terminal = 0;
  ro.reward = 0.0;	

  std::stringstream response;
  response << "VERSION RL-Glue-3.0 PROBLEMTYPE episodic DISCOUNTFACTOR 1 ";
  response << "OBSERVATIONS DOUBLES (" << STATE_DIM << " 0 1) ";
  response << "ACTIONS INTS (0 3) ";
  response << "REWARDS (" << -400.0 * PUDDLERADIUS * PUDDLES << " 0) ";
  response << "EXTRA PuddleWorld implemented by Nicholas K. Jong.";
  static std::string buffer;
  buffer = response.str();
  return buffer.c_str();
}

void env_cleanup()
{
  clearRLStruct(&current_observation);

  current_state.clear();
  GOAL.clear();
  P.clear();
}

const observation_t *env_start()
{
  int count = 0;
  current_state[0] = rng.uniform();
  current_state[1] = rng.uniform();

  while((fabs(current_state[0] - GOAL[0]) + fabs(current_state[1] - GOAL[1]) < 0.2) && count < 10000)
    {
      current_state[0] = rng.uniform();
      current_state[1] = rng.uniform();
      count++;
    }

  std::copy(current_state.begin(),
	    current_state.end(),
	    current_observation.doubleArray);
  ro.reward = 0.0;
  ro.terminal = 0;

  return &current_observation;
}

const reward_observation_terminal_t *env_step(const action_t *a)   
{	
  ro.reward = apply(a->intArray[0]);
  ro.terminal = terminalp() ? 1 : 0;

  std::copy(current_state.begin(),
	    current_state.end(),
	    current_observation.doubleArray);

  return &ro;
}   

const char *env_message(const char *_inMessage) {
  std::stringstream response;
  std::stringstream message(_inMessage);

  // Tokenize the message
  std::istream_iterator<std::string> it(message);
  std::istream_iterator<std::string> end;
  
  while (it != end) {
    std::string command(*it++);
    if (command.compare("set-random-seed") == 0) {
      if (it == end) {
	response << "PuddleWorld received set-random-seed with no argument.\n";
      } else {
	std::string seed_string(*it++);
	int seed = atoi(seed_string.c_str());
	rng.reset(seed);
	response << "PuddleWorld set random seed to " << seed << ".\n";
      }
    } else {
      response << "PuddleWorld did not understand '" << command << "'.\n";
    }
  }

  static std::string buffer;
  buffer = response.str();
  return buffer.c_str();
}

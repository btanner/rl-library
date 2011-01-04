// env_ function prototypes types 
#include <rlglue/Environment_common.h>	  

// helpful functions for allocating structs and cleaning them up 
#include <rlglue/utils/C/RLStruct_util.h> 

#include <algorithm>
#include <cmath>
#include <iterator>
#include <sstream>
#include <string>
#include <vector>

#include "Random.h"

namespace {
  // Declare RL Glue variables.
  observation_t current_observation;
  reward_observation_terminal_t ro;

  const double STEP = 0.05;
  const int PUDDLES = 2;
  const double PUDDLERADIUS = 0.1;
  const int STATE_DIM = 2;
  const int NUM_GOALS = 4;

  const int NUM_ACTIONS = 4 + 1 + NUM_GOALS;

  std::vector<std::vector<std::vector<double> > > P; // puddle loci
  std::vector<std::vector<double> > GOALS; // coordinates of goals

  std::vector<double> pos; // agent coordinates
  std::vector<double> attained_goals; // boolean flags
  int num_attained_goals; // number of 1s in attained_goals
  bool finished; // true iff agent has attempted the finish action

  Random rng; // Random number generator
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
    d = distance(pos, p);
    if ( d < PUDDLERADIUS)
      r += - 400.0 * (PUDDLERADIUS - d); 
  }

  return r;
}

void attain_goal(int a) {
  const int gidx = a;
  const double err = fabs(GOALS[gidx][0]-pos[0]) + fabs(GOALS[gidx][1]-pos[1]);
  if (err < 0.1 && attained_goals[gidx] == 0.0) {
    attained_goals[gidx] = 1.0;
    ++num_attained_goals;
  }
}

double apply(const int &action) {
  double noise; 
 
  // For NIPS, we know that the Action is an int
  int a = action;

//   std::cout << "Current state: (";
//   std::copy(pos.begin(), pos.end(),
// 	    std::ostream_iterator<double>(std::cout, " "));
//   std::copy(attained_goals.begin(), attained_goals.end(),
// 	    std::ostream_iterator<double>(std::cout, " "));
//   std::cout << ") Action: " << a << "\n";

  // compute next state out of current state and action
//   if (a >= 4 && a < NUM_ACTIONS)
  if (a==0)
    pos[1] += STEP; // UP
  else if (a==1)
    pos[1] -= STEP; // DOWN
  else if (a==2) 
    pos[0] += STEP; // RIGHT
  else if (a==3)
    pos[0] -= STEP; // LEFT
  else if (a==4) {
    if (num_attained_goals >= NUM_GOALS)
      finished = true;
  } else if (a >= NUM_ACTIONS) {
    printf("Invalid action %d %d \n", a, a);
    exit(1);
  } else
    attain_goal(a - 5);

  // add noise
  for(int i=0;i<STATE_DIM;i++){
    noise = rng.normal() * 0.01;
    pos[i] += noise; 
    //printf("Noise: %5.3lf ", noise);
  }
 
  // bring state within bounds
  for(int i=0;i<STATE_DIM;i++){
    if (pos[i]>1.0)
      pos[i] = 1.0;
    else if (pos[i]<0.0)
      pos[i] = 0.0;
  }
 
    //printf("New state: (%5.3lf,%5.3lf) ", pos[0], pos[1]); 

  // prepare return values;
  return getReward();

 //printf("Reward: %8.6lf) ", ro.r); 
}

bool terminalp()
{
  // XXX
  return finished;
//   return fabs(GOALS[0][0]-pos[0]) + fabs(GOALS[0][1]-pos[1]) < 0.1;
}

const char *env_init()
{
  P.resize(PUDDLES,
	   std::vector<std::vector<double> >(2, std::vector<double>(2)));
  GOALS.resize(NUM_GOALS, std::vector<double>(STATE_DIM));

  pos.resize(STATE_DIM);
  attained_goals.resize(NUM_GOALS);

  P[0][0][0] = 0.10; //puddle #1, point #1, x
  P[0][0][1] = 0.75; //puddle #1, point #1, y
  P[0][1][0] = 0.45; //puddle #1, point #2, x
  P[0][1][1] = 0.75; //puddle #1, point #2, y
  
  P[1][0][0] = 0.45; //puddle #2, point #1, x
  P[1][0][1] = 0.40; //puddle #2, point #1, y
  P[1][1][0] = 0.45; //puddle #2, point #2, x
  P[1][1][1] = 0.80; //puddle #2, point #2, y
   	   
  //-------------- RANDOM GOAL POS

  for (int gidx = 0; gidx < NUM_GOALS; ++gidx) {
    double d1=0.0, d2 = 0.0;
    while( d1 < PUDDLERADIUS || d2 < PUDDLERADIUS) {
      GOALS[gidx][0] = rng.uniform();
      GOALS[gidx][1] = rng.uniform();
      d1 = distance(GOALS[gidx], 0);
      d2 = distance(GOALS[gidx], 1);
    }
  }

  // Handle RL Glue stuff.
  allocateRLStruct(&current_observation, 0, 2 + NUM_GOALS, 0);
  std::fill(current_observation.doubleArray,
	    current_observation.doubleArray + current_observation.numDoubles,
	    0.0);
  ro.observation = &current_observation;
  ro.terminal = 0;
  ro.reward = 0.0;

  std::stringstream response;
  response << "VERSION RL-Glue-3.0 PROBLEMTYPE episodic DISCOUNTFACTOR 1 ";
  response << "OBSERVATIONS DOUBLES (" << STATE_DIM + NUM_GOALS << " 0 1) ";
  response << "ACTIONS INTS (0 " << NUM_ACTIONS - 1 << ") ";
  response << "REWARDS (" << -400.0 * PUDDLERADIUS * PUDDLES << " 0) ";
  response << "EXTRA FlagPuddleWorld by Nicholas K. Jong.";
  static std::string buffer;
  buffer = response.str();
  return buffer.c_str();
}

void env_cleanup()
{
  clearRLStruct(&current_observation);

  pos.clear();
  GOALS.clear();
  P.clear();
}

const observation_t *env_start()
{
  pos[0] = rng.uniform();
  pos[1] = rng.uniform();
  fill(attained_goals.begin(), attained_goals.end(), 0.0);
  num_attained_goals = 0;
  finished = false;

  std::copy(pos.begin(), pos.end(), current_observation.doubleArray);
  std::copy(attained_goals.begin(),
	    attained_goals.end(),
	    current_observation.doubleArray + pos.size());
  ro.reward = 0.0;
  ro.terminal = 0;

  return &current_observation;
}

const reward_observation_terminal_t *env_step(const action_t *a)
{
  ro.reward = apply(a->intArray[0]);
  ro.terminal = terminalp() ? 1 : 0;

  std::copy(pos.begin(), pos.end(), current_observation.doubleArray);
  std::copy(attained_goals.begin(),
	    attained_goals.end(),
	    current_observation.doubleArray + pos.size());

  return &ro;
}   

const char* env_message(const char* _inMessage) {
  std::stringstream response;
  std::stringstream message(_inMessage);

  // Tokenize the message
  std::istream_iterator<std::string> it(message);
  std::istream_iterator<std::string> end;
  
  while (it != end) {
    std::string command(*it++);
    if (command.compare("set-random-seed") == 0) {
      if (it == end) {
	response << "FlagPuddleWorld received set-random-seed with no argument.\n";
      } else {
	std::string seed_string(*it++);
	int seed = atoi(seed_string.c_str());
	rng.reset(seed);
	response << "FlagPuddleWorld set random seed to " << seed << ".\n";
      }
    } else {
      response << "FlagPuddleWorld did not understand '" << command << "'.\n";
    }
  }

  static std::string buffer;
  buffer = response.str();
  return buffer.c_str();
}

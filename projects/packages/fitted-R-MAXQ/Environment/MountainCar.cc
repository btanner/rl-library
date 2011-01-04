/********************************************************************************
 *  MountainCar.cpp
 *  
 *  Created by Adam White, created on March 29 2007.
 *  Copyright (c) 2005 UAlberta. All rights reserved.
 *
 *  Description.
 *
 *		The Standard Mountain Car, given in Figure 8.8 of the book 
 *		"Reinforcement Learning: An Introduction", by Sutton and Barto.
 *
 *      http://www.cs.ualberta.ca/%7Esutton/book/ebook/node89.html
 *
 *		The environment program implements the dynamics described in Sutton 
 *		and Barto. Except, that the environment program allows episodes to begin 
 *		with the car at the bottom of the hill with zero velocity or with the 
 *		car at a random position and velocity. This is controlled by a flag in 
 *		MCcar_common.h.
 *
 *		Episodic Task
 *		Reward: -1 per step
 *		Actions: Discrete
 *					0 - reverse 
 *					1 - coast 
 *					2 - forward 
 *		State: 2D Continuous
 *					car's x-position (-1.2 to .6)
 *					car's velocity (-.7 to .7)
 *					
 ********************************************************************************/

// env_ function prototypes types 
#include <rlglue/Environment_common.h>	  

// helpful functions for allocating structs and cleaning them up 
#include <rlglue/utils/C/RLStruct_util.h> 

#include <cmath>
#include <iterator>
#include <sstream>

namespace {
  //State variables
  double mcar_position;
  double mcar_velocity;
  
  const int max_steps = 1000000;
  int current_num_steps;

  const int state_size = 2;
  const int num_actions = 3;

  const double mcar_min_position = -1.2;
  const double mcar_max_position = 0.6;
  const double mcar_goal_position = 0.5;
  const double mcar_max_velocity = 0.07;            // the negative of this is also the minimum velocity

  //Create instances of RL-Glue types
  observation_t current_observation;
  reward_observation_terminal_t ro;

  const bool use_random_start = true;
}

//helper functions
void test_termination();  
void set_initial_position_random();
void set_initial_position_at_bottom();
void update_velocity(const action_t *a);
void update_position();

const char *env_init()
{
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
  response << "OBSERVATIONS DOUBLES (" << mcar_min_position << " "
	   << mcar_max_position << ") (" << -mcar_max_velocity
	   << " " << mcar_max_velocity << ") ";
  response << "ACTIONS INTS (0 " << num_actions - 1 << ") ";
  response << "REWARDS (-1 0) ";
  response << "EXTRA MountainCar implemented by Adam White, updated to RL-Glue 3.0 by Nicholas K. Jong.";
  static std::string buffer;
  buffer = response.str();
  return buffer.c_str();
}

const observation_t *env_start()
{
  if (use_random_start)
    set_initial_position_random();
  else
    set_initial_position_at_bottom();
  
  current_observation.doubleArray[0] = mcar_position;
  current_observation.doubleArray[1] = mcar_velocity;
	
  ro.reward = 0.0;
  ro.terminal =0;

  current_num_steps = 0;
	
  return &current_observation;
}

const reward_observation_terminal_t *env_step(const action_t *a)   
{	
  current_num_steps++;

  update_velocity(a);
  update_position();
	
  ro.reward = -1;
		
  test_termination();

  current_observation.doubleArray[0] = mcar_position;
  current_observation.doubleArray[1] = mcar_velocity;
		    
  return &ro;
}   

void env_cleanup()
{
  clearRLStruct(&current_observation);
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
	response << "MountainCar received set-random-seed with no argument.\n";
      } else {
	std::string seed_string(*it++);
	int seed = atoi(seed_string.c_str());
	srand48(seed);
	response << "MountainCar set random seed to " << seed << ".\n";
      }
    } else {
      response << "MountainCar did not understand '" << command << "'.\n";
    }
  }

  static std::string buffer;
  buffer = response.str();
  return buffer.c_str();
}

//Helper functions ----------------------------------------------
void test_termination()  
{
  if (mcar_position >= mcar_goal_position) 
    ro.terminal = 1; //True
  else if(current_num_steps > max_steps)
    ro.terminal = 1; //true
  else
    ro.terminal = 0;
}

void set_initial_position_at_bottom()
{
  mcar_position = -M_PI/6.0; 
  mcar_velocity = 0.0; 
}

void set_initial_position_random()
{
  mcar_position = (drand48()*(mcar_goal_position + fabs(mcar_min_position)) + mcar_min_position);
  mcar_velocity = (drand48()*(mcar_max_velocity*2) - mcar_max_velocity);
}


void update_velocity(const action_t *action)
{
  mcar_velocity += (action->intArray[0]-1)*0.001 + cos(3*mcar_position)*(-0.0025);
  if (mcar_velocity > mcar_max_velocity) mcar_velocity = mcar_max_velocity;
  if (mcar_velocity < -mcar_max_velocity) mcar_velocity = -mcar_max_velocity;
}

void update_position()
{
  mcar_position += mcar_velocity;
  if (mcar_position > mcar_max_position) mcar_position = mcar_max_position;
  if (mcar_position < mcar_min_position) mcar_position = mcar_min_position;
  if (mcar_position==mcar_min_position && mcar_velocity<0) mcar_velocity = 0;	
}


/*********************************************************************************************************
 *  Author: Ioannis Partalas
 *
 *  Based on MontainCar3DSym.cc, created by Matthew Taylor (Based on MountainCar.cc, created by Adam White, 
 *  								created on March 29 2007.)
 *
 *		Episodic Task
 *		Reward: -1 per step
 *		Actions: Discrete
 *					0 - coast
 *					1 - left
 *					2 - right
 *					3 - down
 *					4 - up
 *
 *		State: 3D Continuous
 *					car's x-position (-1.2 to .6)
 *					car's y-position (-1.2 to .6)
 *					car's x-velocity (-.07 to .07)
 *					car's y-velocity (-.07 to .07)
 *					
 ********************************************************************************************************/
#include "mountaincar-3D.h"

static reward_observation_terminal_t this_reward_observation;							
static observation_t observation;

static int fixed_start_state = 1;

const char* env_init()
{	
    char *task_spec_string="VERSION RL-Glue-3.0 PROBLEMTYPE episodic "
                            "DISCOUNTFACTOR 1 OBSERVATIONS DOUBLES (-1.2 0.6) (-1.2 0.6) (-0.07 0.07) (-0.07 0.07) "
                           "ACTIONS INTS (0 4)  REWARDS (-1 0) "
                          "EXTRA Name=MountainCar3D";
    allocateRLStruct(&observation,0,4,0);
    this_reward_observation.observation=&observation;
    this_reward_observation.reward=0;
    this_reward_observation.terminal=0;


  m_offset = float(rand()) / (float)RAND_MAX;
  m_offset -= 0.5;
  m_offset /= 100.0;

  return task_spec_string;
}

const observation_t *env_start()
{

  if(fixed_start_state)
  {
      set_initial_position_at_bottom();
  }
  else
  {
      set_initial_position_random();
  }
  
  observation.doubleArray[0] = mcar_Xposition;
  observation.doubleArray[1] = mcar_Yposition;
  observation.doubleArray[2] = mcar_Xvelocity;
  observation.doubleArray[3] = mcar_Yvelocity;
  
  this_reward_observation.reward = 0.0;
  this_reward_observation.terminal =0;
  
  current_num_steps = 0;
  
  return &observation;
}

const reward_observation_terminal_t *env_step(const action_t *this_action)   
{	

  current_num_steps++;
  

  update_velocity(this_action->intArray[0]);

  
  this_reward_observation.reward = -1;
  
  test_termination();

  this_reward_observation.observation->doubleArray[0] = mcar_Xposition;
  this_reward_observation.observation->doubleArray[1] = mcar_Yposition;
  this_reward_observation.observation->doubleArray[2] = mcar_Xvelocity;
  this_reward_observation.observation->doubleArray[3] = mcar_Yvelocity;


  return &this_reward_observation;
}   

void env_cleanup()
{
  clearRLStruct(&observation);
}

const char* env_message(const char* inMessage){
	if(strcmp(inMessage,"set-random-start-state")==0){
        fixed_start_state=0;
        return "Message understood.  Using random start state.";
    }
    
	return "MountainCar does not respond to that message.";
}

//Helper functions ----------------------------------------------
void test_termination()  
{
    if ((mcar_Xposition >= mcar_goal_position) && (mcar_Yposition >= mcar_goal_position))
	this_reward_observation.terminal = 1; //True
    else if(current_num_steps > max_steps)
	this_reward_observation.terminal = 1; //true
}

void set_initial_position_random()
{
    mcar_Xposition = mcar_min_position+((double)rand()/((double) RAND_MAX+1))*((mcar_max_position-0.2)-mcar_min_position);
    mcar_Yposition = mcar_min_position+((double)rand()/((double) RAND_MAX+1))*((mcar_max_position-0.2)-mcar_min_position);
    mcar_Xvelocity = 0.0; 
    mcar_Yvelocity = 0.0; 
}


void set_initial_position_at_bottom()
{
    mcar_Xposition = -M_PI/6.0 + m_offset; 
    mcar_Yposition = -M_PI/6.0 + m_offset; 
    mcar_Xvelocity = 0.0; 
    mcar_Yvelocity = 0.0; 
}

void update_velocity(int act)
{

  switch (act) {
  case 0:
    mcar_Xvelocity += cos(3*mcar_Xposition)*(-0.0025);
    mcar_Yvelocity += cos(3*mcar_Yposition)*(-0.0025);
    break;
  case 1:
    mcar_Xvelocity += -0.001 + cos(3*mcar_Xposition)*(-0.0025);
    mcar_Yvelocity += cos(3*mcar_Yposition)*(-0.0025);
    break;
  case 2:
    mcar_Xvelocity += +0.001 + cos(3*mcar_Xposition)*(-0.0025);
    mcar_Yvelocity += cos(3*mcar_Yposition)*(-0.0025);
    break;
  case 3:
    mcar_Xvelocity += cos(3*mcar_Xposition)*(-0.0025);
    mcar_Yvelocity += -0.001 + cos(3*mcar_Yposition)*(-0.0025);
    break;
  case 4:
    mcar_Xvelocity += cos(3*mcar_Xposition)*(-0.0025);
    mcar_Yvelocity += +0.001 + cos(3*mcar_Yposition)*(-0.0025);
    break;
  }

  //mcar_Xvelocity *= get_gaussian(1.0,std_dev_eff);
  //mcar_Yvelocity *= get_gaussian(1.0,std_dev_eff);

  if (mcar_Xvelocity > mcar_max_velocity) 
    mcar_Xvelocity = mcar_max_velocity;
  else if (mcar_Xvelocity < -mcar_max_velocity) 
    mcar_Xvelocity = -mcar_max_velocity;
  if (mcar_Yvelocity > mcar_max_velocity) 
    mcar_Yvelocity = mcar_max_velocity;
  else if (mcar_Yvelocity < -mcar_max_velocity) 
    mcar_Yvelocity = -mcar_max_velocity;

  update_position();
}

void update_position()
{
  mcar_Xposition += mcar_Xvelocity;
  mcar_Yposition += mcar_Yvelocity;

  if (mcar_Xposition > mcar_max_position) 
    mcar_Xposition = mcar_max_position;
  if (mcar_Xposition < mcar_min_position) 
    mcar_Xposition = mcar_min_position;
  if (mcar_Xposition==mcar_max_position && mcar_Xvelocity>0) 
    mcar_Xvelocity = 0;	
  if (mcar_Xposition==mcar_min_position && mcar_Xvelocity<0) 
    mcar_Xvelocity = 0;	

  if (mcar_Yposition > mcar_max_position) 
    mcar_Yposition = mcar_max_position;
  if (mcar_Yposition < mcar_min_position) 
    mcar_Yposition = mcar_min_position;
  if (mcar_Yposition==mcar_max_position && mcar_Yvelocity>0) 
    mcar_Yvelocity = 0;	
  if (mcar_Yposition==mcar_min_position && mcar_Yvelocity<0) 
    mcar_Yvelocity = 0;	
}

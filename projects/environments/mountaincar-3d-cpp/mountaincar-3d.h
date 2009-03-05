#ifndef MOUNTAINCAR_H
#define MOUNTAINCAR_H

#include <math.h>
#include <cstring>
#include <cstdio>
#include <cstdlib>"

#include <iostream>
#include <fstream>
#include <string>
#include <iterator>
#include <vector>
#include <rlglue/Environment_common.h>
#include <rlglue/utils/C/TaskSpec_Parser.h>
#include <rlglue/utils/C/RLStruct_util.h>

#ifndef RAND_MAX
#define RAND_MAX ((int) ((unsigned) ~0 >> 1))
#endif

using namespace std;

double m_offset;


/*State variables*/
double mcar_Xposition;
double mcar_Yposition;
double mcar_Xvelocity;
double mcar_Yvelocity;

int max_steps = 4000;
int current_num_steps;

const int state_size = 4;
const int num_actions = 5;

const double mcar_min_position = -1.2;
const double mcar_max_position = 0.6;
const double mcar_goal_position = 0.5;
const double mcar_max_velocity = 0.07;            // the negative of this is also the minimum velocity

//helper functions
void test_termination();  
void set_initial_position_random();
void set_initial_position_at_bottom();
void update_velocity(int a);
void update_position();

//Create instances of RL-Glue types
taskspec_t env_task_spec;

#endif

   

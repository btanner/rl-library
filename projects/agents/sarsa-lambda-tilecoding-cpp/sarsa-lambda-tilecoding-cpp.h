#include "tiles.h"
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <iostream>
#include <fstream>
using namespace std;

#include <rlglue/Agent_common.h>
#include <rlglue/utils/C/RLStruct_util.h> /* helpful functions for structs */
#include <rlglue/utils/C/TaskSpec_Parser.h> /* task spec parser */

#ifndef RAND_MAX
#  define RAND_MAX ((int) ((unsigned) ~0 >> 1))
#endif

double const POS_WIDTH = (1.7/8);
double const VEL_WIDTH = (0.14/8);

#define MEMORY_SIZE 102400                      //number of possible features 
#define NUM_ACTIONS 5                          //number of available actions   
#define  NUM_TILINGS 16			  //number of tiling grids	
#define NUM_OBSERVATIONS 4                     //dimension of observation


// Global RL variables:
double QSA[NUM_ACTIONS];								// action values
double weights[MEMORY_SIZE];							// feature weights
double traces[MEMORY_SIZE];                             // eligibility traces
int activeFeatures[NUM_ACTIONS][NUM_TILINGS];           // sets of features, one set per action


// Standard RL parameters:
double epsilon;                    // probability of random action
double alpha;                      // step size parameter
double lambda;                     // trace-decay parameter
double RLgamma;                // discount-rate parameter

action_t this_action;  //action selected on previous time step
action_t last_action;


observation_t *last_observation=0;

int tmp_action;
double* tmp_obs;


//Helper functions
void computeActionValues();                         
void computeActionValues(int a);                              
void computeActiveFeatures(double* o);                                    
int argmax(double *Q,int,double &b);            
int selectEpsilonGreedyAction();
void updateTraces();
void updateWeights(double delta);
void save_weights(char *);

// ------------------------ Suttons Trace Code -----------------------------------------------------
const int  MAX_NONZERO_TRACES=1000;

int nonzero_traces[MAX_NONZERO_TRACES];
int nonzero_traces_inverse[MEMORY_SIZE];

float minimum_trace = 0.01;
int num_nonzero_traces = 0;

void ClearTrace(int f);                          // clear or zero-out trace, if any, for given feature
void ClearExistentTrace(int f, int loc);         // clear trace at given location in list of nonzero-traces

void DecayTraces(float decay_rate);              // decay all nonzero traces
void SetTrace(int f, float new_trace_value);     // set trace to given value
void IncreaseMinTrace();                         // increase minimal trace value, forcing more to 0, making room for new ones
// ------------------------ Suttons Trace Code -----------------------------------------------------


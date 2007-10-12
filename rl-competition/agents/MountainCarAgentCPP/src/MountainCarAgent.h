/*******************************************************************************
 *  MountainCarAgent.h
 *  
 *  Created by Adam White, created on March 29 2007.
 *  Copyright (c) 2005 UAlberta. All rights reserved.
 *
 *  Description.
 *
 *		A Sarsa Lambda control Agent with Tile coding Function Approximation. 
 *		This agent has been tuned for the Mountain Car task. It is based on 
 *		Sutton's code, found here:
 *
 *      http://www.cs.ualberta.ca/~sutton/MountainCar/MountainCar2.cp
 *
 *      The code follows the psuedo-code for linear, gradient-descent 
 *		Sarsa(lambda) given in Figure 8.8 of the book "Reinforcement Learning: 
 *		An Introduction", by Sutton and Barto. One difference is that we use 
 *		the implementation trick mentioned on page 189 to only keep track of 
 *		the traces that are larger than "min-trace". 
 *					
 ********************************************************************************/
#ifndef MOUNTAINCARAGENT_H
#define MOUNTAINCARAGENT_H

#include "tiles.h"
#include "stdio.h"
#include <string.h>
#include <stdlib.h>

#include "RLStruct_util.h"
#include "Glue_utilities.h"

double const POS_WIDTH = (1.7 / 8);               // the tile width for position
double const VEL_WIDTH = (0.14 / 8);              // the tile width for velocity

#define MEMORY_SIZE 10000                      //number of possible features 
#define NUM_ACTIONS 3                          //number of available actions   
#define  NUM_TILINGS 14						  //number of tiling grids	
#define NUM_OBSERVATIONS 2                     //dimension of observation

// Global RL variables:
double QSA[NUM_ACTIONS];								// action values
double weights[MEMORY_SIZE];							// feature weights
double traces[MEMORY_SIZE];                             // eligibility traces
int activeFeatures[NUM_ACTIONS][NUM_TILINGS];           // sets of features, one set per action

// Standard RL parameters:
double epsilon;                    // probability of random action
double alpha;                      // step size parameter
double lambda;                     // trace-decay parameters
double RLgamma=1.0;                // discount-rate parameters

Action oldAction;						//action selected on previous time step
Action newAction;						//action selected on current time step

Observation currentObservation;			//current observation on current time step

//Helper functions
void computeActionValues();                         
void computeActionValues(Action a);                              
void computeActiveFeatures(Observation o);                                    
int argmax(double Q[NUM_ACTIONS]);            
void selectEpsilonGreedyAction(Action& Action);
void updateTraces();
void updateWeights(double delta);
int getAction(Action action);
double getObservation(Observation o,int i);
void printValue();

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
#endif
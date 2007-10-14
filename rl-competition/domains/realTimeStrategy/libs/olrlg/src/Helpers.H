#ifndef HELPERS_H
#define HELPERS_H

#include "MiniGameState.H"
#include "State.H"
#include "Vector.H"
#include "RL_common.h"
#include <map>
#include <string>

#define DPR if (DEBUG) cout

void merge_actions(const std::map<int, std::string> & p0_actions, 
                   const std::map<int, std::string> & p1_actions,
                   boost::array<std::string, MiniGameState::PLAYER_NUM> & acts); 

// Converts the ortslite state into a byte array in the observation
void rlg_convert_view(Observation & o, const std::string& statestr);

// Converts the RL-Glue action to ortslite actions
std::string rlg_convert_actions(const Action & a);

// Converts an action string to an RL-Glue action
void rlg_convert_actionstr(Action & a, const std::string& actionstr);

// Converts the states/actions to a list of integers. 
// Format is described in README.rlglue
void rlg_view2obs(Observation& obs, const MiniGameState& state);
void rlg_obs2view(MiniGameState& state, const Observation& obs);
std::string rlg_action2str(const Action& action);
void rlg_vector2action(Action& action, const std::vector<int>& integers);

std::string intArray2string(int* arr, int numInts);

void prettyPrintView(const std::string& view);

#endif

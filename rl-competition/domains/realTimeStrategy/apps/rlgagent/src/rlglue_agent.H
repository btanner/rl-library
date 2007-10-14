#ifndef rlglue_agent_h
#define rlglue_agent_h

#include "MiniGameState.H"
#include <RL_common.h>

#include <sstream>

#define PLAYER_NUM   1

/* Implemented in rlglue_agent.C */
void agent_init(const Task_specification task_spec);
Action agent_start(Observation o);
Action agent_step(Reward r, Observation o);
void agent_end(Reward r);
void agent_cleanup();
Message agent_message(const Message msg);
void agent_freeze();

/* Helpers, implemented in rlgagent_helpers.C */

std::string build_state_string(Observation & o); 

// Decision-making happens in here
void get_actions(std::vector<int> & vector, 
                 MiniGameState & state, 
                 MiniGameParameters & parms);

void add_move_action(std::vector<int>& actions, int objId, int x, int y, int max_speed);


#endif


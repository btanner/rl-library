////////////////////////////////////////////////////////////////////////////////
// Authors: Pieter Abbeel, Adam Coates, Andrew Y. Ng --- Stanford University ///
////////////////////////////////////////////////////////////////////////////////


#ifndef WEAK_BASELINE_CONTROLLER_H_
#define WEAK_BASELINE_CONTROLLER_H_

#include <RL_common.h>

// rl_glue functions

void agent_init(const Task_specification task_spec);
Action agent_start(Observation o);
Action agent_step(Reward r, Observation o);
void agent_end(Reward r);
void agent_cleanup();
Message agent_message(const Message msg);


// utility:
void agent_policy(const Observation o, Action& action);

const unsigned int u_err = 0; // forward velocity
const unsigned int v_err = 1; // sideways velocity
const unsigned int w_err = 2; // downward velocity
const unsigned int x_err = 3; // forward error
const unsigned int y_err = 4; // sideways error
const unsigned int z_err = 5; // downward error
const unsigned int p_err = 6; // angular rate around forward axis
const unsigned int q_err = 7; // angular rate around sideways (to the right) axis
const unsigned int r_err = 8; // angular rate around vertical (downward) axis
const unsigned int qx_err = 9; // quaternion entries, x,y,z,w   q = [ sin(theta/2) * axis; cos(theta/2)]
const unsigned int qy_err = 10; // where axis = axis of rotation; theta is amount of rotation around that axis
const unsigned int qz_err = 11;  // [recall: any rotation can be represented by a single rotation around some axis]


#endif // #ifndef WEAK_BASELINE_CONTROLLER_H_

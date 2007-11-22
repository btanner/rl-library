
#ifndef RLGLUE_ENV_H
#define RLGLUE_ENV_H

#include <RL_common.h>

/* RL-Glue interface */

#ifdef BUILDSHLIB
extern "C" {
#endif
  
Task_specification env_init();
Observation env_start();
Reward_observation env_step(Action a);
void env_cleanup();
void env_set_state(State_key sk);
void env_set_random_seed(Random_seed_key rsk);
State_key env_get_state();
Random_seed_key env_get_random_seed();
Message env_message(const Message inMessage);

#ifdef BUILDSHLIB
}
#endif

#endif


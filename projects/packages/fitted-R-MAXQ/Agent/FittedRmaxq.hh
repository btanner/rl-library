#ifndef _FittedRmaxq_hh_
#define _FittedRmaxq_hh_

#include "state.hh"
#include "action.hh"
#include "primitive.hh"
#include "composite.hh"

#include <rlglue/Agent_common.h> /* agent_ functions and RL-Glue types */
#include <rlglue/utils/C/RLStruct_util.h> /* helpful functions for structs */
#include <rlglue/utils/C/TaskSpec_Parser.h> /* task spec parser */

/** A particular instance of FittedRmaxq should implement this
    function, which constructs the task hierarchy that FittedRmaxq
    should use. 
    \param task_specification Known parameters of the environment. */
TaskRef get_task_hierarchy(taskspec_t *task_specification);

#endif

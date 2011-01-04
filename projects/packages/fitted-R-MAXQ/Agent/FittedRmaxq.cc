/** \file
    Implementation of Fitted R-maxq.  Contains definitions of the
    agent stubs from RL Glue. */

#include "FittedRmaxq.hh"

#include <cassert>
#include <fstream>
#include <iterator>
#include <sstream>
#include <vector>

namespace {
  TaskRef root;
  std::vector<TaskRef> stack;
  StateVectorRef laststate; ///< Stores the last state visited.
  PrimitiveTaskRef lastaction; ///< Stores the last action executed.
  action_t dummy; ///< Used to communicate actions to RL Glue.
}

void agent_init(const char* task_spec)
{
  /*Struct to hold the parsed task spec*/
  taskspec_t ts;
  int decode_result = decode_taskspec( &ts, task_spec );
  if (decode_result != 0){
    std::cerr << "Could not decode task spec, code: " << decode_result
	      << "for task spec: " << task_spec << std::endl; 
    exit(1);
  }
  assert(getNumIntAct(&ts) == 1);
  assert(getNumDoubleAct(&ts) == 0);
  allocateRLStruct(&dummy, 1, 0, 0);

  root = get_task_hierarchy(&ts);
}

const action_t *choose_action()
{
  StatePolicyRef pi_s = (stack.empty()
			 ? root->policy(laststate)
			 : stack.back()->policy(laststate));
  while (pi_s) {
    // std::cerr << "Action values:\n";
    // pi_s->debug(std::cerr);

    stack.push_back(pi_s->policy_action());
    pi_s = stack.back()->policy(laststate);
  }

  lastaction = boost::dynamic_pointer_cast<PrimitiveTask, Task>(stack.back());
  assert(lastaction != NULL);
  dummy.intArray[0] = lastaction->action();

//   std::cout << "ACTION " << lastaction->action() << "\n"; // DEBUG
//   static unsigned counter = 0;
//   std::cout << "Step " << counter++ << "\n";
//   if (counter++ == 2800) {
//     std::vector<Task *>::iterator i;
//     for (i = hierarchy.begin(); i != hierarchy.end(); ++i) {
//       Task *task = *i;
//       task->debug(std::cout);
//     }
//     exit(0);
//   }

  // std::cerr << "State " << laststate << ", Action " << lastaction->action() << " \n"; // DEBUG
  // static unsigned counter2 = 0;
  // std::cerr << "Step " << counter2++ << "\n";

  return &dummy;
}

std::vector<double> *create_state_vector(const observation_t *o)
{
  std::vector<double> *result = new std::vector<double>;
  result->insert(result->end(), o->intArray, o->intArray + o->numInts);
  result->insert(result->end(), o->doubleArray, o->doubleArray + o->numDoubles);
  return result;
}

const action_t *agent_start(const observation_t *o)
{
  stack.clear();
  laststate = StateVectorRef(create_state_vector(o));
  // std::cerr << "STATE " << laststate << "\n";
  return choose_action();
}

const action_t *agent_step(double r, const observation_t *o)
{
  StateVectorRef succ(create_state_vector(o));
  // std::cerr << "STATE " << succ << "\n";

  lastaction->update(laststate, r, succ);
  root->propagate_changes();
  // std::cerr << "agent_step: Done propagating changes.\n";
  while (!stack.empty() && stack.back()->terminal(succ))
    stack.pop_back();
  laststate = succ;
  return choose_action();
}

void agent_end(double r)
{
  lastaction->update(laststate, r);

//   std::for_each(hierarchy.rbegin(),
// 		hierarchy.rend(),
// 		std::mem_fun(&Task::clear));

  root->propagate_changes();  

//   hierarchy.back()->debug(std::cout);
}

void agent_cleanup()
{
  root.reset(); // XXX Need to break ref cycles to free all memory
  clearRLStruct(&dummy);
}

const char* agent_message(const char* _inMessage)
{
  std::stringstream response;
  std::stringstream message(_inMessage);

  // Tokenize the message
  std::istream_iterator<std::string> it(message);
  std::istream_iterator<std::string> end;
  
  while (it != end) {
    std::string command(*it++);
    if (command.compare("write-value-function") == 0) {
      if (it == end) {
	response << "FittedRmaxq received write-value-function with no argument.\n";
      } else {
	boost::shared_ptr<CompositeTask> root_task =
	  boost::dynamic_pointer_cast<CompositeTask, Task>(root);
	assert(root_task);
	std::string filename(*it++);
	std::fstream file(filename.c_str(), std::fstream::out);
	assert(file.is_open());
	root_task->write_value_function(file);
	file.close();
	response << "FittedRmaxq wrote root value function to " << filename << "\n";
      }
    } else if (command.compare("write-policy") == 0) {
      if (it == end) {
	response << "FittedRmaxq received write-policy with no argument.\n";
      } else {
	boost::shared_ptr<CompositeTask> root_task =
	  boost::dynamic_pointer_cast<CompositeTask, Task>(root);
	assert(root_task);
	std::string filename(*it++);
	std::fstream file(filename.c_str(), std::fstream::out);
	assert(file.is_open());
	root_task->write_policy(file);
	file.close();
	response << "FittedRmaxq wrote root policy to " << filename << "\n";
      }
    } else {
      response << "FittedRmaxq did not understand '" << command << "'.\n";
    }
  }

  static std::string buffer;
  buffer = response.str();
  return buffer.c_str();
}

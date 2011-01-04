#include <rlglue/RL_glue.h> /* RL_ function prototypes and RL-Glue types */
#include <iostream>
#include <sstream>
#include <string>

namespace {
  const unsigned RANDOM_SEED = 1;
  const unsigned NUM_STEPS = 5000;
  const unsigned OUTPUT_INTERVAL = 50;
  const std::string POLICY_FILENAME = "NewData/pi";
  const std::string VALUE_FUNCTION_FILENAME = "NewData/vf";
}

void set_random_seed()
{
  std::stringstream random_seed_stream;
  random_seed_stream << "set-random-seed " << RANDOM_SEED;
  std::string random_seed_string = random_seed_stream.str();
  std::cout << RL_agent_message(random_seed_string.c_str());
  std::cout << RL_env_message(random_seed_string.c_str());
}

void write_value_function(unsigned i)
{
  static const unsigned BUFFER_SIZE = 8;
  char buffer[BUFFER_SIZE];
  snprintf(buffer, BUFFER_SIZE, "%04d", i);
  std::stringstream message_stream;
  message_stream << "write-value-function " << VALUE_FUNCTION_FILENAME
		 << "." << buffer << ".dat";
  std::string message = message_stream.str();
  std::cout << RL_agent_message(message.c_str());
}

void write_policy(unsigned i)
{
  static const unsigned BUFFER_SIZE = 8;
  char buffer[BUFFER_SIZE];
  snprintf(buffer, BUFFER_SIZE, "%04d", i);
  std::stringstream message_stream;
  message_stream << "write-policy " << POLICY_FILENAME
		 << "." << buffer << ".dat";
  std::string message = message_stream.str();
  std::cout << RL_agent_message(message.c_str());
}

int main(int argc, char *argv[])
{
  set_random_seed();
  
  RL_init();
  RL_start();
  const reward_observation_action_terminal_t *rl_step_result = NULL;
  for (unsigned i = 1; i <= NUM_STEPS; ++i) {
    rl_step_result = RL_step();
    if (i % OUTPUT_INTERVAL == 0) {
      write_value_function(i);
      write_policy(i);
    }
    if (rl_step_result->terminal)
      RL_start();
  }
  RL_cleanup();
  
  return 0;
}

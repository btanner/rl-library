#include <rlglue/RL_glue.h> /* RL_ function prototypes and RL-Glue types */
#include <iostream>
#include <sstream>
#include <string>

namespace {
  const unsigned NUM_EPISODES = 1000;
  const unsigned NUM_STEPS = 1000;
}

void set_random_seed(const char *arg)
{
  int seed = atoi(arg);
  if (seed < 1) {
    std::cout << "Seed set to 1.  Only positive random seeds are allowed.\n";
    seed = 1;
  }

  std::stringstream random_seed_stream;
  random_seed_stream << "set-random-seed " << seed;
  std::string random_seed_string = random_seed_stream.str();
  std::cout << RL_agent_message(random_seed_string.c_str());
  std::cout << RL_env_message(random_seed_string.c_str());
}

int main(int argc, char *argv[])
{
  if (argc > 1) set_random_seed(argv[1]);
  
  RL_init();
  for (unsigned i = 0; i < NUM_EPISODES; ++i) {
    RL_episode(NUM_STEPS);
    std::cout << RL_return() << "\t" << RL_num_steps() << "\n" << std::flush;
  }

  std::cout << RL_agent_message("write-policy raw-pi.dat");

  RL_cleanup();
  
  return 0;
}

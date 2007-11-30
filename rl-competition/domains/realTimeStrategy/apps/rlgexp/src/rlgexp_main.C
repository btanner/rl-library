
#include <RL_glue.h>

#include <iostream>

using namespace std; 

#define NUM_EPISODES 100

static bool initclean_once = true;

//int rl_num_steps[NUM_EPISODES];
//double rl_return[NUM_EPISODES];

void run(int num_episodes)
{
/*run for num_episode number of episodes and store the number of steps and return from each episode*/        
  //int x = 0;

  RL_episode(0); 
  /*
  for(x = 0; x < num_episodes; ++x) {
    RL_episode(0);
    fprintf(stderr, ".");
    rl_num_steps[x] = RL_num_steps();
    rl_return[x] = RL_return();
  }
  */
}

int main(int argc, char *argv[]) {

  /*unsigned int i = 0;
  double avg_steps = 0.0;
  double avg_return = 0.0;*/

  if (initclean_once)
    RL_init();
  
/*basic main loop*/
  double total = 0;
  
  for (int i = 0; i < NUM_EPISODES; i++)
  {
    cout << "E" << i << " ";
    
    if (!initclean_once)
      RL_init();    
  
    //cout << "Running episodes" << endl; 
    run(1);

    double ret =  RL_return();
    cout << " R = " << ret << endl; 
    total += ret;
    fflush(stdout); 
    
    //cout << "Cleaning up" << endl; 
    
    if (!initclean_once)
      RL_cleanup();
  }
  
  cout << "Average return is: " << (total/NUM_EPISODES) << endl;
  
  /*add up all the steps and all the returns*/
  //for (i = 0; i < NUM_EPISODES; i++) {
  //  avg_steps += rl_num_steps[i];
  //  avg_return += rl_return[i];
  //}
  
  if (initclean_once)
    RL_cleanup();
  
/*average steps and returns*/
  //avg_steps /= NUM_EPISODES;
  //avg_return /= NUM_EPISODES;

/*print out results*/
  /*
  printf("\n-----------------------------------------------\n");
  printf("Number of episodes: %d\n",NUM_EPISODES);
  printf("Average number of steps per episode: %f\n", avg_steps);
  printf("Average return per episode: %f\n", avg_return);
  printf("-----------------------------------------------\n");
  */
  
  return 0;
}

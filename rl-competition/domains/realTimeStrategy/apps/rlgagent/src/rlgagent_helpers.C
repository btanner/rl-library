
#include "Global.H"
#include "MiniGameState.H"
#include "Worker.H"
#include "Marine.H"
#include "Base.H"
#include "rlglue_agent.H"

#include <stdlib.h>

#include <sstream>
#include <vector>

using namespace std;

static bool debug = false; 

/* AI state vars */
static bool have_base;
static int build_time;

void reset_state_vars()
{
  have_base = false; 
  build_time = 0;
}

string build_state_string(Observation & o)
{
  string statestr = "";
  FORU(i, o.numInts) {
    char c = (char)(o.intArray[i]);
    //cout <<  "i=" << i << ", " << c << endl;
    statestr.append(1, c);
  }
  cout << endl; 
  
  return statestr;
}

void add_move_action(std::vector<int>& actions, int objId, int x, int y, int max_speed)
{
  actions.push_back(objId);
  actions.push_back(0); // action id
  actions.push_back(x);
  actions.push_back(y);
  actions.push_back(max_speed);
  actions.push_back(-1);  // training type
}

void add_build_base_action(std::vector<int>& actions, int objId)
{
  actions.push_back(objId);
  actions.push_back(1); // action id
  actions.push_back(-1);
  actions.push_back(-1);
  actions.push_back(-1);
  actions.push_back(-1);  // training type
}

void add_stop_action(std::vector<int>& actions, int objId)
{
  actions.push_back(objId);
  actions.push_back(2); // action id
  actions.push_back(-1);
  actions.push_back(-1);
  actions.push_back(-1);
  actions.push_back(-1);  // training type
}

void add_train_worker_action(std::vector<int>& actions, int objId)
{
  actions.push_back(objId);
  actions.push_back(3); // action id
  actions.push_back(-1);
  actions.push_back(-1);
  actions.push_back(-1);
  actions.push_back(-1);  // training type
}

void add_train_marine_action(std::vector<int>& actions, int objId)
{
  actions.push_back(objId);
  actions.push_back(4); // action id
  actions.push_back(-1);
  actions.push_back(-1);
  actions.push_back(-1);
  actions.push_back(-1);  // training type
}


void add_attack_action(std::vector<int>& actions, int objId, int targetId)
{
  actions.push_back(objId);
  actions.push_back(5); // action id
  actions.push_back(-1);
  actions.push_back(-1);
  actions.push_back(-1);
  actions.push_back(targetId);  // training type
}

bool on_map(Worker * workerPtr, MiniGameParameters & parms)
{
  int x = workerPtr->x, y = workerPtr->y;
  return (x >= 0 && y >= 0 && x < parms.width && y < parms.height);
}


/* 
 * It might be better if there was an AI object which kept 
 * state variables as members and had helper functions encapsulated
 * in the class ... 
 * 
 * This code should be replaced by clever RL techniques! 
 */
void get_actions(vector<int> & vector, 
                 MiniGameState & state, 
                 MiniGameParameters & parms)
{
  // fill the vector will strings of actions
  // eg. actions: 
  //   [objId] move [x] [y] [speed] 

  DPR << "Iterating through objects" << endl;
  
  int minerals = state.player_infos[1].pd.minerals; 
  
  if (build_time > 0)
  {
    build_time++;
    if (build_time > 5)
      build_time = 0;
  }
  
  FORALL(state.all_objs, iter)
  {
    GameObj<MiniGameState> * objPtr = (*iter);
    
    ostringstream oss; 
    if (debug) objPtr->serialize(true, oss); 
    
    int objId = objPtr->view_ids[1];
    
    if (objPtr->owner == PLAYER_NUM && objPtr->get_type() == "worker")
    {
      Worker* workerPtr  = (Worker*)objPtr;
      
      double roll = drand48(); 
            
      DPR << "  found worker, id=" << objId << " : " << oss.str() << endl;
    
      if (roll < 0.03 && !have_base && on_map(workerPtr, parms))
        add_build_base_action(vector, objId); 
      else if (roll < 0.01 && on_map(workerPtr, parms))
      {
        int x = rand() % parms.width; 
        int y = rand() % parms.height; 
      
        //ostringstream actionos;
        //actionos << objId << " move " << x << " " << y << " " << workerPtr->max_speed;
        add_move_action(vector, objId, x, y, workerPtr->max_speed); 
      }
    }
    else if (objPtr->owner == PLAYER_NUM && objPtr->get_type() == "marine")
    {
      Marine* marinePtr  = (Marine*)objPtr;
      
      DPR << "  found worker, id=" << objId << " : " << oss.str() << endl;
    
      double roll = drand48(); 

      if (roll < 0.01)
      {
        int x = rand() % parms.width; 
        int y = rand() % parms.height; 
      
        //ostringstream actionos;
        //actionos << objId << " move " << x << " " << y << " " << workerPtr->max_speed;
        add_move_action(vector, objId, x, y, marinePtr->max_speed);
      }      
    }
    else if (objPtr->owner == PLAYER_NUM && objPtr->get_type() == "base")
    {
      have_base = true;    
      
      if (build_time == 0)
      {
        double roll = drand48(); 
        
        if (roll < 0.5 && minerals >= parms.worker_cost)
        {
          build_time = 1;
          add_train_worker_action(vector, objId);
        }
        else if (roll <= 1.0 && minerals >= parms.marine_cost)
        {
          build_time = 1;
          add_train_marine_action(vector, objId);
        }
      }
    }
  }
}

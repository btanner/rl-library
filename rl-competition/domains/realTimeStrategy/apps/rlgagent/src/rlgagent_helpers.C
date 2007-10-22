
#include "Global.H"
#include "MiniGameState.H"
#include "Worker.H"
#include "rlglue_agent.H"

#include <stdlib.h>

#include <sstream>
#include <vector>

using namespace std;

static bool debug = false; 

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


void get_actions(vector<int> & vector, 
                 MiniGameState & state, 
                 MiniGameParameters & parms)
{
  // fill the vector will strings of actions
  // eg. actions: 
  //   [objId] move [x] [y] [speed] 

  DPR << "Iterating through objects" << endl; 
  
  FORALL(state.all_objs, iter)
  {
    GameObj<MiniGameState> * objPtr = (*iter);
    
    ostringstream oss; 
    if (debug) objPtr->serialize(true, oss); 
    
    int objId = objPtr->view_ids[1];
    
    if (objPtr->owner == PLAYER_NUM && objPtr->get_type() == "worker")
    {
      Worker* workerPtr  = (Worker*)objPtr;
      
      DPR << "  found worker, id=" << objId << " : " << oss.str() << endl;
    
      int x = rand() % parms.width; 
      int y = rand() % parms.height; 
    
      //ostringstream actionos;
      //actionos << objId << " move " << x << " " << y << " " << workerPtr->max_speed;
      add_move_action(vector, objId, x, y, workerPtr->max_speed); 
      
    }
  }
}

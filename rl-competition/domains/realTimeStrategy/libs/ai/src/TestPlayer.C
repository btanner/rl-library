
#include "GameObj.H"
#include "MiniGameState.H" 
#include "Player.H"
#include "Worker.H" 

#include <stdlib.h>
#include <time.h>

#include <map>
#include <string> 
#include <sstream>

using namespace std;

TestPlayer::TestPlayer(int num)
{
  statePtr = 0; 
  playerNum = num;  
  init = false; 
  time = 0;
  done_marine_time = 0;
  done_worker_time = 0;
  done_base_time = 0;
  have_base = false; 
  base_y = base_x = 0; 
  mp_x = mp_y = 0;
}

TestPlayer::~TestPlayer()
{
  if (statePtr != 0)
     delete statePtr;  
}

string TestPlayer::receive_actions(string view, MiniGameParameters& parms)
{
  build_state(view); 
  
  time++;
  
  //if (time == 30) ERR("DONE");
  
  if (time == done_worker_time) 
    done_worker_time = 0;
  
  if (time == done_marine_time) 
    done_marine_time = 0; 
  
  if (time == done_base_time)
    done_base_time = 0;
    
  // fill the vector will strings of actions
  // eg. actions: 
  //   [objId] move [x] [y] [speed] 
  
  vector<string> actions;
  
  cout << "TP" << playerNum << ": view is " << view << endl;
  cout << "TP" << playerNum << ": Iterating through objects" << endl; 
  
  int numObjs = 0;
  
  FORALL(statePtr->all_objs, iter)
  {
    numObjs++;
    
    GameObj<MiniGameState> * objPtr = (*iter);
    
    ostringstream oss;
    objPtr->serialize(true, oss);
    
    //cout << "obj->owner = " << objPtr->owner << ", playerNum = " << playerNum << endl; 
    
    // iterate over each one, choose an object
    
    int objId = objPtr->view_ids[playerNum];
    
    if (objPtr->owner == playerNum && objPtr->get_type() == "worker")
    {
      Worker* workerPtr  = (Worker*)objPtr;            
      string act = chooseAction(objId, workerPtr, *statePtr, parms);
      actions.push_back(act); 
        
      //cout << "  found worker, id=" << objId << " : " << oss.str() << endl;
      //cout << "   --> action is " << act << endl;           
    }
    else if (objPtr->owner == playerNum && objPtr->get_type() == "marine")     
    {
      Marine* marinePtr  = (Marine*)objPtr;
      string act = chooseAction(objId, marinePtr, *statePtr, parms);      
      actions.push_back(act);       
       
      //cout << "  found marine, id=" << objId << " : " << oss.str() << endl;
      //cout << "   --> action is " << act << endl; 
    }
    else if (objPtr->owner == playerNum && objPtr->get_type() == "base")             
    {
      have_base = true;
       
      Base* basePtr = (Base*)objPtr;
      
      base_x = basePtr->x;
      base_y = basePtr->y;
      //cout << "base is at " << base_x << "," << base_y << endl;
        
      string act = chooseAction(objId, basePtr, *statePtr, parms);      
      actions.push_back(act);       
        
      //cout << "  found base, id=" << objId << " : " << oss.str() << endl;
      //cout << "   --> action is " << act << endl; 
    }
    else if (objPtr->get_type() == "mineral_patch")
    {
      mp_x = objPtr->x;
      mp_y = objPtr->y;
      
      //cout << "  found mineral_patch, id=" << objId << " : " << oss.str() << endl;      
    }
   
    
  }
  
  cout << "numObjs = " << numObjs << endl;
  
  string actionstr = join(actions, "#");
  
  cout << "actionstr = " << actionstr << endl; 
  
  return actionstr;
}

string TestPlayer::chooseAction(int objId, Worker* workerPtr, 
                                MiniGameState& state, MiniGameParameters& parms)
{
  if (done_base_time > 0) {
    //cout << "building!" << endl;
    return ""; 
  }
    
  if (workerPtr->is_moving) {    
    double roll = drand48();
    if (roll <= 0.01)
    {
      done_base_time = time + parms.base_build_time;
      return compose_action(objId, "build_base");
    }
    else if (roll <= 0.05)
      return compose_action(objId, "stop");    
    else
      return ""; 
  }
  
  // go towards mineral patch if we know about it, and we have a base already
  if ((mp_x > 0 || mp_y > 0) && have_base) {

    // check dist to mineral patch
    double dtmp = distance(workerPtr->x, workerPtr->y, mp_x, mp_y);
    if (dtmp < workerPtr->radius)
    {
      if (workerPtr->carried_minerals < parms.worker_mineral_capacity)
      {
        if (workerPtr->is_moving)
          return compose_action(objId, "stop");    
        else 
        {
          cout << "Worker Mining, minerals = " << workerPtr->carried_minerals << endl;
          return ""; // automatically mine until full
        }
      }
      else
      {
        // when full, move to base
        cout << "Worker full, minerals = " << workerPtr->carried_minerals << endl;
        
        ostringstream oss; 
        oss << objId << " move " << base_x << " " << base_y << " " << workerPtr->max_speed;
        return oss.str();        
      }
    }

    // when full, move to base
    if (workerPtr->carried_minerals >= parms.worker_mineral_capacity)
    {
      cout << "Worker full, minerals = " << workerPtr->carried_minerals << endl;
      
      ostringstream oss; 
      oss << objId << " move " << base_x << " " << base_y << " " << workerPtr->max_speed;
      return oss.str();
    }
    
    ostringstream oss; 
    oss << objId << " move " << mp_x << " " << mp_y << " " << workerPtr->max_speed; 
    return oss.str();     
  }
  
  return rnd_move_action(objId, parms, workerPtr->max_speed);  
}

string TestPlayer::chooseAction(int objId, Marine* marinePtr,
                                MiniGameState& state, MiniGameParameters& parms)
{  
  if (marinePtr->is_moving) {    
    double roll = drand48();
    if (roll <= 0.05)
      return compose_action(objId, "stop");    
    else
      return ""; 
  }

  // otherwise, random move
  return rnd_move_action(objId, parms, marinePtr->max_speed);  
}

string TestPlayer::chooseAction(int objId, Base* basePtr, 
                                MiniGameState& state, MiniGameParameters& parms)
{
  double roll = drand48();

  if (roll < 0.5)
    return "";
  else if (roll < 0.75 && done_worker_time <= 0)
  {
    done_worker_time = time + parms.worker_training_time;
    return compose_action(objId, "train worker");
  }
  else if (roll < 1 && done_marine_time <= 0)
  {
    done_marine_time = time + parms.marine_training_time; 
    return compose_action(objId, "train marine"); 
  }
  
  return "";
}



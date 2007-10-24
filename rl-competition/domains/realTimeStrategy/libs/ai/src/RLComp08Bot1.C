
#include "Global.H"
#include "GameObj.H"
#include "MiniGameState.H" 
#include "Worker.H" 
#include "Player.H"
#include "RLComp08Bot1.H"

#include <stdlib.h>
#include <time.h>

#include <map>
#include <string> 
#include <sstream>

using namespace std;

#define SGN(x)          ((x) < 0 ? (-1) : 1)
#define ABS(x)          ((x) < 0 ? (-(x)) : (x))

static bool debug = false; 

RLComp08Bot1::RLComp08Bot1(int num)
  : Player::Player(num)
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
  obx = oby = -1;
  
  found_ob = found_mp = false;
  
  //profiler.disable();
  //profiler.setFilename("ap_profile.log");
  //profiler.start();
  
}

RLComp08Bot1::~RLComp08Bot1()
{
  // (uses) set_state
  //if (statePtr != 0)
  //   delete statePtr;  
}

void RLComp08Bot1::determineScouts(MiniGameParameters& parms)
{
  if (!have_base)
    return; 
  
  sc_x = sc_y = 0;
  
  FORALL(statePtr->all_objs, iter)
  {
    GameObj<MiniGameState> * objPtr = (*iter);
    int objId = objPtr->view_ids[playerNum];
    
    if (objPtr->owner == playerNum && objPtr->get_type() == "worker")
    {
      if (!found_mp) {
        scouts[objId] = objPtr;
        sc_x += objPtr->x;
        sc_y += objPtr->y;
      }
    }
    else if (objPtr->owner == playerNum && objPtr->get_type() == "marine")
    {
      if (!found_ob) { 
        scouts[objId] = objPtr; 
        sc_x += objPtr->x;
        sc_y += objPtr->y;
      }
    }    
  }  
  
  sc_x = sc_x / scouts.size();
  sc_y = sc_y / scouts.size();
}



string RLComp08Bot1::receive_actions(string view, MiniGameParameters& parms)
{
  //profiler.stamp("rec_actions 0");  
  
  // now uses set_state before hand
  //build_state(view); 

  //profiler.stamp("rec_actions 1");  
  
  time++;
  
  if (time == done_worker_time) 
    done_worker_time = 0;
  
  if (time == done_marine_time) 
    done_marine_time = 0; 
  
  if (time == done_base_time)
    done_base_time = 0;
    
  minerals = statePtr->player_infos[playerNum].pd.minerals; 

  //profiler.stamp("rec_actions 2");    
  
  vector<string> actions;
  
  //determineScouts(parms);
  
  //DPR << "TP" << playerNum << ": view is " << view << endl;
  //DPR << "TP" << playerNum << ": Iterating through objects" << endl; 
  
  int numObjs = 0;
  
  //profiler.stamp("rec_actions 3");  
  
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
      
      if (scouts[objId] == objPtr) 
      {
        DPR << "W SCOUTING" << endl; 
        string act = chooseScoutingAction(objId, workerPtr, *statePtr, parms);
        actions.push_back(act);                
      }
      else
      {
        string act = chooseAction(objId, workerPtr, *statePtr, parms);
        actions.push_back(act);        
      }
        
      //cout << "  found worker, id=" << objId << " : " << oss.str() << endl;
      //cout << "   --> action is " << act << endl;           
    }
    else if (objPtr->owner == playerNum && objPtr->get_type() == "marine")
    {
      Marine* marinePtr  = (Marine*)objPtr;
      
      if (scouts[objId] == objPtr) 
      {
        DPR << "M SCOUTING" << endl; 
        string act = chooseScoutingAction(objId, marinePtr, *statePtr, parms);
        actions.push_back(act);                
      }
      else
      {
        string act = chooseAction(objId, marinePtr, *statePtr, parms);      
        actions.push_back(act);
      }
       
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
    else if (objPtr->owner != playerNum && objPtr->get_type() == "base"
             && statePtr->is_visible(objPtr, playerNum))   // don't want to cheat
    {
      found_ob = true;
       
      Base* basePtr = (Base*)objPtr;
      
      obx = basePtr->x;
      oby = basePtr->y;
    }
    
   
    
  }
  
  //profiler.stamp("rec_actions 4");    
  
  //DPR << "numObjs = " << numObjs << endl;
  
  string actionstr = join(actions, "#");

  //profiler.stamp("rec_actions 5");    
  
  //DPR << "actionstr = " << actionstr << endl; 
  
  return actionstr;
}

string RLComp08Bot1::chooseAction(int objId, Worker* workerPtr, 
                                MiniGameState& state, MiniGameParameters& parms)
{
  if (done_base_time > 0) {
    //cout << "building!" << endl;
    return ""; 
  }
    
  if (workerPtr->is_moving) {    
    double roll = drand48();
    if (roll <= 0.05 && !have_base && onMap(workerPtr, parms))
    {
      done_base_time = time + parms.base_build_time;
      return compose_action(objId, "build_base");
    }
    if (roll <= 0.05)
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
          //DPR << "Worker Mining, minerals = " << workerPtr->carried_minerals << endl;
          return ""; // automatically mine until full
        }
      }
      else
      {
        // when full, move to base
        //DPR << "Worker full, minerals = " << workerPtr->carried_minerals << endl;

        return compose_move_action(objId, base_x, base_y, workerPtr->max_speed);
      }
    }

    // when full, move to base
    if (workerPtr->carried_minerals >= parms.worker_mineral_capacity)
    {
      //DPR << "Worker full, minerals = " << workerPtr->carried_minerals << endl;
      
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

string RLComp08Bot1::chooseAction(int objId, Marine* marinePtr,
                                MiniGameState& state, MiniGameParameters& parms)
{  
  if (found_ob)
    return compose_move_action(objId, obx, oby, marinePtr->max_speed);
  
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

string RLComp08Bot1::chooseAction(int objId, Base* basePtr, 
                                MiniGameState& state, MiniGameParameters& parms)
{
  double roll = drand48();

  if (roll < 0.5)
    return "";
  else if (roll < 0.75 && done_worker_time <= 0 
           && minerals >= parms.worker_cost)
  {
    done_worker_time = time + parms.worker_training_time;
    return compose_action(objId, "train worker");
  }
  else if (roll < 1 && done_marine_time <= 0
           && minerals >= parms.marine_cost)
  {
    done_marine_time = time + parms.marine_training_time; 
    return compose_action(objId, "train marine"); 
  }
  
  return "";
}

string RLComp08Bot1::chooseScoutingAction(int objId, MobileObj<MiniGameState>* objPtr, 
                                              MiniGameState& state, MiniGameParameters& parms)
{  
  double delta_x = objPtr->x - sc_x; 
  double delta_y = objPtr->y - sc_y;
  
  int target_x = -1, target_y = -1;

  if (ABS(delta_x) > ABS(delta_y))
  {
    int shift_x = (int)(SGN(delta_x)*10.0);
    int shift_y = (int)(shift_x*delta_y/delta_x); 
    target_x = objPtr->x + shift_x; 
    target_y = objPtr->y + shift_y;
  }
  else
  {
    int shift_y = (int)(SGN(delta_y)*10.0);
    int shift_x = (int)(shift_y*delta_x/delta_y); 
    target_x = objPtr->x + shift_x; 
    target_y = objPtr->y + shift_y;
  }
  
  return compose_move_action(objId, target_x, target_y, objPtr->max_speed);
  
}



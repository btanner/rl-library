
#include "Global.H"
#include "GameObj.H"
#include "MiniGameState.H" 
#include "Player.H"
#include "RLComp08Bot1.H"

#include <stdlib.h>
#include <time.h>

#include <map>
#include <string> 
#include <sstream>
#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/trim.hpp>

using namespace std;

#define SGN(x)          ((x) < 0 ? (-1) : 1)
#define ABS(x)          ((x) < 0 ? (-(x)) : (x))

static bool debug = false; 

RLComp08Bot1::RLComp08Bot1(int num)
  : Player::Player(num)
{
  statePtr = 0; 
  parmsPtr = 0; 
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
  //profiler.setFilename("bot_profile.log");
  //profiler.start();
  
  phase = 1;
  start_build_base_time = 0;  
  my_workers = 0;
  my_marines = 0;
  cmp_x = cmp_y = 0;
  training_end_time = 0;
  next_guard_post = 0;
  p3_x = p3_y = 0;
}

RLComp08Bot1::~RLComp08Bot1()
{
  // (uses) set_state
  //if (statePtr != 0)
  //   delete statePtr;  
}

void RLComp08Bot1::determineScouts()
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

double RLComp08Bot1::dist(GameObj<MiniGameState>* a, GameObj<MiniGameState>* b)
{
  int x1 = a->x, y1 = a->y, x2 = b->x, y2 = b->y;
  return sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)); 
}

double RLComp08Bot1::dist(int x1, int y1, int x2, int y2)
{
  return sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
}

void RLComp08Bot1::computeVisible()
{
  my_vobjs.clear();
  opp_vobjs.clear();
  vmps.clear(); 

  // Collect ours first
  FORALL(statePtr->all_objs, iter)
  {
    GameObj<MiniGameState> * objPtr = (*iter);
    if (objPtr->owner == playerNum)
    {
      my_vobjs.push_back(objPtr);
    }
  }
  
  FORALL(statePtr->all_objs, iter)
  {
    GameObj<MiniGameState> * objPtr = (*iter);
    
    if (objPtr->owner == playerNum)
      continue;
    
    FORALL(my_vobjs, iter2)
    {
      GameObj<MiniGameState> * objPtr2 = (*iter2);
      if (MiniGameState::a_sees_b(*objPtr2, *objPtr))
      {
        if (objPtr->owner == 2)
        {
          vmps.push_back(objPtr);
        }
        else {
          opp_vobjs.push_back(objPtr);
        }
        
        break;
      }
    }
  }
}

void RLComp08Bot1::preprocess()
{
  // check for any new ones
  FORALL(vmps, iter)
  {
    MineralPatch* mpPtr = (MineralPatch*)(*iter);
    pair<int,int> coord; 
    coord.first = mpPtr->x; 
    coord.second = mpPtr->y;
    mpinfo[coord] = mpPtr->minerals_left;
  }
  
  // find closest MP
  
  double min_dist = 1000000000;  
  
  FORALL(mpinfo, iter) 
  {
    pair<int,int> where = (*iter).first; 
    double d = dist(where.first, where.second, base_x, base_y); 
    if (d < min_dist) {
      cmp_x = where.first;
      cmp_y = where.second;
      min_dist = d;
    }
  }
  
  // count workers + marines
  my_workers = 0;
  my_marines = 0;
  
  FORALL(my_vobjs, iter)
  {
    GameObj<MiniGameState> * objPtr = *iter;
    
    if (objPtr->get_type() == "worker")
      my_workers++;    
    else if (objPtr->get_type() == "marine")
      my_marines++;
  }
}

bool RLComp08Bot1::a_sees_loc(GameObj<MiniGameState>* obj, int x, int y, int radius)
{
  double d =
    square((double)(obj->x - x)) +
    square((double)(obj->y - y));
  
  return d <= square(radius + obj->sight_range);  
}

void RLComp08Bot1::check_mp_gone()
{
  if (mpinfo.size() == 0)
    return;
  
  // if a unit can see the spot and the mp is not there, remove it from our list of tracked ones
  FORALL(my_vobjs, iter) 
  {
    if (a_sees_loc(*iter, cmp_x, cmp_y, parmsPtr->mineral_patch_radius))
    {
      FORALL(vmps, iter2)
      {
        MineralPatch* mp = (MineralPatch*)(*iter2); 
        if (cmp_x == mp->x && cmp_y == mp->y)
        {
          // we see it, not gone!
          return;
        }
      }
      
      // we can't see it anymore, it's gone :( remove it
      
      pair<int, int> coord; 
      coord.first = cmp_x;
      coord.second = cmp_y; 
      mpinfo.erase(coord); 
      return;
    }
  }
}

void RLComp08Bot1::bounds_fix(int * x, int * y)
{
  if (*x < 0) 
    *x = 0;
  else if (*x >= parmsPtr->width)
    *x = parmsPtr->width-1;
  
  if (*y < 0) 
    *y = 0;
  else if (*y >= parmsPtr->height)
    *y = parmsPtr->height-1; 
}



void RLComp08Bot1::set_gather(std::ostringstream & actstream, MiniGameState& state, Worker* worker)
{
  pair<int, int> mploc;
  mploc.first = cmp_x;
  mploc.second = cmp_y; 
  int minerals_left = mpinfo[mploc];
  int objId = worker->view_ids[playerNum];
  
  if (   worker->carried_minerals >= parmsPtr->worker_mineral_capacity
      || minerals_left <= 0)
  {
    // head back home, buddy!
    ADD_ACTION(actstream, compose_move_action(objId, base_x, base_y, worker->max_speed));
  }
  else 
  {
    // go back and work!
    ADD_ACTION(actstream, compose_move_action(objId, cmp_x, cmp_y, worker->max_speed));    
  }
}

void RLComp08Bot1::set_roam(std::ostringstream & actstream, MiniGameState& state, MobileObj<MiniGameState>* obj)
{
  double roll = drand48();
  int objId = obj->view_ids[playerNum];
  
  if (roll < 0.01) 
    ADD_ACTION(actstream, rnd_move_action(objId, obj->max_speed));
}

void RLComp08Bot1::set_onguard(std::ostringstream & actstream, MiniGameState& state, Marine* marine)
{
  int objId = marine->view_ids[playerNum];
  int br = parmsPtr->base_radius;
  int gx = 0, gy = 0;
  
  pair<int, int> post = guardposts[objId]; 
  if (post.first == 0 && post.second == 0) {
    // marine unassigned, get cracking! 
    switch(next_guard_post) {
      case 0: gx = base_x - br; gy = base_y - br; break;
      case 1: gx = base_x; gy = base_y - br; break;
      case 2: gx = base_x + br; gy = base_y - br; break;
      case 3: gx = base_x + br; gy = base_y; break;
      case 4: gx = base_x + br; gy = base_y + br; break;
      case 5: gx = base_x; gy = base_y + br; break;
      case 6: gx = base_x - br; gy = base_y + br; break;
      case 7: gx = base_x - br; gy = base_y; break;
      case 8: gx = -1; gy = -1; break;    // patrol to mp
      case 9: gx = -2; gy = -2; break;    // scout
    }
    
    if (gx < 0 || gy < 0)
    {
      post.first = gx;
      post.second = gy; 
      guardposts[objId] = post; 
    
      next_guard_post = (next_guard_post + 1) % 10;
    }
    else
    { 
      bounds_fix(&gx,&gy);
    
      post.first = gx;
      post.second = gy; 
      guardposts[objId] = post; 
    
      next_guard_post = (next_guard_post + 1) % 10;
      
      if (gx >= 0 && gy >= 0)    
        ADD_ACTION(actstream, compose_move_action(objId, gx, gy, marine->max_speed));
    }
  }
  else if (marine->x == post.first && marine->y == post.second)
    ADD_ACTION(actstream, compose_action(objId, "stop"));
  else if (post.first == -1 && post.second == -1)
  {
    if (marine->is_moving != 1)
    {
      if (marine->x == base_x && marine->y == base_y)
        ADD_ACTION(actstream, compose_move_action(objId, cmp_x, cmp_y, marine->max_speed));
      else if (marine->x == cmp_x && marine->y == cmp_y)
        ADD_ACTION(actstream, compose_move_action(objId, base_x, base_y, marine->max_speed));
      else
        ADD_ACTION(actstream, compose_move_action(objId, cmp_x, cmp_y, marine->max_speed));
    }
  }
  else if (post.first == -2 && post.second == -2)
  {
    if (marine->is_moving != 1)
    {
      int x = rand() % 11;
      int y = rand() % 11;
      
      int dx = parmsPtr->width/10;
      int dy = parmsPtr->height/10;   
      
      int new_x = x*dx; 
      int new_y = y*dy;
      
      bounds_fix(&new_x, &new_y);
      //cout << "Adding " << new_x << ", " << new_y << endl;
      
      ADD_ACTION(actstream, compose_move_action(objId, new_x, new_y, marine->max_speed));
    }
  }
}

void RLComp08Bot1::phase1(ostringstream & actstream, MiniGameState& state)
{
  if (start_build_base_time > 0)
    return;
  
  bool roam = false; 
  
  FORALL(vmps, iter)
  {
    MineralPatch* mpPtr = (MineralPatch*)(*iter);
    pair<int,int> coord; 
    coord.first = mpPtr->x; 
    coord.second = mpPtr->y;
    mpinfo[coord] = mpPtr->minerals_left;
    base_x = coord.first;
    base_y = coord.second;
    targetmp = mpPtr; 
  }
  
  // if haven't found mps yet, then roam
  if (mpinfo.size() == 0) 
    roam = true;
  
  FORALL(my_vobjs, iter)
  {
    Worker* workerPtr = (Worker*)(*iter); 
    int objId = workerPtr->view_ids[playerNum];
    
    if (   time >= 50
        || (!roam && dist(workerPtr, targetmp) < 5))
    {
      start_build_base_time = time; 
      phase = 2; 
      base_x = workerPtr->x;
      base_y = workerPtr->y;
      ADD_ACTION(actstream, compose_action(objId, "build_base"));
    }
    else if (base_x == 0)
    {
      double roll = drand48();
    
      if (roll < 0.01)
        ADD_ACTION(actstream, rnd_move_action(objId, workerPtr->max_speed));        
    }
    else
    {
      ADD_ACTION(actstream, compose_move_action(objId, base_x, base_y, workerPtr->max_speed));              
    }
  }
}

void RLComp08Bot1::phase2(ostringstream & actstream, MiniGameState& state)
{
  if (time <= (start_build_base_time + parmsPtr->base_build_time))
    return; // from phase 1
  
  preprocess();
  
  FORALL(my_vobjs, iter) 
  {
    GameObj<MiniGameState> * objPtr = *iter;
    int objId = objPtr->view_ids[playerNum];

    if (objPtr->get_type() == "worker")
    {
      if (mpinfo.size() == 0)
        set_roam(actstream, state, (MobileObj<MiniGameState>*)objPtr);
      else
        set_gather(actstream, state, (Worker*)objPtr);
    }
    else if (objPtr->get_type() == "marine")
    {
      if (mpinfo.size() == 0)
        set_roam(actstream, state, (MobileObj<MiniGameState>*)objPtr);
      else
        set_onguard(actstream, state, (Marine*)objPtr);
    }
    else if (objPtr->get_type() == "base")
    {
      if (time > training_end_time)
      {
        if (my_workers < 5 && minerals >= parmsPtr->worker_cost)
        {
          training_end_time = time + parmsPtr->worker_training_time; 
          ADD_ACTION(actstream, compose_action(objId, "train worker"));
        }
        else if (minerals >= parmsPtr->marine_cost)
        {
          training_end_time = time + parmsPtr->marine_training_time; 
          ADD_ACTION(actstream, compose_action(objId, "train marine"));
        }
      }
    }
  }
  
  if (obx < 0 && oby < 0)
  {
    FORALL(opp_vobjs, iter)
    {
      if ((*iter)->get_type() == "base")
      {
        obx = (*iter)->x;
        oby = (*iter)->y;
      }
    }
  }
  
  check_mp_gone();  
  
  if (   time >= 1000 
      || (obx > 0 && oby > 0 && time >= 500) )
    phase = 3;
}

void RLComp08Bot1::phase3(ostringstream & actstream, MiniGameState& state)
{
  if (p3_x == 0 && p3_y == 0)
  {
    p3_x = rand() % parmsPtr->width;
    p3_y = rand() % parmsPtr->height;
  }
  
  if (obx < 0 || oby < 0)
  {
    FORALL(opp_vobjs, iter)
    {
      GameObj<MiniGameState> * objPtr = *iter;
      if (objPtr->get_type() == "base")
      {
        obx = objPtr->x; 
        oby = objPtr->y;
        break;
      }
    }
  }
    
  FORALL(my_vobjs, iter) 
  {
    if ((*iter)->get_type() == "base")
      continue;
    
    MobileObj<MiniGameState>* objPtr = (MobileObj<MiniGameState>*)(*iter);
    int objId = objPtr->view_ids[playerNum];
   
    if (obx >= 0 || oby >= 0) 
      ADD_ACTION(actstream, compose_move_action(objId, obx, oby, objPtr->max_speed));
    else if (objPtr->is_moving)
    {      
    }
    else 
    {
      int x = rand() % 11;
      int y = rand() % 11;
      
      int dx = parmsPtr->width/10;
      int dy = parmsPtr->height/10;      
      
      int new_x = x*dx;
      int new_y = y*dy; 
      
      bounds_fix(&new_x, &new_y);
      
      ADD_ACTION(actstream, compose_move_action(objId, new_x, new_y, objPtr->max_speed));
    }
  }
  
}


string RLComp08Bot1::defaultAct(string view)
{
  int numObjs = 0;  

  vector<string> actions;
  
  
  FORALL(statePtr->all_objs, iter)
  {
    numObjs++;
    
    GameObj<MiniGameState> * objPtr = (*iter);
    
    //ostringstream oss;
    //objPtr->serialize(true, oss);
    
    //cout << "obj->owner = " << objPtr->owner << ", playerNum = " << playerNum << endl; 
    
    // iterate over each one, choose an object
    
    int objId = objPtr->view_ids[playerNum];
    
    if (objPtr->owner == playerNum && objPtr->get_type() == "worker")
    {
      Worker* workerPtr  = (Worker*)objPtr;
      
      if (scouts[objId] == objPtr) 
      {
        DPR << "W SCOUTING" << endl; 
        string act = chooseScoutingAction(objId, workerPtr, *statePtr);
        actions.push_back(act);                
      }
      else
      {
        string act = chooseAction(objId, workerPtr, *statePtr);
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
        string act = chooseScoutingAction(objId, marinePtr, *statePtr);
        actions.push_back(act);                
      }
      else
      {
        string act = chooseAction(objId, marinePtr, *statePtr);      
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
        
      string act = chooseAction(objId, basePtr, *statePtr);      
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


string RLComp08Bot1::receive_actions(string view)
{  
  //profiler.stopwatch_start();  
  
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
  
  //determineScouts(parms);
  
  //DPR << "TP" << playerNum << ": view is " << view << endl;
  //DPR << "TP" << playerNum << ": Iterating through objects" << endl; 
  
  //profiler.stamp("rec_actions 3");  
  
  computeVisible();
  
  // phase-based decision-making 
  ostringstream actstream;
  
  if (phase == 1)  
    phase1(actstream, *statePtr);  
  else if (phase == 2)  
    phase2(actstream, *statePtr);  
  else if (phase == 3) 
    phase3(actstream, *statePtr);
  else
    return defaultAct(view);
  
  string actionstr = actstream.str();
  trim(actionstr);
  if (actionstr.length() > 0)
    actionstr.erase(actionstr.length()-1, 1); // chop off extra #
  
  //profiler.stopwatch_stop();
  //profiler.stamp("sw_avg is " + to_string(profiler.stopwatch_avg()));
  
  return actionstr; 
}

string RLComp08Bot1::chooseAction(int objId, Worker* workerPtr, MiniGameState& state)
{
  if (done_base_time > 0) {
    //cout << "building!" << endl;
    return ""; 
  }
    
  if (workerPtr->is_moving) {    
    double roll = drand48();
    if (roll <= 0.05 && !have_base && onMap(workerPtr))
    {
      done_base_time = time + parmsPtr->base_build_time;
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
      if (workerPtr->carried_minerals < parmsPtr->worker_mineral_capacity)
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
    if (workerPtr->carried_minerals >= parmsPtr->worker_mineral_capacity)
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
  
  return rnd_move_action(objId, workerPtr->max_speed);  
}

string RLComp08Bot1::chooseAction(int objId, Marine* marinePtr, MiniGameState& state)
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
  return rnd_move_action(objId, marinePtr->max_speed);  
}

string RLComp08Bot1::chooseAction(int objId, Base* basePtr, MiniGameState& state)
{
  double roll = drand48();

  if (roll < 0.5)
    return "";
  else if (roll < 0.75 && done_worker_time <= 0 
           && minerals >= parmsPtr->worker_cost)
  {
    done_worker_time = time + parmsPtr->worker_training_time;
    return compose_action(objId, "train worker");
  }
  else if (roll < 1 && done_marine_time <= 0
           && minerals >= parmsPtr->marine_cost)
  {
    done_marine_time = time + parmsPtr->marine_training_time; 
    return compose_action(objId, "train marine"); 
  }
  
  return "";
}

string RLComp08Bot1::chooseScoutingAction(int objId, MobileObj<MiniGameState>* objPtr, MiniGameState& state)
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



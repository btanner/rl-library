
#include "Global.H"
#include "GameObj.H"
#include "MiniGameState.H" 
#include "Player.H"
#include "RLComp08Bot2.H"

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

//static bool debug = false; 

RLComp08Bot2::RLComp08Bot2(int num)
  : Player::Player(num)
{
  time = 0; 
  minerals = 0; 
  my_workers = my_marines = 0; 
  base_x = base_y = 0;
  cmp_x = cmp_y = 0; 
  phase = 1; 
  start_build_base_time = 0;
  patrol_type = 0;
  training_end_time = 0;
  my_patrollers = 0;
  my_guards = 0;
  guardpos = 0;
  scout_x = scout_y = -1; 
  oppw_x = oppw_y = 0; 
  oppb_x = oppb_y = oppb_id = 0;  
}

RLComp08Bot2::~RLComp08Bot2()
{
}

void RLComp08Bot2::preprocess()
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
  my_patrollers = 0;
  my_guards = 0;
  
  FORALL(my_vobjs, iter)
  {
    GameObj<MiniGameState> * objPtr = *iter;
    
    if (objPtr->get_type() == "worker")
      my_workers++;    
    else if (objPtr->get_type() == "marine")
    {
      my_marines++;
      int objId = objPtr->view_ids[playerNum];
      unit_state us = unitstates[objId];
      if (us == MMPATROL1 || us == MMPATROL2)
        my_patrollers++;      
      if (us == G || us == GG || us == GP)
        my_guards++;
    }
  }
}

double RLComp08Bot2::dist(GameObj<MiniGameState>* a, GameObj<MiniGameState>* b)
{
  int x1 = a->x, y1 = a->y, x2 = b->x, y2 = b->y;
  return sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)); 
}

double RLComp08Bot2::dist(int x1, int y1, int x2, int y2)
{
  return sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
}

// radius here is the target's radius
bool RLComp08Bot2::a_sees_loc(GameObj<MiniGameState>* obj, int x, int y, int radius)
{
  double d =
    square((double)(obj->x - x)) +
    square((double)(obj->y - y));
  
  return d <= square(radius + obj->sight_range);  
}

void RLComp08Bot2::bounds_fix(int * x, int * y)
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


void RLComp08Bot2::computeVisible()
{
  my_vobjs.clear();
  opp_vobjs.clear();
  vmps.clear(); 
  
  oppw_x = oppw_y = 0; int oppw = 0;
  
  map<int, bool> id2vis; // used for groups calc 
  
  // Collect ours first
  FORALL(statePtr->all_objs, iter)
  {
    GameObj<MiniGameState> * objPtr = (*iter);
    if (objPtr->owner == playerNum)
    {
      my_vobjs.push_back(objPtr);
      int objId = objPtr->view_ids[playerNum];
      id2vis[objId] = true; 
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
          
          if (objPtr->get_type() == "worker")
          {
            oppw_x += objPtr->x; 
            oppw_y += objPtr->y;
            oppw++; 
          }
          else if (objPtr->get_type() == "base")
          {
            oppb_x = objPtr->x; 
            oppb_y = objPtr->y;
            oppb_id = objPtr->view_ids[playerNum];
          }
        }
        
        break;
      }
    }
  }
  
  if (oppw > 0)
  {
    oppw_x = (int)(((double)(oppw_x))/oppw);
    oppw_y = (int)(((double)(oppw_y))/oppw);
  }  
  
  // now unsubscribe from groups whoever is no longer around
  groupsizes.clear();
  FORALL(gid2info, iter)
  {
    int gid = (*iter).first; 
    groupinfo & gi = (*iter).second;
    
    FORALL(gi.objs, iter2)
    {
      int id = (*iter2).first;
      
      if (!id2vis[id])
        unsubscribe(id, gid);
      else
        groupsizes[gid]++;        
    }
    
    grouprolls[gid] = drand48(); 
  }
 
}

void RLComp08Bot2::subscribe(int id, GameObj<MiniGameState>* obj, int gid)
{
  uid2gid[id] = gid; 
  groupinfo & gi = gid2info[gid];
  gi.objs[id] = obj; 
}

void RLComp08Bot2::unsubscribe(int id, int gid)
{
  uid2gid[id] = 0; 
  groupinfo & gi = gid2info[gid];
  gi.objs[id] = 0; 
  gi.objs.erase(id); 
}


void RLComp08Bot2::set_work(std::ostringstream & actstream, MiniGameState& state, Worker* obj)
{
  int objId = obj->view_ids[playerNum];
  
  if (uid2gid[objId] == 0)  
  {
    // not part of a group. assign to mining group
    subscribe(objId, obj, 1);
  }
  
  
  /*
  groupinfo & gi = gid2info[1];
  bool hasmarine = false; 
  
  FORALL(gi.objs, iter)
  {
    GameObj<MiniGameState>* obj = (*iter).second; 
    if (obj->get_type() == "marine")
    {
      hasmarine = true;
      break; 
    }
  }*/
  
  unit_state us = unitstates[objId]; 

  if (mpinfo.size() == 0)
  {
    unitstates[objId] = WRETURN;
    
    if (dist(obj->x, obj->y, base_x, base_y) > 1)
    {
      ADD_ACTION(actstream, compose_move_action(objId, base_x, base_y, obj->max_speed));
      return;
    }
  }  
  
  /*if (!hasmarine)
  {
    if (us != WRETURN)
    {
      unitstates[objId] = WRETURN;
      ADD_ACTION(actstream, compose_move_action(objId, base_x, base_y, obj->max_speed));
    }
  }*/
    if (us == NIL)
    {
      unitstates[objId] = WGOING; 
      ADD_ACTION(actstream, compose_move_action(objId, cmp_x, cmp_y, obj->max_speed));
    }
    else if (obj->carried_minerals >= parmsPtr->worker_mineral_capacity && us != WRETURN)
    {
      unitstates[objId] = WRETURN;
      ADD_ACTION(actstream, compose_move_action(objId, base_x, base_y, obj->max_speed));
    }
    else if (obj->carried_minerals < parmsPtr->worker_mineral_capacity 
             && dist(obj->x, obj->y, cmp_x, cmp_y) < 1 && us != WMINING)
    {
      unitstates[objId] = WMINING;
      ADD_ACTION(actstream, compose_action(objId, "stop"));
    }
    else if (obj->carried_minerals == 0 
             && dist(obj->x, obj->y, base_x, base_y) < 1 
             && us != WGOING) 
    {
      unitstates[objId] = WGOING;
      ADD_ACTION(actstream, compose_move_action(objId, cmp_x, cmp_y, obj->max_speed));
    }
}

void RLComp08Bot2::set_mining_patrol(std::ostringstream & actstream, MiniGameState& state, Marine* obj)  
{
  int objId = obj->view_ids[playerNum];
  
  if (uid2gid[objId] == 0)  
  {
    // not part of a group. assign to mining group
    subscribe(objId, obj, 1);
  }
  
  unit_state us = unitstates[objId]; 
  
  if (us == NIL)
  {
    patrol_type++; 
    
    if (patrol_type % 2 == 1) // Guard at MP
    {
      unitstates[objId] = MMPATROL1;
      
      if (dist(obj->x, obj->y, cmp_x, cmp_y) > 1)
        ADD_ACTION(actstream, compose_move_action(objId, cmp_x, cmp_y, obj->max_speed));      
    }
    else  // Roaming guard
    {
      unitstates[objId] = MMPATROL2;
      
      if (dist(obj->x, obj->y, cmp_x, cmp_y) > 1)
        ADD_ACTION(actstream, compose_move_action(objId, cmp_x, cmp_y, obj->max_speed));      
    }
  }
  else if (us == MMPATROL2)
  {
    if (dist(obj->x, obj->y, cmp_x, cmp_y) < 1)
      ADD_ACTION(actstream, compose_move_action(objId, base_x, base_y, obj->max_speed));
    else if (dist(obj->x, obj->y, base_x, base_y) < 1)
      ADD_ACTION(actstream, compose_move_action(objId, cmp_x, cmp_y, obj->max_speed));
  }
}

void RLComp08Bot2::set_worker_hunt(std::ostringstream & actstream, MiniGameState& state, Marine* obj)
{
  int objId = obj->view_ids[playerNum];
  
  if (uid2gid[objId] == 0)  
  {
    // not part of a group. assign
    subscribe(objId, obj, 2);
  }
  
  unit_state us = unitstates[objId]; 
  
  if (us == NIL)
    unitstates[objId] = WH;
  
  if (oppw_x == 0 || oppw_y == 0)
  {
    if (!obj->is_moving)
      ADD_ACTION(actstream, rnd_move_action(objId, obj->max_speed)); 
    return;
  }

  if (!obj->is_moving)
    ADD_ACTION(actstream, compose_move_action(objId, oppw_x, oppw_y, obj->max_speed)); 
}

void RLComp08Bot2::set_aggressors(std::ostringstream & actstream, MiniGameState& state, Marine* obj)
{
  int objId = obj->view_ids[playerNum];
  
  if (uid2gid[objId] == 0)  
  {
    // not part of a group. assign
    subscribe(objId, obj, 3);
  }
 
  unit_state us = unitstates[objId]; 
  
  if (us == NIL)
    unitstates[objId] = AG;
  
  if (oppb_x == 0 || oppb_y == 0)
  {
    if (!obj->is_moving)
      ADD_ACTION(actstream, rnd_move_action(objId, obj->max_speed));
    return;
  }

  if (dist(obj->x, obj->y, oppb_x, oppb_y) < (parmsPtr->marine_atk_range + parmsPtr->base_radius))
  {
    if (obj->is_moving)
      ADD_ACTION(actstream, compose_action(objId, "stop"));
    else
      ADD_ACTION(actstream, compose_attack_action(objId, oppb_id));
  }
  else if (!obj->is_moving)
    ADD_ACTION(actstream, compose_move_action(objId, oppb_x, oppb_y, obj->max_speed));   
}

void RLComp08Bot2::set_scout(std::ostringstream & actstream, MiniGameState& state, Marine* obj)
{
  int objId = obj->view_ids[playerNum];
  
  if (uid2gid[objId] == 0)  
  {
    // not part of a group. assign to mining group
    subscribe(objId, obj, 4);
  }
  
  unit_state us = unitstates[objId]; 
  
  if (us == NIL)
    unitstates[objId] = SC; 

  if (scout_x == -1 || grouprolls[4] < 0.005)
  {
    scout_x = rand() % parmsPtr->width; 
    scout_y = rand() % parmsPtr->height;
  }  

  if (!obj->is_moving)
    ADD_ACTION(actstream, compose_move_action(objId, scout_x, scout_y, obj->max_speed));    

}

void RLComp08Bot2::set_guard(std::ostringstream & actstream, MiniGameState& state, Marine* obj)
{
  int objId = obj->view_ids[playerNum];
  
  if (uid2gid[objId] == 0)  
  {
    // not part of a group. assign to mining group
    subscribe(objId, obj, 5);
  }
  
  unit_state us = unitstates[objId]; 
  
  if (us == NIL)
  {
    guardpos++; 
    int x = -1, y = -1; 
    
    switch (guardpos % 4)
    {
      case 0: x = base_x + 20; y = base_y; break; 
      case 1: x = base_x - 20; y = base_y; break; 
      case 2: x = base_x; y = base_y - 20; break; 
      case 3: x = base_x; y = base_y + 20; break; 
    }
    
    unitstates[objId] = GG;
    
    bounds_fix(&x, &y); 
    
    ADD_ACTION(actstream, compose_move_action(objId, x, y, obj->max_speed));   
  }
  else if (us == GG && obj->is_moving)
  {
    // do nothing
  }
  else if (us == GG)
  {
    unitstates[objId] = GP; // guard posted
  }
}


void RLComp08Bot2::check_mp_gone()
{
  if (mpinfo.size() == 0)
    return;
  
  bool removed = false;
  
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
      
      removed = true; 
      break;      
    }
  }

  if (!removed)
    return;
  
  // reset assignments for all miners
  
  groupinfo & gi = gid2info[1];
  
  FORALL(gi.objs, iter) 
  {
    GameObj<MiniGameState> * objPtr = (*iter).second;
    int objId = objPtr->view_ids[playerNum];

    unitstates[objId] = NIL;
  }
  
}


// Does not build state from the view -- set_state is called externally
string RLComp08Bot2::receive_actions(string view)
{
  time++;
  minerals = statePtr->player_infos[playerNum].pd.minerals;

  computeVisible();
  
  // phase-based decision-making 
  ostringstream actstream;
  
  if (phase == 1)  
    phase1(actstream, *statePtr);  
  else if (phase == 2)  
    phase2(actstream, *statePtr);  
  else if (phase == 3) 
    phase3(actstream, *statePtr);
  
  string actionstr = actstream.str();
  trim(actionstr);
  if (actionstr.length() > 0)
    actionstr.erase(actionstr.length()-1, 1); // chop off extra #
  
  //profiler.stopwatch_stop();
  //profiler.stamp("sw_avg is " + to_string(profiler.stopwatch_avg()));
  
  return actionstr; 
}

void RLComp08Bot2::phase1(ostringstream & actstream, MiniGameState& state)
{
  if (start_build_base_time > 0) // when building, no actions
    return;
  
  bool roam = false; 
  
  FORALL(vmps, iter)
  {
    MineralPatch* mpPtr = (MineralPatch*)(*iter);
    pair<int,int> coord; 
    coord.first = mpPtr->x; 
    coord.second = mpPtr->y;
    mpinfo[coord] = mpPtr->minerals_left;
    //base_x = coord.first;
    //base_y = coord.second;
    targetmp = mpPtr; 
  }
  
  int target_x = 0, target_y = 0;
  
  // if haven't found enough mps yet, then roam
  if (mpinfo.size() <= 1) 
    roam = true;
  else
  {
    // get middle of MPs we've seen
    int num = 0;
    
    FORALL(mpinfo, iter)
    {
      target_x += (*iter).first.first; 
      target_y += (*iter).first.second;
      num++;
    }
    
    target_x = (int)(((double)target_x) / num);
    target_y = (int)(((double)target_y) / num);
  }
  
  FORALL(my_vobjs, iter)
  {
    Worker* workerPtr = (Worker*)(*iter); 
    int objId = workerPtr->view_ids[playerNum];
    
    if (   opp_vobjs.size() >= 1   // we've been spotted or we're near middle of our MPs
        || time > 50             // or delayed for too long
        || (!roam && dist(workerPtr->x, workerPtr->y, target_x, target_y) < 1) )
    {
      start_build_base_time = time; 
      phase = 2; 
      base_x = workerPtr->x;
      base_y = workerPtr->y;
      ADD_ACTION(actstream, compose_action(objId, "build_base"));
    }
    else
    {
      if (roam)
      {
        double roll = drand48();
    
        if (!workerPtr->is_moving)
          ADD_ACTION(actstream, rnd_move_action(objId, workerPtr->max_speed));
        else if (roll < 0.01)
          ADD_ACTION(actstream, rnd_move_action(objId, workerPtr->max_speed));
      }
      else
      {
        ADD_ACTION(actstream, compose_move_action(objId, target_x, target_y, workerPtr->max_speed));                      
      }
    }
  }
  
}


void RLComp08Bot2::phase2(ostringstream & actstream, MiniGameState& state)
{
  if (time <= (start_build_base_time + parmsPtr->base_build_time))
    return; // from phase 1
  
  preprocess(); 
  
  FORALL(my_vobjs, iter) 
  {
    GameObj<MiniGameState> * objPtr = *iter;
    int objId = objPtr->view_ids[playerNum];
    unit_state us = unitstates[objId];

    if (objPtr->get_type() == "worker")
    {
      //if (mpinfo.size() == 0)
      //  set_roam(actstream, state, (MobileObj<MiniGameState>*)objPtr);      
      set_work(actstream, state, (Worker*)objPtr);       
    }
    else if (objPtr->get_type() == "marine")
    {    
      if (us == NIL && my_guards < 4)
        set_guard(actstream, state, (Marine*)objPtr);
      
      if (   (us == NIL && my_patrollers <= my_workers/2)
          || us == MMPATROL1 || us == MMPATROL2)
      {
        // assign to mining patrol, or scout if no MPs
        if (mpinfo.size() == 0)
          set_scout(actstream, state, (Marine*)objPtr);
        else
          set_mining_patrol(actstream, state, (Marine*)objPtr);  
      }

      int gid = uid2gid[objId];
      
      if (gid == 0)
      {
        // unassigned 
        
        double roll = drand48(); 
        
        if (roll < 0.25)
          set_worker_hunt(actstream, state, (Marine*)objPtr);
        else if (roll < 0.5)
          set_guard(actstream, state, (Marine*)objPtr);
        else if (roll < 0.85)
          set_aggressors(actstream, state, (Marine*)objPtr);
        else 
          set_scout(actstream, state, (Marine*)objPtr);
      }
      else if (gid == 4)
        set_scout(actstream, state, (Marine*)objPtr);
      else if (gid == 5)
        set_guard(actstream, state, (Marine*)objPtr);
      else if (gid == 3)
        set_aggressors(actstream, state, (Marine*)objPtr);
      else if (gid == 2)
        set_worker_hunt(actstream, state, (Marine*)objPtr);                
    }
    else if (objPtr->get_type() == "base")
    {
      if (time > training_end_time)
      {
        // Have at least 2 marines protecting the workers
        if (my_patrollers <= my_workers/2 && minerals >= parmsPtr->marine_cost)
        {
          training_end_time = time + parmsPtr->worker_training_time;
          ADD_ACTION(actstream, compose_action(objId, "train marine"));
        }
        else
        {         
          double roll = drand48(); 
          
          double wprob = 0.2;
          
          if (oppb_x > 0 && oppb_y > 0) 
          {
            double bd = dist(base_x, base_y, oppb_x, oppb_y);
            if (bd < 50)
              wprob = 0.01;
          }
          else
          {
            double mpd = dist(base_x, base_y, cmp_x, cmp_y); 
            if (mpinfo.size() == 0 || mpd > 100)
              wprob = 0.1;
          }
          
          if (roll < wprob && minerals >= parmsPtr->worker_cost)
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
  }
  
  check_mp_gone();  
  
  if (time > 3000)
    phase = 3; 
}

void RLComp08Bot2::phase3(ostringstream & actstream, MiniGameState& state)
{
  preprocess(); 
  
  FORALL(my_vobjs, iter) 
  {
    GameObj<MiniGameState> * objPtr = *iter;
    int objId = objPtr->view_ids[playerNum];
    int gid = uid2gid[objId]; 
    
    if (gid != 0 && gid != 3)
      unsubscribe(objId, gid);
    
    if (objPtr->get_type() == "worker")
    {
    }
    else if (objPtr->get_type() == "marine")
    {
      set_aggressors(actstream, state, (Marine*)objPtr);
    }
  }
}


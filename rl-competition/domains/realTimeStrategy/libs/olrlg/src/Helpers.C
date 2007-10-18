
#include "GameObj.H"
#include "Global.H"
#include "Helpers.H"
#include "Marine.H"
#include "Worker.H"

#include <stdlib.h>
#include <math.h>

#include <map>
#include <string>
#include <sstream>

#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/split.hpp>

using namespace std;

static bool debug = false; 

// Number of attributes per object. See README.rlglue
#define RLG_OBJ_ATTRS           12

// Number of attributes per action. See README.rlglue
#define RLG_ACT_ATTRS           6

static string join(const map<int,string> & actions)
{
  stringstream ss; 
  
  FORALL(actions, iter) {
    ss << iter->first << " " << iter->second << "#";
  }
  
  string str = ss.str(); 
  return str.substr(0, str.length()-1);
}

void merge_actions(const map<int,string> & p0_actions, 
                   const map<int,string> & p1_actions,
                   boost::array<string, MiniGameState::PLAYER_NUM> & acts)
{
  string p0str = join(p0_actions); 
  string p1str = join(p1_actions);
  
  acts[0] = p0str; 
  acts[1] = p1str;
}

// converts a string to an array of integers (ASCII codes of characters)
static void convert_to_rl_type(RL_abstract_type & o, const string & statestr)
{
  int length = statestr.length(); 

  void * ptr = realloc(o.intArray, length*(sizeof(int))); 

  if (ptr == NULL) 
    ERR("Out of memory allocating in rlg_convert_view"); 

  // clear contents
  memset(ptr, '\0', length); 
  
  o.intArray = (int*)ptr;
  
  FORS(i, length) 
    o.intArray[i] = (int)(statestr[i]);
    
  o.numInts = length;
  
}

void rlg_convert_view(Observation & o, const string& statestr)
{  
  convert_to_rl_type(o, statestr);
}

std::string rlg_convert_actions(const Action & a)
{
  ostringstream oss; 
  FORU(i, a.numInts) {
    oss << ((char)a.intArray[i]); 
  }
  
  return oss.str(); 
}

void rlg_convert_actionstr(Action & a, const string& actionstr)
{
  convert_to_rl_type(a, actionstr);
}

/*****
 * Integer serializers and deserializers, specific to RL-Glue
 */

static void serializeGameState(int* array, const MiniGameState& state)
{
  // NOTE: ASSUMES RL PLAYER IS PLAYER 1 !!
  int player = 1;
  
  array[0] = state.player_infos[player].pd.minerals;
  
  int index = 1; 
  
  FORALL(state.all_objs, i) {
    GameObj<MiniGameState>* objPtr = *i; 
    
    // type
    if      (objPtr->get_type() == "worker") array[index] = 0;
    else if (objPtr->get_type() == "marine") array[index] = 1;
    else if (objPtr->get_type() == "base") array[index] = 2;
    else if (objPtr->get_type() == "mineral_patch") array[index] = 3;
    else array[index] = -1;
    index++;
    
    // id
    array[index++] = objPtr->view_ids[player];
    
    array[index++] = objPtr->owner;
    array[index++] = objPtr->x;
    array[index++] = objPtr->y;
    array[index++] = objPtr->radius;
    array[index++] = objPtr->sight_range;
    array[index++] = objPtr->hp;
    array[index++] = objPtr->armor;
    
    if (objPtr->get_type() == "worker")
    {
      Worker* workerPtr = (Worker*)objPtr;
      
      array[index++] = workerPtr->max_speed;
      array[index++] = workerPtr->is_moving;
      array[index++] = workerPtr->carried_minerals;
    }
    else if (objPtr->get_type() == "marine")
    {
      Marine* marinePtr = (Marine*)objPtr;
      
      array[index++] = marinePtr->max_speed;
      array[index++] = marinePtr->is_moving;
      array[index++] = -1;     
    }
    else
    {
      array[index++] = -1;
      array[index++] = -1;
      array[index++] = -1;
    }
  }
  
  DPR << "serialize game state index = " << index << endl; 
}

static void deserializeGameState(MiniGameState& state, int* array, int length)
{
  // ASSUMING RL PLAYER
  int player = 1;
  
  state.player_infos[player].pd.minerals = array[0]; 
  
  int index = 1;
  
  while (index < length) {
    
    int type = array[index++];
    
    GameObj<MiniGameState>* objPtr = 0;
    if   (type == 0) objPtr = state.new_game_object("worker");
    else if (type == 1) objPtr = state.new_game_object("marine");
    else if (type == 2) objPtr = state.new_game_object("base");
    else if (type == 3) objPtr = state.new_game_object("mineral_patch");
    
    // id
    objPtr->view_ids[player] = array[index++];
    
    // all the other stuff
    objPtr->owner = array[index++];
    objPtr->x = array[index++];
    objPtr->y = array[index++];
    objPtr->radius = array[index++];
    objPtr->sight_range = array[index++];
    objPtr->hp = array[index++];
    objPtr->armor = array[index++];
    
    if (objPtr->get_type() == "worker")
    {
      Worker* workerPtr = (Worker*)objPtr;
      
      workerPtr->max_speed = array[index++];
      workerPtr->is_moving = array[index++];
      workerPtr->carried_minerals = array[index++];      
    }
    else if (objPtr->get_type() == "marine")
    {
      Marine* marinePtr = (Marine*)objPtr;
      
      marinePtr->max_speed = array[index++];
      marinePtr->is_moving = array[index++];
      index++;            
    }
    else
    {
      index++;
      index++; 
      index++; 
    }
    
    state.all_objs.push_back(objPtr);
  }
  
  DPR << "serialize game state index = " << index << endl; 
}

void rlg_view2obs(Observation& obs, const MiniGameState& state)
{
  int numObjs = state.all_objs.size(); 
  int length = 1 + RLG_OBJ_ATTRS*numObjs;  

  void * ptr = realloc(obs.intArray, length*(sizeof(int))); 

  if (ptr == NULL) 
    ERR("Out of memory allocating in rlg_convert_view"); 

  // clear contents
  memset(ptr, '\0', length); 
  
  obs.intArray = (int*)ptr;

  serializeGameState(obs.intArray, state);
    
  obs.numInts = length;
}

void rlg_obs2view(MiniGameState& state, const Observation& obs)
{
  int length = obs.numInts; 
  deserializeGameState(state, obs.intArray, length);
}

std::string rlg_action2str(const Action& action)
{
  vector<string> actions; 
  
  FORU(i, action.numInts) {
    int objId = action.intArray[i++]; 
    int actionId = action.intArray[i++];
    
    int f1 = action.intArray[i++];
    int f2 = action.intArray[i++];
    int f3 = action.intArray[i++];
    int f4 = action.intArray[i++];
    
    ostringstream oss; 
    
    if (actionId == 0)  
      oss << objId << " move " << f1 <<  " " << f2 << " " << f3;
    else if (actionId == 1)
      oss << objId << " build_base";
    else if (actionId == 2)
      oss << objId << " stop";
    else if (actionId == 3)
      oss << objId << " train worker";
    else if (actionId == 4)
      oss << objId << " train marine";
    
    // to shut up compiler for now      
    f1 = f1;
    f2 = f2;
    f3 = f3;
    f4 = f4; 
    
    actions.push_back(oss.str());
  }

  return join(actions, "#");
}

void rlg_vector2action(Action& action, const std::vector<int>& integers)
{
  int length = integers.size(); 
  //DPR << "length = " << length << endl;
  if (length == 0) {
    action.numInts = 0;
    return;
  }
  
  void * ptr = realloc(action.intArray, length*(sizeof(int))); 

  if (ptr == NULL) 
    ERR("Out of memory allocating in rlg_convert_view"); 

  // clear contents
  memset(ptr, '\0', length); 
  
  action.intArray = (int*)ptr;

  FORS(i, length)
    action.intArray[i] = integers[i]; 
  
  action.numInts = length;
}

string intArray2string(int* arr, int numInts)
{
  ostringstream oss; 
  
  FORS(i, numInts) {
    oss << arr[i] << " ";
  }
  
  return oss.str(); 
}

void prettyPrintView(const string& view)
{
  vector<string> tokens;
  boost::split(tokens, view, boost::is_any_of(" "));
  FORALL(tokens, iter)
    cout << *iter << endl;
}


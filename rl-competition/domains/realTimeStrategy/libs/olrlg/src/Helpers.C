
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
  state.clear_obj(); 
  
  // ASSUMING RL PLAYER
  int player = 1;
  
  state.player_infos[player].pd.minerals = array[0]; 
  
  int index = 1;
  
  while (index < length) {
    
    int type = array[index++];
    
    // id
    int id = array[index++];
    
    GameObj<MiniGameState>* objPtr = 0;
    if   (type == 0) objPtr = state.new_game_object("worker");
    else if (type == 1) objPtr = state.new_game_object("marine");
    else if (type == 2) objPtr = state.new_game_object("base");
    else if (type == 3) objPtr = state.new_game_object("mineral_patch");
    
    // id
    objPtr->view_ids[player] = id;
    
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

void rlg_view2obs(Observation& obs, const std::string view, int playernum)
{
  /* view looks like:
    minerals=1000 # K,worker,1,o=1,x=494,y=509,r=4,sr=64,hp=50,armor=0,max_speed=2,is_moving=1,carried_minerals=0
     K,mineral_patch,2,o=2,x=532,y=521,r=16,sr=0,hp=1,armor=1000000000
     K,marine,3,o=0,x=539,y=481,r=4,sr=64,hp=50,armor=0,max_speed=2,is_moving=1 # #
   */ 
  
  vector<string> lists;
  boost::split(lists, view, boost::is_any_of("#"));
  
  vector<string> units;
  boost::split(units, lists[1], boost::is_any_of(" ")); 
  
  int length = 1+units.size()*RLG_OBJ_ATTRS; 
  
  void * ptr = realloc(obs.intArray, length*(sizeof(int))); 

  if (ptr == NULL) 
    ERR("Out of memory allocating in rlg_convert_view"); 

  memset(ptr, 0, length); 
  
  obs.intArray = (int*)ptr;
  int * array = obs.intArray;

  vector<string> mparts;
  boost::split(mparts, lists[0], boost::is_any_of("="));
  obs.intArray[0] = to_int(mparts[1]);
  
  int index = 1;
  
  vector<string>::iterator iter;
  for (iter = units.begin(); iter != units.end(); iter++)
  {
    if ((*iter).size() <= 1)
      continue;
    
    //cout << "processing " << *iter << ", size is " << (*iter).size() << endl;
    
    vector<string> attrs;
    boost::split(attrs, *iter, boost::is_any_of(","));
    
    // type
    if      (attrs[1] == "worker") array[index] = 0;
    else if (attrs[1] == "marine") array[index] = 1;
    else if (attrs[1] == "base") array[index] = 2;
    else if (attrs[1] == "mineral_patch") array[index] = 3;
    else array[index] = -1;
    index++;
    
    // id
    array[index++] = to_int(attrs[2]); 

    // owner, x, y, radius, sight_range, hp, armor
    for (int attri = 3; attri <= 9; attri++)
    {
      vector<string> aparts; 
      boost::split(aparts, attrs[attri], boost::is_any_of("="));
      array[index++] = to_int(aparts[1]);    
    }
    
    // all the rest
    
    if (attrs[1] == "worker")
    {
      // max_speed, is_moving, carried_minerals
      for (int attri = 10; attri <= 12; attri++)
      {
        vector<string> aparts; 
        boost::split(aparts, attrs[attri], boost::is_any_of("="));
        array[index++] = to_int(aparts[1]);
      }
    }
    else if (attrs[1] == "marine")
    {
      // max_speed, is_moving
      for (int attri = 10; attri <= 11; attri++)
      {
        vector<string> aparts; 
        boost::split(aparts, attrs[attri], boost::is_any_of("="));
        array[index++] = to_int(aparts[1]);
      }
      
      array[index++] = -1;     
    }
    else
    {
      array[index++] = -1;
      array[index++] = -1;
      array[index++] = -1;
    }    
  }

  obs.numInts = index;
}




void rlg_obs2view(MiniGameState& state, const Observation& obs)
{
  int length = obs.numInts; 
  deserializeGameState(state, obs.intArray, length);
}

std::string rlg_action2str(const Action& action)
{
  bool first = true;
  ostringstream oss; 
  
  FORU(i, action.numInts) {
    
    if (!first)
      oss << "#";

    first = false; 
    
    int objId = action.intArray[i++]; 
    int actionId = action.intArray[i++];
    
    int f1 = action.intArray[i++];
    int f2 = action.intArray[i++];
    int f3 = action.intArray[i++];
    int f4 = action.intArray[i++];
        
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
    else if (actionId == 5)
      oss << objId << " attack " << f4;
    
    // to shut up compiler for now      
    f1 = f1;
    f2 = f2;
    f3 = f3;
    f4 = f4; 
  }

  return oss.str();
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


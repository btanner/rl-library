
#include <vector>
#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/split.hpp>

#include <stdlib.h>

#include "rlgenv_helpers.H"
#include "RLComp08Bot1.H"
#include "RLComp08Bot2.H"

using namespace std;

void timing_start()
{
  system("rm /tmp/rlgenv.log");
  system("echo \"Started at\" >> /tmp/rlgenv.log");
  system("date >> /tmp/rlgenv.log"); 
}

void timing_end()
{
  system("echo \"Ended at\" >> /tmp/rlgenv.log");
  system("date >> /tmp/rlgenv.log"); 
}

void logstr(const std::string & str)
{
  string cmd = "echo \"";
  cmd += str;
  cmd += "\">>/tmp/rlgenv.log"; 
  system(cmd.c_str()); 
}



void copy_parms(ParameterHolder * phPtr, MiniGameParameters * mgpPtr)
{
  mgpPtr->width                        = phPtr->getIntegerParam("width");
  mgpPtr->height                       = phPtr->getIntegerParam("height");
  mgpPtr->mineral_patches              = phPtr->getIntegerParam("mineralpatches");
  
  mgpPtr->base_radius                  = phPtr->getIntegerParam("baseradius");
  mgpPtr->base_sight_range             = phPtr->getIntegerParam("basesightrange");
  mgpPtr->base_atk_range               = phPtr->getIntegerParam("baseatkrange");
  mgpPtr->base_hp                      = phPtr->getIntegerParam("basehp");
  mgpPtr->base_armor                   = phPtr->getIntegerParam("basearmor");  
  mgpPtr->base_cost                    = phPtr->getIntegerParam("basecost");
  mgpPtr->base_build_time              = phPtr->getIntegerParam("basebuildtime");
  mgpPtr->base_atk_value               = phPtr->getIntegerParam("baseatkvalue");
  
  mgpPtr->marine_radius                = phPtr->getIntegerParam("marineradius");
  mgpPtr->marine_sight_range           = phPtr->getIntegerParam("marinesightrange");
  mgpPtr->marine_atk_range             = phPtr->getIntegerParam("marineatkrange");
  mgpPtr->marine_hp                    = phPtr->getIntegerParam("marinehp");
  mgpPtr->marine_armor                 = phPtr->getIntegerParam("marinearmor"); 
  mgpPtr->marine_max_speed             = phPtr->getIntegerParam("marinemaxspeed");   
  mgpPtr->marine_cost                  = phPtr->getIntegerParam("marinecost");
  mgpPtr->marine_training_time         = phPtr->getIntegerParam("marinetrainingtime");
  mgpPtr->marine_atk_value             = phPtr->getIntegerParam("marineatkvalue");
  
  mgpPtr->worker_radius                = phPtr->getIntegerParam("workerradius");
  mgpPtr->worker_sight_range           = phPtr->getIntegerParam("workersightrange");
  mgpPtr->worker_atk_range             = phPtr->getIntegerParam("workeratkrange");
  mgpPtr->worker_hp                    = phPtr->getIntegerParam("workerhp");
  mgpPtr->worker_armor                 = phPtr->getIntegerParam("workerarmor"); 
  mgpPtr->worker_max_speed             = phPtr->getIntegerParam("workermaxspeed");   
  mgpPtr->worker_cost                  = phPtr->getIntegerParam("workercost");
  mgpPtr->worker_training_time         = phPtr->getIntegerParam("workertrainingtime");
  mgpPtr->worker_atk_value             = phPtr->getIntegerParam("workeratkvalue");
  mgpPtr->worker_mining_time           = phPtr->getIntegerParam("workerminingtime");  
  mgpPtr->worker_mineral_capacity      = phPtr->getIntegerParam("workermineralcapacity");
  
  mgpPtr->mineral_patch_radius         = phPtr->getIntegerParam("mineralpatchradius");
  mgpPtr->mineral_patch_sight_range    = phPtr->getIntegerParam("mineralpatchsight_range");
  mgpPtr->mineral_patch_hp             = phPtr->getIntegerParam("mineralpatchhp");
  mgpPtr->mineral_patch_armor          = phPtr->getIntegerParam("mineralpatcharmor");
  mgpPtr->mineral_patch_capacity       = phPtr->getIntegerParam("mineralpatchcapacity");
  
  mgpPtr->bot0                         = phPtr->getStringParam("bot0");
}

Player* get_opponent(MiniGameParameters * mgpPtr)
{
  if (mgpPtr->bot0 == "RLComp08Bot1")
    return new RLComp08Bot1(0);
  if (mgpPtr->bot0 == "RLComp08Bot2")
    return new RLComp08Bot2(0);
  
  // Default to training bot
  return new RLComp08Bot1(0);
}

void printout_phstr(char * parmsfile)
{
  ifstream file(parmsfile, ios::in);
  
  if (!file) {
    cerr << "Error opening file" << endl;
    exit(-1);
  }
  
  ParameterHolder ph; 
  string line; 
  
  while (getline(file, line)) {
    //cout << line << endl;
    
    vector<string> parts;
    boost::split(parts, line, boost::is_any_of(","));
    
    if (parts[0] == "int")
    {
      int val = to_int(parts[2]); 
      ph.addIntegerParam(parts[1], val);
      cout << "Adding integer parm " << parts[1] << " " << val << endl; 
    }
    else if (parts[0] == "str")
    {
      cout << "Adding string parm " << parts[1] << " " << parts[2] << endl; 
      ph.addStringParam(parts[1], parts[2]);
    }    
  }
  
  cout << ph.stringSerialize() << endl;
  
  file.close();
  
  exit(0);
}

void set_parm(MiniGameParameters * mgpPtr, string parm, int val)
{
  if      (parm == "width")                 mgpPtr->width = val;
  else if (parm == "height")                mgpPtr->height = val;
  else if (parm == "mineralpatches")        mgpPtr->mineral_patches = val;
  
  else if (parm == "baseradius")            mgpPtr->base_radius = val;
  else if (parm == "basesightrange")        mgpPtr->base_sight_range = val;
  else if (parm == "baseatkrange")          mgpPtr->base_atk_range = val;
  else if (parm == "basehp")                mgpPtr->base_hp = val;
  else if (parm == "basearmor")             mgpPtr->base_armor = val;
  else if (parm == "basecost")              mgpPtr->base_cost = val;
  else if (parm == "basebuildtime")         mgpPtr->base_build_time = val;
  else if (parm == "baseatkvalue")          mgpPtr->base_atk_value = val;
  
  else if (parm == "marineradius")            mgpPtr->marine_radius = val;
  else if (parm == "marinesightrange")        mgpPtr->marine_sight_range = val;
  else if (parm == "marineatkrange")          mgpPtr->marine_atk_range = val;
  else if (parm == "marinehp")                mgpPtr->marine_hp = val;
  else if (parm == "marinearmor")             mgpPtr->marine_armor = val;
  else if (parm == "marinemaxspeed")          mgpPtr->marine_max_speed = val;
  else if (parm == "marinecost")              mgpPtr->marine_cost = val;
  else if (parm == "marinetrainingtime")      mgpPtr->marine_training_time = val;
  else if (parm == "marineatkvalue")          mgpPtr->marine_atk_value = val;

  else if (parm == "workerradius")            mgpPtr->worker_radius = val;
  else if (parm == "workersightrange")        mgpPtr->worker_sight_range = val;
  else if (parm == "workeratkrange")          mgpPtr->worker_atk_range = val;
  else if (parm == "workerhp")                mgpPtr->worker_hp = val;
  else if (parm == "workerarmor")             mgpPtr->worker_armor = val;
  else if (parm == "workermaxspeed")          mgpPtr->worker_max_speed = val;
  else if (parm == "workercost")              mgpPtr->worker_cost = val;
  else if (parm == "workertrainingtime")      mgpPtr->worker_training_time = val;
  else if (parm == "workeratkvalue")          mgpPtr->worker_atk_value = val;
  else if (parm == "workerminingtime")        mgpPtr->worker_mining_time = val;
  else if (parm == "workermineralcapacity")   mgpPtr->worker_mineral_capacity = val;

  else if (parm == "mineralpatchradius")      mgpPtr->mineral_patch_radius = val;
  else if (parm == "mineralpatchsightrange")  mgpPtr->mineral_patch_sight_range = val;
  else if (parm == "mineralpatchhp")          mgpPtr->mineral_patch_hp = val;
  else if (parm == "mineralpatcharmor")       mgpPtr->mineral_patch_armor = val;  
  else if (parm == "mineralpatchcapacity")    mgpPtr->mineral_patch_capacity = val;    
}

void load_parms(char * parmsfile, MiniGameParameters * mgpPtr)
{
  ifstream file(parmsfile, ios::in);
  
  if (!file) {
    cerr << "Error opening file" << endl;
    exit(-1);
  }
  
  ParameterHolder ph; 
  string line; 
  
  while (getline(file, line)) {
    //cout << line << endl;
    
    vector<string> parts;
    boost::split(parts, line, boost::is_any_of(","));
    
    if (parts[0] == "int")
    {
      int val = to_int(parts[2]); 
      string key = parts[1];
      set_parm(mgpPtr, key, val);
    }
    else if (parts[0] == "str")
    {
      if (parts[1] == "bot0")
        mgpPtr->bot0 = parts[2];
    }    
  }
  
  file.close();
}

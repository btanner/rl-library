
#include <vector>
#include <fstream>
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
  ofstream out("/tmp/rlgenv.log", ios::app);
  out << str << endl;
  out.close();
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
  mgpPtr->mineral_patch_sight_range    = phPtr->getIntegerParam("mineralpatchsightrange");
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


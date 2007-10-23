#ifndef MiniGameState_H
#define MiniGameState_H

#include "State.H"

#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/split.hpp>
#include <iostream>
#include <sstream>

struct MiniGameParameters
{
  static const int FINE_POINTS = 16; // pts per "tile", power of 2
  static const int FPS         = 8;  // frames per second
  
  int width;                   
  int height;        
  int mineral_patches; 

  // -------------

  int base_radius;       
  int base_sight_range;
  int base_atk_range;
  int base_hp;             
  int base_armor;     
  int base_atk_value;    
           
  
  int base_cost;        
  int base_build_time;         

  // -------------
    
  int marine_radius;     
  int marine_sight_range;
  int marine_atk_range;
  int marine_hp;           
  int marine_armor;            

  int marine_max_speed;
  
  int marine_cost;   
  int marine_training_time;    
  int marine_atk_value;    

  // -------------
         
  int mineral_patch_radius;
  int mineral_patch_sight_range;
  int mineral_patch_hp;     
  int mineral_patch_armor;  

  int mineral_patch_capacity;
     
  // -------------

  int worker_radius;      
  int worker_sight_range;
  int worker_atk_range;
  int worker_hp;            
  int worker_armor;    
  int worker_atk_value;      

  int worker_max_speed;
  
  int worker_cost;    
  int worker_training_time;     
  int worker_mining_time; 
  int worker_mineral_capacity;

  void serialize(std::ostream &os)
  {    
    os << "width="                    << width
       << ",height="                  << height
       
       << ",base_radius="             << base_radius
       << ",base_sight_range="        << base_sight_range
       << ",base_atk_range="          << base_atk_range
       << ",base_hp="                 << base_hp
       << ",base_armor="              << base_armor
       << ",base_cost="               << base_cost
       << ",base_build_time="         << base_build_time
       << ",base_atk_value="          << base_atk_value
                                               
       << ",marine_radius="           << marine_radius
       << ",marine_sight_range="      << marine_sight_range
       << ",marine_atk_range="        << marine_atk_range
       << ",marine_hp="               << marine_hp
       << ",marine_armor="            << marine_armor
       << ",marine_max_speed="        << marine_max_speed
       << ",marine_cost="             << marine_cost
       << ",marine_training_time="    << marine_training_time   
       << ",marine_atk_value="        << marine_atk_value    

       << ",mineral_patch_radius="       << mineral_patch_radius
       << ",mineral_patch_sight_range="  << mineral_patch_sight_range
       << ",mineral_patch_hp="           << mineral_patch_hp
       << ",mineral_patch_armor="        << mineral_patch_armor
       << ",mineral_patch_capacity="     << mineral_patch_capacity                                               
       
       << ",worker_radius="           << worker_radius
       << ",worker_sight_range="      << worker_sight_range
       << ",worker_atk_range="        << worker_atk_range
       << ",worker_hp="               << worker_hp
       << ",worker_armor="            << worker_armor
       << ",worker_max_speed="        << worker_max_speed      
       << ",worker_cost="             << worker_cost            
       << ",worker_training_time="    << worker_training_time
       << ",worker_atk_value="        << worker_atk_value
       << ",worker_mining_time="      << worker_mining_time
       << ",worker_mineral_capacity=" << worker_mineral_capacity
      ;
  }
    
  void deserialize(const std::string & str)
  {
    std::vector<std::string> parm_parts;     
    splitup(parm_parts, str, ",");
    
    FORALL(parm_parts, iter)
    {
      std::string part = *iter;       
      std::vector<std::string> keyval;
      
      splitup(keyval, part, "=");
      
      std::string key = keyval[0];
      std::string val = keyval[1];
      
      //std::cout << "deserializing, key=" << key << ", val=" << val << std::endl; 

      if      (key == "width")              width = to_int(val);
      else if (key == "height")             height = to_int(val);
      
      else if (key == "base_radius")        base_radius = to_int(val); 
      else if (key == "base_sight_range")   base_sight_range = to_int(val); 
      else if (key == "base_atk_range")     base_atk_range = to_int(val); 
      else if (key == "base_hp")            base_hp = to_int(val); 
      else if (key == "base_armor")         base_armor = to_int(val); 
      else if (key == "base_cost")          base_cost = to_int(val); 
      else if (key == "base_build_time")    base_build_time = to_int(val); 
      else if (key == "base_atk_value")     base_atk_value = to_int(val); 

      else if (key == "marine_radius")          marine_radius = to_int(val); 
      else if (key == "marine_sight_range")     marine_sight_range = to_int(val); 
      else if (key == "marine_atk_range")       marine_atk_range = to_int(val); 
      else if (key == "marine_hp")              marine_hp = to_int(val); 
      else if (key == "marine_armor")           marine_armor = to_int(val); 
      else if (key == "marine_cost")            marine_cost = to_int(val); 
      else if (key == "marine_training_time")   marine_training_time = to_int(val); 
      else if (key == "marine_atk_value")       marine_atk_value = to_int(val); 
      
      else if (key == "mineral_patch_radius")       mineral_patch_radius = to_int(val); 
      else if (key == "mineral_patch_sight_range")  mineral_patch_sight_range = to_int(val); 
      else if (key == "mineral_patch_hp")           mineral_patch_hp = to_int(val); 
      else if (key == "mineral_patch_armor")        mineral_patch_armor = to_int(val); 
      else if (key == "mineral_patch_capacity")     mineral_patch_capacity = to_int(val); 

      else if (key == "worker_radius")            worker_radius = to_int(val); 
      else if (key == "worker_sight_range")       worker_sight_range = to_int(val); 
      else if (key == "worker_atk_range")         worker_atk_range = to_int(val); 
      else if (key == "worker_hp")                worker_hp = to_int(val); 
      else if (key == "worker_armor")             worker_armor = to_int(val); 
      else if (key == "worker_cost")              worker_cost = to_int(val); 
      else if (key == "worker_training_time")     worker_training_time = to_int(val); 
      else if (key == "worker_atk_value")         worker_atk_value = to_int(val); 
      else if (key == "worker_mining_time")       worker_mining_time = to_int(val); 
      else if (key == "worker_mineral_capacity")  worker_mineral_capacity = to_int(val); 
    
    }
    
  }
  
  MiniGameParameters()
  {
    width                   = 20*FINE_POINTS;
    height                  = 20*FINE_POINTS;
    mineral_patches         = 10;

    // -------------

    base_radius             = FINE_POINTS;
    base_sight_range        = 4*FINE_POINTS;
    base_atk_range          = 2*FINE_POINTS;
    base_hp                 = 50;
    base_armor              = 0;
    
    base_cost               = 400;
    //base_build_time         = 10*FPS; 
    base_build_time         = 10;
    base_atk_value          = 2;

    // -------------
    
    marine_radius           = FINE_POINTS/4;
    marine_sight_range      = 4*FINE_POINTS;
    marine_atk_range        = 3*FINE_POINTS;
    marine_hp               = 50;
    marine_armor            = 0;

    marine_max_speed        = FINE_POINTS/8;
    
    marine_cost             = 50;
    //marine_training_time    = 5*FPS;
    marine_training_time    = 5;
    marine_atk_value        = 3;

    // -------------
    
    mineral_patch_radius      = FINE_POINTS;
    mineral_patch_sight_range = 0;
    mineral_patch_hp          = 1;
    mineral_patch_armor       = 1000000000; // invincible

    mineral_patch_capacity    = 100;

    // -------------
    
    worker_radius             = FINE_POINTS/4;
    worker_sight_range        = 4*FINE_POINTS;
    worker_atk_range          = 1*FINE_POINTS;
    worker_hp                 = 50;
    worker_armor              = 0;

    worker_max_speed          = FINE_POINTS/8;    
    
    worker_cost               = 40;
    //worker_training_time      = 4*FPS;
    worker_training_time      = 4;
    //worker_mining_time        = 3*FPS;
    worker_mining_time        = 3;
    worker_mineral_capacity   = 10;
    worker_atk_value            = 1;
  }
  
};


struct MiniPlayerData
{
  int minerals;
  int bases;

  MiniPlayerData()
  {
    minerals = 1000;
    bases = 0;
  }
  
  void serialize(std::ostream &os)
  {
    os << "minerals=" << minerals;
  }
  
  void deserialize(std::string str)
  {
    std::vector<std::string> parts; 
    boost::split(parts, str, boost::is_any_of("="));
    
    std::string key = parts[0];
    std::string val = parts[1];
    
    if (key == "minerals")     minerals = to_int(val);
  }
};


class MiniGameState : public StateImpl<MiniGameParameters, MiniPlayerData, MiniGameState>
{
  bool p0_base_constructed; 
  bool p1_base_constructed;  
  
public:
  
  MiniGameState() { 
    p0_base_constructed = false; 
    p1_base_constructed = false;
  }

  bool finished() const;

  int score(int player) const;

  void object_setup();
  
  void setMPstr(const std::string& mpstr);
  
  virtual GameObj<MiniGameState>* new_game_object(const std::string & type); 

  virtual GameObj<MiniGameState>* getorcreate_game_object(int id, int player, const std::string & type, bool & newobj); 

  int encode_view_rlg(int player, int* & array, int attrs_per_unit);  
  
  int check_win();
  
  friend class Base;
  friend class MineralPatch;
  friend class Worker;
  friend class Marine;
};

#endif

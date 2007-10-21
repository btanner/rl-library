#ifndef Worker_H
#define Worker_H

#include "GameObj.H"
#include "MiniGameState.H"

class Worker : public MobileObj<MiniGameState>
{
public:

  int mineral_patch_intersection_time; // #steps worker close to mineral patch ("mining")
  int carried_minerals;
  int tick_base_built; 
  
  int attack_value;
  int attack_range;  

  Worker(MiniGameState *s) : MobileObj<MiniGameState>(s)
  {
    MiniGameState::GP &gp = state->gp;
    
    radius      = gp.worker_radius;
    sight_range = gp.worker_sight_range;
    hp          = gp.worker_hp;
    armor       = gp.worker_armor;
    max_hp      = gp.worker_hp;

    max_speed   = gp.worker_max_speed;
    is_moving   = false;
    
    mineral_patch_intersection_time = 0;
    carried_minerals = 0;
    tick_base_built = 0;
    
    attack_range = gp.worker_atk_range;
    attack_value = gp.worker_atk_value;
  }

  virtual void serialize_members(bool allied_view, std::ostream &os) const
  {
    MobileObj<MiniGameState>::serialize_members(allied_view, os);
    os << ",carried_minerals=" << carried_minerals;
  }

  
  virtual bool deserialize_member(const std::string & key, const std::string & val)
  {
    if (MobileObj<MiniGameState>::deserialize_member(key, val))
        return true; 
      
    if      (key == "workero")               owner = to_int(val);
    if      (key == "carried_minerals")      carried_minerals = to_int(val);
    else return false;
    return true;   
  }
  
  void execute();
  const std::string &get_type() const;
};

#endif

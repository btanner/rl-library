#ifndef Marine_H
#define Marine_H

#include "GameObj.H"
#include "MiniGameState.H"

class Marine : public MobileObj<MiniGameState>
{
public:
  
  int attack_value;
  int attack_range;
  
  Marine(MiniGameState *s) : MobileObj<MiniGameState>(s)
  {
    MiniGameState::GP &gp = state->gp;
    
    radius      = gp.marine_radius;
    sight_range = gp.marine_sight_range;
    hp          = gp.marine_hp;
    armor       = gp.marine_armor;
    max_hp      = gp.marine_hp; 

    max_speed   = gp.marine_max_speed;
    is_moving   = false;
    
    attack_value = state->gp.marine_atk_value;
    attack_range = state->gp.marine_atk_range;
  }

  virtual bool deserialize_member(const std::string & key, const std::string & val)
  {
    if (MobileObj<MiniGameState>::deserialize_member(key, val))
        return true; 
      
    if      (key == "marineo")      owner = to_int(val);
    else return false;
    return true;   
  }

  void execute();
  const std::string &get_type() const;
};

#endif

#ifndef Base_H
#define Base_H

#include "GameObj.H"
#include "MiniGameState.H"

class Base : public GameObj<MiniGameState>
{
public:

  int tick_worker_ready; // != 0: training worker in progress, tick = this value: new worker
  int tick_marine_ready; // != 0: training marine in progress, tick = this value: new marine
  
  int attack_value;
  int attack_range;

  Base(MiniGameState *s) : GameObj<MiniGameState>(s)
  {
    MiniGameState::GP &gp = state->gp;
    
    radius      = gp.base_radius;
    sight_range = gp.base_sight_range;
    hp          = gp.base_hp;
    armor       = gp.base_armor;
    max_hp      = gp.base_hp; 
    
    tick_worker_ready = tick_marine_ready = 0;
    
    attack_range = gp.base_atk_range;
    attack_value = gp.base_atk_value;    
  }
  
  void execute();
  const std::string &get_type() const;
};

#endif

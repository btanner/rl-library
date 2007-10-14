#ifndef MineralPatch_H
#define MineralPatch_H

#include "GameObj.H"
#include "MiniGameState.H"

class MineralPatch : public GameObj<MiniGameState>
{
public:

  int minerals_left;

  MineralPatch(MiniGameState *s) : GameObj<MiniGameState>(s)
  {
    MiniGameState::GP &gp = state->gp;
    
    radius      = gp.mineral_patch_radius;
    sight_range = gp.mineral_patch_sight_range;
    hp          = gp.mineral_patch_hp;
    armor       = gp.mineral_patch_armor;

    minerals_left = state->gp.mineral_patch_capacity;
  }

  void execute();
  const std::string &get_type() const;
};

#endif

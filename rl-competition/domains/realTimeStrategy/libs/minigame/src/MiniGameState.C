#include "MiniGameState.H"
#include "Worker.H"
#include "Marine.H"
#include "Base.H"
#include "MineralPatch.H"

using namespace std;

bool MiniGameState::finished() const { return false; }

int MiniGameState::score(int /*player*/) const { return 0; }

void MiniGameState::object_setup()
{
  Worker *w = new Worker(this);
  new_obj(w, 0);
  w->x = 100;
  w->y = 50;
  
  w = new Worker(this);
  new_obj(w, 1);
  w->x = 50;
  w->y = 100;

  MineralPatch *mp = new MineralPatch(this);
  new_obj(mp, 2);
  mp->x = 50;
  mp->y = 50;
}

GameObj<MiniGameState>* MiniGameState::new_game_object(const std::string & type)
{
  //std::cout << "calling minigame new_game_object" << std::endl; 
  
  if      (type == "worker")          return new Worker(this); 
  else if (type == "marine")          return new Marine(this);
  else if (type == "base")            return new Base(this);
  else if (type == "mineral_patch")   return new MineralPatch(this);
  
  return 0;
}


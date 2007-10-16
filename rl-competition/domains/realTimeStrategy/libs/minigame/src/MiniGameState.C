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
  int width = gp.width, height = gp.height; 
  int mid_x = width/2, mid_y = height/2;
  
  Worker *w = new Worker(this);
  new_obj(w, 0);  
  w->x = rand() % width;
  w->y = rand() % height;
  
  w = new Worker(this);
  new_obj(w, 1);
  w->x = rand() % width;
  w->y = height-5;

  MineralPatch *mp = new MineralPatch(this);
  new_obj(mp, 2);
  mp->x = mid_x;
  mp->y = mid_y;
}

GameObj<MiniGameState>* MiniGameState::new_game_object(const std::string & type)
{
  //std::cout << "calling minigame new_game_object" << std::endl; 
  GameObj<MiniGameState>* ptr = 0;
  
  if (type == "worker")
  {
    ptr = new Worker(this);
    ptr->max_hp = gp.worker_hp;
  }
  else if (type == "marine") {
    ptr = new Marine(this);
    ptr->max_hp = gp.marine_hp;
  }
  else if (type == "base") {
    ptr = new Base(this);
    ptr->max_hp = gp.base_hp;
  }
  else if (type == "mineral_patch") {
    ptr = new MineralPatch(this);
    ptr->max_hp = gp.mineral_patch_hp;
  }
  
  return ptr;
}


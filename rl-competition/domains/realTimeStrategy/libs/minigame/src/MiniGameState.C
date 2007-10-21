
#include "MiniGameState.H"
#include "Worker.H"
#include "Marine.H"
#include "Base.H"
#include "MineralPatch.H"

#include <vector>
#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/split.hpp>

using namespace std;


bool MiniGameState::finished() const { return false; }

int MiniGameState::score(int /*player*/) const { return 0; }

void MiniGameState::object_setup()
{
  int width = gp.width, height = gp.height; 
  //int mid_x = width/2, mid_y = height/2;
  
  Worker *w = new Worker(this);
  new_obj(w, 0);  
  w->x = rand() % width;
  w->y = rand() % height;
  
  w = new Worker(this);
  new_obj(w, 1);
  w->x = rand() % width;
  w->y = height-5;

  for (int i = 0; i < gp.mineral_patches; i++)
  {
    MineralPatch *mp = new MineralPatch(this);
    new_obj(mp, 2);
    mp->x = rand() % width;
    mp->y = rand() % height;
  }
}

void MiniGameState::setMPstr(const string& mpstr)
{
  vector<string> parts;
  boost::split(parts, mpstr, boost::is_any_of("-"));
  
  for (unsigned int i = 0; i < parts.size(); i++)
  {
    int x = to_int(parts[i]);
    i++;
    int y = to_int(parts[i]); 
    
    MineralPatch *mp = new MineralPatch(this);
    new_obj(mp, 2);
    mp->x = x;
    mp->y = y;    
  }
  
  apply_new_objs();
}


GameObj<MiniGameState>* MiniGameState::new_game_object(const std::string & type)
{
  //std::cout << "calling minigame new_game_object" << std::endl; 
  GameObj<MiniGameState>* ptr = 0;
  
  if (type == "worker")
    ptr = new Worker(this);
  else if (type == "marine") 
    ptr = new Marine(this);
  else if (type == "base") 
    ptr = new Base(this);
  else if (type == "mineral_patch") 
    ptr = new MineralPatch(this);
  
  return ptr;
}


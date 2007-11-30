
#include "Global.H"
#include "GameObj.H"
#include "MiniGameState.H" 
#include "Player.H"
#include "RLComp08Bot2.H"

#include <stdlib.h>
#include <time.h>

#include <map>
#include <string> 
#include <sstream>
#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/trim.hpp>

using namespace std;

#define SGN(x)          ((x) < 0 ? (-1) : 1)
#define ABS(x)          ((x) < 0 ? (-(x)) : (x))

//static bool debug = false; 

RLComp08Bot2::RLComp08Bot2(int num)
  : Player::Player(num)
{
  time = 0; 
}

RLComp08Bot2::~RLComp08Bot2()
{
}

void RLComp08Bot2::preprocess()
{
  // check for any new ones
  FORALL(vmps, iter)
  {
    MineralPatch* mpPtr = (MineralPatch*)(*iter);
    pair<int,int> coord; 
    coord.first = mpPtr->x; 
    coord.second = mpPtr->y;
    mpinfo[coord] = mpPtr->minerals_left;
  }
  
  // find closest MP
  
  double min_dist = 1000000000;  
  
  FORALL(mpinfo, iter) 
  {
    pair<int,int> where = (*iter).first; 
    double d = dist(where.first, where.second, base_x, base_y); 
    if (d < min_dist) {
      cmp_x = where.first;
      cmp_y = where.second;
      min_dist = d;
    }
  }
  
  // count workers + marines
  my_workers = 0;
  my_marines = 0;
  
  FORALL(my_vobjs, iter)
  {
    GameObj<MiniGameState> * objPtr = *iter;
    
    if (objPtr->get_type() == "worker")
      my_workers++;    
    else if (objPtr->get_type() == "marine")
      my_marines++;
  }
}

double RLComp08Bot2::dist(GameObj<MiniGameState>* a, GameObj<MiniGameState>* b)
{
  int x1 = a->x, y1 = a->y, x2 = b->x, y2 = b->y;
  return sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)); 
}

double RLComp08Bot2::dist(int x1, int y1, int x2, int y2)
{
  return sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
}



// Does not build state from the view -- set_state is called externally
string RLComp08Bot2::receive_actions(string view)
{
  time++;
  minerals = statePtr->player_infos[playerNum].pd.minerals;

  return "";
}


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

// Does not build state from the view -- set_state is called externally
string RLComp08Bot2::receive_actions(string view)
{
  time++;
  minerals = statePtr->player_infos[playerNum].pd.minerals;

  return "";
}

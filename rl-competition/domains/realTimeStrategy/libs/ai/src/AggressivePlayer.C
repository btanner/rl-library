
#include "GameObj.H"
#include "MiniGameState.H" 
#include "Worker.H" 
#include "Player.H"
#include "AggressivePlayer.H"

#include <stdlib.h>
#include <time.h>

#include <map>
#include <string> 
#include <sstream>

using namespace std;

AggressivePlayer::AggressivePlayer(int num)
  : TestPlayer::TestPlayer(num)
{
}

AggressivePlayer::~AggressivePlayer()
{
  if (statePtr != 0)
     delete statePtr;  
}

void AggressivePlayer::commanderPlan()
{
}


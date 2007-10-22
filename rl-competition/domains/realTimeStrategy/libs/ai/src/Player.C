
#include "GameObj.H" 
#include "MiniGameState.H" 
#include "Player.H"
#include "Worker.H" 

#include <map>
#include <string> 
#include <sstream>

using namespace std; 

Player::Player() 
{
  statePtr = 0; 
  do_not_free = false;
}

Player::Player(int num) 
{
  statePtr = 0; 
  playerNum = num; 
}

Player::~Player()
{
  if (statePtr != 0 && !do_not_free)
    delete statePtr;
}

string Player::rnd_move_action(int objId, MiniGameParameters& parms, int speed) 
{
  int x = rand() % parms.width; 
  int y = rand() % parms.height; 

  ostringstream actionos;
  actionos << objId << " move " << x << " " << y << " " << speed;
  
  return actionos.str(); 
}

void Player::build_state(const string & view)
{
  //if (statePtr != 0)
  //  delete statePtr; 
  if (statePtr == NULL)
    statePtr = new MiniGameState; 
  
  statePtr->decode_view(playerNum, view);
}


void Player::set_state(MiniGameState * stateptr)
{
  statePtr = stateptr; 
  do_not_free = true;
}


string Player::compose_action(int id, const std::string& act)
{
  ostringstream oss; 
  oss << id << " " << act; 
  return oss.str(); 
}

std::string Player::compose_move_action(int id, int x, int y, int speed)
{
  ostringstream oss; 
  oss << id << " move " << x << " " << y << " " << speed; 
  return oss.str(); 
}

bool Player::onMap(GameObj<MiniGameState>* ptr, MiniGameParameters & parms)
{ 
  return (   ptr->x >= 0 && ptr->y >= 0 
          && ptr->x < parms.width
          && ptr->y < parms.height);  
}



/****** Null Player ******/
 
NullPlayer::NullPlayer(int num)
{
  name = "NullPlayer";
  statePtr = 0; 
  playerNum = num;   
}

NullPlayer::~NullPlayer()
{  
}

string NullPlayer::receive_actions(string view, MiniGameParameters& parms)
{
  return "";
}


// Other players go down here: 

RandomPlayer::RandomPlayer(int num)
{
  name = "RandomPlayer";
  statePtr = 0; 
  playerNum = num;   
}

RandomPlayer::~RandomPlayer()
{
  if (statePtr != 0)
     delete statePtr;  
}

string RandomPlayer::receive_actions(string view, MiniGameParameters& parms)
{
  build_state(view); 
  
  // fill the vector will strings of actions
  // eg. actions: 
  //   [objId] move [x] [y] [speed] 
  
  vector<string> actions;

  cout << "RP" << playerNum << ": Iterating through objects" << endl; 
  
  FORALL(statePtr->all_objs, iter)
  {
    GameObj<MiniGameState> * objPtr = (*iter);
    ostringstream oss;
    objPtr->serialize(true, oss); 
    
    int objId = objPtr->view_ids[playerNum];
    
    if (objPtr->owner == playerNum && objPtr->get_type() == "worker")
    {
      Worker* workerPtr  = (Worker*)objPtr;
      
      cout << "  found worker, id=" << objId << " : " << oss.str() << endl;
      
      string act = rnd_move_action(objId, parms, workerPtr->max_speed); 
      cout << "action is " << act << endl; 
          
      actions.push_back(act); 
    }
  }
  
  string actionstr = join(actions, "#");
  
  return actionstr;
}


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
  parmsPtr = 0;
  do_not_free = false;
}

Player::Player(int num) 
{
  statePtr = 0;
  parmsPtr = 0;
  playerNum = num; 
}

Player::~Player()
{
  if (statePtr != 0 && !do_not_free)
    delete statePtr;
  
  // parms are se externally either way, so no freeing needed
}

string Player::rnd_move_action(int objId, int speed) 
{
  int x = rand() % parmsPtr->width; 
  int y = rand() % parmsPtr->height; 

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

void Player::set_parms(MiniGameParameters * parmsptr)
{
  parmsPtr = parmsptr;
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

std::string Player::compose_attack_action(int id, int target_id)
{
  ostringstream oss; 
  oss << id << " attack " << target_id; 
  return oss.str(); 
}


bool Player::onMap(GameObj<MiniGameState>* ptr)
{ 
  return (   ptr->x >= 0 && ptr->y >= 0 
          && ptr->x < parmsPtr->width
          && ptr->y < parmsPtr->height);  
}



/****** Null Player ******/
 
NullPlayer::NullPlayer(int num)
{
  name = "NullPlayer";
  statePtr = 0; 
  parmsPtr = 0;
  playerNum = num;   
}

NullPlayer::~NullPlayer()
{  
}

string NullPlayer::receive_actions(string view)
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

string RandomPlayer::receive_actions(string view)
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
      
      string act = rnd_move_action(objId, workerPtr->max_speed); 
      cout << "action is " << act << endl; 
          
      actions.push_back(act); 
    }
  }
  
  string actionstr = join(actions, "#");
  
  return actionstr;
}

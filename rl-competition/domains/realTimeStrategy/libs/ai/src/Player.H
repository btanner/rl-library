#ifndef PLAYER_H
#define PLAYER_H

#include "MiniGameState.H"
#include "Worker.H"
#include "Marine.H"
#include "Base.H"

#include <map>
#include <string>

class Player
{
protected:
  std::string name;
  std::string view;
  MiniGameState* statePtr;
  int playerNum;
  
  void build_state(const std::string & view);
  
  std::string compose_action(int id, const std::string& act);
  std::string compose_move_action(int id, int x, int y, int speed);
  bool onMap(GameObj<MiniGameState>* ptr, MiniGameParameters& parms); 
  
public: 
  Player(); 
  Player(int num); 
  virtual ~Player();
  
  void set_state(MiniGameState * stateptr); 
  
  virtual std::string receive_actions(std::string view, MiniGameParameters& parms)  = 0;  

  std::string rnd_move_action(int objId, MiniGameParameters& parms, int speed);
  
  
};

class NullPlayer : public Player
{
public:
  NullPlayer(int num); 
  virtual ~NullPlayer(); 
  virtual std::string receive_actions(std::string view, MiniGameParameters& parms);  
};

// Add player classes with diff policies here ...

class RandomPlayer : public Player
{
public:
  RandomPlayer(int num); 
  virtual ~RandomPlayer(); 
  virtual std::string receive_actions(std::string view, MiniGameParameters& parms);  
};


class TestPlayer : public Player
{
protected:
  bool init; 
  int time;   
  int done_base_time, done_marine_time, done_worker_time;
  bool have_base; 
  int base_x, base_y;
  int mp_x, mp_y; // mineral patch
  
  // Called individually in big loop
  virtual std::string chooseAction(int objId, Worker* workerPtr, 
                                   MiniGameState& state, MiniGameParameters& parms);
  
  virtual std::string chooseAction(int objId, Marine* marinePtr, 
                                   MiniGameState& state, MiniGameParameters& parms);
  
  virtual std::string chooseAction(int objId, Base* basePtr, 
                                   MiniGameState& state, MiniGameParameters& parms);
    
public:
  TestPlayer(int num); 
  virtual ~TestPlayer(); 
  virtual std::string receive_actions(std::string view, MiniGameParameters& parms);  
  
};



#endif


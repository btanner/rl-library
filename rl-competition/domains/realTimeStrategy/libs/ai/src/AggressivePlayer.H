#ifndef AGGRESSIVEPLAYER_H
#define AGGRESSIVEPLAYER_H

#include "GameObj.H"
#include "MiniGameState.H"
#include "Worker.H"
#include "Marine.H"
#include "Player.H"
#include "Base.H"
#include "Profiler.H"

#include <map>
#include <string>
#include <vector>

class AggressivePlayer : public Player
{
  bool init; 
  int time;   
  int done_base_time, done_marine_time, done_worker_time;
  bool have_base; 
  int base_x, base_y;
  int mp_x, mp_y; // mineral patch  
  
  std::map<int,GameObj<MiniGameState>* > scouts; 
  bool found_ob, found_mp; 
  int sc_x, sc_y; 
  int obx, oby;  
  int minerals;
  
  Profiler profiler; 
  
  void determineScouts(MiniGameParameters& parms);

  virtual std::string chooseAction(int objId, Worker* workerPtr, 
                                   MiniGameState& state, MiniGameParameters& parms);
  
  virtual std::string chooseAction(int objId, Marine* marinePtr, 
                                   MiniGameState& state, MiniGameParameters& parms);
  
  virtual std::string chooseAction(int objId, Base* basePtr, 
                                   MiniGameState& state, MiniGameParameters& parms);  

  virtual std::string chooseScoutingAction(int objId, MobileObj<MiniGameState>* objPtr, 
                                           MiniGameState& state, MiniGameParameters& parms);  
  
  
public:
  AggressivePlayer(int num); 
  virtual ~AggressivePlayer();
  
  virtual std::string receive_actions(std::string view, MiniGameParameters& parms);  
  
  // inherited
  //virtual std::string receive_actions(std::string view, MiniGameParameters& parms);  
    
  // chooseActions (defaults used, which are inherited)
  
  // Called individually in big loop

};

#endif

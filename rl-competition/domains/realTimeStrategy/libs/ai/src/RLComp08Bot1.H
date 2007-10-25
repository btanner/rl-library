#ifndef RLCOMP08BOT1_H
#define RLCOMP08BOT1_H

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
#include <sstream> 

class RLComp08Bot1 : public Player
{
  bool init; 
  int time;   
  int done_base_time, done_marine_time, done_worker_time;
  bool have_base; 
  int base_x, base_y;
  int mp_x, mp_y; // mineral patch  
  
  MiniGameParameters parms;   
  
  std::map<int,GameObj<MiniGameState>* > scouts; 
  bool found_ob, found_mp; 
  int sc_x, sc_y; 
  int obx, oby;  
  int minerals;
  
  Profiler profiler; 
  
  int phase; 
  
  void determineScouts();

  virtual std::string chooseAction(int objId, Worker* workerPtr, MiniGameState& state);
  
  virtual std::string chooseAction(int objId, Marine* marinePtr, MiniGameState& state);
  
  virtual std::string chooseAction(int objId, Base* basePtr, MiniGameState& state);  

  virtual std::string chooseScoutingAction(int objId, MobileObj<MiniGameState>* objPtr, 
                                           MiniGameState& state);  
    
  std::string defaultAct(std::string view);   
  void phase1(std::ostringstream & actstream, MiniGameState& state); 
  
  
public:
  RLComp08Bot1(int num); 
  virtual ~RLComp08Bot1();
  
  virtual std::string receive_actions(std::string view);  
  
  // inherited
  //virtual std::string receive_actions(std::string view, MiniGameParameters& parms);  
    
  // chooseActions (defaults used, which are inherited)
  
  // Called individually in big loop

};

#endif

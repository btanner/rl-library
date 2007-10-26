#ifndef RLCOMP08BOT1_H
#define RLCOMP08BOT1_H

#include "GameObj.H"
#include "MiniGameState.H"
#include "Worker.H"
#include "Marine.H"
#include "MineralPatch.H" 
#include "Base.H" 
#include "Player.H"
#include "Profiler.H"

#include <map>
#include <string>
#include <vector>
#include <sstream> 

#define ADD_ACTION(oss, str)        (oss) << (str) << "#"         

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
  int start_build_base_time; 
  MineralPatch * targetmp; 
  int cmp_x, cmp_y; 
  int my_workers, my_marines; 
  int training_end_time;
  int next_guard_post;
  int p3_x, p3_y;
  std::vector<GameObj<MiniGameState>* > my_vobjs;   // visible objs only 
  std::vector<GameObj<MiniGameState>* > opp_vobjs;  // visible objs only 
  std::vector<GameObj<MiniGameState>* > vmps;       // visible mps only
  std::map<std::pair<int,int>, int> mpinfo;
  std::map<int, std::pair<int, int> > guardposts; 
  
  void determineScouts();
  void computeVisible(); 

  virtual std::string chooseAction(int objId, Worker* workerPtr, MiniGameState& state);
  
  virtual std::string chooseAction(int objId, Marine* marinePtr, MiniGameState& state);
  
  virtual std::string chooseAction(int objId, Base* basePtr, MiniGameState& state);  

  virtual std::string chooseScoutingAction(int objId, MobileObj<MiniGameState>* objPtr, 
                                           MiniGameState& state); 
  
  double dist(int x1, int y1, int x2, int y2);
  double dist(GameObj<MiniGameState>* a, GameObj<MiniGameState>* b);
    
  std::string defaultAct(std::string view);  
  
  bool a_sees_loc(GameObj<MiniGameState>* obj, int x, int y, int radius); 
  void check_mp_gone();
  void set_gather(std::ostringstream & actstream, MiniGameState& state, Worker* worker);
  void set_roam(std::ostringstream & actstream, MiniGameState& state, MobileObj<MiniGameState>* obj);
  void set_onguard(std::ostringstream & actstream, MiniGameState& state, Marine* marine);
  
  void preprocess(); 
  void bounds_fix(int * x, int * y); 
  void phase1(std::ostringstream & actstream, MiniGameState& state); 
  void phase2(std::ostringstream & actstream, MiniGameState& state); 
  void phase3(std::ostringstream & actstream, MiniGameState& state); 
  
  
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

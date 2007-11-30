
#ifndef RLCOMP08BOT2_H
#define RLCOMP08BOT2_H

/* 
 * Note: a lot of code is simply copied from Bot1
 * Should fix this in the future: use inheritance to avoid code duplication. 
 */

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

class RLComp08Bot2 : public Player
{
  int time; 
  int minerals; 
  int my_workers, my_marines, my_patrollers;
  int my_whunters, my_aggressors, my_scouts, my_guards; 
  int base_x, base_y;
  int cmp_x, cmp_y; // closest mineral patch
  int scout_x, scout_y; 
  int phase; 
  int start_build_base_time;
  int training_end_time;
  int patrol_type;
  int guardpos;
  int oppw_x, oppw_y; 
  int oppb_x, oppb_y, oppb_id;
  
  MineralPatch * targetmp;
  
  typedef enum unitstate 
  { NIL, WGOING, WMINING, WRETURN, MMPATROL1, MMPATROL2, WH, AG, SC, G, GG, GP } unit_state;
  
  // group types: 
  //   1 - mining
  
  typedef struct _group {
    int type;  
    int target_x, target_y;
    std::map<int, GameObj<MiniGameState>* > objs;
  } groupinfo;
    
  std::map<int, unit_state> unitstates;
  std::map<int, int> uid2gid;
  std::map<int, groupinfo> gid2info; 
  std::map<int, int> groupsizes; 
  std::map<int, double> grouprolls; 
  
  std::map<std::pair<int,int>, int> mpinfo;
  std::vector<GameObj<MiniGameState>* > my_vobjs;   // visible objs only 
  std::vector<GameObj<MiniGameState>* > opp_vobjs;  // opponent's visible objs only
  std::vector<GameObj<MiniGameState>* > vmps;       // visible mps only  

  void preprocess();
  void computeVisible();
  
  void subscribe(int id, GameObj<MiniGameState>* obj, int gid); 
  void unsubscribe(int id, int gid); 
  
  double dist(int x1, int y1, int x2, int y2);
  double dist(GameObj<MiniGameState>* a, GameObj<MiniGameState>* b);  
  bool a_sees_loc(GameObj<MiniGameState>* obj, int x, int y, int radius);
  void bounds_fix(int * x, int * y);  
  
  void set_work(std::ostringstream & actstream, MiniGameState& state, Worker* obj);  
  void set_mining_patrol(std::ostringstream & actstream, MiniGameState& state, Marine* obj);  
  void set_worker_hunt(std::ostringstream & actstream, MiniGameState& state, Marine* obj);  
  void set_aggressors(std::ostringstream & actstream, MiniGameState& state, Marine* obj);  
  void set_scout(std::ostringstream & actstream, MiniGameState& state, Marine* obj);  
  void set_guard(std::ostringstream & actstream, MiniGameState& state, Marine* obj);  
  void check_mp_gone();
  
  void phase1(std::ostringstream & actstream, MiniGameState& state);   
  void phase2(std::ostringstream & actstream, MiniGameState& state);   
  void phase3(std::ostringstream & actstream, MiniGameState& state);   
  
public:
  RLComp08Bot2(int num); 
  virtual ~RLComp08Bot2();
  
  virtual std::string receive_actions(std::string view);  
  
  // inherited
  //virtual std::string receive_actions(std::string view, MiniGameParameters& parms);  
    
  // chooseActions (defaults used, which are inherited)
  
  // Called individually in big loop

};

#endif

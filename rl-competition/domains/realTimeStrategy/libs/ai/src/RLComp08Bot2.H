
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
  int my_workers, my_marines; 
  int base_x, base_y;
  int cmp_x, cmp_y; // closest mineral patch

  std::map<std::pair<int,int>, int> mpinfo;  
  std::vector<GameObj<MiniGameState>* > my_vobjs;   // visible objs only 
  std::vector<GameObj<MiniGameState>* > vmps;       // visible mps only  

  void preprocess();
  
  double dist(int x1, int y1, int x2, int y2);
  double dist(GameObj<MiniGameState>* a, GameObj<MiniGameState>* b);    
  
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

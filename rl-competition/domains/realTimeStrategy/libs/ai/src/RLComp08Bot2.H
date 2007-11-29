
#ifndef RLCOMP08BOT2_H
#define RLCOMP08BOT2_H

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

#ifndef AGGRESSIVEPLAYER_H
#define AGGRESSIVEPLAYER_H

#include "MiniGameState.H"
#include "Worker.H"
#include "Marine.H"
#include "Player.H"
#include "Base.H"

#include <map>
#include <string>

class AggressivePlayer : public TestPlayer
{
  
public:
  AggressivePlayer(int num); 
  virtual ~AggressivePlayer();
  
  // inherited
  //virtual std::string receive_actions(std::string view, MiniGameParameters& parms);  
    
  virtual void commanderPlan();
  
  // chooseActions (defaults used)
};

#endif

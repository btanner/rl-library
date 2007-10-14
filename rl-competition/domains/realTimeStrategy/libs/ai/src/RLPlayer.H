#ifndef RLPlayer_H
#define RLPlayer_H

#include "MiniGameState.H"

class RLPlayer : public Player
{
  virtual std::string receive_actions(MiniGameState& state, MiniGameParameters& parms) 
  { return ""; }  
};

#endif

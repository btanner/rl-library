#ifndef ORTSLITE_H
#define ORTSLITE_H

#include "MiniGameState.H"

class OrtsLite
{
private:
  MiniGameState state; 
  //  Player* players[2]; 
  int id_count;

public:
  OrtsLite();
  virtual ~OrtsLite();

  int simulate_game();
  int get_next_id(); 
  void time_step(); 
};

#endif


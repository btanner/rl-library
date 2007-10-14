// $Id: OrtsLite.C 5582 2007-08-05 03:16:58Z mburo $

// ortslite test

// (c) Michael Buro, Marc Lanctot
// licensed under GPLv3

#include "OrtsLite.H"
//#include "Player.H"
//#include "RLPlayer.H"
#include "State.H"

#include <iostream>

using namespace std; 

#if 0
int main()
{
  //  FORS (r, 10) {
  //  REM2("Starting run", r+1);
  //  OrtsLite ortslite; 
  //  ortslite.simulate_game();

  return 0;
}
#endif

OrtsLite::OrtsLite()
{
  id_count = 0; 
}

OrtsLite::~OrtsLite()
{
}

int OrtsLite::get_next_id()
{
  int id = id_count; 
  id_count++;
  return id;
}

// Returns winner or -1 for undecided
int OrtsLite::simulate_game()
{
#if 0  
  players[0] = new NullPlayer; 
  players[1] = new RLPlayer;

  FORS(t, 100) {
    cout << "starting time step " << (t+1) << endl; 
    time_step();
  }

  delete players[0]; 
  delete players[1]; 
#endif
  
  return -1; 
}

void OrtsLite::time_step()
{
#if 0
  map<int, string> actions[2]; 

  players[0]->receive_actions(state, actions[0]);
  players[1]->receive_actions(state, actions[1]);

  // now handle the actions, update the state
#endif
}

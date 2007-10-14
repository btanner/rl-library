#include "Worker.H"
#include "State.H"
#include "Base.H"
#include "MineralPatch.H"

using namespace std;

static std::string type = "worker";

const std::string &Worker::get_type() const { return type; }

void Worker::execute()
{
  istringstream is(action);
  string op;
  is >> op;
  
  MiniGameState::PlayerInfo &pi = state->player_infos[owner];
  
  if (!!is) {

    // std::cout << "WORKER ACTION " << op << endl;
    
    if (op == "move") {

      // move x y speed

      string r = move_action(is);
      if (!r.empty()) 
        REM("execute worker action move: " << r << ", got: " << action);
      goto other_actions;

    } else if (op == "stop") {

      // cancel building action, stop motion
      
      is_moving = false;
      
      if (tick_base_built > 0) {
        
        // stop building, money back
        
        tick_base_built = 0;
        pi.pd.minerals += state->gp.base_cost;
        x = x_old;
      }
      
    } else if (op == "build_base") {

      // starts erecting a base
      
      if (off_map()) {
        REM("execute worker action: build, but off map");
        goto other_actions;
      }
      
      if (pi.pd.bases >= 1) {
        REM("already have a base");
        goto other_actions;
      }
      
      if (pi.pd.minerals >= state->gp.base_cost) {
        
        // pay up, start building, move worker off map
        
        pi.pd.minerals -= state->gp.base_cost;
        tick_base_built = state->gp.base_build_time + state->tick;
        x_old = x; // save x position
        x = -100; // off map
      }
      
    } else
      REM("execute worker action: illegal action : " << action);
  }
  
 other_actions:;

  // check for any bases that might be ready  
  if (state->tick == tick_base_built) {         
    // build it !

    tick_base_built = 0; 

    cout << "Building base!" << endl;
        
    Base * base = new Base(state);
    base->x = x_old;
    base->y = y; 
        
    state->new_obj(base, owner);
    pi.pd.bases++;
        
    // put the worker back
    x = x_old;     
  }
 
  if (off_map()) return;
    
  // automatic mining when close to mine / automatic release when close to base

  bool acted = false;
  
  FORALL (state->all_objs, i) {

    if ((*i)->get_type() == "mineral_patch" && carried_minerals < state->gp.worker_mineral_capacity) {

      // how much can I mine?
      int mineral_potential = state->gp.worker_mineral_capacity - carried_minerals;
      
      double dist2 = square((double)(*i)->x - x) + square((double)(*i)->y - y);
      if (dist2 <= square((double)(*i)->radius)) {
      
        // close to patch, wait a while
        
        MineralPatch *mp = dynamic_cast<MineralPatch*>(*i);
        cout << "Worker.C (mining) " << mp->minerals_left << " " << state->gp.worker_mineral_capacity << endl;
      
        if (++mineral_patch_intersection_time >= state->gp.worker_mining_time) {
        
          // mine
                    
          mineral_patch_intersection_time = 0;
          
          // only mine what I can afford to mine
          int mined_minerals = ::min(mp->minerals_left, mineral_potential);
          mp->minerals_left -= mined_minerals;
          carried_minerals += mined_minerals;
        }

        acted = true;
        break; // only one mining action per frame
      }
    }
  }

  if (!acted) {
    
    FORALL (state->all_objs, i) {
      
      if ((*i)->owner == owner && (*i)->get_type() == "base") {
        
        double dist2 = square((double)(*i)->x - x) + square((double)(*i)->y - y);
        if (dist2 <= square((double)(*i)->radius)) {
          
          // close to own base, drop minerals
          
          pi.pd.minerals += carried_minerals;
          carried_minerals = 0;
        }
      }
    }
  }

  
  advance();
}

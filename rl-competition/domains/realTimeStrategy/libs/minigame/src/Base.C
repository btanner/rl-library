#include "Base.H"
#include "Worker.H"
#include "Marine.H"

using namespace std;

static std::string type = "base";

const std::string &Base::get_type() const { return type; }

void Base::execute()
{
  istringstream is(action);
  string op;
  is >> op;
  
  MiniGameState::PlayerInfo &pi = state->player_infos[owner];
  
  // train worker done?

  if (tick_worker_ready > 0 && state->tick >= tick_worker_ready) {
    tick_worker_ready = 0;

    // create new worker

    Worker* worker = new Worker(state); 
    state->new_obj(worker, owner); // set other properties, too
    worker->x = x; 
    worker->y = y;
  }

  // train marine done?
    
  if (tick_marine_ready > 0 && state->tick >= tick_marine_ready) {
    tick_marine_ready = 0;
    
    // create new marine

    Marine* marine = new Marine(state);
    state->new_obj(marine, owner); // set other properties, too
    marine->x = x;
    marine->y = y; 
  }

  // no new action?
  //if (!is) return;
  bool acted = false;
  
  if (!is)
  {
    acted = false; 
  }
  else if (op == "stop") {

    // stops training (money back)

    if (tick_worker_ready) {
      tick_worker_ready = 0;
      pi.pd.minerals += state->gp.worker_cost;
    }
    if (tick_marine_ready) {
      tick_marine_ready = 0;
      pi.pd.minerals += state->gp.marine_cost;
    }
    
    acted = true;
  }
  else if (op == "train") {

    // train worker|marine
    
    string what;
    is >> what;
    if (!is) { REM("execute base action: train requires an argument"); return; }
      
    // pending actions?
      
    if (tick_worker_ready || tick_marine_ready)
    {
      acted = true;
      goto other_actions;
    }

    if (what == "worker" && pi.pd.minerals >= state->gp.worker_cost) {
      pi.pd.minerals -= state->gp.worker_cost;      
      tick_worker_ready = state->tick + state->gp.worker_training_time;
      
      acted = true; 
      goto other_actions;
    }
      
    if (what == "marine" && pi.pd.minerals >= state->gp.marine_cost) {
      pi.pd.minerals -= state->gp.marine_cost;
      tick_marine_ready = state->tick + state->gp.marine_training_time;
      
      acted = true;
      goto other_actions;
    }
      
    REM("execute base action: train failed, o=" << owner);
    //return;
  }

  other_actions:;
  
    if (!acted)
    {
      // attack closest enemy
    
      double min_dist = 100000000;
      GameObj<MiniGameState>* closest_enemy = 0;
      
      FORALL(state->all_objs, iter)
      {
        GameObj<MiniGameState>* obj = *iter;
      
        // don't attack your own units!
        if (obj->owner == owner)
          continue;
        
        double dist = square((double)obj->x - x) + square((double)obj->y - y);
        
        // check if in attack range
        
        if (dist <= square(obj->radius + attack_range)) {
          if (dist < min_dist) {
            min_dist = dist; 
            closest_enemy = obj;
          }
        }      
      }
    
      if (closest_enemy != 0) {
        if (attack_value > closest_enemy->armor) {
          closest_enemy->hp -= attack_value;
        }
      }
    }
  
  
}

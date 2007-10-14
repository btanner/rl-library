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
  if (!is) return;

  if (op == "stop") {

    // stops training (money back)

    if (tick_worker_ready) {
      tick_worker_ready = 0;
      pi.pd.minerals += state->gp.worker_cost;
    }
    if (tick_marine_ready) {
      tick_marine_ready = 0;
      pi.pd.minerals += state->gp.marine_cost;
    }

    return;
  }

  if (op == "train") {

    // train worker|marine
    
    string what;
    is >> what;
    if (!is) { REM("execute base action: train requires an argument"); return; }
      
    // pending actions?
      
    if (tick_worker_ready || tick_marine_ready)
      return;

    if (what == "worker" && pi.pd.minerals >= state->gp.worker_cost) {
      pi.pd.minerals -= state->gp.worker_cost;      
      tick_worker_ready = state->tick + state->gp.worker_training_time;
      return;
    }
      
    if (what == "marine" && pi.pd.minerals >= state->gp.marine_cost) {
      pi.pd.minerals -= state->gp.marine_cost;
      tick_marine_ready = state->tick + state->gp.marine_training_time;
      return;
    }
      
    REM("execute base action: train failed");
    return;
  }

  REM("execute base action: illegal action " << op);
  return;
}

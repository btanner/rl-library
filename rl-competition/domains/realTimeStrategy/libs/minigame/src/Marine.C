#include "Marine.H"

using namespace std;

static std::string type = "marine";

const std::string &Marine::get_type() const { return type; }

void Marine::execute()
{
  istringstream is(action);
  string op;
  is >> op;

  MiniGameState::PlayerInfo &pi = state->player_infos[owner];
  
  bool acted = false;
  
  if (!!is) {

    if (op == "move") {

      // move x y speed

      string r = move_action(is);
      if (!r.empty()) 
        REM("execute marine action move: " << r << ", got: " << action);
      
    } else if (op == "stop") {

      // stops motion
      
      is_moving = false;
      
      
    } else if (op == "attack") {
      
      // attack obj
      // one shot
      
      MiniGameState::ObjId id;

      is >> id;
      if (!is) {
        REM("execute marine action: corrupt target id :" << action);
        goto other_actions;
      }

      // target known?
      
      FIND (pi.id2obj, i, id);
      if (i == pi.id2obj.end()) {
        REM("execute marine action: unknown target " << id);
        goto other_actions;
      }

      // target hull within range?

      double dist2 = square((double)i->second->x - x) + square((double)i->second->y - y);

      if (dist2 <= square(i->second->radius + attack_range)) {
        
        // yes! decrease hp of target if armor is insufficient

        if (attack_value > i->second->armor) {
          i->second->hp -= attack_value;
        }
      }
      
      acted = true;       
      
    } else
      REM("execute marine action: illegal action : " << action);
  }

  other_actions:;
  
    if (!acted)
    {
      // attack lowest enemy nearby
    
      double min_hp = 100000000;
      GameObj<MiniGameState>* lowest_enemy = 0;
      
      FORALL(state->all_objs, iter)
      {
        GameObj<MiniGameState>* obj = *iter;
      
        // don't attack yout own units or neutral units
        if (obj->owner == owner || obj->owner == 2)
          continue;
        
        double dist = square((double)obj->x - x) + square((double)obj->y - y);
        
        // check if in attack range
        
        if (dist <= square(obj->radius + attack_range)) {
          if (obj->hp < min_hp) {
            min_hp = obj->hp; 
            lowest_enemy = obj;
          }
        }      
      }
    
      if (lowest_enemy != 0) {
        if (attack_value > lowest_enemy->armor) {
          lowest_enemy->hp -= attack_value;
        }
      }
    }
      
    advance();
}

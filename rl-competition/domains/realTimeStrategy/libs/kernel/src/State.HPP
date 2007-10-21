// $Id: State.HPP 5881 2007-09-18 22:48:55Z lanctot $

// State.H implementation

// (c) Michael Buro
// licensed under GPLv3

#include "State.H"

#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/split.hpp>

// object registration

template <class GameParameters, class PlayerData, class State>
void StateImpl<GameParameters,PlayerData,State>::new_obj(GameObjBase *obj, int owner)
{
  obj->owner = owner;
  
  //if (!all_objs.insert(obj).second) ERR("State::new_obj: obj already there!");
  // check if already in vector
  if (all_objs.contains(obj)) 
    ERR("State::new_obj: obj already there!");
  
  new_objs.push_back(obj); 
}

template <class GameParameters, class PlayerData, class State>
void StateImpl<GameParameters,PlayerData,State>::delete_obj(GameObjBase *obj)
{
  if (all_objs.erase(obj) == 0) ERR("State::delete_obj: double delete!");

  // remove obj from all id2obj maps

  FORS (i, PLAYER_NUM) {
    ObjId id = obj->view_ids[i];

    if (id != 0) {
      if (player_infos[i].id2obj.erase(id) == 0) ERR("obj id not in id2obj map");
    }
  }
}

// needed by the client, for when they deserialize and add objects
template <class GameParameters, class PlayerData, class State>
void StateImpl<GameParameters,PlayerData,State>::clear_obj()
{
  FORALL (all_objs, iter)
    delete (*iter); 
    
  all_objs.clear(); 
}


// ------ driving the simulation

// initializes the state
template <class GameParameters, class PlayerData, class State>
void StateImpl<GameParameters,PlayerData,State>::init(const GameParameters &gp)
{
  // clean up from last run
  
  FORALL (all_objs, i)
    delete *i;

  all_objs.clear();

  FORALL (player_infos, i) i->clear();

  // setup new start state
  
  who_am_i = PLAYER_NUM; // server for now
  this->gp = gp;

  width  = gp.width;
  height = gp.height;

  tick = 0;
  
  object_setup();
  
  apply_new_objs();  
}

template <class GameParameters, class PlayerData, class State>
void StateImpl<GameParameters,PlayerData,State>::apply_new_objs()
{
  // add pending new objects
  FORALL (new_objs, i)
    all_objs.push_back(*i);  
  new_objs.clear();    
}

// execute actions from both players encoded as string

template <class GameParameters, class PlayerData, class State>
void StateImpl<GameParameters,PlayerData,State>
::execute_actions(const boost::array<std::string, PLAYER_NUM> &acts)
{
  // action string for player has form:
  //   a [ # a]*  where a=obj-id op [ param]*
  
  // clear all action slots

  FORALL (all_objs, j)
    (*j)->action.clear();

  // set new actions

  FORS (i, PLAYER_NUM) {

    // split action string into individual actions

    Vector<std::string> actions;
    
    split(actions, acts[i], boost::is_any_of("#"), boost::token_compress_on); 

    //DPR << "actions: " << acts[i] << std::endl;

    FORALL (actions, j) {

      // parse action

      if (j->empty()) continue;

      // first string is id
      
      int id;
      std::istringstream iss(*j);
      iss >> id;
      if (!iss) {
        REM("State::execute_actions: corrupt action: " << (*j));
        continue;
      }

      // collect remaining strings (the actual action)
      std::string act, s;

      FOREVER {
        iss >> s;
        if (!iss) break;
        act += ' ' + s;
      } 

      // find object in player map

      FIND(player_infos[i].id2obj, k, id);

      if (k == player_infos[i].id2obj.end()) {
        REM("State::execute_actions: obj does not exist : " << (*j));
        continue;
      }

      if (k->second->owner != i) {
        REM("State::execute_actions: not the owner : " << (*j));
      }

      k->second->action = act;
    }
  }

  // execute new actions / continue previous ones

  // randomize all objects

  Vector<GameObjBase*> all = all_objs;
  std::random_shuffle(all.begin(), all.end());

  FORALL (all, i) {
    //DPR << "execute action: " << (*i)->action << std::endl;
    (*i)->execute();
  }
}

// remove killed objects

template <class GameParameters, class PlayerData, class State>
void StateImpl<GameParameters,PlayerData,State>::remove_dead_objs()
{
  Vector<GameObjBase*> killed;
  
  FORALL (all_objs, i) {
    if ((*i)->dead()) {
      killed.push_back(*i);
    }
  }
  
  FORALL (killed, j) {
    delete_obj(*j);
  }
}


template <class GameParameters, class PlayerData, class State>
bool StateImpl<GameParameters,PlayerData,State>::a_sees_b(GameObjBase &a, GameObjBase &b)
{
  double d =
    square((double)(a.x - b.x)) +
    square((double)(a.y - b.y));
  
  //   a--srange--|-radius---b
  //
  //   dist(a,b) - radius(b) <= srange(a) <=> a sees b
  //
  //     <=>
  //
  //   dist(a,b)**2 <= ( radius(b)+srange(a) )**2
  //
  return d <= square(b.radius + a.sight_range);
}


// compute string that encodes player's view

/*
  view format:

    player-data # [obj ]* # [vanished-obj-id ]* # [dead-obj-id ]*

    where obj = N|K,type,id[,x=z]* (obj-members)
 
    N = new , K = previously known
*/

template <class GameParameters, class PlayerData, class State>
void StateImpl<GameParameters,PlayerData,State>::encode_view(int player, std::ostream &os)
{
  PlayerInfo &pi = player_infos[player];

  pi.pd.serialize(os);
  os << " # ";

  // fixme: speed this up if # of objects gets big

  Vector<GameObjBase*> visibleObjs;
  Vector<GameObjBase*> invisibleObjs;
  Vector<ObjId>        dead, vanished;
  Vector<GameObjBase*> playerObjs;

  // collect player objs
  
  FORALL (all_objs, i)
    if ((*i)->owner == player)
      playerObjs.push_back(*i);
  
  FORALL (all_objs, i) {

    bool visible = (*i)->owner == player; // visible if owner

    if (!visible) {

      // check if other player's object is visible by player

      FORALL (playerObjs, j) {

        if (a_sees_b(**j, **i))
        { visible = true; break; }
      }
    }
        
    if (visible)
      visibleObjs.push_back(*i);
    else 
      invisibleObjs.push_back(*i);
  }
    
  FORALL (visibleObjs, i) {

    char flag = ' '; 
      
    if ((*i)->view_ids[player] != 0) {
      flag = 'K';
    } else {
      // new objects assign id
      flag = 'N';
      ObjId id = (*i)->view_ids[player] = pi.next_obj_id(); // for next round
      // add id to map
      pi.id2obj[id] = *i;
    }

    os << flag
       << ',' << (*i)->get_type() 
       << ',' << (*i)->view_ids[player] 
       << ',';
    (*i)->serialize(player == (*i)->owner, os);
    os << ' ';
  } 
  
  FORALL (invisibleObjs, i) {
    
    ObjId &id = (*i)->view_ids[player];
    if (id != 0) {

      // remove id from map
      if (pi.id2obj.erase(id) == 0) {
        //DPR << id << " " << player << std::endl; 
        REM("id not in map");
      }
      id = 0; // obj no longer known

      // add to dead/vanished list

      if ((*i)->dead()) {
        dead.push_back(id);
      } else {
        vanished.push_back(id);
      }
    }
  }

  // write vanished/dead obj ids

  os << "# ";
  FORALL (vanished, i) os << *i << ' ';

  os << "# ";
  FORALL (dead, i) os << *i << ' ';
}




template <class GameParameters, class PlayerData, class State>
void StateImpl<GameParameters,PlayerData,State>
::simulation_step(const boost::array<std::string, PLAYER_NUM> &acts,
                  boost::array<std::string, PLAYER_NUM> &views)
{
  tick++;
  
  // add pending new objects
  apply_new_objs();
  
  // execute actions
  execute_actions(acts);

  // add pending new objects
  apply_new_objs();
  
  // compute views
  FORS (i, PLAYER_NUM) {
    std::ostringstream os;
    encode_view(i, os);
    views[i] = os.str();
  }
}


  // collect action string for player
template <class GameParameters, class PlayerData, class State>
void StateImpl<GameParameters,PlayerData,State>::collect_actions(int player, std::string &acts)
{
  std::ostringstream os;
  
  FORALL (all_objs, i) {
    if ((*i)->owner == player) {
      if (!(*i)->action.empty()) {
        os << "# " << (*i)->view_ids[player]
           << ' ' << (*i)->action;
      }
    }
  }

  acts = os.str();
}



// ------ client functions
  
/* 
  
  Decodes (AKA deserializes) the view. See encode_view for view format.
   
*/
template <class GameParameters, class PlayerData, class State>
std::string StateImpl<GameParameters,PlayerData,State>::decode_view(int player, const std::string &view)
{
  //clear_obj(); 
  
  //DPR << "decode_view, view = #" << view << "#" << std::endl;
  
  std::vector<std::string> view_parts; 
  splitup(view_parts, view, "#");
  
  // parts[0] = global info
  PlayerInfo &pi = player_infos[player];
  pi.pd.deserialize(trim(view_parts[0]));
  
  // parts[1] = active objs
  std::vector<std::string> active_objs;
  std::string part1 = trim(view_parts[1]);
  splitup(active_objs, part1, " ");
  
  FORALL(active_objs, obj_iter) {
    std::string entire_objstr = *obj_iter;
    
    //DPR << "state.hpp deserializing " << entire_objstr << std::endl;
    
    std::vector<std::string> obj_props;  
    splitup(obj_props, entire_objstr, ",");
    
    if (obj_props.size() <= 1)
      break;
    
    // not sure what to do with these
    std::string flag = obj_props[0]; 
    std::string type = obj_props[1];
    int id = to_int(obj_props[2]);
    
    std::string objstr = join(obj_props, ",", 3);
    GameObjBase * gameObj = new_game_object(type);
    gameObj->deserialize(objstr); 
        
    gameObj->view_ids[player] = id; 
    
    new_obj(gameObj, gameObj->owner);
  }
  
  // parts[2] = vanished
  
  // parts[3] = dead
  
  apply_new_objs();
  
  return "";
}

// encode actions for player (who_am_i), also clears action slots
template <class GameParameters, class PlayerData, class State>
std::string StateImpl<GameParameters,PlayerData,State>::encode_actions()
{
  return "";
}

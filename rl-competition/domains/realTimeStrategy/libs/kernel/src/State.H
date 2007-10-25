#ifndef STATE_H
#define STATE_H

// $Id: State.H 5878 2007-09-18 18:37:22Z lanctot $

// simple state/obj classes

// (c) Michael Buro, Marc Lanctot
// licensed under GPLv3

#include "Global.H"
#include "GameObj.H"

// contracts:
// GameParameters: serialize, width, height
// PlayerData:     serialize

template <class GameParameters, class PlayerData, class State> class StateImpl
{
public:

  static const int PLAYER_NUM = 2;

  typedef GameParameters GP;
  typedef PlayerData PD;
  typedef GameObj<State> GameObjBase;
  
  typedef uint4 ObjId;
  typedef std::map<ObjId, GameObjBase*> Id2Obj;

  // contains data specific to player

  struct PlayerInfo {

    PlayerData pd;
    int bases; 
    
    // internal
    Id2Obj id2obj; // for decoding ids in actions, view ids are stored in GameObj itself
    
    PlayerInfo() { obj_id = 0; }

    ObjId next_obj_id() { return ++obj_id; bases = 0; }

    void clear()
    {
      obj_id = 0;
      id2obj.clear();
    }
    
  private:
    ObjId obj_id; // id sent to player
  };
   
  int who_am_i;      // 0,1,...: player , PLAYER_NUMBER: server
  int width, height; // playfield size
  int tick;          // incremented in each simulation step

  Vector<GameObjBase*> all_objs; // owner
  Vector<GameObjBase*> new_objs; // objects to be added
  Vector<GameObjBase*> dead_objs; // objects to be added
  
  // [PLAYER_NUM]: neutral
  boost::array<PlayerInfo, PLAYER_NUM+1> player_infos;

  virtual ~StateImpl() { clear_obj(); }

public:

  // ------ server functions

  // initializes the state
  void init(const GameParameters &gp);

  // populates world with objects
  virtual void object_setup() = 0;
  
  // gets the "specification of the state", eg. global variables that aren't sent
  // every time, just initially
  std::string get_spec() const { std::ostringstream os; gp.serialize(os); return os.str(); }

  // compute string that encodes player's view
  
  void encode_view(int player, std::ostream &os);
  
  // execute one step
  void simulation_step(const boost::array<std::string, PLAYER_NUM> &acts,
                        boost::array<std::string, PLAYER_NUM> &views);

  void simulation_step(const boost::array<std::string, PLAYER_NUM> &acts,
                        boost::array<std::string, PLAYER_NUM> &views, int only_this_view);
  
  
  // collect action string for player
  void collect_actions(int player, std::string &acts);

  // game finished yet?
  virtual bool finished() const = 0;
  
  // call when game is finished
  virtual int score(int player) const = 0;

  // update score after a unit is killed
  virtual void update_score(GameObjBase* obj) = 0;

  // ------ client functions (not implemented yet)
  
  // decode view string
  std::string decode_view(int player, const std::string &view);

  // encode actions for player (who_am_i), also clears action slots
  std::string encode_actions();
  
  virtual GameObjBase* getorcreate_game_object(int id, int player, const std::string & type, bool & newobj); 
  
  bool is_visible(GameObjBase*, int player);
  
  void clear_obj();
  
  static bool a_sees_b(GameObjBase &a, GameObjBase &b);  
  
protected:
  
  // object registration

  void new_obj(GameObjBase *obj, int owner);
  
  // This second one does not compile
  void remove_obj(GameObjBase *obj); 
  //void delete_obj(GameObjBase* obj, Vector<GameObjBase*>::iterator & iter);
  
  void apply_new_objs();

  // for deserialization in client
  virtual GameObjBase* new_game_object(const std::string & type) = 0;
  
  // execute actions from both players encoded as string
  
  void execute_actions(const boost::array<std::string, PLAYER_NUM> &acts);
  
  // remove killed objects
  
  void remove_dead_objs();

  // game parameters

  GameParameters gp;

  
  // template <class X> friend class GameObj;
};

// implementation

#include "State.HPP"

#endif

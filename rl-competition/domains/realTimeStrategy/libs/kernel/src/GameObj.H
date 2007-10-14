#ifndef GameObj_H
#define GameObj_H

#include "Global.H"

#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/split.hpp>

template <class State> class GameObj
{
public:

  // serialized
  int owner;
  int x, y;
  int radius; // 0->point
  int sight_range;
  int hp;
  int armor;

  virtual void serialize_members(bool /*allied_view*/, std::ostream &os) const
  {
    os << "o="   << owner
       << ",x="  << x
       << ",y="  << y
       << ",r="  << radius
       << ",sr=" << sight_range     
       << ",hp=" << hp
       << ",armor=" << armor
      ;
  }
  
  virtual bool deserialize_member(const std::string & key, const std::string & val)
  {
    // baseo and mineral_patcho did not set owner properly in virtual override. why!?
    // no idea. so had to put them in here.
    
    if      (key == "o")                owner = to_int(val);
    else if (key == "x")                x = to_int(val);  
    else if (key == "y")                y = to_int(val);  
    else if (key == "r")                radius = to_int(val);  
    else if (key == "sr")               sight_range = to_int(val);  
    else if (key == "hp")               hp = to_int(val);  
    else if (key == "armor")            armor = to_int(val);
    else return false;
    return true;
  }
  
  // internal
  int x_old;  
  State *state; // to access global information such as player resources
                                  // initialize after construction!
  std::string action;

  // under which ids do players know this object?
  boost::array<typename State::ObjId, State::PLAYER_NUM> view_ids; // 0: was not known
  
  GameObj(State *s)
  {
    state = s;
    
    owner = 
      x =
      y =
      sight_range =
      hp =
      armor =
      -1;
        
    FORS(i, s->PLAYER_NUM)
      view_ids[i] = 0;     
  }

  virtual ~GameObj() { }

  // set action to be sent
  void set_action(const std::string &a) {
    // complain if action already set
    action = a;
  }
  
  // when erecting buildings, workers are place off map
  bool off_map() const { return x < 0; }

  // is obj dead?
  bool dead() const { return hp <= 0; }
  
  // type + data members sent to players
  void serialize(bool allied_view, std::ostream &os) const
  {
    //os << get_type();
    serialize_members(allied_view, os);
  }
  
  void deserialize(const std::string & str) {
    
    //std::cout << "deserializing " << get_type() << ": " << str << std::endl;
    
    std::vector<std::string> parts;
    boost::split(parts, str, boost::is_any_of(","));
    
    FORALL(parts, iter) {
      std::string part = *iter;
      
      std::vector<std::string> keyval;
      boost::split(keyval, part, boost::is_any_of("="));
      
      std::string key = keyval[0];
      std::string val = keyval[1];
      
      //std::cout << "deserializing [key=" << key << ", val=" << val << "]" << std::endl; 
      
      deserialize_member(key, val); 
    }
    
    //std::ostringstream oss;
    //serialize(true, oss);
    //std::cout << "deserializing done. final product is " << oss.str() << std::endl; 
  }
  
  // useful for debugging
  std::string to_string() {
    std::ostringstream oss; 
    serialize(true, oss);
    return oss.str();
  }  
  
  // return objects type (name)
  virtual const std::string &get_type() const = 0;

  // execute action
  virtual void execute() = 0;
};


template <typename State> class MobileObj : public GameObj<State>
{
public:

  // serialized
  int max_speed;
  bool is_moving;

  virtual void serialize_members(bool allied_view, std::ostream &os) const
  {
    GameObj<State>::serialize_members(allied_view, os);
    os << ",max_speed=" << max_speed
       << ",is_moving=" << is_moving;
  }

  virtual bool deserialize_member(const std::string & key, const std::string & val)
  {
    if (GameObj<State>::deserialize_member(key, val))
      return true; 
    
    if      (key == "max_speed")      max_speed = to_int(val);
    else if (key == "is_moving")      is_moving = to_bool(val);  
    else return false;
    
    return true;
  }
    
  // internal
  int x0, y0;     // move cmd. start location
  int x1, y1;     // move cm.d target location
  int speed;      
  double dx, dy;  // per tick
  int move_ticks; // to compute current x,y (rounded)
  int move_ticks_done; // arrived at x1,y1

  MobileObj(State *s) : GameObj<State>(s)
  {
    max_speed = 0;
    is_moving = false;
  }

  // return error message iff there is a problem
  
  std::string move_action(std::istream &is)
  {
    if (this->off_map()) {
      return "move action: object off map";
    }

    int mx, my, ms; // x,y,speed

    is >> mx >> my >> ms;

    //std::cout << "move " << mx << " " << my << " " << ms << std::endl; // !!!
    
    if (!!is &&
        mx >= 0 && mx < this->state->width &&
        my >= 0 && my < this->state->height &&
        ms >= 1) {

      x0 = this->x;  y0 = this->y;
      x1 = mx; y1 = my;

      //std::cout << "motion " << x0 << " " << y0 << " " << x1 << " " << y1 << std::endl; // !!!
      
      if (x1 == this->x && y1 == this->y) {

        is_moving = false;

      } else {

        //std::cout << "set in motion to " << x1 << " " << y1 << std::endl; // !!!

        is_moving = true;
        speed = std::min(ms, max_speed);
        double l = sqrt((x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));
        double inv = speed / l;
        dx = (x1-x0)*inv;
        dy = (y1-y0)*inv;
        move_ticks = 0;
        move_ticks_done = int(ceil(l / speed));
      }

    } else
      return "move action parameter problem";

    return "";
  }

  // step towards target location

  void advance()
  {
    if (is_moving) {
      
      // move along line to target location
      
      move_ticks++;

      if (move_ticks >= move_ticks_done) {
        // arrived
        this->x = x1;
        this->y = y1;
        is_moving = false;
      } else {
        // one step towards goal location
        int new_x = int(round(x0 + move_ticks * dx));
        int new_y = int(round(y0 + move_ticks * dy));
        
        if (new_x >= this->state->width || new_y >= this->state->height)
          is_moving = false;
        else {
          this->x = new_x;
          this->y = new_y;
        }
      }
    }
  }

};

#endif

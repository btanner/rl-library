// $Id: ol_main.C 5667 2007-08-14 02:42:38Z mburo $

// (c) Michael Buro
// licensed under GPLv3

#include "MiniGameState.H"
#include "SDL_GUI.H"
#include "SDL_init.H"
#include "Player.H"

using namespace std;

void tests(); 

typedef MobileObj<MiniGameState> MobObj;

int main()
{
#if 0

  vector<int> v;
  const int N = 1000;
  int count = 0;
  
  for (int i=0; i < N; i++) v.push_back(i);

  FORALL (v, i) {
    FORALL (v, j) {
      count++;
    }
  }

  cout << count << endl;
  exit(0);

#endif


  
  srandom(time(0));
  
  //tests(); 
  
  MiniGameState s;
  MiniGameParameters gp;
  
  TestPlayer p0(0); 
  p0.set_parms(&gp);
  
  TestPlayer p1(1);
  p1.set_parms(&gp);

  s.init(gp);

  SDL_init::video_init();
  SDL_GUI<MiniGameState> gui;

  std::map<std::string, SDL_GUI<MiniGameState>::Marker> markers;
  markers["worker"] = SDL_GUI<MiniGameState>::MARKER_H; // mark workers
  
  gui.init(1024, 768, s, markers);
  gui.display();

  
  // compute initial views
  
  boost::array<std::string, MiniGameState::PLAYER_NUM> views;

  FORS (i, MiniGameState::PLAYER_NUM) {
    ostringstream os;
    s.encode_view(i, os);
    views[i] = os.str();
  }

  // send them ...

  // run game

  boost::array<std::string, MiniGameState::PLAYER_NUM> acts;
  int time = 0; 
  
  do {
    time++;
    cout << endl << "## Time step " << time << endl;

    boost::array<std::string, MiniGameState::PLAYER_NUM> views;
  
    s.simulation_step(acts, views);

    gui.event();
    gui.display();

    // send views + receive actions ...

    acts[0] = p0.receive_actions(views[0]);
    acts[1] = p1.receive_actions(views[1]);        
    
    // create random move actions (now handled in Player objects)
    // these will be used for separate opponent policies.
    /*
    FORALL (s.all_objs, i) {
      MobObj *p = dynamic_cast<MobObj*>(*i);
      if (p && !p->is_moving) {
        int x = random() % s.width;
        int y = random() % s.height;
        ostringstream os;
        os << "move " << x << ' ' << y << ' ' << p->max_speed;
        p->set_action(os.str());
      } else {
        (*i)->set_action("");
      }
    }

    FORS (i, s.PLAYER_NUM) {
      s.collect_actions(i, acts[i]);
      //cout << i << " " << acts[i] << endl;
    }
    */

    gui.delay(125);
    
  } while (!gui.quit && !s.finished());
  
  return 0;
}

// Used for testing functions. Please ignore. 
void tests()
{
  { 
    // splitup test
    string str = "hello,my,name,is";
    vector<string> vec; 
    splitup(vec, str, ","); 
    
    FORALL(vec, itr) 
      cout << "###" << (*itr) << "###" << endl;
    
    string str2 = "hello,^$my,^$name,^$is";
    vector<string> vec2; 
    splitup(vec2, str2, ",^$"); 
    
    FORALL(vec2, itr) 
      cout << "###" << (*itr) << "###" << endl;

    string str3 = "bla**bla**";
    vector<string> vec3; 
    splitup(vec3, str3, "**"); 
    
    FORALL(vec3, itr) 
      cout << "###" << (*itr) << "###" << endl; 
    
    exit(0);
  }
  
  {
    // join test
    vector<string> vec;
    vec.push_back("hello");
    vec.push_back("my");
    vec.push_back("name");
    vec.push_back("is");
    
    cout << join(vec, ",", 2) << endl;
    exit(0);
  }
  
  {
    // trim test
    string str = "   \t \r qklsd dfj  \t asl;k   \n   ";
    string newstr = trim(str);
    cout << "##" << newstr << "##" << endl; 
    exit(0);
  }
  
  
  {
    // split test
    string t = " # 1,2,3    4,5,6";
  
    Vector<std::string> actions;
  
    split(actions, t, boost::is_any_of(" "), boost::token_compress_on); 
  
    FORALL (actions, i) {
      cout << '<' << *i << '>' << endl;
    }
  
    exit(0);
  }
}

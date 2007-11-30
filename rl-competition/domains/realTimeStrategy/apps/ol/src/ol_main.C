// $Id: ol_main.C 5667 2007-08-14 02:42:38Z mburo $

// (c) Michael Buro
// licensed under GPLv3

#include <string>
#include <vector>
#include <boost/algorithm/string.hpp>
#include <boost/algorithm/string/split.hpp>

#include "MiniGameState.H"
#include "SDL_GUI.H"
#include "SDL_init.H"
#include "Player.H"
#include "RLComp08Bot1.H"
#include "RLComp08Bot2.H"

using namespace std;

void tests(); 
typedef MobileObj<MiniGameState> MobObj;

vector<string> * trace = NULL; 
static char * parmsfile = NULL; 

void open_trace(char * filename)
{
  cout << "Reading in trace file ... " << endl;
  
  ifstream inputStream;
  inputStream.open(filename);
  if( !inputStream ) {
    cerr << "Error opening trace file" << endl;
    exit(-1);
  }          
  
  string str; 
  getline(inputStream, str); 
  boost::replace_all(str, "$", "=");
  
  trace = new vector<string>;
  
  boost::split(*trace, str, boost::is_any_of("@"));
  
  long seed = to_long((*trace)[0]);
  srand(seed); 
}

void usage()
{
  cout << "Usage: bin/ol [options]" << endl;
  cout << endl;
  cout << "Available options are: " << endl;
  cout << "   --parms <filename>" << endl; 
  cout << "   --replay <filename>" << endl; 
  
  exit(-1);
}

void handle_clargs(int argc, char ** argv)
{
  //cout << "argc=" << argc << endl;
  //exit(-1);
  
  for (int i = 1; i < argc; i++)
  {
    if (strcmp(argv[i], "--replay") == 0)
    {
      i++; if (i >= argc) usage();
      open_trace(argv[i]);
    }
    else if (strcmp(argv[i], "--parms") == 0)
    {
      i++; if (i >= argc) usage();
      parmsfile = argv[i];
    }
    else
      usage();
  }
}

int main(int argc, char ** argv)
{
  srandom(time(0));

  handle_clargs(argc, argv);
  
  //tests();   
  
  MiniGameState s;
  MiniGameParameters gp;
  if (parmsfile != NULL);
    load_parms(parmsfile, &gp);
  
  if (trace != NULL)
  {
    // trace->[1] contains the parms
    gp.deserialize((*trace)[1]);
  }
  
  //RLComp08Bot1 p0(0); <-- can't do this yet because of all the
  // assumptions about being player 1 in the RL framework
  
  TestPlayer p0(0); 
  p0.set_parms(&gp);
  
  TestPlayer p1(1);
  p1.set_parms(&gp);

  if (trace != NULL)
  {
    s.init(gp, true);   // delay object setup
    
    // trace->[2] contains the initial state
    s.from_string((*trace)[2]); 
  }  
  else
  {
    s.init(gp, false);
  }
    
  unsigned int trace_index = 3; 
  
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

    if (trace == NULL)
    {
      acts[0] = p0.receive_actions(views[0]);
      acts[1] = p1.receive_actions(views[1]);
    }
    else 
    {
      if (trace_index >= trace->size())
        break; 
      
      acts[0] = (*trace)[trace_index++];
      acts[1] = (*trace)[trace_index++];
    }
      
    
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

    gui.delay(25);
    
  } while (!gui.quit && !s.finished());
  
  return 0;
}

// Used for testing functions. Please ignore. 
void tests()
{
  {
    // vector test
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
  }
  
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
    // trim test (NOTE: There is one in boost!)
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

// env_ function prototypes types 
#include <rlglue/Environment_common.h>	  

// helpful functions for allocating structs and cleaning them up 
#include <rlglue/utils/C/RLStruct_util.h> 

#include <algorithm>
#include <iterator>
#include <sstream>
#include <vector>

#include "Random.h"
#include "gridworld.hh"

namespace {
  // Declare RL Glue variables.
  observation_t current_observation;
  reward_observation_terminal_t ro;
  
  // Declare Taxi helper functions and types.
  enum taxi_action_t {NORTH, SOUTH, EAST, WEST, PICKUP, PUTDOWN};
  typedef std::pair<double,double> coord_t;
  
  // Declare Taxi configuration variables.
  const bool nonMarkov = false;
  const bool noisy = false;
  Random rng;
  const Gridworld *grid = NULL;
  std::vector<coord_t> landmarks;

  // Declare Taxi state variables.
  int ns, ew, pass, dest;
  bool fickle = false;
}

const Gridworld *create_default_map();
int add_noise(int action);
void apply_fickle_passenger();
double apply(int action);
  
const char *env_init()
{
  grid = create_default_map();
  landmarks.push_back(coord_t(4.,0.));
  landmarks.push_back(coord_t(0.,3.));
  landmarks.push_back(coord_t(4.,4.));
  landmarks.push_back(coord_t(0.,0.));

  // Handle RL Glue stuff.
  allocateRLStruct(&current_observation, 4, 0, 0);
  std::fill(current_observation.intArray,
	    current_observation.intArray + current_observation.numInts,
	    0);
  ro.observation = &current_observation;
  ro.terminal = 0;
  ro.reward = 0.0;	

  std::stringstream response;
  response << "VERSION RL-Glue-3.0 PROBLEMTYPE episodic DISCOUNTFACTOR 1 ";
  response << "OBSERVATIONS INTS (3 0 4) (0 3) ";
  response << "ACTIONS INTS (0 5) ";
  response << "REWARDS (-10 20) ";
  response << "EXTRA Taxi implemented by Nicholas K. Jong.";
  static std::string buffer;
  buffer = response.str();
  return buffer.c_str();
}

void env_cleanup()
{
  clearRLStruct(&current_observation);

  delete grid;
  grid = NULL;
  landmarks.clear();
}

const observation_t *env_start()
{
  ns = rng.uniformDiscrete(1, grid->height()) - 1;
  ew = rng.uniformDiscrete(1, grid->width()) - 1;
  pass = rng.uniformDiscrete(1, landmarks.size()) - 1;
  do dest = rng.uniformDiscrete(1, landmarks.size()) - 1;
  while (dest == pass);
  fickle = false;

  current_observation.intArray[0] = ns;
  current_observation.intArray[1] = ew;
  current_observation.intArray[2] = pass;
  current_observation.intArray[3] = dest;
  ro.reward = 0.0;
  ro.terminal = 0;

//   std::cout << ns << " " << ew << " " << pass << " " << dest << "\n";

  return &current_observation;
}

const reward_observation_terminal_t *env_step(const action_t *a)
{	
  ro.reward = apply(a->intArray[0]);
  ro.terminal = (pass == dest) ? 1 : 0;

  current_observation.intArray[0] = ns;
  current_observation.intArray[1] = ew;
  current_observation.intArray[2] = pass;
  current_observation.intArray[3] = dest;

  //std::cout << a->intArray[0] << " -> " 
  //<< ns << " " << ew << " " << pass << " " << dest << "\n";
  
  return &ro;
}   

const char* env_message(const char* _inMessage) {
  std::stringstream response;
  std::stringstream message(_inMessage);

  // Tokenize the message
  std::istream_iterator<std::string> it(message);
  std::istream_iterator<std::string> end;
  
  while (it != end) {
    std::string command(*it++);
    if (command.compare("set-random-seed") == 0) {
      if (it == end) {
	response << "Taxi received set-random-seed with no argument.\n";
      } else {
	std::string seed_string(*it++);
	int seed = atoi(seed_string.c_str());
	rng.reset(seed);
	response << "Taxi set random seed to " << seed << ".\n";
      }
    } else {
      response << "Taxi did not understand '" << command << "'.\n";
    }
  }

  static std::string buffer;
  buffer = response.str();
  return buffer.c_str();
}

const Gridworld *create_default_map() {
  std::vector<std::vector<bool> > nsv(5, std::vector<bool>(4,false));
  std::vector<std::vector<bool> > ewv(5, std::vector<bool>(4,false));
  ewv[0][0] = true;
  ewv[0][2] = true;
  ewv[1][0] = true;
  ewv[1][2] = true;
  ewv[3][1] = true;
  ewv[4][1] = true;
  return new Gridworld(5,5,nsv,ewv);
}

int add_noise(int action) {
  switch(action) {
  case NORTH:
  case SOUTH:
    return rng.bernoulli(0.8) ? action : (rng.bernoulli(0.5) ? EAST : WEST);
  case EAST:
  case WEST:
    return rng.bernoulli(0.8) ? action : (rng.bernoulli(0.5) ? NORTH : SOUTH);
  default:
    return action;
  }
}

void apply_fickle_passenger() {
  if (fickle) {
    fickle = false;
    if (rng.bernoulli(0.3)) {
      dest += rng.uniformDiscrete(1, landmarks.size() - 1);
      dest = dest % landmarks.size();
    }
  }
}

double apply(int action) {
  const int effect = noisy ? add_noise(action) : action;
  switch(effect) {
  case NORTH:
    if (!grid->wall(static_cast<unsigned>(ns),
		    static_cast<unsigned>(ew),
		    effect))
      {
	++ns;
	apply_fickle_passenger();
      }
    return -1;
  case SOUTH:
    if (!grid->wall(static_cast<unsigned>(ns),
		    static_cast<unsigned>(ew),
		    effect))
      {
	--ns;
	apply_fickle_passenger();
      }
    return -1;
  case EAST:
    if (!grid->wall(static_cast<unsigned>(ns),
		    static_cast<unsigned>(ew),
		    effect))
      {
	++ew;
	apply_fickle_passenger();
      }
    return -1;
  case WEST:
    if (!grid->wall(static_cast<unsigned>(ns),
		    static_cast<unsigned>(ew),
		    effect))
      {
	--ew;
	apply_fickle_passenger();
      }
    return -1;
  case PICKUP: {
    if (pass < static_cast<int>(landmarks.size())
	&& coord_t(ns,ew) == landmarks[static_cast<unsigned>(pass)])
      {
	pass = landmarks.size();
	fickle = nonMarkov && noisy;
	return -1;
      } else
	return -10;
  }
  case PUTDOWN:
    if (pass == static_cast<int>(landmarks.size())
	&& coord_t(ns,ew) == landmarks[static_cast<unsigned>(dest)]) {
      pass = dest;
      return 20;
    } else
      return -10;
  }
  std::cerr << "Unreachable point reached in Taxi::apply!!!\n";
  return 0; // unreachable, I hope
}

// #include "taxi.hh"

// std::ostream &operator<<(std::ostream &out, const Taxi &taxi) {
//   out << "map:\n" << *taxi.grid << "landmarks:\n";
//   for (unsigned i = 0; i < taxi.landmarks.size(); ++i)
//     out << "row " << taxi.landmarks[i].first
// 	<< ", column " << taxi.landmarks[i].second << "\n";
//   return out;
// }

// void Taxi::randomize_landmarks() {
//   std::vector<unsigned> indices(landmarks.size());
//   const unsigned n = grid->height() * grid->width();
//   for (unsigned i = 0; i < indices.size(); ++i) {
//     unsigned index;
//     bool duplicate;
//     do {
//       index = rng.uniformDiscrete(1,n) - 1;
//       duplicate = false;
//       for (unsigned j = 0; j < i; ++j)
// 	if (index == indices[j])
// 	  duplicate = true;
//     } while (duplicate);
//     indices[i] = index;
//   }
//   for (unsigned i = 0; i < indices.size(); ++i)
//     landmarks[i] = coord_t(indices[i] / grid->width(),
// 			   indices[i] % grid->width());
// }

// void Taxi::randomize_landmarks_to_corners() {
//   for (unsigned i = 0; i < landmarks.size(); ++i) {
//     int ns = rng.uniformDiscrete(0,1);
//     int ew = rng.uniformDiscrete(0,1);
//     if (1 == i/2)
//       ns = grid->height() - ns - 1;
//     if (1 == i%2)
//       ew = grid->width() - ew - 1;
//     landmarks[i] = coord_t(ns,ew);
//   }
// }

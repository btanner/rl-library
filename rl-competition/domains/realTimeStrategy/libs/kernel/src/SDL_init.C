// $Id: SDLinit.C 5279 2007-06-23 02:49:08Z mburo $

// This is an ORTS file (c) Michael Buro, licensed under the GPLv3

#include "Global.H"
#include "SDL_init.H"

using namespace std;

void SDL_init::video_init()
{
  // Initialize the SDL library
#ifdef SDL
  if (SDL_Init(// SDL_INIT_VIDEO |
	       // SDL_FULLSCREEN |
	       // SDL_HWSURFACE |
	       // SDL_HWACCEL | SDL_PREALLOC |
	       // SDL_DOUBLEBUF |
	       SDL_INIT_NOPARACHUTE) < 0) {
    ERR2("Couldn't initialize SDL: ", SDL_GetError());
  }
    
  atexit(SDL_Quit);
    
  if (SDLNet_Init() == -1) {
    ERR2("SDLNet_Init: ", SDLNet_GetError());
  }
#endif
}

#if 0
void SDL_init::network_init()
{
  if (SDLNet_Init() == -1) {
    ERR2("SDLNet_Init error: ", SDLNet_GetError());
  }
  atexit(SDLNet_Quit);
}
#endif

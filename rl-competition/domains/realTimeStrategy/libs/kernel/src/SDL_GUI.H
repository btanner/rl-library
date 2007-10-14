#ifndef SDL_GUI_H
#define SDL_GUI_H

/** @file GUI.H
    Basic/debug server display.

    $Id: GUI.H 5407 2007-07-10 09:37:53Z furtak $
    $Source$
*/

// This is an ORTSLITE file (c) Michael Buro, licensed under the GPLv3

#include "Global.H"

template <class T> class GameObj;

//===================================================================

struct RGB
{
  uint1 R, G, B;
  uint4 col;

  void from(sint4 r, sint4 g, sint4 b=0) {
    R = r; G = g; B = b;
    col = ((((R << 8)+G) << 8) + B);
  }
  
  RGB(sint4 r=0, sint4 g=0, sint4 b=0) : R(r), G(g), B(b) {
    //col = (((((R << 8)+G) << 8) + B) << 8) + 255;
    col = ((((R << 8)+G) << 8) + B);
  }
  
  uint4 color() const { return col; }
  uint4 color_alpha(sint4 alpha=255) const {
    assert(alpha >=0 && alpha < 256);
    return (col << 8) + alpha;
  }  
};

//===================================================================

// play field view

struct GfxView2
{
  sint4 x, y, w, h; // rectangle on screen
  sint4 mx, my;     // upper left "fine" pixel on map
  sint4 mag;
};

//===================================================================

/** Basic/debug server display */

template <class State> class SDL_GUI
{

protected:

  // options
  sint4 DISP_VISI, DISP_SEEN, DISP_KNOW, DISP_GRID;

public:

  typedef GameObj<State> GameObjBase;
  
  enum Marker { MARKER_NO, MARKER_H, MARKER_V, MARKER_HV, MARKER_DOT, MARKER_CIRCLE };

  State *state;
  std::map<std::string, Marker> type2marker; // markers for each object type
#ifdef SDL  
  SDL_Surface *screen;
#else
  void * screen;
#endif
  sint4 s_x, s_y;
  sint4 gfx_mag_frac;
  sint4 adj_x, adj_y;
  Vector<GfxView2> views;
  sint4 active_view;
  GfxView2 minimap;
  bool quit;  // true <=> quit application
  sint4 scale(sint4 x, sint4 mag) const { return (x*mag)/gfx_mag_frac; }
  real4 scale(real4 x, sint4 mag) const { return (x*mag)/gfx_mag_frac; }
  
  SDL_GUI();
  ~SDL_GUI();
  void init(sint4 width, sint4 height, State &s, std::map<std::string, Marker> &markers);

  void event();  
  bool adjust_view_xy(sint4 dx, sint4 dy);
  bool adjust_view_mag(sint4 dm);
  bool correct_view();
  void display(const GameObjBase* obj1=0, const GameObjBase* obj2=0, bool trylock=true);
  void display(const GfxView2 &gs, const GameObjBase* obj1=0, const GameObjBase* obj2=0);
  void display_minimap(const GfxView2 &active_view);
  void display_health_bars(const GfxView2 &gs);
  void draw_marker(const GfxView2 &gv, sint4 cx, sint4 cy, sint4 r, sint4 m);
  void delay(int d); 

  bool lock();
  void unlock();

  void draw_line(const GfxView2 &gv, real4 x1, real4 y1, real4 x2, real4 y2, const RGB &c, int alpha);
  void draw_rect(const GfxView2 &gv, real4 x1, real4 y1, real4 x2, real4 y2, const RGB &c, int alpha, bool filled );  
  void draw_circle(const GfxView2 &gv, real4 x1, real4 y1, real4 r, const RGB &c, int alpha, bool filled);

  // static void add_options();
  static RGB colors[State::PLAYER_NUM*2];
  static bool effective_key(sint4 sym, sint4 mod);
  
private:

  // not implemented
  SDL_GUI(const SDL_GUI &) { ERR("what"); }
  SDL_GUI &operator=(const SDL_GUI &) { ERR("what"); }
};

//===================================================================

// implementation

#include "SDL_GUI.HPP"

// REGISTER_TYPEOF(66, Vector<GfxView2>::iterator);
#endif

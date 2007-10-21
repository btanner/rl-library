/** @file GUI.C
    @see GUI.H

    $Id: SDL_GUI.HPP 5655 2007-08-12 22:45:36Z lanctot $
    $Source$
*/

// This is an ORTSLITE file (c) Michael Buro, licensed under the GPLv3

//===================================================================

#include "SDL_gfxPrimitives.H"

#ifdef SDL
const sint4 WATER  = 1;
const sint4 GROUND = 2;
const sint4 CLIFF  = 3;

const sint4 TILE_POINTS = 1;

//static sint4 CMD_H = 128; // command window height
// disable command window for now
static sint4 CMD_H = 0; // command window height

const sint4 ALPHA = 255;
#endif

template <class State>
RGB SDL_GUI<State>::colors[State::PLAYER_NUM*2] = {
  RGB(0,255,0),
  RGB(255,0,0),
  RGB(0,0,255),
  RGB(255,255,0)
#if 0  
  RGB(0,96,0),
  RGB(96,0,0),
  RGB(0,0,96),
  RGB(96,96,0)
#endif  
};

//===================================================================

#if 0
template <class State>
void SDL_GUI<State>::add_options()
{
  Options o("GUI");
  o.put("-dvisi", "2d: mark visible tiles");
  o.put("-dknow", "2d: mark known tiles");
  o.put("-dseen", "2d: mark which players can see each object");
  o.put("-dgrid", "2d: show the tile grid");
}
#endif

//-------------------------------------------------------------------

template <class State>
SDL_GUI<State>::SDL_GUI()
{
  screen = 0;
  gfx_mag_frac = 16;
  active_view = 0;
  quit = false;

#if 0  
  bool op_temp;
  Options::get("-dvisi", op_temp); DISP_VISI = op_temp ? 1 : 0;
  Options::get("-dseen", op_temp); DISP_SEEN = op_temp ? 1 : 0;
  Options::get("-dknow", op_temp); DISP_KNOW = op_temp ? 1 : 0;
  Options::get("-dgrid", op_temp); DISP_GRID = op_temp ? 1 : 0;
#endif  
}

//-------------------------------------------------------------------

template <class State>
SDL_GUI<State>::~SDL_GUI()
{
  // fixme: close screen?
}

//-------------------------------------------------------------------

template <class State>
void SDL_GUI<State>::init(sint4 w, sint4 h, State &s, std::map<std::string, Marker> &markers)
{
#ifdef SDL
  state = &s;
  type2marker = markers;
  s_x = w;
  s_y = h;
  adj_x = w/32;
  adj_y = h/32;  

  // open screen
    
  // Have a preference for 8-bit, but accept any depth

  screen = SDL_SetVideoMode(s_x, s_y, 0, SDL_HWSURFACE | SDL_ANYFORMAT);
  if (screen == 0) {
    //   cerr << "Couldn't set " << s_x << "x" << s_y << "x8 video mode: " << SDL_GetError() << endl;
    exit(1);
  }
    
  printf("Set %dx%d at %d bits-per-pixel mode\n",
	 static_cast<int>(s_x), static_cast<int>(s_y),
	 static_cast<int>(screen->format->BitsPerPixel));

  std::ostringstream oss;
  oss << "ortslite";

  SDL_WM_SetCaption(oss.str().c_str(), 0);

  GfxView2 gv;
  gv.x = gv.y = 0;
  gv.w = s_x; gv.h = s_y-CMD_H;
  gv.mx = gv.my = 0;
  gv.mag = gfx_mag_frac; // 1.0
  views.push_back(gv);

  // minimap
  minimap.x = 0; minimap.y = s_y - CMD_H;
  minimap.w = minimap.h = CMD_H;
#endif
}

//-------------------------------------------------------------------

template <class State>
void SDL_GUI<State>::display_minimap(const GfxView2 &av)
{
#ifdef SDL
  // clear minimap
  SDL_Rect rect;
  rect.x = minimap.x;
  rect.y = minimap.y;
  rect.w = minimap.w;
  rect.h = minimap.h;
  SDL_SetClipRect(screen, &rect);
  SDL_FillRect(screen, &rect, 0);

  // frame
  RGB c = RGB(255,255,255);

  rectangleColor(this->screen, minimap.x, minimap.y, minimap.x+minimap.w-1,
		 minimap.y+minimap.h-1, c.color_alpha());

  // active view

  c = RGB(255,0,0);

  sint4 total_w = scale(state->width, av.mag);
  sint4 total_h = scale(state->height, av.mag);
  sint4 x, y, w, h;
  
  if (av.w >= total_w) { x = 0; w = minimap.w-1; }
  else {
    x = (av.mx*minimap.w)/total_w;
    w = (av.w *minimap.w)/total_w;
    if (x+w >= minimap.w) w = minimap.w-x-1;
  }

  if (av.h >= total_h) { y = 0; h = minimap.h-1; }
  else {
    y = (av.my*minimap.h)/total_h;
    h = (av.h*minimap.h)/total_h;
    if (y+h >= minimap.h) h = minimap.h-y-1;    
  }

  x += minimap.x; y += minimap.y;
  
  rectangleColor(this->screen, x, y, x+w, y+h, c.color_alpha());  
#endif
}

//-------------------------------------------------------------------

template <class State>
void SDL_GUI<State>::display(const GameObjBase *obj1, const GameObjBase *obj2, bool trylock)
{
#ifdef SDL
  if (trylock && !lock()) return;

  // draw minimap and mark active view
  // do this first so that the clipping rectangle is still good after display()
  // lanctot: Removed so we can see the whole playing field 
  //display_minimap(views[active_view]);

  FORALL (views, it) {
    correct_view();
    display(*it, obj1, obj2);
  }

  //  dot.draw_line(0, 0, 600, 600, Vec3<real4>(1.0, 1.0, 1.0), 1);

  if (trylock) unlock();
#endif
}

//-------------------------------------------------------------------

template <class State>
bool SDL_GUI<State>::lock()
{
#ifdef SDL
  if (SDL_MUSTLOCK(screen) && SDL_LockSurface(screen) < 0 ) {
    fprintf(stderr, "Can't lock screen: %s\n", SDL_GetError());
    return false;
  }
  return true;
#else
  return true;
#endif
}

//-------------------------------------------------------------------

template <class State>
void SDL_GUI<State>::unlock()
{
#ifdef SDL
  if (SDL_MUSTLOCK(screen)) SDL_UnlockSurface(screen);
  //SDL_Flip(screen);
  SDL_UpdateRect(screen, 0, 0, 0, 0);
#endif
}

//===================================================================

template <class State>
void SDL_GUI<State>::display_health_bars(const GfxView2 &gv)
{
#ifdef SDL
  const RGB black(0,0,0);
  const RGB white(255,255,255);
  
  Vector<GameObjBase*> objs(state->all_objs.begin(), state->all_objs.end());
  std::random_shuffle(objs.begin(), objs.end());
  
  FORALL (objs, it) {
    GameObjBase* gob = (*it); 
    if (gob == 0) continue; 
   
    sint4 cur_hp = (sint4)gob->hp;
    if(cur_hp < 0) cur_hp = 0;
    sint4 max_hp = (sint4)gob->max_hp;
    if (max_hp <= 0) max_hp = 1;
    
    sint4 x0, w, y;
  
    sint4 x = gob->x;
    if(x < 0) continue;
  
    sint4 radius = gob->radius;
  
    x0 = x - radius;
    w  = 2 * radius + 1;
    y  = gob->y - radius - 3;
  
    if (cur_hp >= 1)
    {
      SDL_Rect rect;
      rect.x = scale(x0, gv.mag) - gv.mx;
      rect.y = scale(y, gv.mag) - gv.my;
      rect.w = scale(w, gv.mag);
      rect.h = scale(2, gv.mag);
      SDL_FillRect(screen, &rect, black.color());
    
      rect.x = scale(x0, gv.mag) - gv.mx;
      rect.y = scale(y, gv.mag) - gv.my;
      rect.w = scale(w * cur_hp / max_hp, gv.mag);
      rect.h = scale(2, gv.mag);
      SDL_FillRect(screen, &rect, white.color());
    }
  }
  
#if 0
  // old code
  const RGB black(0,0,0);
  const RGB white(255,255,255);

  Vector<GameObjBase*> objs(state->all_objs.begin(), state->all_objs.end());
  std::random_shuffle(objs.begin(), objs.end());
  
  FORALL (objs, i) {
    GameObj* gob = (*it(); 
    if (gob == 0) continue; 
  }
  
  FORS (i, state->PLAYER_NUM+1) {
    FORALL (game->get_objs(i), it) {
      GameObj *gob = (*it)->get_GameObj();
      if (gob == 0) continue;

      if (!gob->has_attr("hp")) continue;
      if (!gob->has_attr("max_hp")) continue;

      sint4 cur_hp = gob->get_int("hp");
      if(cur_hp < 0) cur_hp = 0;
      sint4 max_hp = gob->get_int("max_hp");
      if(max_hp == 0) continue;            

      if (!gob->has_attr("shape")) continue;

      sint4 x0, w, y;

      if (gob->get_int("shape") == Object::CIRCLE) {
        sint4 x = *gob->sod.x;
        if(x < 0) continue;

        sint4 radius = *gob->sod.radius;

        x0 = x - radius;
        w  = 2 * radius + 1;
        y  = *gob->sod.y - radius - 3;
      } else if (gob->get_int("shape") == Object::RECTANGLE)  {
        x0 = *gob->sod.x1;
        w  = *gob->sod.x2 - x0 + 1;
        y  = *gob->sod.y1 - 3;
      } else {
        continue;
      }

      SDL_Rect rect;
      rect.x = scale(x0, gv.mag) - gv.mx;
      rect.y = scale(y, gv.mag) - gv.my;
      rect.w = scale(w, gv.mag);
      rect.h = scale(2, gv.mag);
      SDL_FillRect(screen, &rect, black.color());

      if (cur_hp >= 1) {
        rect.x = scale(x0, gv.mag) - gv.mx;
        rect.y = scale(y, gv.mag) - gv.my;
        rect.w = scale(w * cur_hp / max_hp, gv.mag);
        rect.h = scale(2, gv.mag);
        SDL_FillRect(screen, &rect, white.color());
      }
    }
  }
#endif   // 0
#endif   // SDL
}

template <class State>
void SDL_GUI<State>::SDL_GUI::display(const GfxView2 &gv,
                                      const GameObjBase* /*obj1*/, const GameObjBase* /*obj2*/)
{
#ifdef SDL
  // sint4 tile_pixels = scale(TILE_POINTS, gv.mag);
  
  SDL_Rect clip_rect;
  clip_rect.x = gv.x;
  clip_rect.y = gv.y;
  clip_rect.w = gv.w-1;
  clip_rect.h = gv.h-1;
  SDL_SetClipRect(screen, &clip_rect);

  // draw terrain
  
  RGB rgb_GROUND(100,42,42);
  RGB rgb_WATER(0,0,128);
  RGB rgb_PLATEAU(122, 82, 48);

  if (gv.mx < 0 || gv.my < 0) {
    SDL_Rect rect;
    rect.x = rect.y = 0; rect.w = gv.w; rect.h = gv.h;
    SDL_FillRect(screen, &rect, 0);
  }

  // !!! std::cout << state->width << " " << state->height << std::endl;
  
  draw_rect(gv, 0, 0, state->width, state->height, rgb_GROUND, ALPHA, true);

#if 0
  
  sint4 tx0 = gv.mx/tile_pixels;
  sint4 ty0 = gv.my/tile_pixels;  
  if (tx0 < 0) tx0 = 0;
  if (ty0 < 0) ty0 = 0;  
  sint4 x0  = tx0 * tile_pixels - gv.mx + gv.x;
  sint4 y0  = ty0 * tile_pixels - gv.my + gv.y;
  sint4 txn = (gv.w+gv.mx-tx0*tile_pixels+tile_pixels-1)/tile_pixels;
  sint4 tyn = (gv.h+gv.my-ty0*tile_pixels+tile_pixels-1)/tile_pixels;

  txn = std::min(txn, state->width);
  tyn = std::min(tyn, state->height);
  
#if 0
  cout << "XXXXXXXXXXX" << endl;
  cout << gv.mx << " " << gv.my << e
    ndl;
  cout << gv.w << " " << gv.h << endl;
  cout << tx0 << " " << ty0 << endl;
  cout << txn << " " << tyn << endl;
  cout << x0 << " " << y0 << endl;    
#endif

  RGB black(0,0,0);

  FORS (ty, tyn) {

    const GameTile *p = &game->get_map()(tx0, ty+ty0);

    sint4 x = x0;
    FORS (tx, txn) {

      SDL_Rect rect;

      Tile::Split split = p->get_split();

      sint4 c = p->get_typeW();
      uint4 col;
      if (c == GROUND)     col = p->get_min_h() == 0 ? rgb_GROUND.color() : rgb_PLATEAU.color();
      else if (c == WATER) col = rgb_WATER.color();
      else                 col = black.color();
      
      rect.x = x; rect.y = y0; rect.h = rect.w = tile_pixels;
      SDL_FillRect(screen, &rect, col);

      if (split != Tile::NO_SPLIT) {

        int tp = tile_pixels+1;
        int tx, ty, dx, dy;
        if (split == Tile::TB_SPLIT) {
          tx = x+tile_pixels;
          ty = y0+tile_pixels;
          dx = -tp;
          dy = -tp;
        } else {
          tx = x+tile_pixels;
          ty = y0;
          dx = -tp;
          dy = tp;
        }

        sint4 c = p->get_typeE();
        uint4 col;
        if (c == GROUND)     col = p->get_min_h() == 0 ? rgb_GROUND.color_alpha() : rgb_PLATEAU.color_alpha();
        else if (c == WATER) col = rgb_WATER.color_alpha();
        else                 col = black.color_alpha();

        filledRTriangle(screen, tx, ty, dx, dy, col);
      }

      if (DISP_GRID)
	rectangleColor(screen,
		       x, y0, x + tile_pixels, y0 + tile_pixels,
		       black.color_alpha());
      
      // views

      if (DISP_KNOW && tile_pixels > game->get_player_num()*2+2) {

	FORS (i, game->get_player_num()) {
	  if (game->get_player_info(i).explored.is_set(tx+tx0, ty+ty0)) {
	    rect.x = x + tile_pixels/2 - (game->get_player_num()/2) * 2 + i * 2;
	    rect.y = y0+tile_pixels/2;
	    rect.x -= 2;
	    rect.y -= 2;
	    rect.h = rect.w = 6;
	    SDL_FillRect(screen, &rect, colors[i+4].color());
	  }
	}
      }

      if (DISP_VISI && tile_pixels > game->get_player_num()*2+2) {

	// draw colored 2x2 blocks each player who sees tile

	FORS (i, game->get_player_num()) {
          if (game->get_map()(tx, ty).get_split() != Tile::NO_SPLIT) {
            if (game->get_player_info(i).visible_W.is_set(tx+tx0, ty+ty0)) {
              rect.x = x + tile_pixels/2 - (game->get_player_num()/2) * 2 + i * 2 - 2;
              rect.y = y0+tile_pixels/2;
              rect.h = rect.w = 2;
              SDL_FillRect(screen, &rect, colors[i].color());
            }
            if (game->get_player_info(i).visible_E.is_set(tx+tx0, ty+ty0)) {
              rect.x = x + tile_pixels/2 - (game->get_player_num()/2) * 2 + i * 2 + 2;
              rect.y = y0+tile_pixels/2;
              rect.h = rect.w = 2;
              SDL_FillRect(screen, &rect, colors[i].color());
            }
          } else {
            if (game->get_player_info(i).visible_W.is_set(tx+tx0, ty+ty0)) {
              rect.x = x + tile_pixels/2 - (game->get_player_num()/2) * 2 + i * 2;
              rect.y = y0+tile_pixels/2;
              rect.h = rect.w = 2;
              SDL_FillRect(screen, &rect, colors[i].color());
            }
          }
	}
      }

      ++p;
      x += tile_pixels;
    }
    y0 += tile_pixels;
  }

#endif
  
  // draw objects

  // randomize objects

  Vector<GameObjBase*> objs(state->all_objs.begin(), state->all_objs.end());
  std::random_shuffle(objs.begin(), objs.end());
  
  FORALL (objs, i) {

    const RGB &rgb = colors[(*i)->owner];

    if ((*i)->dead()) continue;

    int radius = std::max((*i)->radius, 5);
    draw_circle(gv, (*i)->x, (*i)->y, radius, rgb, ALPHA, true);

    //  std::cout << "draw " << (*i)->x << " " << (*i)->y << " " << radius << std::endl;
    
    //      SDL_Rect rect;
    //      rect.x = cx-gv.mx; rect.y = cy-gv.my; rect.h = rect.w = 2*r;
    // SDL_FillRect(screen, &rect, rgb.color());    

#if 0    

    if (DISP_SEEN) {
      FORS (j, game->get_player_num()) {
        if (j != i && gob->sod.seen_by.is_set(j)) {
          const RGB black(0,0,0);
          draw_circle(gv, x, y, 3, black);
          break;
        }
      }
    }

#endif

    // mark objects
    FIND (type2marker, j, (*i)->get_type());

    Marker marker = MARKER_NO;
    
    if (j != type2marker.end()) {
      marker = j->second;
    }
    
    draw_marker(gv, (*i)->x, (*i)->y, radius, marker);

#if 0    
    // highlight objects of interest
    if (gob == obj1 || gob == obj2) {
      RGB white(255,255,255);
      draw_circle(gv, x, y, radius, white);
    }
#endif
  }

#if 0

  // render what's visible
  
  tx0 = gv.mx/tile_pixels;
  ty0 = gv.my/tile_pixels;  
  if (tx0 < 0) tx0 = 0;
  if (ty0 < 0) ty0 = 0;  
  x0  = tx0 * tile_pixels - gv.mx + gv.x;
  y0  = ty0 * tile_pixels - gv.my + gv.y;
  txn = (gv.w+gv.mx-tx0*tile_pixels+tile_pixels-1)/tile_pixels;
  tyn = (gv.h+gv.my-ty0*tile_pixels+tile_pixels-1)/tile_pixels;

  txn = ::min(txn, game->get_map().get_width());
  tyn = ::min(tyn, game->get_map().get_height());

  FORS (ty, tyn) {

    sint4 x = x0;
    FORS (tx, txn) {

      SDL_Rect rect;
      rect.x = x; rect.y = y0; rect.h = rect.w = tile_pixels;
      
      // views

      if (DISP_KNOW && tile_pixels > game->get_player_num()*2+2) {

        FORS (i, game->get_player_num()) {
          if (game->get_player_info(i).explored.is_set(tx+tx0, ty+ty0)) {
            rect.x = x + tile_pixels/2 - (game->get_player_num()/2) * 2 + i * 2;
            rect.y = y0+tile_pixels/2;
            rect.x -= 2;
            rect.y -= 2;
            rect.h = rect.w = 6;
            SDL_FillRect(screen, &rect, colors[i+4].color());
          }
        }
      }

      if (DISP_VISI && tile_pixels > game->get_player_num()*2+2) {

        // draw colored 2x2 blocks each player who sees tile

        FORS (i, game->get_player_num()) {

          if (game->get_map()(tx,ty).get_split() != Tile::NO_SPLIT) {

            if (game->get_player_info(i).visible_W.is_set(tx+tx0, ty+ty0)) {
              rect.x = x + tile_pixels/2 - (game->get_player_num()/2) * 2 + i * 2 - 2;
              rect.y = y0+tile_pixels/2;
              rect.h = rect.w = 2;
              SDL_FillRect(screen, &rect, colors[i].color());
            }
            if (game->get_player_info(i).visible_E.is_set(tx+tx0, ty+ty0)) {
              rect.x = x + tile_pixels/2 - (game->get_player_num()/2) * 2 + i * 2 + 2;
              rect.y = y0+tile_pixels/2;
              rect.h = rect.w = 2;
              SDL_FillRect(screen, &rect, colors[i].color());
            }

          } else {
            if (game->get_player_info(i).visible_W.is_set(tx+tx0, ty+ty0)) {
              rect.x = x + tile_pixels/2 - (game->get_player_num()/2) * 2 + i * 2;
              rect.y = y0+tile_pixels/2;
              rect.h = rect.w = 2;
              SDL_FillRect(screen, &rect, colors[i].color());
            }
          }
        }
      }

      x += tile_pixels;
    }
    y0 += tile_pixels;
  }

  const RGB rgb(255,255,255);

  FORALL (game->get_boundaries(), it) {

    GameObj *gob = (*it)->get_GameObj();
    if (gob == 0) continue;
    gob->update_points();
    draw_line(gv, *gob->sod.x1, *gob->sod.y1, *gob->sod.x2, *gob->sod.y2, rgb);
  }

#endif
  

  /*
    FORALL(game->get_dead_objs(), it) {
    
    GameObj *gob = (*it)->get_GameObj();
    if (gob == 0) continue;

    if (*gob->sod.owner == -1) {
    sint4 cx = scale(gob->get_int("x"), gv.mag);
    sint4 cy = scale(gob->get_int("y"), gv.mag);
    sint4 r  = scale(gob->get_int("radius"), gv.mag);

    RGB white(255,255,255);
    circleColor(screen, cx-gv.mx, cy-gv.my, r, white.color_alpha());
    }
    }
  */

  display_health_bars(gv);
#endif
}

//===================================================================

// true iff view changed
template <class State>
bool SDL_GUI<State>::adjust_view_xy(sint4 dx, sint4 dy)
{
#ifdef SDL
  if (!dx && !dy) return false;
  GfxView2 &v = views[0];
  GfxView2 vold = v;
  sint4 pix_x = scale(TILE_POINTS, v.mag) * state->width;
  sint4 pix_y = scale(TILE_POINTS, v.mag) * state->height;

  v.mx += dx * adj_x;
  if (v.mx < 0) v.mx = 0;
  else if (v.mx + v.w >= pix_x) v.mx = pix_x - v.w;

  v.my += dy * adj_y;
  if (v.my < 0) v.my = 0;
  else if (v.my + v.h >= pix_y) v.my = pix_y - v.h;

  return v.mx != vold.mx || v.my != vold.my;
#else
  return true;
#endif
  
}

//-------------------------------------------------------------------

// true iff view changed
template <class State>
bool SDL_GUI<State>::adjust_view_mag(sint4 dm) {
#ifdef SDL 
  if (!dm) return false;
  GfxView2 &v = views[0];
  sint4 &m = v.mag;
  sint4 mold = v.mag;
  m += dm;
  if (m < 1) m = 1;
  else if (m > 2*gfx_mag_frac) m = 2*gfx_mag_frac;

  if (m == mold) return false;

  // map view midpoint and center view around the resulting point

  v.mx = (((2*v.mx + v.w) * m) / mold - v.w)/2;
  v.my = (((2*v.my + v.h) * m) / mold - v.h)/2;
  return true;
#else
  return true;
#endif
}

//-------------------------------------------------------------------

// true iff view changed
template <class State>
bool SDL_GUI<State>::correct_view()
{
#ifdef SDL
  GfxView2 &v = views[0], vold = views[0];

  // view larger than playing field?
  sint4 w = scale(state->width*TILE_POINTS, v.mag);

  // cout << w << " " << v.w << " ";
  
  if (v.w > w) {
    // center
    v.mx = -(v.w - w)/2;
  } else {
    if (v.mx < 0) v.mx = 0;
    else if (v.mx + v.w > w) v.mx = w - v.w;
  }

  //cout << " mx=" << v.mx << endl;
  
  sint4 h = scale(state->height * TILE_POINTS, v.mag);  
  if (v.h > h) {
    // center
    v.my = -(v.h - h)/2;
  } else {
    if (v.my < 0) v.my = 0;
    else if (v.my + v.h > h) v.my = h - v.h;
  }

  //cout << "mx=" << v.mx << " my=" << v.my << endl;
  
  return vold.mx != v.mx || vold.my != v.my;
#else
  return true;
#endif
} 

//-------------------------------------------------------------------

template <class State>
bool SDL_GUI<State>::effective_key(sint4 sym, sint4 mod)
{
#ifdef SDL
  mod = 0;
  sint4 ignore[] = { SDLK_LSHIFT, SDLK_RSHIFT, SDLK_LCTRL, SDLK_RCTRL,
                     SDLK_LALT, SDLK_RALT,  SDLK_LMETA, SDLK_RMETA };

  FORU (i, sizeof(ignore)/sizeof(ignore[0])) {
    if (sym == ignore[i]) return false;
  }

  return true;
#else
  return true;
#endif
}

//-------------------------------------------------------------------

template <class State>
void SDL_GUI<State>::event()
{
#ifdef SDL
  SDL_Event event;
  SDL_EnableUNICODE(true);

  static sint4 dx = 0, dy = 0;

  while (SDL_PollEvent(&event)) {

    // cout << "." << flush;
      
    if (event.type == SDL_KEYDOWN || event.type == SDL_KEYUP) {

      //      cout << "%" << flush;
      bool up = event.type == SDL_KEYUP;
      char ch;
      
      if ((event.key.keysym.unicode & 0xFF80) == 0) {
        ch = event.key.keysym.unicode & 0x7F;
        //cout << "ch= " << (int)ch << endl;
      }
      else {
        printf("An International Character.\n");
      }
      
      sint4 sym = event.key.keysym.sym;
      sint4 mod = event.key.keysym.mod;
      if (effective_key(sym, mod)) {
        //cout << sym << " " << mod << endl;

        switch (sym) {
        case 27: quit = true; break; // ESC -> quit
        case SDLK_LEFT:  dx = -1; break;
        case SDLK_RIGHT: dx = +1; break;
        case SDLK_UP:    dy = -1; break;
        case SDLK_DOWN:  dy = +1; break;
        case SDLK_MINUS:
          if (!up && adjust_view_mag(-1)) {
            //	    display(); SDL_Delay(50);
          }
          break;
        case SDLK_EQUALS:
          if (!up && adjust_view_mag(+1)) {
            //	    display(); SDL_Delay(50);
          }
          break;
        }
      }

      if (up) dx = dy = 0;
    }
    if (event.type == SDL_QUIT) { quit = true; break; }
  }

  // cout << dx << " " << dy << endl;
  if ((dx || dy) && (adjust_view_xy(dx, dy))) {
    //    display();
    // SDL_Delay(50);
  }
#endif
}

//-------------------------------------------------------------------

template <class State>
void SDL_GUI<State>::draw_marker(const GfxView2 &gv, sint4 cx, sint4 cy, sint4 r, sint4 m)
{
#ifdef SDL
  if (r <= 2) return;

  cx = scale(cx, gv.mag) - gv.mx;
  cy = scale(cy, gv.mag) - gv.my;

  RGB rgb(0,0,0);

  switch (m) {
  case MARKER_NO:
    return;
  case MARKER_H:
    hlineColor(this->screen, cx-r, cx+r, cy, rgb.color_alpha());
    break;
  case MARKER_V:
    vlineColor(this->screen, cx, cy-r, cy+r, rgb.color_alpha());
    break;
  case MARKER_HV:
    hlineColor(this->screen, cx-r, cx+r, cy, rgb.color_alpha());    
    vlineColor(this->screen, cx, cy-r, cy+r, rgb.color_alpha());
    break;
  case MARKER_DOT:
    r /= 4; if (r <= 0) r = 1;
    filledCircleColor(this->screen, cx, cy, r, rgb.color_alpha());
    break;
  case MARKER_CIRCLE:
    r = (r*3)/4; if (r <= 0) r = 1;    
    circleColor(this->screen, cx, cy, r, rgb.color_alpha());
    break;
  default:
    ERR2("unknown marker", m);
  }
#endif
}

//===================================================================

template <class State>
void SDL_GUI<State>::draw_line(const GfxView2 &gv, real4 x1, real4 y1, real4 x2, real4 y2, const RGB &c, int alpha)
{
#ifdef SDL
  x1 = scale(x1, gv.mag) - gv.mx;
  x2 = scale(x2, gv.mag) - gv.mx;
  y1 = scale(y1, gv.mag) - gv.my;
  y2 = scale(y2, gv.mag) - gv.my;

  if (x1 < 0    && x2 < 0   ) return;
  if (x1 > gv.w && x2 > gv.w) return;
  if (y1 < 0    && y2 < 0   ) return;
  if (y1 > gv.h && y2 > gv.h) return;

  lineColor(this->screen, (sint2)x1, (sint2)y1, (sint2)x2, (sint2)y2, c.color_alpha(alpha));
#endif
}

//-------------------------------------------------------------------

template <class State>
void SDL_GUI<State>::draw_rect(const GfxView2 &gv, real4 x1, real4 y1, real4 x2, real4 y2, const RGB &c, int alpha, bool filled)
{
#ifdef SDL
  x1 = scale(x1, gv.mag) - gv.mx;
  x2 = scale(x2, gv.mag) - gv.mx;
  y1 = scale(y1, gv.mag) - gv.my;
  y2 = scale(y2, gv.mag) - gv.my;

  if (x1 < 0) x1 = 0;
  if (x2 < 0) x2 = 0;
  if (x1 > gv.w) x1 = gv.w;
  if (x2 > gv.w) x2 = gv.w;

  if (y1 < 0) y1 = 0;
  if (y2 < 0) y2 = 0;
  if (y1 > gv.h) y1 = gv.h;
  if (y2 > gv.h) y2 = gv.h;

  if (filled)
    boxColor(this->screen, (sint2)x1, (sint2)y1, (sint2)x2, (sint2)y2, c.color_alpha(alpha));
  else
    rectangleColor(this->screen, (sint2)x1, (sint2)y1, (sint2)x2, (sint2)y2, c.color_alpha(alpha));
#endif
}

//-------------------------------------------------------------------

template <class State>
void SDL_GUI<State>::draw_circle(const GfxView2 &gv, real4 x1, real4 y1, real4 r, const RGB &c, int alpha, bool filled)
{
#ifdef SDL
  x1 = scale(x1, gv.mag) - gv.mx;
  y1 = scale(y1, gv.mag) - gv.my;
  r = scale(r, gv.mag);

  if (x1 + r < 0   ) return;
  if (x1 - r > gv.w) return;
  if (y1 + r < 0   ) return;
  if (y1 - r > gv.h) return;

  if (!filled)
    circleColor(this->screen, (sint2)x1, (sint2)y1, (sint2)r, c.color_alpha(alpha));
  else
    filledCircleColor(this->screen, (sint2)x1, (sint2)y1, (sint2)r, c.color_alpha(alpha));
#endif
}

template <class State>
void SDL_GUI<State>::delay(int d)
{
#ifdef SDL
  SDL_Delay(d);
#endif
}


//===================================================================

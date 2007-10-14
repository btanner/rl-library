#ifndef All_H
#define All_H

// Standard header file collection (precompiled)
// $Id: All.H 5984 2007-10-13 14:42:40Z lanctot $

// This is an ORTS file (c) Michael Buro, licensed under the GPL

#define __STDC_LIMIT_MACROS 1

#ifdef __GNUC__
#  include <stdint.h>
#else
#  define WIN32_LEAN_AND_MEAN		// Exclude rarely-used stuff from Windows headers
#  pragma once
//#  include <wstdint.h>
#  include <tchar.h>
#endif


#include <cstdlib>
#include <cassert>
#include <cmath>
#include <cstdarg>
#include <cstdio>
#include <csignal>
#include <string>
#include <iomanip>
#include <iostream>
#include <sstream>
#include <fstream>
#include <iosfwd>
#include <map>
#include <list>
#include <set>
#include <vector>
#include <stack>
#include <queue>
#include <algorithm>
#include <limits>
#ifdef SDL
#include <SDL.h>
#include <SDL_net.h>
#include <SDL_thread.h>
#endif
#include <boost/static_assert.hpp>
#include <boost/format.hpp>
#include <boost/array.hpp>

#ifdef GCC 
#  include <unistd.h>
#  include <ext/hash_map>
#  include <ext/hash_set>
#  define EXT_NAMESPACE __gnu_cxx
#endif

#ifdef WIN32

// Windows
#  include <time.h>
#  include <hash_map>
#  include <hash_set>
#  define vsnprintf _vsnprintf
#  define snprintf _snprintf
#  define EXT_NAMESPACE stdext

#endif


#if 0
// !!! commented out, should not be here because kernel is independent of gfx (-> GfxGlobal.H)
// need to include windows at the start
// remaining code is for compatibility (moved from GfxGlobal.H)
#  define _WINSOCKAPI_
#  include "windows.h"
#  undef min
#  undef max
#  undef RGB
#  include "glew.h"
#  include <glut.h>
#endif


#endif

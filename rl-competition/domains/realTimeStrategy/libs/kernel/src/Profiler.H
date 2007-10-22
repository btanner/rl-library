
#ifndef _PROFILER_H_
#define _PROFILER_H_

#include "time.h"
#include "sys/time.h"

#include <string>
#include <iostream>
#include <fstream>

class Profiler
{
  std::string filename;
  std::fstream file;
  bool enabled; 
  
  struct timeval starttv, currenttv, prevtv; 
  
public:
  
  Profiler() 
  {
    enabled = true;
    filename = "profile.log";
  }
  
  Profiler(const std::string & _filename)
    : filename(_filename) 
  {
    enabled = true;
  }
  
  void setFilename(const std::string& name) { filename = name; }
  void disable() { enabled = false; }
  
  void start();
  int diff(struct timeval & now, struct timeval & prev);
  void stamp(const std::string& msg, bool puttime = true);
  void end(); 
  
};

#endif


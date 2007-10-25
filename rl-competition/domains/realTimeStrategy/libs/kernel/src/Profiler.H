
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
    
  struct timeval sw_start, sw_end;
  long sw_total;
  int sw_times; 
  
public:
  
  Profiler() 
  {
    enabled = true;
    filename = "profile.log";
    sw_times = 0;
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
  
  void stopwatch_start();
  void stopwatch_stop();
  double stopwatch_avg(); 
};

#endif


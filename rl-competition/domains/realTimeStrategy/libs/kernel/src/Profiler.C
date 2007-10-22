
#include "Profiler.H"

using namespace std; 

int Profiler::diff(struct timeval & now, struct timeval & prev)
{
  int delta_sec = now.tv_sec - prev.tv_sec; 
  int delta_usec = now.tv_usec - prev.tv_usec; 
  
  return (delta_sec*1000000 + delta_usec);
}


void Profiler::start()
{
  if (!enabled)
    return; 
  
  file.open(filename.c_str(), ios::out);
  gettimeofday(&starttv, NULL);
  prevtv = starttv; 
  stamp("Starting profiler");
}

void Profiler::stamp(const std::string& msg, bool puttime)
{
  if (!enabled)
    return;
  
  gettimeofday(&currenttv, NULL);
  
  file << msg;
  
  if (puttime)
  {
    int delta = diff(currenttv, prevtv);
    prevtv = currenttv;
    
    long now_sec = currenttv.tv_sec;
    long now_usec = currenttv.tv_usec;
    
    file << "\t\t" << now_sec << "." << now_usec 
         << ", diff (usec) = " << delta << endl;
  }
}

void Profiler::end()
{
  if (!enabled)
    return;
  
  file.close(); 
}


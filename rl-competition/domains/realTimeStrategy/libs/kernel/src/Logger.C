
#include <string>
#include <iostream>
#include <sstream>
#include <fstream>

#include "Logger.H"

using namespace std; 

Logger::~Logger() 
{
  // used for testing
  //dump_to_file("/tmp/rlgenv_episode.log");
}

void Logger::append(const string & str)
{
  outstream << str; 
}

string Logger::str() 
{
  return outstream.str(); 
}

void Logger::dump_to_file(const char * filename)
{
  ofstream fout(filename, ios::out);
  fout << str() << endl; 
  fout.close(); 
}



#ifndef _RLGENV_HELPERS_H_
#define _RLGENV_HELPERS_H_

#include "ParameterHolder.h"
#include "MiniGameState.H"
#include "Player.H"

void timing_start();
void timing_end();
void logstr(const std::string & str); 

void copy_parms(ParameterHolder * phPtr, MiniGameParameters * mgpPtr);
Player* get_opponent(MiniGameParameters * mgpPtr);

void printout_phstr(char * parmsfile);



#endif

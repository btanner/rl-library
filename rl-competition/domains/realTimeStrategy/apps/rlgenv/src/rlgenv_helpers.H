
#ifndef _RLGENV_HELPERS_H_
#define _RLGENV_HELPERS_H_

#include "ParameterHolder.h"
#include "MiniGameState.H"

void timing_start();
void timing_end();

void copyParms(ParameterHolder * phPtr, MiniGameParameters * mgpPtr);

#endif

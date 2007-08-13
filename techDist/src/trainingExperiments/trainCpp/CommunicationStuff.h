



#ifndef _COMMUNICATIONSTUFF_H_
#define _COMMUNICATIONSTUFF_H_

#include "ParameterHolder.h"
#include <string>


ParameterHolder *getParameterHolderForEnvironment(std::string envName);
void loadEnvironment(std::string envName, ParameterHolder *theParamHolder);
void unloadEnvironment();



#endif /* _COMMUNICATIONSTUFF_H_ */

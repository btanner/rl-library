
#include "CommunicationStuff.h"
#include <RL_glue.h>
#include <iostream>

#include <sstream>
#define BENCHMARK 0
#define ENVSHELL 1
#define LISTQUERY 1
#define NOVALUE 3
#define STRINGLIST 0
#define LOADQUERY 2
#define UNLOADQUERY 3



ParameterHolder *getParameterHolderForEnvironment(std::string envName){
	char theRequest[1024]={0};
	sprintf(theRequest,"TO=%d FROM=%d CMD=%d VALTYPE=%d VALS=NULL",ENVSHELL, BENCHMARK, LISTQUERY, NOVALUE);

	std::string theResponse=std::string(RL_env_message(theRequest));
	std::string::size_type lastColonPos = theResponse.find_last_of ("=");
	std::string thePayLoad=theResponse.substr(lastColonPos+1);

	std::vector<std::string> payLoadVector;
	std::string thisItem;

	std::istringstream iss(thePayLoad);
	while (getline(iss,thisItem,':'))
	{
	  payLoadVector.push_back (thisItem);
	}

	std::vector<std::string> theNames;
	std::vector<ParameterHolder *> theParamHolders;

	for(size_t i=1;i<payLoadVector.size();i+=2){
		theNames.push_back(payLoadVector[i]);
		theParamHolders.push_back(new ParameterHolder(payLoadVector[i+1]));							
	}

	unsigned int indexOfMyEnv=0;

	for(unsigned int i=0;i<theNames.size();i++){
		if(theNames[i]==envName){
			indexOfMyEnv=i;
		}
	}
	
	return theParamHolders[indexOfMyEnv];
}


void unloadEnvironment(){
	char theRequest[1024]={0};

	sprintf(theRequest,"TO=%d FROM=%d CMD=%d VALTYPE=%d VALS=NULL",ENVSHELL, BENCHMARK, UNLOADQUERY, NOVALUE);
	RL_env_message(theRequest);
}

void loadEnvironment(std::string envName, ParameterHolder *theParamHolder){
	char theRequest[1024]={0};
	std::string loadPayLoad=envName+":"+theParamHolder->stringSerialize();
	
	sprintf(theRequest,"TO=%d FROM=%d CMD=%d VALTYPE=%d VALS=%s",ENVSHELL, BENCHMARK, LOADQUERY, STRINGLIST,loadPayLoad.c_str());
	RL_env_message(theRequest);
}

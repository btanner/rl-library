/*
 *  ParameterHolder.cpp
 *  BT-Glue
 *
 *  Created by Brian Tanner on 09/04/07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

#include "ParameterHolder.h"

#include <iostream>
ParameterHolder::ParameterHolder(){}

ParameterHolder::~ParameterHolder(){}

std::string ParameterHolder::getAlias(std::string alias){
	if(aliases.count(alias)==0){
		std::cerr<<"You wanted to look up original for alias: "<<alias<<", but that alias hasn't been set"<<std::endl;
		exit(-1);
	}
	return  aliases[alias];
}

void ParameterHolder::setAlias(std::string alias, std::string original){
	if(allParams.count(original)==0){
		std::cerr<<"Careful, you are setting an alias of:"<<alias<<" to: "<<original<<" but: "<<original<<" isn't in the parameter set"<<std::endl;
		exit(1);
	}
	aliases[alias]=original;
}


void ParameterHolder::addIntParam(std::string name, int defaultValue){
	addIntParam(name);
	setIntParam(name, defaultValue);
}
void ParameterHolder::addFloatParam(std::string name, float defaultValue){
	addFloatParam(name);
	setFloatParam(name, defaultValue);
}
void ParameterHolder::addBoolParam(std::string name, bool defaultValue){
	addBoolParam(name);
	setBoolParam(name, defaultValue);
}
void ParameterHolder::addStringParam(std::string name, std::string defaultValue){
	addStringParam(name);
	setStringParam(name, defaultValue);
}

void ParameterHolder::addIntParam(std::string name){
	allParams[name]=INTPARAM;
	allParamNames.push_back(name);
	allParamTypes.push_back(INTPARAM);
	setAlias(name,name);
}
void ParameterHolder::addFloatParam(std::string name){
	allParams[name]=FLOATPARAM;
	allParamNames.push_back(name);
	allParamTypes.push_back(FLOATPARAM);
	setAlias(name,name);
}
void ParameterHolder::addBoolParam(std::string name){
	allParams[name]=BOOLPARAM;
	allParamNames.push_back(name);
	allParamTypes.push_back(BOOLPARAM);
	setAlias(name,name);
}
void ParameterHolder::addStringParam(std::string name){
	allParams[name]=STRINGPARAM;
	allParamNames.push_back(name);
	allParamTypes.push_back(STRINGPARAM);
	setAlias(name,name);
}


void ParameterHolder::setIntParam(std::string alias, int value){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);
	if(allParams.count(name)==0){
		std::cerr<<"Careful, you are setting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;
	}
	intParams[name]=value;
}

void ParameterHolder::setFloatParam(std::string alias, float value){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);
	if(allParams.count(name)==0){
		std::cerr<<"Careful, you are setting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;
	}
	floatParams[name]=value;
}

void ParameterHolder::setBoolParam(std::string alias, bool value){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){
		std::cerr<<"Careful, you are setting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;
	}
	boolParams[name]=value;
}

void ParameterHolder::setStringParam(std::string alias, std::string value){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){
		std::cerr<<"Careful, you are setting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;
	}
	stringParams[name]=value;
}
int ParameterHolder::getIntParam(std::string alias){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;exit(1);}
	if(intParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter isn't an int parameter...\n"<<std::endl;exit(1);}
	int retVal=intParams[name];
	return retVal;
}
float ParameterHolder::getFloatParam(std::string alias){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;exit(1);}
	if(floatParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter isn't an float parameter...\n"<<std::endl;exit(1);}

	float retVal=floatParams[name];
	return retVal;
}
bool ParameterHolder::getBoolParam(std::string alias){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;exit(1);}
	if(boolParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter isn't an bool parameter...\n"<<std::endl;exit(1);}

	bool retVal=boolParams[name];
	return retVal;
}

std::string ParameterHolder::getStringParam(std::string alias){
	//Convert from an alias to the real name
	std::string name=getAlias(alias);

	if(allParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter hasn't been added...\n"<<std::endl;exit(1);}
	if(stringParams.count(name)==0){std::cerr<<"Careful, you are getting the value of parameter: "<<name<<" but the parameter isn't a string parameter...\n"<<std::endl;exit(1);}

	std::string retVal=stringParams[name];
	return retVal;
}

int ParameterHolder::getParamCount(){
	return allParamNames.size();
}
const char* ParameterHolder::getParamName(int which){
	return allParamNames[which].c_str();
}
int ParameterHolder::getParamType(int which){
	return allParamTypes[which];
}

bool ParameterHolder::supportsParam(std::string alias){
	return (aliases.count(alias)!=0);
}




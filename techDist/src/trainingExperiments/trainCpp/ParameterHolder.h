/*
 *  ParameterHolder.h
 *  BT-Glue
 *
 *  Created by Brian Tanner on 09/04/07.
 *  Copyright 2007 __MyCompanyName__. All rights reserved.
 *
 */

//Ok, so Parameter holder could make use of templates, should tell Andrew.
// Ahahahahaha! -- Andrew

#ifndef ParameterHolder_H
#define ParameterHolder_H

#include <vector>
#include <map>

//I think this stuff doens't need to exist.
// You're right. I remember how this got in here, but not why... -- Andrew.
//#pragma GCC visibility push(default)
#include <string>
//#pragma GCC visibility pop

#define INTPARAM 0
#define FLOATPARAM 1
#define BOOLPARAM 2
#define STRINGPARAM 3


class ParameterHolder{
private:
	typedef std::map<std::string, int> TStrIntMap;
	typedef std::map<std::string, float> TStrFloatMap;
	typedef std::map<std::string, bool> TStrBoolMap;
	typedef std::map<std::string, std::string> TStrStrMap;

	typedef std::vector<std::string> TStrVec;
	typedef std::vector<int> TIntVec;
	
	TStrIntMap intParams;
	TStrFloatMap floatParams;
	TStrBoolMap boolParams;
	TStrStrMap stringParams;
	
	TStrIntMap allParams;
	//we'll let everything be an alias to itself, and then we'll always just look up aliases
	TStrStrMap aliases;
	TStrVec allParamNames;
	TIntVec allParamTypes;
	
public:
	ParameterHolder();
	virtual ~ParameterHolder();

	virtual std::string getAlias(std::string alias);
	bool supportsParam(std::string alias);

	virtual void setAlias(std::string alias, std::string original);

	virtual void setIntParam(std::string alias, int value);
	virtual void setFloatParam(std::string alias, float value);
	virtual void setBoolParam(std::string alias, bool value);
	virtual void setStringParam(std::string alias, std::string value);

	virtual void addIntParam(std::string alias);
	virtual void addFloatParam(std::string alias);
	virtual void addBoolParam(std::string alias);
	virtual void addStringParam(std::string alias);

//Should have done this a while ago
	virtual void addIntParam(std::string alias, int defaultValue);
	virtual void addFloatParam(std::string alias, float defaultValue);
	virtual void addBoolParam(std::string alias, bool defaultValue);
	virtual void addStringParam(std::string alias, std::string defaultValue);


	virtual int getIntParam(std::string alias);
	virtual float getFloatParam(std::string alias);
	virtual bool getBoolParam(std::string alias);
	virtual std::string getStringParam(std::string alias);
	
	virtual int getParamCount();
	virtual const char* getParamName(int which);
	virtual int getParamType(int which);
	
	
};

#endif


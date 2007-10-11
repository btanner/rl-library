	#ifndef CONSOLETRAINERHELPER_H
	#define CONSOLETRAINERHELPER_H
	
	#include <string>
	class ParameterHolder;
	
	
	void load(std::string envNameString, ParameterHolder *theParams);
	ParameterHolder *preload(std::string envNameString);

	void preloadAndLoad(std::string envNameString);

	/*
	* Tetris has an integer parameter called pnum that takes values in [0,9]
	* Setting this parameter changes the exact tetris problem you are solving
	*/
	void loadTetris(int whichParamSet);

	/*
	* MountainCar has an integer parameter called pnum that takes values in [0,9]
	* Setting this parameter changes the exact tetris problem you are solving
	*/
	void loadMountainCar(int whichParamSet);	

	/*
	* Helicopter has no user controllable parameters
	*/
	void loadHelicopter();	

#endif

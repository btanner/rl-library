#include <sstream>
#include <iostream>
#include <string>
#include <RL_glue.h>
#include <vector>
#include "ParameterHolder.h"
#include "CommunicationStuff.h"

using std::string;
using std::cerr;

void runTrial();

int main(int argc, char *argv[])
{
	string theEnv="Tetrlais";
//    string theEnv = "MountainCar";

	ParameterHolder *theParams = getParameterHolderForEnvironment(theEnv);
//Some examples of how to set problem paramemters	

//Exactly what problem parameters are available for each are detailed in the README
//Anything you don't set just keeps its default value
	if(theEnv=="MountainCar"){
		theParams->setBoolParam("randomStartStates",true);
		theParams->setFloatParam("acceleration", .002);
	}

	if(theEnv=="Tetrlais"){
		theParams->setBoolParam("TriBlock",false);
		theParams->setIntParam("Width", 8);
	}
	
	printf("\n--------------\nAbout to load environment %s\n--------------\n",theEnv.c_str());
	loadEnvironment(theEnv,theParams);
	
	runTrial();

	unloadEnvironment();
	
	//Now just run with the default params for the other problem
	theEnv="MountainCar";
	printf("\n--------------\nAbout to load environment %s\n--------------\n",theEnv.c_str());

	theParams = getParameterHolderForEnvironment(theEnv);

	loadEnvironment(theEnv,theParams);

	runTrial();


    cerr << "Program over\n";

    return 0;
}


void runTrial(){
    RL_init();

    int sum = 0;
	unsigned int numEpisodes = 100;
    for (unsigned int i = 0; i < numEpisodes; ++i)
    {
        RL_episode(100000);
        sum += RL_num_steps();

        if ( (i+1) % 5== 0 )
        {
            cerr << "Running episode: " << (i+1) << " total steps in last bunch is: " << sum << "\n";
            sum = 0;
        }    
    }
    RL_cleanup();
}


#include <sstream>
#include <iostream>
#include <string>
#include <RL_glue.h>
#include <vector>
#include "ParameterHolder.h"
#include "CommunicationStuff.h"

using std::string;
using std::cerr;


int main(int argc, char *argv[])
{
		string theEnv="Tetrlais";
//    string theEnv = "MountainCar";

	ParameterHolder *theParams = getParameterHolderForEnvironment(theEnv);
	
	//IF env == MountainCa
//	theParams->setBoolParam("randomStartStates",true);
	
	loadEnvironment(theEnv,theParams);

    RL_init();

    int sum = 0;
	unsigned int numEpisodes = 5000;
    for (int i = 0; i < numEpisodes; ++i)
    {
        RL_episode(100000);
        sum += RL_num_steps();

        if ( (i+1) % 100 == 0 )
        {
            cerr << "Running episode: " << i << " total steps in last bunch is: " << sum << "\n";
            sum = 0;
        }    
    }
    RL_cleanup();
    cerr << "Program over\n";

    return 0;
}



/*

	public static void main(String[] args) throws InterruptedException {
		EnvShellListResponse ListResponse = EnvShellListRequest.Execute();
		
		int thisEnvIndex=ListResponse.getTheEnvList().indexOf(theEnv);

		ParameterHolder p = ListResponse.getTheParamList().get(thisEnvIndex);
		
		EnvShellLoadRequest.Execute(theEnv,p);

		RLGlue.RL_init();
}

*/

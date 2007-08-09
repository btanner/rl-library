#include <sstream>
#include <iostream>
#include <string>
#include <RL_glue.h>

using std::string;
using std::cerr;

const int kNumTrials = 10000;
const int kNumSteps  = 1000;

int main(int argc, char *argv[])
{
    int sum = 0;
    string theEnv = "MountainCar";

    cerr << "Running with Parameter Settings: " << "<put parameter holder here>\n";
    RL_init();
    for (int i = 0; i < kNumTrials; ++i)
    {
        RL_episode(kNumSteps);
        sum += RL_num_steps();
        if ( i % 500 == 0 )
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

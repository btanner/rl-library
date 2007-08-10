#include <sstream>
#include <iostream>
#include <string>
#include <RL_glue.h>

using std::string;
using std::cerr;

const int kNumTrials = 10000;
const int kNumSteps  = 1000;

#define BENCHMARK 0
#define ENVSHELL 1
#define LISTQUERY 1
#define NOVALUE 3


int main(int argc, char *argv[])
{
    int sum = 0;
//		string theEnv="Tetrlais";
    string theEnv = "MountainCar";

	char theRequest[128]={0};
	sprintf(theRequest,"TO=%d FROM=%d CMD=%d VALTYPE=%d VALS=NULL",ENVSHELL, BENCHMARK, LISTQUERY, NOVALUE);

	std::string theResponse=std::string(RL_env_message(theRequest));
	std::string::size_type lastColonPos = theResponse.rfind (":",0);
	std::string thePayLoad=theResponse.substr(lastColonPos+1);
	
	std::cout<<"The payLoad is: "<<thePayLoad<<std::endl;
	
	
	
	std::cout<<"The message is: "<<theRequest<<std::endl;
		// EnvShellListResponse ListResponse = EnvShellListRequest.Execute();
		// 
		// int thisEnvIndex=ListResponse.getTheEnvList().indexOf(theEnv);
		// 
		// ParameterHolder p = ListResponse.getTheParamList().get(thisEnvIndex);
		// 
		// System.out.println("Running with Parameter Settings: "+p);
		// 
		// EnvShellLoadRequest.Execute(theEnv,p);
		// 


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

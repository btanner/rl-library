import rlVizLib.general.ParameterHolder;
import rlVizLib.messaging.environment.EnvReceiveRunTimeParametersRequest;
import rlVizLib.messaging.environmentShell.EnvShellListRequest;
import rlVizLib.messaging.environmentShell.EnvShellListResponse;
import rlVizLib.messaging.environmentShell.EnvShellLoadRequest;
import rlglue.RLGlue;

public class JavaTrainer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String theEnv="MountainCar";

		
		EnvShellListResponse ListResponse = EnvShellListRequest.Execute();
		
		int thisEnvIndex=ListResponse.getTheEnvList().indexOf(theEnv);
		
		System.out.println("Looks like: "+theEnv+" is environment number: "+thisEnvIndex);
		
		ParameterHolder p = ListResponse.getTheParamList().get(thisEnvIndex);
		
		p.setBooleanParam("randomStartStates",true);
		
		EnvShellLoadRequest.Execute(theEnv,p);
		
		
		EnvReceiveRunTimeParametersRequest.Execute(p);
		
		//This is like sending the following, which goes to the environmentShell Loader and loads the theEnv.
		//RL_env_message("TO=1 FROM=0 CMD=2 VALTYPE=1 VALS="+theEnv);
		
		RLGlue.RL_init();
		
		int numEpisodes=10;
	
		for(int i=0;i<numEpisodes;i++){
			RLGlue.RL_episode(0);
			System.out.println("Steps: "+RLGlue.RL_num_steps());
		}
		RLGlue.RL_cleanup();
	}

}

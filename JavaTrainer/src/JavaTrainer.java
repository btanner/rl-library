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
//		String theEnv="Tetrlais";
		String theEnv="MountainCar";

		System.out.println("Java Trainer about to send request for List");
		EnvShellListResponse ListResponse = EnvShellListRequest.Execute();
		
		int thisEnvIndex=ListResponse.getTheEnvList().indexOf(theEnv);
		
		System.out.println("Looks like: "+theEnv+" is environment number: "+thisEnvIndex);
		
		ParameterHolder p = ListResponse.getTheParamList().get(thisEnvIndex);
		
//		p.setBooleanParam("randomStartStates",true);
		
		EnvShellLoadRequest.Execute(theEnv,p);
		
		System.out.println("Environment is loaded");
		
//		EnvReceiveRunTimeParametersRequest.Execute(p);
		
		//This is like sending the following, which goes to the environmentShell Loader and loads the theEnv.
		//RL_env_message("TO=1 FROM=0 CMD=2 VALTYPE=1 VALS="+theEnv);
		
		RLGlue.RL_init();
		
//		RLGlue.RL_start();
//		RLGlue.RL_step();
		
		int numEpisodes=10;
	
		for(int i=0;i<numEpisodes;i++){
			RLGlue.RL_episode(10000);
			System.out.println("Steps: "+RLGlue.RL_num_steps());
		}
		RLGlue.RL_cleanup();
	}

}

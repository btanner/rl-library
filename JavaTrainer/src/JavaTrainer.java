import rlVizLib.general.ParameterHolder;
import rlVizLib.general.TinyGlue;
import rlVizLib.messaging.environmentShell.EnvShellListRequest;
import rlVizLib.messaging.environmentShell.EnvShellListResponse;
import rlVizLib.messaging.environmentShell.EnvShellLoadRequest;
import rlVizLib.visualization.EnvVisualizer;
import rlglue.RLGlue;
import visualizers.mountainCar.MountainCarVisualizer;

public class JavaTrainer {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
//		String theEnv="Tetrlais";
		String theEnv="MountainCar";

		EnvShellListResponse ListResponse = EnvShellListRequest.Execute();
		
		int thisEnvIndex=ListResponse.getTheEnvList().indexOf(theEnv);

		ParameterHolder p = ListResponse.getTheParamList().get(thisEnvIndex);
		
		System.out.println("Running with Parameter Settings: "+p);

		EnvShellLoadRequest.Execute(theEnv,p);

		RLGlue.RL_init();
		
		int sum=0;
		for(int i=0;i<100;i++){
			RLGlue.RL_episode(10000);
			sum+=RLGlue.RL_num_steps();
			if(i%25==0){
			System.out.println("Running episode: "+i+" total steps in last bunch is: "+sum);
			sum=0;
			}
		}
		TinyGlue theTinyGlue= new TinyGlue();
		//Set this otherwise the first step of theTinyGlue will call RL_init and undo all our learning
		theTinyGlue.setInited(true);
		
		RLVizWatchFrame theViz=new RLVizWatchFrame(theEnv,"");
		theViz.startVisualizing();
		
		
		
		//Run a least 500 steps
		for(int i=0;i<500;i++){
			theTinyGlue.step();
			Thread.sleep(10);
		}
		//Run out the episode 
		while(theTinyGlue.step());
		
		
	System.out.println("out of the display loop");
	theViz.stopVisualizing();
	
	

	System.out.println("running 1000 steps quietly");
	for(int i=0;i<1000;i++){
		theTinyGlue.step();
	}
	System.out.println("running 1000 steps showing again");
	theViz.startVisualizing();
	for(int i=0;i<1000;i++){
		theTinyGlue.step();
		Thread.sleep(10);
		
		}

		RLGlue.RL_cleanup();
	}

}

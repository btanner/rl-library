import rlVizLib.general.ParameterHolder;
import rlVizLib.general.TinyGlue;
import rlVizLib.messaging.environmentShell.EnvShellListRequest;
import rlVizLib.messaging.environmentShell.EnvShellListResponse;
import rlVizLib.messaging.environmentShell.EnvShellLoadRequest;
import rlVizLib.visualization.AbstractVisualizer;
import rlglue.RLGlue;
import visualizers.mountainCar.MountainCarVisualizer;

public class JavaTrainer {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		/* change theEnv String to the name of the environment you wish to test*/
//		String theEnv="Tetrlais";
		String theEnv="MountainCar";

		EnvShellListResponse ListResponse = EnvShellListRequest.Execute();
		int thisEnvIndex=ListResponse.getTheEnvList().indexOf(theEnv);

		ParameterHolder p = ListResponse.getTheParamList().get(thisEnvIndex);

		System.out.println("Running with Parameter Settings: "+p);

		//Optionally you can set some parameters
		if(theEnv.equalsIgnoreCase("MountainCar")){
			p.setBooleanParam("randomStartStates",true);
			p.setDoubleParam("acceleration", .002);
		}
		if(theEnv.equalsIgnoreCase("Tetrlais")){
			p.setBooleanParam("TriBlock",false);
			p.setIntegerParam("Width", 8);
		}


		EnvShellLoadRequest.Execute(theEnv,p);

		RLGlue.RL_init();

		//This portion of the code is the same as a regular RL-Glue experiment program
		// we have not opened the visualizer yet, we are simply running the Agent through 
		// a number of episodes. Change the number of iterations in the loop to increase the number
		//of episodes run. You have 100 000 steps per episode to terminate in, other wise 
		// the glue terminates the episode
		int sum=0;
		for(int i=0;i<1000000;i++){
			RLGlue.RL_episode(100000);
			sum+=RLGlue.RL_num_steps();
			if(i%50==0){
				System.out.println("Running episode: "+i+" total steps in last bunch is: "+sum);
				sum=0;
			}
		}
		TinyGlue theTinyGlue= new TinyGlue();
		//Set this otherwise the first step of theTinyGlue will call RL_init and undo all our learning
		theTinyGlue.setInited(true);

		//Opens a visualizer frame and starts visualizing
		RLVizWatchFrame theViz=new RLVizWatchFrame(theEnv,"");
		theViz.startVisualizing();



		//Run a few steps here so the visualizer can be seen by the user. Change this number
		// if you want to watch it for longer
		for(int i=0;i<500;i++){
			theTinyGlue.step();
			Thread.sleep(10);
		}

		//finishes off the episode you were in when you ran out of steps
		while(theTinyGlue.step());


		//Stop visualizing the agent's actions
		System.out.println("out of the display loop");
		theViz.stopVisualizing();

		//Run a few more steps without the visualizer
		System.out.println("running 1000 steps quietly");
		for(int i=0;i<1000;i++){
			theTinyGlue.step();
		}

		//Run a few more steps with the visualizer on again
		System.out.println("running 1000 steps showing again");
		theViz.startVisualizing();
		for(int i=0;i<1000;i++){
			theTinyGlue.step();
			Thread.sleep(10);
		}

		//clean up the environment and end the program
		RLGlue.RL_cleanup();
		System.out.println("Program over");
	}

}

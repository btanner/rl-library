import rlglue.*;
import java.io.IOException;

import messaging.EnvMessageType;

public class Driver
{
    protected static final int kNumEpisodes = 100;
    protected static int rlNumSteps[];
    protected static double rlReturn[];

    protected static void run(int numEpisodes) throws IOException
    {
	/*run for num_episode number of episodes and store the number of steps and return from each episode*/        	
    	for(int x = 0; x < numEpisodes; ++x) {
	    RLGlue.RL_episode(0);
	    System.out.println(RLGlue.RL_num_steps());
	    
	    rlNumSteps[x] = RLGlue.RL_num_steps();
	    rlReturn[x] = RLGlue.RL_return();
	}
    }
    
    public static void main(String [] args) throws IOException {
	double avgSteps = 0.0;
	double avgReturn = 0.0;
	
	rlNumSteps = new int[Driver.kNumEpisodes];
	rlReturn = new double[Driver.kNumEpisodes];

	/*basic main loop*/
	
	RLGlue.RL_init();
	
	System.out.println(EnvMessageType.kEnvResponse.id());
	String theMessage="TO=3 FROM=0 CMD=1 VALTYPE=0 VALS=NULL";
	
	System.out.println("About to send message: "+theMessage);
	RLGlue.RL_env_message(theMessage);
	run(kNumEpisodes);
	RLGlue.RL_cleanup();
	
	/*add up all the steps and all the returns*/
	for (int i = 0; i < Driver.kNumEpisodes; ++i) {
	    avgSteps += rlNumSteps[i];
	    avgReturn += rlReturn[i];
	}
	
	/*average steps and returns*/
	avgSteps /= (double)Driver.kNumEpisodes;
	avgReturn /= (double)Driver.kNumEpisodes;
	
	/*print out results*/
	System.out.println("\n-----------------------------------------------\n");
	System.out.println("Number of episodes: " + Driver.kNumEpisodes);
	System.out.println("Average number of steps per episode: " +  avgSteps);
	System.out.println("Average return per episode: " + avgReturn);
	System.out.println("-----------------------------------------------\n");
    }   
}

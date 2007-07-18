import rlglue.*;
import java.io.IOException;
import java.util.Vector;

import messaging.agent.AgentValueForObsRequest;
import messaging.agent.AgentValueForObsResponse;
import messaging.environment.EnvMessageType;
import messaging.environment.EnvObsForStateRequest;
import messaging.environment.EnvObsForStateResponse;
import messaging.environment.EnvRangeRequest;
import messaging.environment.EnvRangeResponse;

public class Driver
{
    protected static final int kNumEpisodes = 5;
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
	
	EnvRangeResponse theERResponse=EnvRangeRequest.Execute();
	System.out.println("Reponse to Range request was: "+theERResponse);
	
	Vector<Observation> theQueryStates=new Vector<Observation>();
	Observation tmpObs=new Observation(0,2);
	tmpObs.doubleArray[0]=0;
	tmpObs.doubleArray[1]=0;
	theQueryStates.add(tmpObs);
	tmpObs=new Observation(0,2);
	tmpObs.doubleArray[0]=0;
	tmpObs.doubleArray[1]=.5;
	theQueryStates.add(tmpObs);
	EnvObsForStateResponse theObsForStateResponse=EnvObsForStateRequest.Execute(theQueryStates);

	System.out.println("Reponse to EnvObsForState Request  was: "+theObsForStateResponse);
	System.out.println("Continuing with the experiment");

	run(kNumEpisodes);
	
	System.out.println("Now I want to know the value of those two states...");

	Vector<Observation> theQueryObservations=theObsForStateResponse.getTheObservations();
	
	AgentValueForObsResponse theValueResponse = AgentValueForObsRequest.Execute(theQueryObservations);
	
	System.out.println("The learned values are: "+theValueResponse);

	
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

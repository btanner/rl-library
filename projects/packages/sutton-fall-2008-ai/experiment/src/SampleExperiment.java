/*
Copyright 2007 Brian Tanner
brian@tannerpages.com
http://brian.tannerpages.com

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/
import org.rlcommunity.rlglue.codec.RLGlue;
import org.rlcommunity.rlglue.codec.types.*;
import java.io.IOException;
import java.util.Vector;
import rlVizLib.messaging.environmentShell.*;
import rlVizLib.messaging.agentShell.*;
import rlVizLib.general.ParameterHolder;
import rlVizLib.glueProxy.LocalGlue;
import agentShell.AgentShell;
import environmentShell.EnvironmentShell;


public class SampleExperiment
{
	protected static final int kNumEpisodes = 25;
	protected static int rlNumSteps[];
	protected static double rlReturn[];
	
	//cutoff
	protected static int maxStepsPerEpisode=1000;
	

	protected static void run(int numEpisodes) throws IOException
	{
		/*run for num_episode number of episodes and store the number of steps and return from each episode*/        	
		for(int x = 0; x < numEpisodes; ++x) {
			RLGlue.RL_episode(maxStepsPerEpisode);
			rlNumSteps[x] = RLGlue.RL_num_steps();
			rlReturn[x] = RLGlue.RL_return();
		}
	}

	public static void main(String [] args) throws IOException {
		/* Magic */
		EnvironmentShell E = new EnvironmentShell();
        AgentShell A = new AgentShell();
		RLGlue.setGlue(new LocalGlue(E, A));
		/* End Magic */

		String environmentName="SampleEnvironment - Java";
		String agentName="sarsaAgent - Java";
		loadEnvironment(environmentName);
		loadAgent(agentName);

		
		double avgSteps = 0.0;
		double avgReturn = 0.0;

		rlNumSteps = new int[SampleExperiment.kNumEpisodes];
		rlReturn = new double[SampleExperiment.kNumEpisodes];

		RLGlue.RL_init();
		
		run(kNumEpisodes);
		RLGlue.RL_cleanup();
		
		/*add up all the steps and all the returns*/
		for (int i = 0; i < SampleExperiment.kNumEpisodes; ++i) {
		    avgSteps += rlNumSteps[i];
		    avgReturn += rlReturn[i];
		}
		System.out.print("Steps per episode: ");
		for (int i = 0; i < SampleExperiment.kNumEpisodes; ++i) {
		    System.out.print(rlNumSteps[i]+" ");
		}
		System.out.println();

		System.out.print("Return per episode:");
		for (int i = 0; i < SampleExperiment.kNumEpisodes; ++i) {
		    System.out.print(rlReturn[i]+" ");
		}
		System.out.println();
		
		/*average steps and returns*/
		avgSteps /= (double)SampleExperiment.kNumEpisodes;
		avgReturn /= (double)SampleExperiment.kNumEpisodes;
		
		/*print out results*/
		System.out.println("\n-----------------------------------------------\n");
		System.out.println("Number of episodes: " + SampleExperiment.kNumEpisodes);
		System.out.println("Average number of steps per episode: " +  avgSteps);
		System.out.println("Average return per episode: " + avgReturn);
		System.out.println("-----------------------------------------------\n");
	}   
	
	/**
	Don't feel like you need to look below.
	
	It's magic stuff to make things easier.
	
	**/
	
	private static void loadAgent(String agentName){
				System.out.println("Available Agents: "+ getAgentNames());
				ParameterHolder pAgent=getAgentParams(agentName);
				AgentShellLoadRequest.Execute(agentName,pAgent);
	}
	private static void loadEnvironment(String envName){
				System.out.println("Available Environments: "+ getEnvNames());
				ParameterHolder pEnv=getEnvParams(envName);
				EnvShellLoadRequest.Execute(envName,pEnv);
	}
	
	
	
	private static Vector<String> getEnvNames(){
		EnvShellListResponse ListResponse = EnvShellListRequest.Execute();
		return ListResponse.getTheEnvList();
	}
	private static Vector<String> getAgentNames(){
		AgentShellListResponse ListResponse = AgentShellListRequest.Execute();
		return ListResponse.getTheAgentList();
	}
	
	private static ParameterHolder getEnvParams(String theEnvString){
		EnvShellListResponse ListResponse = EnvShellListRequest.Execute();
		int thisEnvIndex=ListResponse.getTheEnvList().indexOf(theEnvString);
		ParameterHolder p = ListResponse.getTheParamList().get(thisEnvIndex);
		return p;
	}
	private static ParameterHolder getAgentParams(String theAgentString){
		AgentShellListResponse ListResponse = AgentShellListRequest.Execute();
		int thisAgentIndex=ListResponse.getTheAgentList().indexOf(theAgentString);
		ParameterHolder p = ListResponse.getTheParamList().get(thisAgentIndex);
		return p;
	}
}

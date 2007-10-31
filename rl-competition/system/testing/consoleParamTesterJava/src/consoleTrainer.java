/* console Trainer for RL Competition
* Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
* 
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA. */
import rlglue.RLGlue;

public class consoleTrainer {

//A better example of an actual experiment is to call the runMountainCarExperiment() method
	public static void main(String[] args) throws InterruptedException {
		runMountainCarExperiment();
	}
	
	//A sample program to run through the different mountain car training values
	public static void runMountainCarExperiment(){
		for(int whichParamSet=0;whichParamSet<35;whichParamSet++){
			System.out.println("Param Set: "+whichParamSet);
			consoleTrainerHelper.loadMountainCar(whichParamSet);
			runExperiment();
		}
	}
	
	private static void runExperiment(){
		RLGlue.RL_init();
		
		int episodesToRun=500;
		int trialsToRun=10;
		
		int totalSteps=0;
		int last50EpisodeTotal=0;
		
		long startTime=System.currentTimeMillis();
		for(int i=0;i<trialsToRun;i++){
			int[] stepCounts=runEpisodes(episodesToRun,100000);
			totalSteps+=stepCounts[0];
			last50EpisodeTotal+=stepCounts[1];
		}
		long endTime=System.currentTimeMillis();
		
		long totalTime=endTime-startTime;
		
		long seconds=totalTime/1000;
		
		long stepsPerSecond=totalSteps/seconds;
		double averageSteps=(double)totalSteps/(double)(episodesToRun*trialsToRun);
		double averageLast50Steps=(double)last50EpisodeTotal/(double)(50*trialsToRun);
		System.out.printf("Average steps per episode averaged over %d trials = %.2f.\tLast 50 episodes is %.2f.\tTook %d seconds or %d steps per second \n",trialsToRun,averageSteps,averageLast50Steps,seconds,stepsPerSecond);

		RLGlue.RL_cleanup();
		
	}

	private static int[] runEpisodes(int episodeCount, int maxEpisodeLength){
		int[] stepCounts=new int[2];
		int maxSteps=0;
		int minSteps=100000000;
		
		for(int i=0;i<episodeCount;i++){
			RLGlue.RL_episode(maxEpisodeLength);
			
			int thisSteps=RLGlue.RL_num_steps();
			if(thisSteps>maxSteps)maxSteps=thisSteps;
			if(thisSteps<minSteps)minSteps=thisSteps;
			stepCounts[0]+=thisSteps;
			if(episodeCount-i<=50)
				stepCounts[1]+=thisSteps;
		}
		return stepCounts;
	}
	

	

}

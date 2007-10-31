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
		
		int episodesToRun=100;
		
		int totalSteps=runEpisodes(episodesToRun,100000);
		
		double averageSteps=(double)totalSteps/(double)episodesToRun;
		System.out.printf("Average steps per episode: %.2f\n",averageSteps);

		RLGlue.RL_cleanup();
		
	}

	private static int runEpisodes(int episodeCount, int maxEpisodeLength){
		int totalSteps=0;
		int maxSteps=0;
		int minSteps=100000000;
		
		for(int i=0;i<episodeCount;i++){
			RLGlue.RL_episode(maxEpisodeLength);
			
			int thisSteps=RLGlue.RL_num_steps();
			if(thisSteps>maxSteps)maxSteps=thisSteps;
			if(thisSteps<minSteps)minSteps=thisSteps;
			totalSteps+=thisSteps;		
		}
		System.out.println("\tMin Steps:"+minSteps);
		System.out.println("\tMax Steps:"+maxSteps);
		return totalSteps;
	}
	

	

}

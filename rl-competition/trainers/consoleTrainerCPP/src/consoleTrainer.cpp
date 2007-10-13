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
#include <sstream>
#include <iostream>
#include <string>
#include <RL_glue.h>
#include <vector>

#include "ParameterHolder.h"
#include "consoleTrainerHelper.h"

#include "consoleTrainer.h"


using std::string;
using std::cerr;

void runTrial();

int main(int argc, char *argv[])
{
//Another option:
		runMountainCarExperiment();
		exit(1);
//You should write a bit of code to look at command line args to see what you want to run I guess.
//		loadTetris(0);
		loadMountainCar(0);
//		loadHelicopter();


		RL_init();

		//This portion of the code is the same as a regular RL-Glue experiment program
		// we have not opened the visualizer yet, we are simply running the Agent through 
		// a number of episodes. Change the number of iterations in the loop to increase the number
		//of episodes run. You have 100 000 steps per episode to terminate in, other wise 
		// the glue terminates the episode
		int episodeCount=10;
		int maxEpisodeLength=20000;
		
		int totalSteps=0;

		for(int i=0;i<episodeCount;i++){
			RL_episode(maxEpisodeLength);
			std::cout<<"Episode: "<<i<<" steps: "<<RL_num_steps()<<std::endl;
			totalSteps+=RL_num_steps();
		}
		

		std::cout<<"Total Steps : "<<totalSteps<<std::endl;		
		

		//clean up the environment and end the program
		RL_cleanup();

    return 0;
}


	//A sample program to run through the different mountain car training values
	void runMountainCarExperiment(){
		for(int whichParamSet=0;whichParamSet<10;whichParamSet++){
			loadMountainCar(whichParamSet);
			runExperiment();
		}
	}

	int runEpisodes(int episodeCount, int maxEpisodeLength){
		int totalSteps=0;
		for(int i=0;i<episodeCount;i++){
			RL_episode(maxEpisodeLength);
			totalSteps+=RL_num_steps();
		}
		return totalSteps;
	}
	
	void runExperiment(){
		RL_init();
		
		int episodesToRun=10;
		
		int totalSteps=runEpisodes(episodesToRun,10000);
		
		double averageSteps=(double)totalSteps/(double)episodesToRun;
		printf("Average steps per episode: %.2f\n",averageSteps);

		RL_cleanup();
	}


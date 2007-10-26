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
#include <iostream>
#include <RL_glue.h>

#include "consoleTrainerHelper.h"

int main(int argc, char *argv[])
{
	int whichTrainingMDP = 0;
	// Uncomment ONE of the following lines to choose your experiment
	//loadTetris(whichTrainingMDP); //put the desired parameter set in where the 0 is
	loadMountainCar(whichTrainingMDP); //put the desired parameter set in where the 0 is
	//loadHelicopter();

	RL_init();

	int episodeCount=10; //number of episodes to run
	int maxEpisodeLength=20000; //set a maxEpisodeLength to ensure termination (RL_glue defaults to 100,000)
		
	int totalSteps=0;//counter for the total number of steps taken to finish all episodes
	//run the episodes with RL_episode(maxEpisodeLength)
	for(int i=0;i<episodeCount;i++){
		RL_episode(maxEpisodeLength);
		std::cout<<"Episode: "<<i<<" steps: "<<RL_num_steps()<<std::endl;//output to see how many steps the episode took
		totalSteps+=RL_num_steps();
	}
	std::cout<<"Total Steps : "<<totalSteps<<std::endl;		
	
	//clean up the environment and end the program
	RL_cleanup();

    return 0;
}

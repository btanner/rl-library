/* console Trainer for RL Competition
* Copyright (C) 2007, Marc Lanctot
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
import java.io.IOException;

public class realTimeStrategyTrainerJava
{
	protected static final int kNumEpisodes = 100;
	protected static int rlNumSteps[];
	protected static double rlReturn[];

	protected static void run(int numEpisodes) throws IOException
	{
		//run for num_episode number of episodes and store the number of steps and return from each episode
    	System.out.println("Running: "+kNumEpisodes+" episodes (each one is a .)");
		for(int x = 0; x < numEpisodes; ++x) {
			RLGlue.RL_episode(0);
			System.out.print(".");
			rlNumSteps[x] = RLGlue.RL_num_steps();
			rlReturn[x] = RLGlue.RL_return();
		}
    
	}

	public static void main(String [] args) throws IOException {
		double avgSteps = 0.0;
		double avgReturn = 0.0;

		rlNumSteps = new int[realTimeStrategyTrainerJava.kNumEpisodes];
		rlReturn = new double[realTimeStrategyTrainerJava.kNumEpisodes];


		///basic main loop

		for (int run = 0; run < 2; ++run)
		{
			RLGlue.RL_init();

			run(kNumEpisodes);
			RLGlue.RL_cleanup();

			// add up all the steps and all the returns
			for (int i = 0; i < realTimeStrategyTrainerJava.kNumEpisodes; ++i) {
				avgSteps += rlNumSteps[i];
				avgReturn += rlReturn[i];
			}

			// average steps and returns
			avgSteps /= (double)realTimeStrategyTrainerJava.kNumEpisodes;
			avgReturn /= (double)realTimeStrategyTrainerJava.kNumEpisodes;

			//print out results
			System.out.println("\n-----------------------------------------------\n");
			System.out.println("Number of episodes: " + realTimeStrategyTrainerJava.kNumEpisodes);
			System.out.println("Average number of steps per episode: " +  avgSteps);
			System.out.println("Average return per episode: " + avgReturn);
			System.out.println("-----------------------------------------------\n");
		}   
	}
}

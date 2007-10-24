# console Trainer for RL Competition
# Copyright (C) 2007, Brian Tanner brian@tannerpages.com (http://brian.tannerpages.com/)
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

import rlglue.RLGlue as RLGlue
from consoleTrainerHelper import *

def main():
	# Basically you want to do: (num is the problem number)
	# 	loadTetris(num) OR
	# 	loadMountainCar(num) OR
	# 	loadHelicopter()
	#
	# and then,
	#		runExperiment()
	# runMountainCarExperiment does this for each of the 9 versions of mountain car
	runMountainCarExperiment()


# A sample trainer to run through the different mountain car training values
def runMountainCarExperiment():
	#for paramSet in range(10):
	loadMountainCar(0)
	runExperiment()

# Run a certain number of episodes and return the total number of steps
def runEpisodes(episodeCount, maxEpisodeLength):
	totalSteps = 0
	for i in range(episodeCount):
		RLGlue.RL_episode(maxEpisodeLength)
		totalSteps += RLGlue.RL_num_steps()
	return totalSteps

# Runs 10 episodes and reports the average number of steps for each
def runExperiment():
	RLGlue.RL_init()
	episodesToRun = 10
	totalSteps = runEpisodes(episodesToRun,1000)
	averageSteps = float(totalSteps)/float(episodesToRun)
	print "Average steps per episode: %.2f\n" % (averageSteps)
	RLGlue.RL_cleanup()

if __name__ == "__main__":
	main()
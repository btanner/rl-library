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
	whichTrainingMDP = 0
	# Basically you want to do: (whichTrainingMDP is the problem number)
	# 	loadTetris(whichTrainingMDP) OR
	# 	loadMountainCar(whichTrainingMDP) OR
	# 	loadHelicopter(whichTrainingMDP)
	#
	loadMountainCar(whichTrainingMDP)

	# and then,
	#		just run the experiment:
	RLGlue.RL_init()
	episodesToRun = 10
	totalSteps = 0
	for i in range(episodesToRun):
		RLGlue.RL_episode(1000)
		totalSteps += RLGlue.RL_num_steps()
	averageSteps = float(totalSteps)/float(episodesToRun)
	print "Average steps per episode: %.2f\n" % (averageSteps)
	RLGlue.RL_cleanup()

if __name__ == "__main__":
	main()
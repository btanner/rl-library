#
# Brian Tanner took this agent from the rl-competition code at http://rl-competition.googlecode.com/
# 
# Copyright (C) 2007, Mark Lee
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

import random
import sys
from rlglue.agent.Agent import Agent
from rlglue.types import Action
from rlglue.types import Observation

class RandomAgent(Agent):
	
	#"2:e:2_[f,f]_[-1.2,0.6]_[-0.07,0.07]:1_[i]_[0,2]";
	def agent_init(self,taskSpec):
		self.action = Action()
		self.action_types = []
		random.seed(0)
		(version,episodic,states,actions,reward) = taskSpec.split(':')
		(stateDim,stateTypes,stateRanges) = states.split('_',2)
		(actionDim,actionTypes,actionRanges) = actions.split('_',2)
		actionTypes = actionTypes.strip()[1:-1]
		actionTypes = actionTypes.split(',')
		actionRanges = actionRanges.split('_')
		for i in range(int(actionDim)):
			actionRanges[i] = actionRanges[i].strip()[1:-1]
			(min_action,max_action) = actionRanges[i].split(',')
			self.action_types.append( (actionTypes[i],float(min_action),float(max_action)) )
	
	def agent_start(self,observation):
		self.randomify()
		return self.action
	
	def agent_step(self,reward, observation):
		self.randomify()
		return self.action
	
	def agent_end(self,reward):
		pass
	
	def agent_cleanup(self):
		pass
	
	def agent_freeze(self):
		pass
	
	def agent_message(self,inMessage):
		return None
	
	def randomify(self):
		self.action.intArray = []
		self.action.doubleArray = []
		for i in range(len(self.action_types)):
			(action_type,min_action,max_action) = self.action_types[i]
			if action_type == 'i':
				act = random.randrange(int(min_action),int(max_action+1))
				self.action.intArray.append(act)
			if action_type == 'f':
				self.action.doubleArray.append(random.uniform(min_action,max_action))


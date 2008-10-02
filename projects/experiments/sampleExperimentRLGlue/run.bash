#!/bin/bash

#
#	Runs an RL-Glue experiment without any RL-Viz fancies.
#


#Don't let this be too scary for you, it's not as bad as it looks.  Running an experiment consists of starting 4 different programs:
#	- RL_glue executable
#	- Environment
#	- Agent
#	- Experiment

# The fact that we're starting all 4 parts in a regular way is what makes this look complicated.  This files sources (includes)
#  /system/scripts/rl-library-includes.sh
# If you check out that file you'll see there is no magic.

#Path back to rl-library main directory from here
basePath=../../..
systemPath=$basePath/system

#Source a script that sets all important functions and variables
source $systemPath/scripts/rl-library-includes.sh

#Compile SampleExperiment
javac -d classes -classpath $systemPath/libs/RLGlueCodec/JavaRLGlueCodec.jar src/SampleExperiment.java

##Agent Stuff
#Item for the class path so your agent can be found
AgentExtraPath=$systemPath/dist/RandomAgent.jar		 	#Path to agent's class files
AgentPackageName=org.rlcommunity.agents.random  		#Name of the package the Agent is in
AgentClassName=RandomAgent    							#Name of the agent class
AgentMaxMemory=128M			 							#Max amount of memory to give the agent (Java default is often too low)

##Env Stuff
#Item for the class path so your env can be found
EnvExtraPath=$systemPath/dist/MountainCar.jar		 
EnvPackageName=org.rlcommunity.environments.mountaincar  	#Name of the package the environment is in.
EnvClassName=MountainCar    								#Name of the environment class
EnvMaxMemory=128M											#Max amount of memory to give the agent (Java default is often too low)


startRLGlueInBackGround
startJavaEnvironmentInBackGround $EnvExtraPath $EnvPackageName $EnvClassName $EnvMaxMemory
startJavaAgentInBackGround $AgentExtraPath $AgentPackageName $AgentClassName $AgentMaxMemory

java -classpath $systemPath/libs/RLGlueCodec/JavaRLGlueCodec.jar:./classes SampleExperiment

waitForAgentToDie
waitForEnvironmentToDie
waitForRLGlueToDie

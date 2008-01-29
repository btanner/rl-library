#!/bin/bash

#
#	Runs an RL-Viz experiment with NO AGENT. Minimal fancies.  It's networked (3 executables)
#


#Don't let this be too scary for you, it's not as bad as it looks.  Running an experiment consists of starting 4 different programs:
#	- RL_glue executable
#	- EnvironmentShell
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
javac -d classes -classpath $rlVizLibPath src/SampleExperiment.java



startRLGlueInBackGround
startEnvShellInBackGround
echo "-------- NOTE ---------"
echo "Start your RL-Glue agent"
echo "-----------------------"

java -classpath $rlVizLibPath:./classes SampleExperiment dynamicEnvironments

waitForEnvShellToDie
waitForRLGlueToDie

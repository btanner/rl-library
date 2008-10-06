#!/bin/bash
basePath=../../..
systemPath=$basePath/system

RLVizLibPath=$systemPath/libs/rl-viz/RLVizLib.jar


javac -classpath $RLVizLibPath sarsaAgent/*.java
cd sarsaAgent
jar cmf MANIFEST.MF ../JARS/sarsaAgent.jar sarsaAgent.class
cd ..
#Source a script that sets all important functions and variables
source $systemPath/scripts/rl-library-includes.sh

#Override where to look for agents and envs
	RLVIZ_LIB_PATH=./JARS
startLocalGuiTrainer


# RLVizPath=../system/libs/rl-viz
# AgentEnvPath=../system/dist
# java -Xmx1024M  -DRLVIZ_LIB_PATH=$AgentEnvPath -classpath $RLVizPath/RLVizApp.jar:$RLVizPath/RLVizLib.jar:$RLVizPath/EnvironmentShell.jar:$RLVizPath/AgentShell.jar btViz.LocalGraphicalDriver

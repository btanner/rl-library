#!/bin/bash
basePath=../../..
systemPath=$basePath/system

RLVizLibPath=$systemPath/libs/rl-viz/RLVizLib.jar
#Source a script that sets all important functions and variables
source $systemPath/scripts/rl-library-includes.sh

#Override where to look for agents and envs
RLVIZ_LIB_PATH=./JARS
startLocalGuiTrainer

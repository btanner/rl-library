#!/bin/bash
basePath=..
systemPath=$basePath/system

#Source a script that sets all important functions and variables
source $systemPath/scripts/rl-library-includes.sh


startLocalGuiTrainer


# RLVizPath=../system/libs/rl-viz
# AgentEnvPath=../system/dist
# java -Xmx1024M  -DRLVIZ_LIB_PATH=$AgentEnvPath -classpath $RLVizPath/RLVizApp.jar:$RLVizPath/RLVizLib.jar:$RLVizPath/EnvironmentShell.jar:$RLVizPath/AgentShell.jar btViz.LocalGraphicalDriver

#!/bin/bash

basePath=../../..
systemPath=$basePath/system

#Source a script that sets all important functions and variables
source $systemPath/scripts/rl-library-includes.sh

startRLGlueInBackGround
startEnvShellInBackGround

echo "-------- NOTE ---------"
echo "Start your RL-Glue agent"
echo "-----------------------"
startNetGuiTrainerDynamicEnvironmentStandardAgent

waitForEnvShellToDie
waitForRLGlueToDie


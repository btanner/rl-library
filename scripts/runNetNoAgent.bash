#!/bin/bash
basePath=..
systemPath=$basePath/system
#Source a script that sets all important functions and variables
source $systemPath/script-includes.sh

startRLGlueInBackGround
startEnvShellInBackGround
startGuiTrainer

waitForEnvShellToDie
waitForRLGlueToDie




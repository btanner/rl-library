#!/bin/bash


killall RL_Glue

echo "---------------------------"
echo "runViz.Bash -- making all the jars"
echo "---------------------------"
./makeAllJars.bash
echo "---------------------------"
echo "runViz.Bash -- making the training package"
echo "---------------------------"
./makeTrainingPackage.bash

cd ../trainingPack
pwd

export RLGLUEPATH=bin/RL-Glue.jar
export RLVIZPATH=bin/RL-Viz.jar
export RLVIZLIBPATH=bin/RL-VizLib.jar
export ENVSHELLPATH=bin/EnvShell.jar
export AGENTPATH=bin/agent/
export RLCLASSPATH=$RLGLUEPATH:$RLVIZPATH:$RLVIZLIBPATH:$ENVSHELLPATH:$AGENTPATH
export RLGLUEEXECPATH=bin/RL_Glue

echo "ClassPath $RLCLASSPATH"
echo "Running the Glue"
$RLGLUEEXECPATH &
echo "Running the Environment loader as: java -cp $RLCLASSPATH rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell &"
java -cp $RLCLASSPATH rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell &
java -cp $RLCLASSPATH rlglue.agent.AgentLoader GenericSarsaLambda.GenericSarsaLambda &
java -cp $RLCLASSPATH:./bin/ rlViz.GraphicalDriver
killall RL_Glue
killall java


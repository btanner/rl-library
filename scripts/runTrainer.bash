#!/bin/bash

cd ../
killall RL_Glue

./makeAllJars.bash
./makeTrainingPackage.bash

cd trainingPack

export RLGLUEPATH=bin/RL-Glue.jar
export RLVIZPATH=bin/RL-Viz.jar
export RLVIZLIBPATH=bin/RL-VizLib.jar
export ENVSHELLPATH=bin/EnvShell.jar
export AGENTPATH=bin/agent/
export RLCLASSPATH=$RLGLUEPATH:$RLVIZPATH:$RLVIZLIBPATH:$ENVSHELLPATH:$AGENTPATH
export RLGLUEEXECPATH=bin/RL_Glue

echo $RLCLASSPATH
echo "Running the Glue"
$RLGLUEEXECPATH &
echo "Running the Environment loader as: java -cp $RLCLASSPATH EnvironmentLoader environmentShell.EnvironmentShell &"
java -cp $RLCLASSPATH rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell &
java -cp $RLCLASSPATH rlglue.agent.AgentLoader GenericSarsaLambda.GenericSarsaLambda &
java -cp $RLCLASSPATH:./bin/ JavaTrainer
killall RL_Glue
killall java


#!/bin/bash


killall RL_glue

#Just for now
export RLTRAINPATH=./bin/RL-Train.jar
export AGENTPATH=./bin/agents/GenericSarsaLambdaJava/
export EXPERIMENTPATH=./bin/trainingExperiments/trainJava/
export RLCLASSPATH=$RLTRAINPATH:$AGENTPATH:$EXPERIMENTPATH

export GLUECOMMAND=bin/RL_glue
export ENVCOMMAND="java -cp $RLCLASSPATH rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell"
export EXPERIMENTCOMMAND="java -cp $RLCLASSPATH JavaTrainer"

echo "Executing Glue: $GLUECOMMAND"

$GLUECOMMAND &

echo "Executing Environment: $ENVCOMMAND"
$ENVCOMMAND &

echo "Executing Experiment: $EXPERIMENTCOMMAND"
$EXPERIMENTCOMMAND

killall RL_glue
killall java


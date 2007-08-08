#!/bin/bash


killall RL_glue

cd ../trainingPack
pwd

#Just for now
cp ~/ProgrammingProjects/rl-glue/Examples/Network_Java/bin/RL_glue bin/RL_glue
export RLTRAINPATH=./bin/RL-Train.jar
export AGENTPATH=./bin/agents/GenericSarsaLambdaJava/
export RLCLASSPATH=$RLTRAINPATH:$AGENTPATH

export GLUECOMMAND=bin/RL_glue
export ENVCOMMAND="java -cp $RLCLASSPATH rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell"
export VIZCOMMAND="java -cp $RLCLASSPATH:./bin/ rlViz.GraphicalDriver"

echo "Executing Glue: $GLUECOMMAND"

$GLUECOMMAND &

echo "Executing Environment: $ENVCOMMAND"
$ENVCOMMAND &

echo "Executing Viz: $VIZCOMMAND"
$VIZCOMMAND

killall RL_glue
killall java


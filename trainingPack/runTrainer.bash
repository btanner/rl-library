#!/bin/bash


killall RL_glue

cd ../trainingPack
pwd

#Just for now
cp ~/ProgrammingProjects/rl-glue/Examples/Network_Java/bin/RL_glue bin/RL_glue
export RLTRAINPATH=./bin/RL-Train.jar
export AGENTPATH=./bin/agents/GenericSarsaLambdaJava/
export EXPERIMENTPATH=./bin/trainingExperiments/trainJava/
export RLCLASSPATH=$RLTRAINPATH:$AGENTPATH:$EXPERIMENTPATH

export GLUECOMMAND=bin/RL_glue
export ENVCOMMAND="java -cp $RLCLASSPATH rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell"
export AGENTCOMMAND="java -cp $RLCLASSPATH rlglue.agent.AgentLoader GenericSarsaLambda.GenericSarsaLambda"
export EXPERIMENTCOMMAND="java -cp $RLCLASSPATH JavaTrainer"

echo "Executing Glue: $GLUECOMMAND"

$GLUECOMMAND &

echo "Executing Environment: $ENVCOMMAND"
$ENVCOMMAND &

echo "Executing Agent: $AGENTCOMMAND"
$AGENTCOMMAND &

echo "Executing Experiment: $EXPERIMENTCOMMAND"
$EXPERIMENTCOMMAND

killall RL_glue
killall java


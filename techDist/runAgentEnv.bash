#!/bin/bash


killall RL_glue

export RLTRAINPATH=./bin/RL-Train.jar
export AGENTPATH=./bin/agents/GenericSarsaLambdaJava/
export RLCLASSPATH=$RLTRAINPATH:$AGENTPATH

export GLUECOMMAND=bin/RL_glue
export ENVCOMMAND="java -cp $RLCLASSPATH rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell"
export AGENTCOMMAND="java -cp $RLCLASSPATH rlglue.agent.AgentLoader GenericSarsaLambda.GenericSarsaLambda"


echo "Executing Environment: $ENVCOMMAND"
$ENVCOMMAND &
echo "Executing Agent: $AGENTCOMMAND"
$AGENTCOMMAND &

echo "Executing Glue: $GLUECOMMAND"
$GLUECOMMAND 

killall RL_glue
killall java


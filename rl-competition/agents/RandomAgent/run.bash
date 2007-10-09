#/bin/bash

#Variables
compLib=../../libraries/RLVizLib.jar


java -Xmx128M -cp $compLib:./bin rlglue.agent.AgentLoader RandomAgent.RandomAgent

echo "-- Agent was killed"

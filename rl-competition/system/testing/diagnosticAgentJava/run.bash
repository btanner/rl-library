#/bin/bash

#Variables
compLib=../../system/libraries/RLVizLib.jar


java -Xmx128M -cp $compLib:./bin rlglue.agent.AgentLoader RandomAgent.RandomAgent

echo "-- Agent is complete"

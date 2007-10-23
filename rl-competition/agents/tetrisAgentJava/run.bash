#/bin/bash

#Variables
compLib=../../system/libraries/RLVizLib.jar


java -Xmx128M -cp $compLib:./bin rlglue.agent.AgentLoader TetrisAgent.TetrisAgent

echo "-- Agent was killed"

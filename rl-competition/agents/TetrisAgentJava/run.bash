#/bin/bash

#Variables
compLib=../../libraries/RLVizLib.jar


java -Xmx128M -cp $compLib:./bin rlglue.agent.AgentLoader TetrisAgent.TetrisAgent

echo "-- Agent was killed"

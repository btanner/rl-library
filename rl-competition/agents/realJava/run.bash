#/bin/bash
#Variables
compLib=../../libraries/RLVizLib.jar


java -Xmx128M -cp $compLib:./bin rlglue.agent.AgentLoader OLAgent

echo "-- Agent is complete"


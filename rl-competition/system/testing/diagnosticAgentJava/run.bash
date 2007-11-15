#/bin/bash

#Variables
compLib=../../../system/libraries/RLVizLib.jar


java -Xmx128M -cp $compLib:./bin rlglue.agent.AgentLoader DiagnosticAgent.DiagnosticAgent

echo "-- Agent is complete"

#/bin/bash

#Variables
compLib=../../../../system/libs/rl-viz/RLVizLib.jar


java -Xmx128M -cp $compLib:./bin org.rlcommunity.rlglue.codec.util.AgentLoader DiagnosticAgent.DiagnosticAgent

echo "-- Agent is complete"

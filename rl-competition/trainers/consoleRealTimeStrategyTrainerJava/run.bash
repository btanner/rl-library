#/bin/bash


#Variables
basePath=../..
libPath=$basePath/libraries

compLib=$libPath/RLVizLib.jar

glueExe=$libPath/RL_glue
rtsEXE=$basePath/domains/realTimeStrategy/bin/rlgenv

$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"

$rtsEXE &
rtsPID=$!
echo "Starting up real time strategy game - PID=$rtsPID"

java -Xmx128M -cp $compLib:./bin/ realTimeStrategyTrainerJava

echo "-- Console Real Time Strategy Trainer finished"

echo "-- Waiting for the real time strategy game to die..."
wait $rtsPID
echo "   + real time strategy game terminated"
echo "-- Waiting for the Glue to die..."
wait $gluePID
echo "   + Glue terminated"



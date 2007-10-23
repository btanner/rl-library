#/bin/bash


#Variables
systemPath=../../system
libPath=$systemPath/libraries
RLVIZ_LIB_PATH=$PWD/$libPath

compLib=$libPath/RLVizLib.jar
envShellLib=$libPath/EnvironmentShell.jar

glueExe=$systemPath/RL_glue

$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"


java -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH -Xmx128M -cp $compLib:$envShellLib rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell &
envShellPID=$!
echo "Starting up dynamic environment loader - PID=$envShellPID"

python ./RL_experiment

echo "-- Console Trainer finished"

echo "-- Waiting for the Environment to die..."
wait $envShellPID
echo "   + Environment terminated"
echo "-- Waiting for the Glue to die..."
wait $gluePID
echo "   + Glue terminated"

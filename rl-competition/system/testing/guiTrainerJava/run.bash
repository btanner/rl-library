#/bin/bash


#Variables
basePath=../../..
systemPath=$basePath/system
libPath=$systemPath/libraries

compLib=$libPath/RLVizLib.jar

glueExe=$systemPath/RL_glue
guiLib=$libPath/forms-1.1.0.jar
envShellLib=$libPath/EnvironmentShell.jar

RLVIZ_LIB_PATH=$PWD/$libPath

$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"

java -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH -cp $compLib:$envShellLib rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell &
envShellPID=$!
echo "Starting up dynamic environment loader - PID=$envShellPID"

java -Xrunshark -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH -cp $compLib:$guiLib:./bin/rlViz.jar btViz.GraphicalDriver

echo "-- Visualizer is finished"

echo "-- Waiting for the Environment to die..."
wait $envShellPID
echo "   + Environment terminated"
echo "-- Waiting for the Glue to die..."
wait $gluePID
echo "   + Glue terminated"



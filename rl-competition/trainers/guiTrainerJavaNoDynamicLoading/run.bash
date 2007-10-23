#/bin/bash


#Variables
#Variables
basePath=../..
systemPath=$basePath/system
libPath=$systemPath/libraries

compLib=$libPath/RLVizLib.jar

glueExe=$systemPath/RL_glue
guiLib=$libPath/forms-1.1.0.jar


$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"


java -Xmx128M -cp $compLib:$guiLib:./bin/rlViz.jar btViz.NoDynamicLoadingGraphicalDriver

echo "-- Visualizer is finished"


echo "-- Waiting for the Glue to die..."
wait $gluePID
echo "   + Glue terminated"



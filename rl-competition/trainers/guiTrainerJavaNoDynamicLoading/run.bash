#/bin/bash


#Variables
libPath=../../libraries

compLib=$libPath/RLVizLib.jar
guiLib=$libPath/forms-1.1.0.jar

glueExe=$libPath/RL_glue

$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"


java -Xmx128M -cp $compLib:$guiLib:./bin/rlViz.jar btViz.NoDynamicLoadingGraphicalDriver

echo "-- Visualizer is finished"


echo "-- Waiting for the Glue to die..."
wait $gluePID
echo "   + Glue terminated"



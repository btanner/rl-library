#/bin/bash


#Variables
#Variables
basePath=../..
systemPath=$basePath/system
libPath=$systemPath/libraries
RLVIZ_LIB_PATH=$PWD/$libPath



compLib=$libPath/RLVizLib.jar

glueExe=$systemPath/RL_glue
rtsExe=$systemPath/bin/rlgenv

guiLib=$libPath/forms-1.1.0.jar



$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"

$rtsExe &
rtsPID=$!
echo "Starting up real time strategy - PID=$rtsPID"

#
#Start the visualizer program
#
macAboutNameCommand=-Dcom.apple.mrj.application.apple.menu.about.name=RLVizApp
java -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH $macAboutNameCommand -Xmx128M -cp $compLib:$guiLib:./bin/rlViz.jar btViz.NoDynamicLoadingGraphicalDriver

echo "-- Visualizer is finished"

echo "-- Waiting for real time strategy to die..."
wait $rtsPID

echo "-- Waiting for the Glue to die..."
wait $gluePID
echo "   + Glue terminated"



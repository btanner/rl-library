#/bin/bash


#Variables
libPath=../../libraries

compLib=$libPath/RLVizLib.jar
guiLib=$libPath/forms-1.1.0.jar
envShellLib=$libPath/EnvironmentShell.jar

glueExe=$libPath/RL_glue

$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"


java -Xmx128M -cp $compLib:$envShellLib rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell &
envShellPID=$!
echo "Starting up dynamic environment loader - PID=$envShellPID"

java -Xmx128M -cp $compLib:$guiLib:./bin/rlViz.jar btViz.GraphicalDriver

echo "-- Visualizer was killed"
echo "-- Killing RL_glue - PID=$gluePID"
kill $gluePID
echo " --Killing dynamic environment loader - PID=$envShellPID"
kill $envShellPID
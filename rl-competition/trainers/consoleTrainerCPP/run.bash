#/bin/bash


#Variables
libPath=../../libraries

compLib=$libPath/RLVizLib.jar
envShellLib=$libPath/EnvironmentShell.jar

glueExe=$libPath/RL_glue

$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"

java -Xmx128M -cp $compLib:$envShellLib rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell &
envShellPID=$!
echo "Starting up dynamic environment loader - PID=$envShellPID"

./bin/consoleTrainer

echo "-- Console Trainer was killed"
echo "-- Killing RL_glue - PID=$gluePID"
kill $gluePID
echo " --Killing dynamic environment loader - PID=$envShellPID"
kill $envShellPID
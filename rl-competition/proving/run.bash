#/bin/bash


#Variables
basePath=../
systemPath=$basePath/system
libPath=$systemPath/libraries
provingPath=$systemPath/proving

compLib=$libPath/RLVizLib.jar

envShellLib=$libPath/EnvironmentShell.jar 




# glueExe=$systemPath/RL_glue
# 
# $glueExe &
# gluePID=$!
# echo "Starting up RL-glue - PID=$gluePID"
# 
#java -DRLVIZ_LIB_PATH=$PWD/$provingPath -Xmx128M -cp $compLib:$envShellLib rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell  &
#envShellPID=$!

#echo "Starting up dynamic environment loader - PID=$envShellPID"

java -Xmx128M -cp $compLib -jar ./bin/Proving.jar

echo "-- Console Trainer finished"

#echo "-- Waiting for the Environment to die..."
#wait $envShellPID
#echo "   + Environment terminated"
# echo "-- Waiting for the Glue to die..."
# wait $gluePID
# echo "   + Glue terminated"
# 


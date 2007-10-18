#/bin/bash


#Variables
libPath=../../libraries

compLib=$libPath/RLVizLib.jar
jarPath=$libPath/envJars
envJarFile=$jarPath/GeneralizedMountainCar.jar

java -Xmx128M -cp $compLib:$envJarFile rlglue.environment.EnvironmentLoader GeneralizedMountainCar.GeneralizedMountainCar
envShellPID=$!
echo "Starting up dynamic environment loader - PID=$envShellPID"

echo "-- Environment is finished"



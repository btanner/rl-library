#!/bin/bash
echo "Making the Glue Jar"
./makeGlueJar.bash
echo "---------------------------"
echo "Making the Viz Lib Jar"
echo "---------------------------"
./makeVizLibJar.bash
echo "---------------------------"
echo "Making the Environment Jars"
echo "---------------------------"
./makeEnvJars.bash
echo "---------------------------"
echo "Making the training package"
echo "---------------------------"
./makeTrainingPackage.bash
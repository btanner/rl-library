#!/bin/bash

#This script should be fun from the workspace root directory
echo "============================"
echo "Running makeBigJar.bin"
echo "Is Comprised of: "
echo " -- RL Glue Codecs from the rl-glue project"
echo " -- Vizlib files from the RL-Viz project"
echo " -- A visualizer from the RL-Viz project"
echo " -- A dynamic environment loader for Java compatible with RL-Viz"
echo "============================"

rm -Rf bigJarTmp
mkdir bigJarTmp
mkdir bigJarTmp/bin/

echo " --Getting the .class files together"
cp -R ./RLGlueJava/bin/* bigJarTmp/bin/
cp -R ./RL-VizLib/bin/* bigJarTmp/bin/
cp -R ./RL-Viz/bin/* bigJarTmp/bin/
cp -R ./EnvironmentShell/bin/* bigJarTmp/bin/

cd bigJarTmp/bin/
echo " --Making RL-Train.jar (quietly)"
jar -cf ../../RL-Train.jar *
cd ../../
rm -Rf bigJarTmp
#jar -cvf ../../trainingPack/bin/RL-Viz.jar *

echo " -- makeBigJar.bash completed"
echo "------------------------------"

#!/bin/bash

echo "============================"
echo "Running makeBigJar.bin"
echo "============================"

rm -Rf bigJarTmp
mkdir bigJarTmp
mkdir bigJarTmp/bin/

cp -R ../RLGlueJava/bin/* bigJarTmp/bin/
cp -R ../RL-VizLib/bin/* bigJarTmp/bin/
cp -R ../RL-Viz/bin/* bigJarTmp/bin/

cd bigJarTmp/bin/
jar -cvf ../RL-Train.jar *
#jar -cvf ../../trainingPack/bin/RL-Viz.jar *


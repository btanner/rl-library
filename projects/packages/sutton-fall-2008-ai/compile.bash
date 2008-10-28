#!/bin/bash
basePath=.
systemPath=$basePath/system

RLVizLibPath=$systemPath/libs/rl-viz/RLVizLib.jar

cd sarsaAgent
./compile.bash
cd ..

cd sampleEnvironment
./compile.bash
cd ..

cd myEnvironment
./compile.bash
cd ..

cd experiment
./compile.bash
cd ..


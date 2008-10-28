#!/bin/bash
basePath=../../..
systemPath=$basePath/system

RLVizLibPath=$systemPath/libs/rl-viz/RLVizLib.jar

cd sarsaAgent
./compile.bash
cd ..

cd gridWorldEnv
./compile.bash
cd ..

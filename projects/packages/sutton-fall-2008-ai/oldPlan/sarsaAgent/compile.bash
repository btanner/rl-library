#!/bin/bash
basePath=../../../..
systemPath=$basePath/system

RLVizLibPath=$systemPath/libs/rl-viz/RLVizLib.jar


rm -f src/*.class
javac -classpath $RLVizLibPath src/*.java
cd src
jar cmf MANIFEST.MF ../../JARS/sarsaAgent.jar *.class

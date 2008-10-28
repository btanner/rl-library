#!/bin/bash
basePath=..
AgentEnvJarPath=$basePath/JARS
systemPath=$basePath/system
RLVizLibPath=$systemPath/libs/rl-viz
RLVizLibJar=$RLVizLibPath/RLVizLib.jar
AgentShellJar=$RLVizLibPath/AgentShell.jar
EnvironmentShellJar=$RLVizLibPath/EnvironmentShell.jar
AllLibJars=$RLVizLibJar:$AgentShellJar:$EnvironmentShellJar

#Source a script that sets all important functions and variables
source $systemPath/scripts/rl-library-includes.sh

java -Xmx128M -DRLVIZ_LIB_PATH=$AgentEnvJarPath -classpath $AllLibJars:bin SampleExperiment



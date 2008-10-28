#!/bin/bash
basePath=..
systemPath=$basePath/system
RLVizLibPath=$systemPath/libs/rl-viz
RLVizLibJar=$RLVizLibPath/RLVizLib.jar
AgentShellJar=$RLVizLibPath/AgentShell.jar
EnvironmentShellJar=$RLVizLibPath/EnvironmentShell.jar
AllJars=$RLVizLibJar:$AgentShellJar:$EnvironmentShellJar
rm -f src/*.class
javac -classpath $AllJars src/*.java -d bin

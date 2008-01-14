#/bin/bash
RLVizPath=../system/libs/rl-viz
AgentEnvPath=../system/dist
java -Xmx1024M  -DRLVIZ_LIB_PATH=$AgentEnvPath -classpath $RLVizPath/RLVizApp.jar:$RLVizPath/RLVizLib.jar:$RLVizPath/EnvironmentShell.jar:$RLVizPath/AgentShell.jar btViz.LocalGraphicalDriver

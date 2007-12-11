#/bin/bash
RLVizPath=../system/libs/rl-viz
AgentEnvPath=../system/dist
java -Xmx1024M  -DRLVIZ_LIB_PATH=$AgentEnvPath -classpath $RLVizPath/rlVizApp.jar:$RLVizPath/rlVizLib.jar:$RLVizPath/EnvironmentShell.jar:$RLVizPath/AgentShell.jar btViz.LocalGraphicalDriver
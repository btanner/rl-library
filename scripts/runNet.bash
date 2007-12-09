#/bin/bash
glueRoot=../system/rl-glue
glueExe=$glueRoot/RL-Glue/bin/RL_glue

RLVizDir=../system/libs/rl-viz
RLVizJar=$RLVizDir/RLVizApp.jar
RLVizLibJar=$RLVizDir/RLVizLib.jar
EnvShellJar=$RLVizDir/EnvironmentShell.jar
AgentShellJar=$RLVizDir/AgentShell.jar


$glueExe &
java -Xmx128M -DRLVIZ_LIB_PATH=../system/dist -classpath $RLVizJar:$RLVizLibJar btViz.NetGraphicalDriverBothDynamic &
java -Xmx128M -DRLVIZ_LIB_PATH=../system/dist -classpath $RLVizLibJar:$AgentShellJar rlglue.agent.AgentLoader agentShell.AgentShell &
java -Xmx128M -DRLVIZ_LIB_PATH=../system/dist -classpath $RLVizLibJar:$EnvShellJar rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell 

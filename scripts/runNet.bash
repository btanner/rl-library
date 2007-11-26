#/bin/bash
glueRoot=../system/rl-glue
glueExe=$glueRoot/RL-Glue/bin/RL_glue

RLVizDir=../system/libs
RLVizJar=$RLVizDir/RLVizApp.jar


$glueExe &
java -Xmx128M -DRLVIZ_LIB_PATH=../system/dist -classpath $RLVizJar btViz.NetGraphicalDriverBothDynamic &
java -Xmx128M -DRLVIZ_LIB_PATH=../system/dist -classpath $RLVizJar rlglue.agent.AgentLoader agentShell.AgentShell &
java -Xmx128M -DRLVIZ_LIB_PATH=../system/dist -classpath $RLVizJar rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell 

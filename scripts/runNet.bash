#/bin/bash
glueRoot=../rl-glue
glueExe=$glueRoot/RL-Glue/bin/RL_glue

RLVizDir=../libs
RLVizJar=$RLVizDir/RLVizApp.jar


$glueExe &
java -Xmx128M -DRLVIZ_LIB_PATH=../dist -classpath $RLVizJar btViz.NetGraphicalDriverBothDynamic &
java -Xmx128M -DRLVIZ_LIB_PATH=../dist -classpath $RLVizJar rlglue.agent.AgentLoader agentShell.AgentShell &
java -Xmx128M -DRLVIZ_LIB_PATH=../dist -classpath $RLVizJar rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell 

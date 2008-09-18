#/bin/bash
RLVizDir=../system/libs/rl-viz
RLVizJar=$RLVizDir/RLVizApp.jar
AgentDir=../system/dist

RLVizLibJar=$RLVizDir/RLVizLib.jar
EnvShellJar=$RLVizDir/EnvironmentShell.jar
AgentJar=$AgentDir/btannerAgentLib.jar

java -Xmx128M -DRLVIZ_LIB_PATH=../system/dist -classpath $RLVizLibJar:$AgentJar rlglue.agent.AgentLoader org.rlcommunity.btanner.agents.EpsilonGreedyCMACSarsaLambda &

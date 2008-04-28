#Environments and Agent Jars are generally in here
jarsPath=$systemPath/dist

#Path to all of the RL-Viz libraries and stuff
libPath=$systemPath/libs/rl-viz
rlVizLibPath=$libPath/RLVizLib.jar
vizAppLib=$libPath/RLVizApp.jar
envShellLib=$libPath/EnvironmentShell.jar
agentShellLib=$libPath/AgentShell.jar

glueExe=$systemPath/rl-glue/RL-Glue/bin/RL_glue

guiLib=$libPath/forms-1.1.0.jar

pKillScript=$systemPath/bin/pkill

RLVIZ_LIB_PATH=$PWD/$jarsPath
ENV_CLASSPATH=$rlVizLibPath:$envShellLib
AGENT_CLASSPATH=$rlVizLibPath:$agentShellLib
VIZ_CLASSPATH=$rlVizLibPath:$guiLib:$vizAppLib:$envShellLib:$agentShellLib

setMacAboutName ()
{ # This is about as simple as functions get.
if [ "$(uname)" == "Darwin" ]; then
	osExtras="-Dcom.apple.mrj.application.apple.menu.about.name=RLVizApp"
fi
}

setCygwinPaths ()
{
if [[ `uname` == CYGWIN* ]]
then
	glueExe="$glueExe.exe"
	RLVIZ_LIB_PATH=`cygpath -wp $RLVIZ_LIB_PATH`
	ENV_CLASSPATH=`cygpath -wp $ENV_CLASSPATH`
	VIZ_CLASSPATH=`cygpath -wp $VIZ_CLASSPATH`
	vizAppLib=`cygpath -wp $vizAppLib`
	rlVizLibPath=`cygpath -wp $rlVizLibPath`
fi
}

makeLine(){
echo "--------------------------------------------------------------------"
}

checkIfRLGlueExists(){
if [ ! -e "$glueExe" ]       # Check if file exists.
  then
	makeLine
    echo "RL_glue not found at $glueExe.  "
	echo "Did you remember to \"make rl-glue\" or \"ant rl-glue\" from the root rl-library directory?"
  	makeLine
    exit
   fi
}


startEnvShellInBackGround(){
java -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH -classpath $ENV_CLASSPATH rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell &
envShellPID=$!
echo "-- Starting up dynamic environment loader - PID=$envShellPID"
}

waitForEnvShellToDie(){
echo "-- Waiting for the dynamic environment loader to die..."
wait $envShellPID
echo "++ Dynamic environment loader terminated"
}

startAgentShellInBackGround(){
java -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH -classpath $AGENT_CLASSPATH rlglue.agent.AgentLoader agentShell.AgentShell &
agentShellPID=$!
echo "-- Starting up dynamic agent loader - PID=$agentShellPID"
}

waitForAgentShellToDie(){
echo "-- Waiting for the dynamic agent loader to die..."
wait $agentShellPID
echo "++ Dynamic agent loader terminated"
}

startLocalGuiTrainer(){
echo "-- Starting up Gui Trainer"
java -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH $osExtras -classpath $VIZ_CLASSPATH btViz.LocalGraphicalDriver
echo "++ Gui Trainer is finished"
}
startLocalGuiTrainerBothViz(){
echo "-- Starting up Gui Trainer"
java -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH $osExtras -classpath $VIZ_CLASSPATH btViz.LocalGraphicalDriverBothDynamicBothViz
echo "++ Gui Trainer is finished"
}

startNetGuiTrainer(){
echo "-- Starting up Networked Gui Trainer"
java -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH $osExtras -classpath $VIZ_CLASSPATH btViz.NetGraphicalDriverBothDynamic
echo "++ Gui Trainer is finished"
}

startNetGuiTrainerDynamicEnvironmentStandardAgent(){
echo "-- Starting up Networked Gui Trainer"
java -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH $osExtras -classpath $VIZ_CLASSPATH btViz.NetGraphicalDriverDynamicEnvStandardAgent
echo "++ Gui Trainer is finished"
}



killRLGlue(){
$pKillScript RL_glue
}
startRLGlueInBackGround(){
checkIfRLGlueExists
#Make sure its not running from before
killRLGlue
$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"
}

waitForRLGlueToDie(){
echo "-- Waiting for RL_glue to die..."
wait $gluePID
echo "++ RL_glue terminated"
}

startJavaAgent(){
privateExtraPath="$1"
privatePackageName="$2"
privateClassName="$3"
privateMaxMemory="$4"

agentPath=$rlVizLibPath:$privateExtraPath
#Sortof a hack for now
if [[ `uname` == CYGWIN* ]]
then 
	agentPath=`cygpath -wp $agentPath`
fi
java -Xmx$privateMaxMemory -cp $agentPath rlglue.agent.AgentLoader $privatePackageName.$privateClassName
}

startJavaAgentInBackGround(){
privateExtraPath="$1"
privatePackageName="$2"
privateClassName="$3"
privateMaxMemory="$4"

agentPath=$rlVizLibPath:$privateExtraPath
#Sortof a hack for now
if [[ `uname` == CYGWIN* ]]
then 
	agentPath=`cygpath -wp $agentPath`
fi

java -Xmx$privateMaxMemory -cp $agentPath rlglue.agent.AgentLoader $privatePackageName.$privateClassName &
agentPID=$!
}
waitForAgentToDie(){
echo "-- Waiting for the agent to die..."
wait $agentPID
echo "++ Agent terminated"
}

startJavaEnvironmentInBackGround(){
privateExtraPath="$1"
privatePackageName="$2"
privateClassName="$3"
privateMaxMemory="$4"

envPath=$rlVizLibPath:$privateExtraPath
#Sortof a hack for now
if [[ `uname` == CYGWIN* ]]
then 
	envPath=`cygpath -wp $envPath`
fi

java -Xmx$privateMaxMemory -cp $envPath rlglue.environment.EnvironmentLoader $privatePackageName.$privateClassName &
envPID=$!
}
waitForEnvironmentToDie(){
echo "-- Waiting for the environment to die..."
wait $envPID
echo "++ Agent terminated"
}

setMacAboutName
setCygwinPaths

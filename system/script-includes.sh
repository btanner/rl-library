libPath=$systemPath/libs
distPath=$systemPath/dist
vizAppLib=$libPath/rl-viz/RLVizApp.jar
vizLib=$libPath/rl-viz/RLVizLib.jar

glueExe=$systemPath/rl-glue/RL-Glue/bin/RL_glue
rtsExe=$basePath/domains/realTimeStrategy/bin/rlgenv

envShellLib=$libPath/rl-viz/EnvironmentShell.jar
pKillScript=$systemPath/bin/pkill

RLVIZ_LIB_PATH=$PWD/$distPath
ENV_CLASSPATH=$vizLib:$envShellLib
VIZ_CLASSPATH=$vizLib:$vizAppLib

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
	rtsExe="$rtsExe.exe"
	vizLib=`cygpath -wp $vizLib`
	RLVIZ_LIB_PATH=`cygpath -wp $RLVIZ_LIB_PATH`
	ENV_CLASSPATH=`cygpath -wp $ENV_CLASSPATH`
	VIZ_CLASSPATH=`cygpath -wp $VIZ_CLASSPATH`
	vizAppLib=`cygpath -wp $vizAppLib`
fi
}

makeLine(){
echo "--------------------------------------------------------------------"
}
checkIfRTSExists(){
if [ ! -e "$rtsExe" ]       # Check if file exists.
  then
  	makeLine
    echo "Error :: rlgenv not found at $rtsExe."
  	echo "Did you remember to \"make rlgenv\" in domains/realTimeStrategy ?"
  	makeLine
    exit 1
   fi
}

checkIfRLGlueExists(){
if [ ! -e "$glueExe" ]       # Check if file exists.
  then
	makeLine
    echo "RL_glue not found at $glueExe.  "
	echo "Did you remember to \"make \" from the main competition directory?"
  	makeLine
    exit
   fi
}


startRTSInBackGround(){
checkIfRTSExists
$rtsExe &
rtsPID=$!
echo "Starting up Real Time Strategy - PID=$rtsPID"
}
waitForRTSToDie(){
echo "-- Waiting for  Real Time Strategy to die..."
wait $rtsPID
echo "++ Real Time Strategy terminated"
}



startEnvShellInBackGround(){
java -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH -classpath $ENV_CLASSPATH rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell &
envShellPID=$!
echo "-- Starting up dynamic environment loader - PID=$envShellPID"
}

startGuiTrainer(){
echo "-- Starting up Gui Trainer"
java -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH $osExtras -classpath $VIZ_CLASSPATH btViz.GraphicalDriver
echo "++ Gui Trainer is finished"
}

startRTSGuiTrainer(){
echo "-- Starting up Gui Trainer"
java -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH $osExtras -Xmx128M -classpath $VIZ_CLASSPATH btViz.NoDynamicLoadingGraphicalDriver
echo "++ Gui Trainer is finished"
}

waitForEnvShellToDie(){
echo "-- Waiting for the dynamic environment loader to die..."
wait $envShellPID
echo "++ Dynamic environment loader terminated"
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

agentPath=$vizLib:$privateExtraPath
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

agentPath=$vizLib:$privateExtraPath
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
setMacAboutName
setCygwinPaths

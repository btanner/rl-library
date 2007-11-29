#/bin/bash


#Variables
basePath=.
systemPath=$basePath/system
libPath=$systemPath/libraries

compLib=$libPath/RLVizLib.jar

glueExe=$systemPath/RL_glue
guiLib=$libPath/forms-1.1.0.jar
envShellLib=$libPath/EnvironmentShell.jar

agentPath=agents/randomAgentJava
demoPath=trainers/guiTrainerJava

			   
RLVIZ_LIB_PATH=$PWD/$libPath

#
# First do a quick check to see if the RL_glue exe exists.  If it does not, they probably have not done a "make all" yet
#
if [ ! -e "$glueExe" ]       # Check if file exists.
  then
    echo "RL_glue not found at $glueExe.  Did you remember to \"make all\"?"; echo
    exit 1                $Quit
   fi
#
#Start the RL_glue program  in the background and get it's process id
#
$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"

#
#Start the random agent in the background and get it's process id
#
java -Xmx128M -cp $compLib:$agentPath/bin rlglue.agent.AgentLoader RandomAgent.RandomAgent &
agentPID=$!


#
#Start the environment in the background and get it's process id
#
java -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH -cp $compLib:$envShellLib rlglue.environment.EnvironmentLoader environmentShell.EnvironmentShell &
envShellPID=$!
echo "Starting up dynamic environment loader - PID=$envShellPID"

#
#Start the demo visualizer program
#
macAboutNameCommand=-Dcom.apple.mrj.application.apple.menu.about.name=RLVizApp
java -Xcheck:jni -Xmx128M -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH $macAboutNameCommand -cp $compLib:$guiLib:$demoPath/bin/RLVizApp.jar btViz.GraphicalDriver

echo "-- Visualizer is finished"

echo "-- Waiting for the Environment to die..."
wait $envShellPID
echo "   + Environment terminated"
echo "-- Waiting for the Agent to die..."
wait $agentPID
echo "   + Agent terminated"
echo "-- Waiting for the Glue to die..."
wait $gluePID
echo "   + Glue terminated"



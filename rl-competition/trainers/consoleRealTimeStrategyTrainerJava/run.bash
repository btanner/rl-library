#/bin/bash


#Variables
basePath=../..
systemPath=$basePath/system
libPath=$systemPath/libraries
RLVIZ_LIB_PATH=$PWD/$libPath

compLib=$libPath/RLVizLib.jar

glueExe=$systemPath/RL_glue
rtsEXE=$basePath/domains/realTimeStrategy/bin/rlgenv

#
# First do a quick check to see if the RL_glue exe exists.  If it does not, they probably have not done a "make all" yet
#
if [ ! -e "$rtsEXE" ]       # Check if file exists.
  then
    echo "rlgenv not found at $rtsEXE.  Did you remember to \"make rlgenv\" in domains/realTimeStrategy ?"; echo
    exit 1                $Quit
   fi

#
# First do a quick check to see if the RL_glue exe exists.  If it does not, they probably have not done a "make all" yet
#
if [ ! -e "$glueExe" ]       # Check if file exists.
  then
    echo "RL_glue not found at $glueExe.  Did you remember to \"make all\"?"; echo
    exit 1                $Quit
   fi




$glueExe &
gluePID=$!
echo "Starting up RL-glue - PID=$gluePID"

$rtsEXE &
rtsPID=$!
echo "Starting up real time strategy game - PID=$rtsPID"

java -DRLVIZ_LIB_PATH=$RLVIZ_LIB_PATH -Xmx128M -cp $compLib:./bin/ realTimeStrategyTrainerJava

echo "-- Console Real Time Strategy Trainer finished"

echo "-- Waiting for the real time strategy game to die..."
wait $rtsPID
echo "   + real time strategy game terminated"
echo "-- Waiting for the Glue to die..."
wait $gluePID
echo "   + Glue terminated"



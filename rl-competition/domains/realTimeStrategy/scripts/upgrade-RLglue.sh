#!/bin/sh

RLGDIR=$1

if [ "$RLGDIR" = "" ]
then
  echo "usage: upgrad-RLGlue <rl-glue dir>"
  echo "  eg. scripts/upgrade-RLGlue /home/lanctot/rl-glue"
  exit
fi

PWD=`pwd`
CURDIR=`basename $PWD`

if [ "$CURDIR" = "scripts" ]
then
  echo "Please run script from main ortslite directory. "
  echo "  eg. scripts/upgrade-RLGlue <rl-glue dir>"
  exit
fi

DIR1=libs/rl-glue/src
DIR2=libs/rl-glue/src/Network
DIR3=apps/rlglue/src

echo "Part 1"

# removed Glue_utilities.cpp
for file in \
  Glue_utilities.c Glue_utilities.h \
  RL_common.h RL_glue.h RL_network.c \
  RL_network_glue.c RL_network_agent.c RL_network_experiment.c RL_network_environment.c 
do
  fullfile=`find $RLGDIR -name $file`
  echo "$fullfile -> $DIR1"
  cp $fullfile $DIR1 || exit
done

echo ""
echo "Part 2"

fullfile=`find $RLGDIR -name RL_network.h`
echo "$fullfile -> $DIR2"
cp $fullfile $DIR2 || exit

echo ""
echo "Part 3"

for file in RL_glue.c RL_server_agent.c RL_server_environment.c
do
  fullfile=`find $RLGDIR -name $file`
  echo "$fullfile -> $DIR3"
  cp $fullfile $DIR3 || exit
done

echo ""
echo "Part 4 (Java)"

cp -v $RLGDIR/RL-Glue/Java/RL-Glue.jar libs/rl-glue/src/Java || exit 

echo ""
echo "Part 5 (mains)"

cp -v $RLGDIR/RL-Glue/Network/Agent/RL_client_agent.c apps/rlgagent/src/rlgagent_main.C || exit
cp -v $RLGDIR/RL-Glue/Network/Environment/RL_client_environment.c apps/rlgenv/src/rlgenv_main.C || exit
cp -v $RLGDIR/RL-Glue/Network/Glue/RL_server_experiment.c apps/rlglue/src/rlglue_main.C || exit
cp -v $RLGDIR/RL-Glue/Network/Experiment/RL_client_experiment.c apps/rlgexp/src/rlglue_exp.C || exit

echo ""
echo "Success!"







#!/bin/bash
###
#Environments
###

pushd projects/environments/mountainCar
svn up
SKIPUPLOAD=true bash makeDistribution.bash
popd

pushd projects/environments/acrobot
svn up
SKIPUPLOAD=true bash makeDistribution.bash
popd

pushd projects/environments/helicopter
svn up
SKIPUPLOAD=true bash makeDistribution.bash
popd

pushd projects/environments/cartpole
svn up
SKIPUPLOAD=true bash makeDistribution.bash
popd

pushd projects/environments/tetris
svn up
SKIPUPLOAD=true bash makeDistribution.bash
popd

###
#Agents
###
pushd projects/agents/randomAgentJava
svn up
SKIPUPLOAD=true bash makeDistribution.bash
popd


###
#Experiments
###
pushd projects/experiments/sampleExperimentRLGlue
svn up
SKIPUPLOAD=true bash makeDistribution.bash
popd


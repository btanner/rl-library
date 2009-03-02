#!/bin/bash
###
#Environments
###

pushd projects/environments/mountainCar
svn up
bash makeDistribution.bash
popd

pushd projects/environments/acrobot
svn up
bash makeDistribution.bash
popd

pushd projects/environments/helicopter
svn up
bash makeDistribution.bash
popd

pushd projects/environments/cartpole
svn up
bash makeDistribution.bash
popd

pushd projects/environments/tetris
svn up
bash makeDistribution.bash
popd

###
#Agents
###
pushd projects/agents/randomAgentJava
svn up
bash makeDistribution.bash
popd


###
#Experiments
###
pushd projects/experiments/sampleExperimentRLGlue
svn up
bash makeDistribution.bash
popd


#!/bin/bash

#################
## Create directories and stuff
#################
echo "============================"
echo "Running makeTrainingPackage.bin"
echo "============================"

echo " * Making directories..."
cd ../
rm -Rf trainingPack
mkdir trainingPack
mkdir trainingPack/bin
mkdir trainingPack/bin/environments

mkdir trainingPack/bin/agents
mkdir trainingPack/bin/agents/GenericSarsaLambdaJava

mkdir trainingPack/bin/trainingExperiments
mkdir trainingPack/bin/trainingExperiments/trainJava

mkdir trainingPack/src
mkdir trainingPack/src/agents
mkdir trainingPack/src/agents/GenericSarsaLambdaJava

mkdir trainingPack/src/trainingExperiments
mkdir trainingPack/src/trainingExperiments/trainJava

mkdir trainingPack/src/environments
mkdir trainingPack/src/environments/mountainCar
mkdir trainingPack/src/environments/Tetrlais

echo " * Building the big jar..."
./scripts/makeBigJar.bash

echo " * Copying that jar to the bin directory of trainingPack..."
cp ./RL-Train.jar trainingPack/bin/


echo " * Exporting a copy of rl-glue from google code repository to the training pack (quietly)"
#Copy the RL-Glue source
svn export -q https://rl-glue.googlecode.com/svn/trunk/ trainingPack/rl-glue
#cp ~/ProgrammingProjects/rl-glue/Examples/Network_Java/bin/RL_glue trainingPack/bin/



#################
## MOUNTAIN CAR
#################
echo " * Copying mountainCar source code to the training pack"
##Copy the source
cd MountainCar
cp -R src/* ../trainingPack/src/environments/mountainCar/

echo " * Building the mountainCar jar and moving it to the trainingPack to the training pack"
##Make the MountainCar Jar
cd bin
jar -cf ../../trainingPack/bin/environments/MountainCar.jar ./MountainCar/*.class
cd ../../

#################
## Tetrlais
#################
echo " * Copying Tetris source code to the training pack"
##Copy the source
cd Tetrlais
cp -R src/* ../trainingPack/src/environments/Tetrlais/

echo " * Building the Tetris jar and moving it to the trainingPack to the training pack"
cd bin
jar -cf ../../trainingPack/bin/environments/Tetrlais.jar ./Tetrlais/*.class
cd ../../


#################
## GenericSarsaLambdaJava
#################
#Copy source and binaries for GenericSarsaLambda
echo " * Copying to source and binaries for GenericSarsaLamda"
cp -R GenericSarsaLambda/src/* trainingPack/src/agents/GenericSarsaLambdaJava/
cp -R GenericSarsaLambda/bin/* trainingPack/bin/agents/GenericSarsaLambdaJava/

#################
## Java Trainer
#################
echo " * Copying to source and binaries for Java Trainer"
cp JavaTrainer/src/*.java trainingPack/src/trainingExperiments/trainJava/
cp JavaTrainer/bin/*.class trainingPack/bin/trainingExperiments/trainJava/
cp scripts/runTrainer.bash trainingPack/
cp scripts/runViz.bash trainingPack/



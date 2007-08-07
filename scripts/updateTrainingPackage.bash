#!/bin/bash

#################
## Create directories and stuff
#################
echo "============================"
echo "Running updateTrainingPackage.bin"
echo "============================"
cd ..

echo " * Building the big jar..."
./scripts/makeBigJar.bash

echo " * Copying that jar to the bin directory of training pack..."
mv -f ./RL-Train.jar trainingPack/bin/


#################
## MOUNTAIN CAR
#################
echo " * Copying mountainCar source code to the training pack"
##Copy the source
cd MountainCar
rm -Rf ../trainingPack/src/environments/mountainCar/*
cp -R src/* ../trainingPack/src/environments/mountainCar/
echo " * Building the mountainCar jar and moving it to the trainingPack to the training pack"
##Make the MountainCar Jar
cd bin
rm ../../trainingPack/bin/environments/MountainCar.jar
jar -cf ../../trainingPack/bin/environments/MountainCar.jar ./MountainCar/*.class
cd ../../

#################
## Tetrlais
#################
echo " * Copying Tetris source code to the training pack"
##Copy the source
cd Tetrlais
rm -Rf  ../trainingPack/src/environments/Tetrlais/*
cp -R src/* ../trainingPack/src/environments/Tetrlais/

echo " * Building the Tetris jar and moving it to the trainingPack to the training pack"
cd bin
rm ../../trainingPack/bin/environments/Tetrlais.jar
jar -cf ../../trainingPack/bin/environments/Tetrlais.jar ./Tetrlais/*.class
cd ../../


#################
## GenericSarsaLambdaJava
#################
#Copy source and binaries for GenericSarsaLambda
echo " * Copying to source and binaries for GenericSarsaLamda"
rm -Rf trainingPack/src/agents/GenericSarsaLambdaJava/*
rm -Rf trainingPack/bin/agents/GenericSarsaLambdaJava/*
cp -R GenericSarsaLambda/src/* trainingPack/src/agents/GenericSarsaLambdaJava/
cp -R GenericSarsaLambda/bin/* trainingPack/bin/agents/GenericSarsaLambdaJava/

#################
## Java Trainer
#################
echo " * Copying to source and binaries for Java Trainer"
rm -Rf trainingPack/src/trainingExperiments/trainJava/*
rm -Rf trainingPack/bin/trainingExperiments/trainJava/*
cp -R JavaTrainer/src/* trainingPack/src/trainingExperiments/trainJava/
cp -R JavaTrainer/bin/* trainingPack/bin/trainingExperiments/trainJava/
cp scripts/runTrainer.bash trainingPack/
cp scripts/runViz.bash trainingPack/



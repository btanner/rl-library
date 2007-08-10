#!/bin/bash

#################
## Create directories and stuff
#################
echo "============================"
echo "Running updatetechDistage.bin"
echo "============================"
cd ..

echo " * Building the big jar..."
./scripts/makeBigJar.bash

echo " * Copying that jar to the bin directory of training pack..."
mv -f ./RL-Train.jar techDist/bin/


#################
## MOUNTAIN CAR
#################
echo " * Copying mountainCar source code to the training pack"
##Copy the source
cd MountainCar
rm ../techDist/src/environments/mountainCar/*.class
rm ../techDist/src/environments/mountainCar/MountainCar/*.class

cp src/*.java ../techDist/src/environments/mountainCar/
cp src/MountainCar/*.java ../techDist/src/environments/mountainCar/MountainCar/

echo " * Building the mountainCar jar and moving it to the techDist to the training pack"
##Make the MountainCar Jar
cd bin
rm ../../techDist/bin/environments/MountainCar.jar
jar -cf ../../techDist/bin/environments/MountainCar.jar ./MountainCar/*.class
cd ../../

#################
## Tetrlais
#################
echo " * Copying Tetris source code to the training pack"
##Copy the source
cd Tetrlais
rm  ../techDist/src/environments/Tetrlais/*.class
rm  ../techDist/src/environments/Tetrlais/Tetrlais/*.class

cp src/*.java ../techDist/src/environments/Tetrlais/
cp src/Tetrlais/*.java ../techDist/src/environments/Tetrlais/Tetrlais/

echo " * Building the Tetris jar and moving it to the techDist to the training pack"
cd bin
rm ../../techDist/bin/environments/Tetrlais.jar
jar -cf ../../techDist/bin/environments/Tetrlais.jar ./Tetrlais/*.class
cd ../../

#################
## Helicopter
#################
echo " * Copying Helicopter source code to the training pack"
##Copy the source
cd Helicopter
rm  ../techDist/src/environments/Helicopter/*.java
rm  ../techDist/src/environments/Helicopter/Helicopter/*.java
cp src/*.java ../techDist/src/environments/Helicopter/
cp src/Helicopter/*.java ../techDist/src/environments/Helicopter/Helicopter/

echo " * Building the Helicopter jar and moving it to the techDist to the training pack"
cd bin
rm ../../techDist/bin/environments/Helicopter.jar
jar -cf ../../techDist/bin/environments/Helicopter.jar ./Helicopter/*.class
cd ../../



#################
## GenericSarsaLambdaJava
#################
#Copy source and binaries for GenericSarsaLambda
echo " * Copying to source and binaries for GenericSarsaLamda"
rm -f techDist/src/agents/GenericSarsaLambdaJava/*.java
rm -f techDist/src/agents/GenericSarsaLambdaJava/GenericSarsaLambda/*.java

rm -f techDist/bin/agents/GenericSarsaLambdaJava/*.class
rm -f techDist/bin/agents/GenericSarsaLambdaJava/GenericSarsaLambda/*.class

cp GenericSarsaLambda/src/*.java techDist/src/agents/GenericSarsaLambdaJava/
cp GenericSarsaLambda/src/GenericSarsaLambda/*.java techDist/src/agents/GenericSarsaLambdaJava/GenericSarsaLambda/

cp GenericSarsaLambda/bin/*.class techDist/bin/agents/GenericSarsaLambdaJava/
cp GenericSarsaLambda/bin/GenericSarsaLambda/*.class techDist/bin/agents/GenericSarsaLambdaJava/GenericSarsaLambda/

#################
## Java Trainer
#################
echo " * Copying to source and binaries for Java Trainer"
rm techDist/src/trainingExperiments/trainJava/*.java
rm techDist/bin/trainingExperiments/trainJava/*.class

cp JavaTrainer/src/*.java techDist/src/trainingExperiments/trainJava/
cp JavaTrainer/bin/*.class techDist/bin/trainingExperiments/trainJava/




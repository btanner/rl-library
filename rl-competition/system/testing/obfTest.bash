#!/bin/bash
export PROPATH=~/Desktop/proguard4.0/lib
echo "copying Tetris jar to testing directory"
cp ../libraries/envJars/GeneralizedTetris.jar oldEnv.jar
echo "obfuscate"
java -jar $PROPATH/proguard.jar @envConfig.pro

echo "obfuscating done: copy the new jar back in"
echo "cp newEnv.jar ../libraries/envJars/GeneralizedTetris.jar"
echo "cp newEnv.jar ../libraries/envJars/Tetris.jar"
cp newEnv.jar ../libraries/envJars/GeneralizedTetris.jar
cp newEnv.jar ../libraries/envJars/Tetrlais.jar

echo "copying MC jar to testing directory"
cp ../libraries/envJars/GeneralizedMountainCar.jar oldEnv.jar
echo "obfuscate"
java -jar $PROPATH/proguard.jar @envConfig.pro

echo "obfuscating done: copy the new jar back in"
echo "cp newEnv.jar ../libraries/envJars/GeneralizedMountainCar.jar"
echo "cp newEnv.jar ../libraries/envJars/MountainCar.jar"
cp newEnv.jar ../libraries/envJars/GeneralizedMountainCar.jar
cp newEnv.jar ../libraries/envJars/MountainCar.jar

echo "copying Helicopter jar to testing directory"
cp ../libraries/envJars/GeneralizedHelicopter.jar oldEnv.jar
echo "obfuscate"
java -jar $PROPATH/proguard.jar @envConfig.pro

echo "obfuscating done: copy the new jar back in"
echo "cp newEnv.jar ../libraries/envJars/GeneralizedHelicopter.jar"
echo "cp newEnv.jar ../libraries/envJars/Helicopter.jar"
cp newEnv.jar ../libraries/envJars/GeneralizedHelicopter.jar
cp newEnv.jar ../libraries/envJars/Helicopter.jar
cp newEnv.jar ~/Desktop

rm *.jar
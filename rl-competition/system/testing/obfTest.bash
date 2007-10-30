#!/bin/bash
echo "copying MC jar to testing directory"
cp ../libraries/envJars/GeneralizedMountainCar.jar oldEnv.jar
echo "obfuscate"
java -jar ~/Desktop/proguard4.0/lib/proguard.jar @envConfig.pro

echo "obfuscating done: copy the new jar back in"
echo "cp newEnv.jar ../libraries/envJars/GeneralizedMountainCar.jar"
echo "cp newEnv.jar ../libraries/envJars/MountainCar.jar"
cp newEnv.jar ../libraries/envJars/GeneralizedMountainCar.jar
cp newEnv.jar ../libraries/envJars/MountainCar.jar

echo "copying Tetris jar to testing directory"
cp ../libraries/envJars/GeneralizedTetris.jar oldEnv.jar
echo "obfuscate"
java -jar ~/Desktop/proguard4.0/lib/proguard.jar @envConfig.pro

echo "obfuscating done: copy the new jar back in"
echo "cp newEnv.jar ../libraries/envJars/GeneralizedTetris.jar"
echo "cp newEnv.jar ../libraries/envJars/Tetris.jar"
cp newEnv.jar ../libraries/envJars/GeneralizedTetris.jar
cp newEnv.jar ../libraries/envJars/Tetrlais.jar

rm *.jar
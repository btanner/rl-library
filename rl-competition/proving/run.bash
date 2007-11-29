#/bin/bash


#Variables
basePath=../
systemPath=$basePath/system
libPath=$systemPath/libraries
provingPath=$systemPath/proving

compLib=$libPath/RLVizLib.jar

echo "Starting up proving software..."
java -Xmx128M -jar ./bin/Proving.jar
#java -Xmx128M -cp $compLib -jar ./bin/Proving.jar
echo "-- Proving software finished"

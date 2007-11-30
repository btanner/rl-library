#/bin/bash


#Variables
basePath=../
systemPath=$basePath/system
libPath=$systemPath/libraries
provingPath=$systemPath/proving

compLib=$libPath/RLVizLib.jar

echo "Starting up proving software..."
macAboutNameCommand=-Dcom.apple.mrj.application.apple.menu.about.name=RLVizApp
java -Xmx128M  $macAboutNameCommand -jar ./bin/Proving.jar
echo "-- Proving software finished"

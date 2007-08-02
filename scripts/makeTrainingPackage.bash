#/bin/bash
cd ../
rm -Rf trainingPack
mkdir trainingPack
mkdir trainingPack/bin
mkdir trainingPack/bin/environmentJars
mkdir trainingPack/bin/agent
mkdir trainingPack/src

#Copy the Glue Executable
cp ~/ProgrammingProjects/rl-glue/Examples/Network_Java/bin/RL_glue trainingPack/bin/

#Copy the RL-Glue Jar
mv RL-Glue.jar trainingPack/bin/

#Make the MountainCar Jar
cd MountainCar/bin
jar -cvf ../../trainingPack/bin/environmentJars/MountainCar.jar ./MountainCar/*.class
cd ../../

#Copy the bin files for SarsaLambda
cp -R GenericSarsaLambda/bin/* trainingPack/bin/agent/


#Make the VizLib Jar
cd RL-VizLib/bin/
jar -cvf ../../trainingPack/bin/RL-VizLib.jar *
cd ../../

#Make the Viz Jar
cd RL-Viz/bin/
jar -cvf ../../trainingPack/bin/RL-Viz.jar *
cd ../../

#Make the EnvShell Jar
cd EnvironmentShell/bin/
jar -cvf ../../trainingPack/bin/EnvShell.jar ./environmentShell/*.class
cd ../../

#Copy the EnvTrainer
cp JavaTrainer/src/*.java trainingPack/src/
cp JavaTrainer/bin/*.class trainingPack/bin/
cp scripts/runTrainer.bash trainingPack/



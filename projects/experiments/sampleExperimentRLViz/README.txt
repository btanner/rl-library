This is a sample experiment that runs a very simple 10 episode trial of the Random java agent with the Mountain Car.

===
Experiment Details
===
Language: All Java
Infrastructure: RL-Glue Only

Agent:			RandomAgent from agents/RandomAgent found in RandomAgent.jar
Environment:	MountainCar from environments/MountainCar found in MountainCar.jar
Experiment:		src/SampleExperiment.java

Executing run.bash will:

$>bash run.bash

1) compile SampleExperiment.java into classes/SampleExperiment.class
2) start RL_glue executable
3) load MountainCar
4) load RandomAgent
5) Run SampleExperiment.class

The file run.bash shows exactly how to run a simple experiment.  It's actually much easier than it looks.
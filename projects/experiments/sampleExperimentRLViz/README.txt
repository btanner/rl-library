This is a sample experiment that runs a very simple 10 episode trial of the agent/environment combination of your choice.

===
Experiment Details
===
Language: All Java
Infrastructure: RL-Viz Networked

Agent:			Using AgentShell so can run with any agent in /system/dist/
Environment:	Using EnvironmentShell so can run with any environment /system/dist/
Experiment:		src/SampleExperiment.java

Executing run.bash will:

$>bash run.bash

1) compile SampleExperiment.java into classes/SampleExperiment.class
2) start RL_glue executable
3) start EnvironmentShell
4) load AgentShell
5) Run SampleExperiment.class

The SampleExperiment will then choose the agent and environment to run.

The file run.bash shows exactly how to run a simple experiment.  It's actually much easier than it looks.
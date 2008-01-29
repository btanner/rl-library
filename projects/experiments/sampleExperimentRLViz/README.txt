This is a sample experiment that runs a very simple 10 episode trial of the agent/environment combination of your choice.

There are actual many ways this experiment can be run, depending on which bash script it is run with.

***************
runDynamicEnvAgent.bash
***************
===
Experiment Details
===
Language: All Java
Infrastructure: RL-Viz Networked

Agent:			Using AgentShell so can run with any agent in /system/dist/
Environment:	Using EnvironmentShell so can run with any environment /system/dist/
Experiment:		src/SampleExperiment.java

Executing runDynamicEnvAgent.bash will:

$>bash runDynamicEnvAgent.bash

1) compile SampleExperiment.java into classes/SampleExperiment.class
2) start RL_glue executable
3) start EnvironmentShell
4) start AgentShell
5) Run SampleExperiment.class

The SampleExperiment will then choose the agent and environment to run.

***************
runDynamicEnvStandardAgent.bash
***************
===
Experiment Details
===
Language: Java Experiment, Environment.  Agent is any networked RL-Glue agent that you start.
Infrastructure: RL-Viz Networked

Agent:			You start your own agent.  See projects/agents/randomAgentJava and projects/agents/randomAgentPython for inspiration
Environment:	Using EnvironmentShell so can run with any environment /system/dist/
Experiment:		src/SampleExperiment.java

Executing runDynamicEnvStandardAgent.bash will:

$>bash runDynamicEnvStandardAgent.bash

1) compile SampleExperiment.java into classes/SampleExperiment.class
2) start RL_glue executable
3) start EnvironmentShell
4) Run SampleExperiment.class

The SampleExperiment will then choose environment to run.

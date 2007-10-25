==========================================
RL-Competition 2008 Training Distribution
==========================================
http://rl-competition.org/

Thank you for downloading the training distribution!


--------------------------
INSTALLATION INSTRUCTIONS
--------------------------
To install the core RL-Glue code, as well as all of the sample agents, environments, and trainers, type:

$> make all

Several of the competition domains (Mountain Car, Tetris, Helicopter Control) are implemented in Java and are already packaged and ready to use.  Other domains like real time strategy and keep-away require additional installation.  Please see the /domains/ directory for more information on this domains.

--------------------------
THIS PACKAGE
--------------------------
The purpose of this package is to provide the software foundation for the reinforcement learning competition.  This package contains all of resources required to create agents and experiments and to test them on the testing versions of the competition problems.

Every experiment that is run using this software consists of four components:
	- An experiment program (a trainer)
	- An agent program
	- An environment program
	- The RL_glue communication software

We have taken care of RL_glue and the environments, so the important components for you are agents and trainers.


----------------------------------------------------
RUNNING YOUR FIRST GRAPHICAL EXPERIMENT
----------------------------------------------------
Running your first experiment is as simple as choosing a trainer and an agent and running the run.bash script in each of their directories.  Details about trainers and agents are explained later in this file.

For now, to run a random agent on any of the Java domains, open two terminal windows:

Terminal 1:
$>cd trainers/guiTrainerJava
$>bash run.bash

Terminal 2:
$>cd agents/randomAgentJava
$>bash run.bash

This will bring up the rlVizApp visualization window.  Choose the problem you want to use (GeneralizedMountainCar, GeneralizedTetris, or AlteredHelicopter), press "Load Experiment", and press "Start".  Voila!  You can select the speed of the experiment using the slider bar, or proceed one time-step at a time by pressing "stop" and then "step" repeatedly.

Since you are running the random agent, the experiment is probably not going very well.  To see better results, try running one of the specialized sample agents and matching the environment appropriately.  Notice that if you mismatch the agents and environments (run the mountain car agent on Tetris), the experiment may crash.


--------------------------
AGENTS
--------------------------
Agents are located in the /agents directory.

We have provided several sample agents, implemented in C/C++, Python, and Java.

There is at least one sample agent specifically created for each domain, these are:
/agents/mountainCarAgentCPP
/agents/tetrisAgentJava
/agents/realTimeStrategyAgentJava
/agents/realTimeStrategyAgentCPP

TODO: add /agents/helicopterAgentCPP
TODO: add a soccer agent
TODO: add a python agent

There is also one general purpose agent that can work on any problem except real time strategy:
/agents/randomAgentJava


To run any agent, go into its directory and type:
$>bash run.bash 

*****
**Note: running an agent is only half of what is necessary to run an experiment, you also need a trainer!  See the next section.
*****

The source of each agent is in the /agents/<someAgent>/src directory

To rebuild an agent, in that agent's directory type:
$>make clean;make

----------------------------------------------------
TRAINERS (Mountain Car, Tetris, and Helcopter Control)
----------------------------------------------------
Trainers are programs that put an agent into an environment and control the experiment.  Sample trainers are located in the /trainers directory.

There are two types of trainers: console trainers, and graphical trainers.

Console trainers are well suited to running proper experiment, trying parameters, etc.  The graphical trainer gives a visual representation of the problem and can be very handing for debugging and visually evaluating your agent's performance.

We have provided several sample trainers, implemented in C/C++, Python, and Java.  They are all identical in function, and are located in:
/trainers/consoleTrainerJava
/trainers/consoleTrainerCPP
/trainers/consoleTrainerPython

These console trainers will allow run any of the Java domains, Mountain Car, Tetris, or Helicopter.  Check the source code in /trainers/<someTrainer>/src/ for an idea how to select which problem the trainer will select.  Special training programs are required for real time strategy and keep-away.

To run any trainer, go into its directory and type:
$>bash run.bash

*****
**Note: running a trainer is only half of what is necessary to run an experiment, you also need an agent!  See the previous section!
*****
To rebuild a trainer, in that trainer's directory type:
$>make clean;make

The graphical trainer that will run for any of the Java domains is in:
/trainers/guiTrainerJava

To run this trainer, go into its directory and type:
$>bash run.bash

The source of this trainer is not provided, it is provided on an AS-IS basis.



----------------------------------------------------
RUNNING YOUR FIRST CONSOLE EXPERIMENT
----------------------------------------------------
Running a console experiment is very similar to running a graphical experiment, you need to choose a trainer and an agent.  By default, the trainers are set to run 10 episodes of mountain car.  In order to run Tetris or Helicopter, go into the src/ directory and uncomment the appropriate lines, and then type "make" back in the trainer's home directory.

To run the default mountain car experiment, open two terminal windows:


Terminal 1:
$>cd trainers/consoleTrainerJava
$>bash run.bash

Terminal 2:
$>cd agents/mountainCarAgentCPP
$>bash run.bash

You should see an experiment unfold as the sample mountain-car agent learns.

----------------------------------------------------
Making your first agent
----------------------------------------------------
TODO: Fill this in


----------------------------------------------------
CREDITS
----------------------------------------------------
Brian Tanner
Adam White
Mark Lee
Andrew Butcher
Matthew Radkie
Leah Hackman


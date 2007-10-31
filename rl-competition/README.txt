===========================================
RL-Competition 2008 Training Distribution
===========================================
http://rl-competition.org/

Thank you for downloading the training distribution!

==========
Contents:

1: Hyper Quick Start
2: Installation Instructions
3: Information about what is in this package
4: Running your first graphical experiment
5: Agents
6: Trainers
7: Running your first console experiment
8: Real Time Strategy
9: Keepaway
10: Where to go for more info
11: Credits
==========


--------------------------
1 :: HYPER QUICK START
--------------------------
If you just want to see something happen without learning anything about what's in this package, do:
$> make all
$> bash runDemo.bash

***
* Note: This is a graphical demo and will only work if you can display a graphical Java window!
***
This will run the graphical trainer for Tetris, Helicopter Hovering, Polyathlon and Mountain Car with a random agent.  Please read the rest of this document to learn what that means and what other things you can do!

--------------------------
2 :: INSTALLATION INSTRUCTIONS
--------------------------
To install the core RL-Glue code, as well as all of the sample agents, environments, and trainers, type:

$> make all

Several of the competition domains (Mountain Car, Tetris, Helicopter Control, Polyathlon) are implemented in Java and are already packaged and ready to use.  Other domains like real time strategy and keep-away require additional installation.  Please see the /domains/ directory for more information on this domains.

--------------------------
3 :: THIS PACKAGE
--------------------------
The purpose of this package is to provide the software foundation for the reinforcement learning competition.  This package contains all of the resources required to create agents and experiments and to test them on the testing versions of the competition problems.

Every experiment that is run using this software consists of four components:
	- An experiment program (a trainer)
	- An agent program
	- An environment program
	- The RL_glue communication software

We have taken care of RL_glue and the environments, so the important components for you are the agents and the trainers.


----------------------------------------------------
4 :: RUNNING YOUR FIRST GRAPHICAL EXPERIMENT
----------------------------------------------------
Running your first experiment is as simple as choosing a trainer and an agent and running the run.bash script in each of their directories.  Details about trainers and agents are explained later in this file.

For now, to run a random agent on any of the Java domains, open two terminal windows:

Terminal 1:
$>cd trainers/guiTrainerJava
$>bash run.bash

Terminal 2:
$>cd agents/randomAgentJava
$>bash run.bash

This will bring up the rlVizApp visualization window.  Choose the problem you want to use (GeneralizedMountainCar, GeneralizedTetris, GeneralizedHelicopter or Polyathlon), press "Load Experiment", and press "Start".  Voila!  You can select the speed of the experiment using the slider bar, or proceed one time-step at a time by pressing "stop" and then "step" repeatedly.

Since you are running the random agent, the experiment is probably not going very well.  To see better results, try running one of the specialized sample agents and matching the environment appropriately.  Notice that if you mismatch the agents and environments (run the mountain car agent on Tetris), the experiment may crash.


--------------------------
5 :: AGENTS
--------------------------
Agents are located in the /agents directory.

We have provided several sample agents, implemented in C/C++, Python, and Java.

There is at least one sample agent specifically created for each domain, these are:
/agents/mountainCarAgentCPP
/agents/tetrisAgentJava
/agents/realTimeStrategyAgentJava
/agents/realTimeStrategyAgentCPP
/agents/helicopterAgentCPP

There is also one general purpose agent that can work on any problem except real time strategy:
/agents/randomAgentJava


To run any agent, go into its directory and type:
$>bash run.bash 

*****
** Note 1: running an agent is only half of what is necessary to run an experiment, you also need a trainer!  See the next section.
*****

The source of each agent is in the /agents/<someAgent>/src directory

To rebuild an agent, in that agent's directory type:
$>make clean;make

*****
** Note 2: compiling and running an agent for the Real Time Strategy and Keepaway domains is different from the procedure described above please consult the README files in domains/realTimeStrategy/ and domains/keepAwaySoccer for more information. 
*****

----------------------------------------------------
6 :: TRAINERS (Mountain Car, Tetris, Helicopter Control, and Polyathlon)
----------------------------------------------------
Trainers are programs that put an agent into an environment and control the experiment.  Sample trainers are located in the /trainers directory.

There are two types of trainers: console trainers, and graphical trainers.

Console trainers are well suited to running a proper experiment, trying parameters, etc.  The graphical trainer gives a visual representation of the problem and can be very handy for debugging and visually evaluating your agent's performance.

We have provided several sample trainers, implemented in C/C++, Python, and Java.  They are all identical in function, and are located in:
/trainers/consoleTrainerJava
/trainers/consoleTrainerCPP
/trainers/consoleTrainerPython

These console trainers will run any of the Java domains: Mountain Car, Tetris, Helicopter, or Polyathlon.  Check the source code in /trainers/<someTrainer>/src/ for an idea how to select which problem the trainer will select.  Special training programs are required for real time strategy and keep-away.

To run any trainer, go into its directory and type:
$>bash run.bash

*****
**Note 2: running a trainer is only half of what is necessary to run an experiment, you also need an agent!  See the previous section!
*****
To rebuild a trainer, in that trainer's directory type:
$>make clean;make

The graphical trainer that will run for any of the Java domains is in:
/trainers/guiTrainerJava

To run this trainer, go into its directory and type:
$>bash run.bash

The source code for the GUI trainer is not provided.  This trainer is provided on an AS-IS basis.

*****
** Note 2: compiling and running a trainer for the RTS and Keepaway domains is different from the procedure described above please consult the README files in domains/realTimeStrategy/ and domains/keepAwaySoccer for more information. 
*****

----------------------------------------------------
7 :: RUNNING YOUR FIRST CONSOLE EXPERIMENT
----------------------------------------------------
Running a console experiment is very similar to running a graphical experiment, you need to choose a trainer and an agent.  By default, the trainers are set to run 10 episodes of mountain car.  In order to run Tetris, Helicopter, or Polyathlon go into the src/ directory and uncomment the appropriate lines, and then type "make" back in the trainer's home directory.

To run the default mountain car experiment, open two terminal windows:


Terminal 1:
$>cd trainers/consoleTrainerJava
$>bash run.bash

Terminal 2:
$>cd agents/mountainCarAgentCPP
$>bash run.bash

You should see an experiment unfold as the sample mountain-car agent learns.

*****
** Note 1: compiling and running a trainer for the Real Time Strategy and Keepaway domains is different from the procedure described above please consult the README files in domains/realTimeStrategy/ and domains/keepAwaySoccer for more information. 
*****

----------------------------------------------------
8: Real Time Strategy
----------------------------------------------------
To run an Real Time Strategy experiment and, in general, find out more about compiling and running Real Time Strategy agents please read the INSTALL and README files in domains/realTimeStrategy.

----------------------------------------------------
9: Keepaway
----------------------------------------------------
To run an Keepaway experiment and, in general, find out more about compiling and running Keepaway agents please read the README file in domains/keepAwaySoccer.

----------------------------------------------------
10 :: Where to go for more info
----------------------------------------------------
To get more information about how to use this package, how to create custom experiments, new agents, and trouble shooting, please visit:
http://rl-competition.org 

----------------------------------------------------
11 :: CREDITS
----------------------------------------------------
Brian Tanner
Adam White
Mark Lee
Andrew Butcher
Matthew Radkie
Leah Hackman


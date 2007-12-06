This version of the Keepaway players is derived from UT-Austin's
Keepaway framework, version 0.6.  In the standard Keepaway package,
Keepers can learn (using any appropriate RL learning algorithm) by
implementing the SMDPAgent interface. In this package, we have added
LearningAgent.cc, which implements the SMDPAgent interface, but does
no learning itself. Instead, it utilizes the RL-Glue interface so that
RL-Glue-compatable agents may be used to control Keepers. 

In the current resease, we assume that only one Keeper is controled at
a time, but it should be easily extendable to controling all the
keepers with multiple instances of RL-Glue learning agents. The
benifits include:
1) Plug-and-play with existing RL-Glue agents
2) Less Keepaway-specific knowledege is needed to begin developing
3) Multiple languages can be used (RL-Glue supports C, Java, and Python)

In the subdirectory ExampleAgent/, a simple agent that acts randomly
to demonstrate how the RL-Glue interface may be used.

Keepaway Framework code, tutorials, and reference papers:
www.cs.utexas.edu/~AustinVilla/sim/keepaway/

RL-Glue downloads and documentation:
rlai.cs.ualberta.ca/RLBB/top.html
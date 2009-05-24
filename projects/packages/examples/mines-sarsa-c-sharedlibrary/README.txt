=================================
RL-Glue C MINES-SARSA-SAMPLE-SHAREDLIBRARY README
=================================

----------------------------
Introduction
----------------------------
This is a sample experiment that has the "Mines" environment and a simple tabular Sarsa agent.  This project lives in RL-Library, but is also distributed with the RL-Viz project.  

The point of this sample is to show how a C RL-Glue project can be compiled into a shared library so that it can be loaded at runtime by the RL-Viz external CPPEnvLoader and CPPAgentLoader.  This allows C/C++ environments and agents to be used transparently with RL-Viz with or without using network sockets.  It also allows for run-time parameterization and configuration of environments and agents using the parameter holder.

This example requires the C/C++ Codec:
http://glue.rl-community.org/Home/Extensions/c-c-codec
----------------------------
Compiling
----------------------------
This project has been tested in Mac OS X and Ubuntu 9.04.  Making shared libraries varies from platform to platform, so your mileage may vary.  We would be happy to receive contributions on how to make this cleaner and more portable.

Depending on whether you have RL-Glue Core and the C/C++ codec installed in your include and library paths, you may have to update the Makefile or not.
If the installation was to the default location, this Makefile may not need to be edited.
If you have to update the Makefile, set the -I and -L for CFLAGS and LDFLAGS to point to where you installed the headers and libs.  

To make with OS X (create SampleMinesEnvironment.dylib)
>$ make
or
>$ make OSX

To make with Linux (create SampleMinesEnvironment.so)
>$ make Linux

----------------------------
Running
----------------------------
Compiling into Dylibs is specifically so that you don't run these components directly, but rather, you load them with the RL-Viz External CPPEnvLoader and/or CPPAgentLoader.  A link to a brief tutorial on these advanced techniques is here:
http://code.google.com/p/rl-viz/wiki/CPPLoaders

----------------------------
More Information
----------------------------
Please see the C/C++ Codec Manual and FAQ if you are looking for more information:
http://glue.rl-community.org/Home/Extensions/c-c-codec

-- 
Brian Tanner
btanner@rl-community.org


# Adjust this to have the path to your RL-Glue Directory.
RL-GLUE = ../../../rl-glue/RL-Glue

# Adjust this to point to your build directory
BUILD_PATH = Build

#Adjust this to point to your bin directory
BIN_PATH = bin

#Adjust this to point to your source files directory
SRC_PATH = src

#Points to the Rl-Glue Utilities Directory. 
UTILS = ../../../rl-glue/Utils

#Change this to a C compiler if all your code is in C. If you have C++ you will want a C++ compiler..
CC = g++

# These are the Compiler flags used to compile all the code. Quick example: -I$(RL-GLUE) lets the compiler see all the 
# files in RL-GLue. 
CFLAGS = -I$(RL-GLUE)/ -I$(UTILS)/ -ansi -pedantic -Wall

#These are the Compiler Linking flags.
LDFLAGS = 

#There are the object files for all the things the agent needs. 
# In this example, we need the agent itself, then the RL_Client_agent and RL_network code to run over the network. and then
#The parser is in Glue_utilities.
AGENT_OBJECTS = RandomAgent.o RL_client_agent.o RL_network.o Glue_utilities.o

#This links everything together into an executable named RL_agent
RL_agent: $(AGENT_OBJECTS)
	$(CC) -o $(BIN_PATH)/$@ $(addprefix $(BUILD_PATH)/, $(AGENT_OBJECTS))

#This builds the RandomAgent
RandomAgent.o: $(SRC_PATH)/RandomAgent.cpp 
	$(CC) -c $(CFLAGS) -I ../../../Utils $< -o $(BUILD_PATH)/$@

#this Builds the Client Agent code
RL_client_agent.o: $(RL-GLUE)/Network/Agent/RL_client_agent.c
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@

#This builds the parser
Glue_utilities.o: $(UTILS)/Glue_utilities.cpp
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@

#This builds the network code
RL_network.o: $(RL-GLUE)/Network/RL_network.c
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@

#This cleans up the bin and Build directories.
clean:
	rm -rf $(BIN_PATH)/*
	rm -rf $(BUILD_PATH)/*
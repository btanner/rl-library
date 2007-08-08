RL-GLUE = ../../../rl-glue/RL-Glue
BUILD_PATH = Build
BIN_PATH = bin
SRC_PATH = src
UTILS = ../../../rl-glue/Utils

CC = g++
CFLAGS = -I$(RL-GLUE)/ -I$(UTILS)/ -ansi -pedantic -Wall
LDFLAGS = 

AGENT_OBJECTS = RandomAgent.o RL_client_agent.o RL_network.o Glue_utilities.o

RL_agent: $(AGENT_OBJECTS)
	$(CC) -o $(BIN_PATH)/$@ $(addprefix $(BUILD_PATH)/, $(AGENT_OBJECTS))
	
RandomAgent.o: $(SRC_PATH)/RandomAgent.cpp 
	$(CC) -c $(CFLAGS) -I ../../../Utils $< -o $(BUILD_PATH)/$@

RL_client_agent.o: $(RL-GLUE)/Network/Agent/RL_client_agent.c
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@

Glue_utilities.o: $(UTILS)/Glue_utilities.cpp
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@

RL_network.o: $(RL-GLUE)/Network/RL_network.c
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@

clean:
	rm -rf $(BIN_PATH)/*
	rm -rf $(BUILD_PATH)/*
# must create symbolic link to rl-glue directory

RL-GLUE = ./rl-glue/RL-Glue
UTILS   = ./rl-glue/Utils

BUILD_PATH = ../obj/obj.rlglue
BIN_PATH = ..
SRC_PATH = .

CC      = g++  
CFLAGS  = -I$(RL-GLUE)/ -ansi -pedantic -Wall
LDFLAGS =

AGENT_OBJECTS = rlglue_agent.o Glue_utilities.o RL_client_agent.o RL_network_agent.o

RL_agent: $(AGENT_OBJECTS)
	$(CC) -o $(BIN_PATH)/$@ $(addprefix $(BUILD_PATH)/, $(AGENT_OBJECTS))
	
rlglue_agent.o: $(SRC_PATH)/rlglue_agent.C $(SRC_PATH)/rlglue_agent.H
	$(CC) -c $(CFLAGS) -I$(UTILS) $< -o $(BUILD_PATH)/$@

Glue_utilities.o: $(UTILS)/Glue_utilities.c $(UTILS)/Glue_utilities.h
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@

RL_client_agent.o: $(RL-GLUE)/Network/Agent/RL_client_agent.c
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@
	
RL_network_agent.o: $(RL-GLUE)/Network/RL_network.c
	$(CC) -c $(CFLAGS) $< -o $(BUILD_PATH)/$@

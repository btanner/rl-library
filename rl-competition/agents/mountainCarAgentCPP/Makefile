
AGENT_OBJECTS = MountainCarAgent.o Glue_utilities.o RLStruct_util.o tiles.o

AGENT_SRC_PATH = src
BUILD_PATH = bin
BIN_PATH = bin
RL_GLUE_PATH = ../../system/RL-Glue/RL-Glue
UTILS_PATH = ../../system/RL-Glue/Utils
CC = g++
CFLAGS = -I$(RL_GLUE_PATH) -I$(UTILS_PATH) -I$(AGENT_SRC_PATH) -Wall -g -O3

all: RL_agent

include $(RL_GLUE_PATH)/RL_glue.makefile

RL_agent: $(addprefix $(BUILD_PATH)/,$(AGENT_OBJECTS)) $(BUILD_PATH)/RL_client_agent.o $(BUILD_PATH)/RL_network.o
	$(CC) -o $(BIN_PATH)/RL_agent $^

$(BUILD_PATH)/MountainCarAgent.o: $(AGENT_SRC_PATH)/MountainCarAgent.cpp
	$(CC) -c $(CFLAGS) $^ -o $@

$(BUILD_PATH)/RLStruct_util.o: $(AGENT_SRC_PATH)/RLStruct_util.cpp
	$(CC) -c $(CFLAGS) $^ -o $@

$(BUILD_PATH)/tiles.o: $(AGENT_SRC_PATH)/tiles.cpp
	$(CC) -c $(CFLAGS) $^ -o $@

clean:
	rm -f $(BUILD_PATH)/*










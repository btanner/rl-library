
AGENT_OBJECTS = RandomAgent.o Glue_utilities.o

AGENT_SRC_PATH = src
BUILD_PATH = bin
BIN_PATH = bin
RL_GLUE_PATH = ../../system/RL-Glue/RL-Glue
UTILS_PATH = ../../system/RL-Glue/Utils
CC = g++
CFLAGS = -I$(RL_GLUE_PATH) -I$(UTILS_PATH) -I$(AGENT_SRC_PATH) -Wall -g

all: RL_agent

include $(RL_GLUE_PATH)/RL_glue.makefile

RL_agent: $(addprefix $(BUILD_PATH)/,$(AGENT_OBJECTS)) $(BUILD_PATH)/RL_client_agent.o $(BUILD_PATH)/RL_network.o
	$(CC) -o $(BIN_PATH)/RL_agent $^

$(BUILD_PATH)/RandomAgent.o: $(AGENT_SRC_PATH)/RandomAgent.c
	$(CC) -c $(CFLAGS) $^ -o $@

clean:
	rm -f $(BUILD_PATH)/*










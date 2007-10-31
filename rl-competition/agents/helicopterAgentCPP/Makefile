
AGENT = weak_baseline_controlle.cpp
AGENT_OBJECTS = $(basename $(AGENT)).o Glue_utilities.o

AGENT_SRC_PATH = ./src
BUILD_PATH = ./bin
RL_GLUE_BASE = ../../system/RL-Glue
RL_GLUE_PATH = $(RL_GLUE_BASE)/RL-Glue
UTILS_PATH = $(RL_GLUE_BASE)/Utils
BIN_PATH = ./bin

CFLAGS = -I$(RL_GLUE_PATH) -I$(UTILS_PATH) -I$(AGENT_SRC_PATH)

ifeq ($(suffix $(AGENT)),.cpp)
	CC = $(CXX)
endif

all: $(BIN_PATH)/RL_agent

include $(RL_GLUE_PATH)/RL_glue.makefile

AGENT_OBJECTS += RL_client_agent.o RL_network.o

$(BIN_PATH)/RL_agent: $(addprefix $(BUILD_PATH)/, $(AGENT_OBJECTS))
	$(CC) -o $(BIN_PATH)/RL_agent $^ -lm

$(BUILD_PATH)/$(basename $(AGENT)).o: $(AGENT_SRC_PATH)/$(AGENT)
	$(CC) -c $(CFLAGS) $^ -o $@

clean:
	rm -rf $(BIN_PATH)/RL_agent $(BUILD_PATH)/*
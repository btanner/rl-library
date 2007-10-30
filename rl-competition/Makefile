BIN_PATH = ./system
BIN2_PATH = ./system/bin
RL_GLUE_PATH = ./system/RL-Glue/RL-Glue
BUILD_PATH = ./system/build

AGENT_NETWORKED=1
ENV_NETWORKED=1
EXP_NETWORKED=1

CFLAGS = -I$(RL_GLUE_PATH)

include $(RL_GLUE_PATH)/RL_glue.makefile

all: $(BIN_PATH)/RL_glue
	cd ./agents/randomAgentJava/ && make
	cd ./agents/tetrisAgentJava/ && make
	cd ./agents/mountainCarAgentCPP/ && make
	cd ./agents/realTimeStrategyAgentJava/ && make
	cd ./agents/helicopterAgentCPP/ && make
	cd ./trainers/consoleTrainerJava/ && make
	cd ./trainers/consoleTrainerPython/ && make
	cd ./trainers/consoleTrainerCPP/ && make
	cd ./trainers/consoleRealTimeStrategyTrainerJava/ && make

clean:
	
	-rm $(BUILD_PATH)/*
	-rm $(BIN_PATH)/RL_glue
	cd ./agents/randomAgentJava/ && make clean
	cd ./agents/tetrisAgentJava/ && make clean
	cd ./agents/mountainCarAgentCPP/ && make clean
	cd ./agents/realTimeStrategyAgentJava/ && make clean
	cd ./agents/helicopterAgentCPP/ && make clean
	cd ./trainers/consoleTrainerJava/ && make clean
	cd ./trainers/consoleTrainerPython/ && make clean
	cd ./trainers/consoleTrainerCPP/ && make clean
	cd ./trainers/consoleRealTimeStrategyTrainerJava/ && make clean

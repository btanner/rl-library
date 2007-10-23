RLVizPath = ../../system/libraries
SRC_PATH = src
BIN_PATH = bin

all: 
	javac $(SRC_PATH)/*.java -d $(BIN_PATH) -cp $(RLVizPath)/RLVizLib.jar

RL_agent:
	javac $(SRC_PATH)/OLAgent.java -d $(BIN_PATH)/ -cp $(RLVizPath)/RLVizLib.jar

OLJ:
	javac $(SRC_PATH)/Parameters.java -d $(BIN_PATH)

clean:
	-rm -rf $(BIN_PATH)/*.class
